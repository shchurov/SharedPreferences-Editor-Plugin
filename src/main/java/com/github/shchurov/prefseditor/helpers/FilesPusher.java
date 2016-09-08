package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.exceptions.PushFilesException;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.util.Map;

public class FilesPusher {

    private Project project;
    private AdbCommandBuilder cmdBuilder = new AdbCommandBuilder();
    private AdbCommandExecutor cmdExecutor = new AdbCommandExecutor();

    public FilesPusher(Project project) {
        this.project = project;
    }

    public void pushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) throws PushFilesException {
        ProgressManagerUtils.runWithProgressDialog(project, "Applying Changes",
                () -> performPushFiles(unifiedNamesMap, bundle));
    }

    private Void performPushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) {
        execute(cmdBuilder.buildPushFile(bundle.localUnifiedDir, bundle.deviceMainDir));
        reverseUnifyFileNames(unifiedNamesMap, bundle);
        String applicationId = ProjectUtils.getApplicationId(project);
        execute(cmdBuilder.buildOverwritePrefs(bundle.deviceNormalDir, applicationId));
        execute(cmdBuilder.buildRemoveDir(bundle.deviceMainDir));
        restartApp(project);
        return null;
    }

    private String execute(String cmd) {
        try {
            return cmdExecutor.execute(cmd);
        } catch (IOException e) {
            throw new PushFilesException(e);
        }
    }

    private void reverseUnifyFileNames(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) {
        for (Map.Entry<String, String> entry : unifiedNamesMap.entrySet()) {
            String src = bundle.deviceUnifiedDir + "/" + entry.getValue();
            String dst = bundle.deviceNormalDir + "/" + entry.getKey();
            execute(cmdBuilder.buildMoveFile(src, dst));
        }
    }

    private void restartApp(Project project) {
        String applicationId = ProjectUtils.getApplicationId(project);
        execute(cmdBuilder.buildKillApp(applicationId));
        String activityName = ProjectUtils.getDefaultActivityName(project);
        execute(cmdBuilder.buildStartApp(applicationId, activityName));
    }

}
