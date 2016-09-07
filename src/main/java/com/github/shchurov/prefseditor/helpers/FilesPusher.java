package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.Map;

public class FilesPusher {

    private static final String ANDROID_SEPARATOR = "/";

    private Project project;
    private AdbCommandBuilder cmdBuilder = new AdbCommandBuilder();
    private AdbCommandExecutor cmdExecutor = new AdbCommandExecutor();

    public FilesPusher(Project project) {
        this.project = project;
    }

    public void pushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) throws IOException {
        execute(cmdBuilder.buildPushFile(bundle.localUnifiedDir, bundle.deviceMainDir));
        reverseUnifyFileNames(unifiedNamesMap, bundle);
        String applicationId = ProjectUtils.getApplicationId(project);
        execute(cmdBuilder.buildOverwritePrefs(bundle.deviceNormalDir, applicationId));
    }

    private void reverseUnifyFileNames(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle)
            throws IOException {
        for (Map.Entry<String, String> entry : unifiedNamesMap.entrySet()) {
            String src = bundle.deviceUnifiedDir + ANDROID_SEPARATOR + entry.getValue();
            String dst = bundle.deviceNormalDir + ANDROID_SEPARATOR + entry.getKey();
            execute(cmdBuilder.buildMoveFile(src, dst));
        }
    }

    private String execute(String cmd) throws IOException {
        return cmdExecutor.execute(cmd);
    }

}
