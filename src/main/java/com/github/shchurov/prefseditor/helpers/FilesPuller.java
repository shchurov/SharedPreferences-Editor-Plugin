package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbShellHelper;
import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;
import com.github.shchurov.prefseditor.helpers.exceptions.PreferencesFilesNotFoundException;
import com.github.shchurov.prefseditor.helpers.exceptions.PullFilesException;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.HashMap;
import java.util.Map;

public class FilesPuller {

    private Project project;
    private AdbShellHelper shellHelper;
    private String applicationId;

    public FilesPuller(Project project, AdbShellHelper shellHelper, AndroidFacet facet) {
        this.project = project;
        this.shellHelper = shellHelper;
        applicationId = Utils.getApplicationId(facet);
    }

    public Map<String, String> pullFiles(DirectoriesBundle bundle) throws PullFilesException,
            PreferencesFilesNotFoundException {
        return Utils.runWithProgressDialog(project, "Pulling Files", () -> {
            try {
                return performPullFiles(bundle);
            } catch (ExecuteAdbCommandException e) {
                throw new PullFilesException(e);
            }
        });
    }

    private Map<String, String> performPullFiles(DirectoriesBundle bundle) {
        shellHelper.clearDir(bundle.deviceNormalDir);
        shellHelper.clearDir(bundle.deviceUnifiedDir);
        shellHelper.setPrefsPermissions(applicationId);
        shellHelper.copyPrefsToDir(bundle.deviceNormalDir, applicationId);
        Map<String, String> unifiedNamesMap = buildUnifiedNamesMap(bundle.deviceNormalDir);
        unifyFileNames(unifiedNamesMap, bundle);
        shellHelper.pullFile(bundle.deviceUnifiedDir, bundle.localMainDir);
        return unifiedNamesMap;
    }

    private Map<String, String> buildUnifiedNamesMap(String dir) {
        String filesStr = shellHelper.getDirFiles(dir).trim();
        if (filesStr.isEmpty()) {
            throw new PreferencesFilesNotFoundException();
        }
        String[] files = filesStr.split("\n\n");
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
            shellHelper.moveFile(src, dst);
        }
    }

}
