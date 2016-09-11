package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.helpers.exceptions.CreateDirectoriesException;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DirectoriesCreator {

    private static final String MAIN_DIR_NAME = "prefs_editor";
    private static final String NORMAL_DIR_NAME = "normal";
    private static final String UNIFIED_DIR_NAME = "unified";

    private Project project;
    private AdbCommandBuilder cmdBuilder;
    private AdbCommandExecutor cmdExecutor;

    public DirectoriesCreator(Project project, AdbCommandBuilder cmdBuilder, AdbCommandExecutor cmdExecutor) {
        this.project = project;
        this.cmdBuilder = cmdBuilder;
        this.cmdExecutor = cmdExecutor;
    }

    public DirectoriesBundle createDirectories() throws CreateDirectoriesException {
        return ProgressManagerUtils.runWithProgressDialog(project, "Creating Directories",
                this::performCreateDirectories);
    }

    private DirectoriesBundle performCreateDirectories() {
        String sdCard = execute(cmdBuilder.buildGetSdCardPath());
        String deviceMainDir = sdCard + "/" + MAIN_DIR_NAME;
        String deviceNormalDir = deviceMainDir + "/" + NORMAL_DIR_NAME;
        String deviceUnifiedDir = deviceMainDir + "/" + UNIFIED_DIR_NAME;
        execute(cmdBuilder.buildMakeDir(deviceNormalDir));
        execute(cmdBuilder.buildMakeDir(deviceUnifiedDir));
        String localMainDir;
        try {
            localMainDir = Files.createTempDirectory(null).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String localUnifiedDir = localMainDir + File.separator + UNIFIED_DIR_NAME;
        return new DirectoriesBundle(deviceMainDir, deviceNormalDir, deviceUnifiedDir, localMainDir, localUnifiedDir);
    }

    private String execute(String command) {
        try {
            return cmdExecutor.execute(command);
        } catch (IOException e) {
            throw new CreateDirectoriesException(e);
        }
    }

}
