package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;
import com.github.shchurov.prefseditor.helpers.exceptions.PushFilesException;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;

import java.util.Map;

public class FilesPusher {

    private Project project;
    private AdbCommandBuilder cmdBuilder;
    private AdbCommandExecutor cmdExecutor;

    public FilesPusher(Project project, AdbCommandBuilder cmdBuilder, AdbCommandExecutor cmdExecutor) {
        this.project = project;
        this.cmdBuilder = cmdBuilder;
        this.cmdExecutor = cmdExecutor;
    }

    public void pushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) throws PushFilesException {
        ProgressManagerUtils.runWithProgressDialog(project, "Applying Changes",
                () -> performPushFiles(unifiedNamesMap, bundle));
    }

    private Void performPushFiles(Map<String, String> unifiedNamesMap, DirectoriesBundle bundle) {
        execute(cmdBuilder.buildPushFile(bundle.localUnifiedDir, bundle.deviceMainDir));
        reverseUnifyFileNames(unifiedNamesMap, bundle);
        String applicationId = ProjectUtils.getApplicationId(project);
        killApp();
        execute(cmdBuilder.buildOverwritePrefs(bundle.deviceNormalDir, applicationId));
        execute(cmdBuilder.buildRemoveDir(bundle.deviceMainDir));
        startApp();
        return null;
    }

    private String execute(String cmd) {
        try {
            return cmdExecutor.execute(cmd);
        } catch (ExecuteAdbCommandException e) {
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

    private void killApp() {
        String applicationId = ProjectUtils.getApplicationId(project);
        execute(cmdBuilder.buildKillApp(applicationId));
    }

    private void startApp() {
        String applicationId = ProjectUtils.getApplicationId(project);
        String activityName = ProjectUtils.getDefaultActivityName(project);
        execute(cmdBuilder.buildStartApp(applicationId, activityName));
    }

}
