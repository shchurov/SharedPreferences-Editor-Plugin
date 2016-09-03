package com.github.shchurov.prefseditor;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.ProjectUtils;
import com.github.shchurov.prefseditor.presentation.EditorDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class OpenEditorAction extends AnAction {

    private static final String ANDROID_SEPARATOR = "/";
    private static final String MAIN_DIR_NAME = "prefs_editor";
    private static final String NORMAL_DIR_NAME = "normal";
    private static final String UNIFIED_DIR_NAME = "unified";

    private AdbCommandBuilder commandBuilder = new AdbCommandBuilder();
    private AdbCommandExecutor commandExecutor = new AdbCommandExecutor();
    private String deviceMainDir;
    private String deviceNormalDir;
    private String deviceUnifiedDir;
    private String localMainDir;
    private String localUnifiedDir;
    private Map<String, String> unifiedNamesMap = new HashMap<>();

    @Override
    public void actionPerformed(AnActionEvent action) {
        Project project = ProjectUtils.getProject(action);
        String applicationId = ProjectUtils.getApplicationId(project);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try {
                initDeviceDirs();
                initLocalDirs();
                pullSharedPreferences(applicationId);
                ApplicationManager.getApplication().invokeLater(() -> openEditorDialog(project));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Pulling Files", false, project);
    }

    private void initDeviceDirs() throws IOException {
        String sdCard = execute(commandBuilder.buildGetSdCardPath());
        deviceMainDir = sdCard + ANDROID_SEPARATOR + MAIN_DIR_NAME;
        execute(commandBuilder.buildRemoveDir(deviceMainDir));
        deviceNormalDir = deviceMainDir + ANDROID_SEPARATOR + NORMAL_DIR_NAME;
        deviceUnifiedDir = deviceMainDir + ANDROID_SEPARATOR + UNIFIED_DIR_NAME;
        execute(commandBuilder.buildMakeDir(deviceNormalDir));
        execute(commandBuilder.buildMakeDir(deviceUnifiedDir));
    }

    private void initLocalDirs() throws IOException {
        localMainDir = Files.createTempDirectory(null).toString();
        localUnifiedDir = localMainDir + File.separator + UNIFIED_DIR_NAME;
    }

    private void pullSharedPreferences(String applicationId) throws IOException {
        execute(commandBuilder.buildSetPrefsPermissions(applicationId));
        execute(commandBuilder.buildCopyPrefsToDir(deviceNormalDir, applicationId));
        String filesStr = execute(commandBuilder.buildGetDirFiles(deviceNormalDir));
        String[] files = filesStr.split("\n\n");
        buildUnifiedNamesMap(files);
        unifyFileNames();
        execute(commandBuilder.buildPullFile(deviceUnifiedDir, localMainDir));
        System.out.println(localUnifiedDir);
    }

    private String execute(String cmd) throws IOException {
        return commandExecutor.execute(cmd);
    }

    private void buildUnifiedNamesMap(String[] files) {
        for (int i = 0; i < files.length; i++) {
            unifiedNamesMap.put(files[i], "pref" + i + ".xml");
        }
    }

    private void unifyFileNames() throws IOException {
        for (Map.Entry<String, String> entry : unifiedNamesMap.entrySet()) {
            String src = deviceNormalDir + ANDROID_SEPARATOR + entry.getKey();
            String dst = deviceUnifiedDir + ANDROID_SEPARATOR + entry.getValue();
            execute(commandBuilder.buildMoveFile(src, dst));
        }
    }

    private void openEditorDialog(Project project) {
        new EditorDialog(project, localUnifiedDir, unifiedNamesMap).show();
    }

}
