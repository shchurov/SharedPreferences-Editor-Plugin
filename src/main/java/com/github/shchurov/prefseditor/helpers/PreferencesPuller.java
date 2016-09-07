package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PreferencesPuller {

    private static final String ANDROID_SEPARATOR = "/";

    private Project project;
    private AdbCommandBuilder cmdBuilder = new AdbCommandBuilder();
    private AdbCommandExecutor cmdExecutor = new AdbCommandExecutor();

    public PreferencesPuller(Project project) {
        this.project = project;
    }

    public Map<String, String> pullPreferences(DirectoriesBundle bundle) throws IOException {
        String applicationId = ProjectUtils.getApplicationId(project);
        execute(cmdBuilder.buildSetPrefsPermissions(applicationId));
        execute(cmdBuilder.buildCopyPrefsToDir(bundle.deviceNormalDir, applicationId));
        String filesStr = execute(cmdBuilder.buildGetDirFiles(bundle.deviceNormalDir));
        String[] files = filesStr.split("\n\n");
        Map<String, String> unifiedNamesMap = buildUnifiedNamesMap(files);
        unifyFileNames(unifiedNamesMap, bundle);
        execute(cmdBuilder.buildPullFile(bundle.deviceUnifiedDir, bundle.localMainDir));
        return unifiedNamesMap;
    }

    private String execute(String cmd) throws IOException {
        return cmdExecutor.execute(cmd);
    }

    private Map<String, String> buildUnifiedNamesMap(String[] files) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            map.put(files[i], "pref" + i + ".xml");
        }
        return map;
    }

    private void unifyFileNames(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) throws IOException {
        for (Map.Entry<String, String> entry : unifiedNamesMap.entrySet()) {
            String src = bundle.deviceNormalDir + ANDROID_SEPARATOR + entry.getKey();
            String dst = bundle.deviceUnifiedDir + ANDROID_SEPARATOR + entry.getValue();
            execute(cmdBuilder.buildMoveFile(src, dst));
        }
    }

}
