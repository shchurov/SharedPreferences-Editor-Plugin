package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;
import com.github.shchurov.prefseditor.helpers.exceptions.PullFilesException;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.HashMap;
import java.util.Map;

public class FilesPuller {

    private Project project;
    private AdbCommandBuilder cmdBuilder;
    private AdbCommandExecutor cmdExecutor;
    private String applicationId;

    public FilesPuller(Project project, AdbCommandBuilder cmdBuilder, AdbCommandExecutor cmdExecutor,
            AndroidFacet facet) {
        this.project = project;
        this.cmdBuilder = cmdBuilder;
        this.cmdExecutor = cmdExecutor;
        applicationId = ProjectUtils.getApplicationId(facet);
    }

    public Map<String, String> pullFiles(DirectoriesBundle bundle) throws PullFilesException {
        return ProgressManagerUtils.runWithProgressDialog(project, "Pulling Files", () -> performPullFiles(bundle));
    }

    private Map<String, String> performPullFiles(DirectoriesBundle bundle) {
        execute(cmdBuilder.buildClearDir(bundle.deviceNormalDir));
        execute(cmdBuilder.buildClearDir(bundle.deviceUnifiedDir));
        execute(cmdBuilder.buildSetPrefsPermissions(applicationId));
        execute(cmdBuilder.buildCopyPrefsToDir(bundle.deviceNormalDir, applicationId));
        String filesStr = execute(cmdBuilder.buildGetDirFiles(bundle.deviceNormalDir));
        String[] files = filesStr.split("\n\n");
        Map<String, String> unifiedNamesMap = buildUnifiedNamesMap(files);
        unifyFileNames(unifiedNamesMap, bundle);
        execute(cmdBuilder.buildPullFile(bundle.deviceUnifiedDir, bundle.localMainDir));
        return unifiedNamesMap;
    }

    private String execute(String cmd) {
        try {
            return cmdExecutor.execute(cmd);
        } catch (ExecuteAdbCommandException e) {
            throw new PullFilesException(e);
        }
    }

    private Map<String, String> buildUnifiedNamesMap(String[] files) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            map.put(files[i], "pref" + i + ".xml");
        }
        return map;
    }

    private void unifyFileNames(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) {
        for (Map.Entry<String, String> entry : unifiedNamesMap.entrySet()) {
            String src = bundle.deviceNormalDir + "/" + entry.getKey();
            String dst = bundle.deviceUnifiedDir + "/" + entry.getValue();
            execute(cmdBuilder.buildMoveFile(src, dst));
        }
    }

}
