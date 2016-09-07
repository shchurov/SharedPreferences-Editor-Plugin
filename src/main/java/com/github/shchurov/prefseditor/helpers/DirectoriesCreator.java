package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbCommandBuilder;
import com.github.shchurov.prefseditor.helpers.adb.AdbCommandExecutor;
import com.github.shchurov.prefseditor.model.DirectoriesBundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DirectoriesCreator {

    private static final String ANDROID_SEPARATOR = "/";
    private static final String MAIN_DIR_NAME = "prefs_editor";
    private static final String NORMAL_DIR_NAME = "normal";
    private static final String UNIFIED_DIR_NAME = "unified";

    private AdbCommandBuilder cmdBuilder = new AdbCommandBuilder();
    private AdbCommandExecutor cmdExecutor = new AdbCommandExecutor();

    public DirectoriesBundle createDirectories() throws IOException {
        String sdCard = execute(cmdBuilder.buildGetSdCardPath());
        String deviceMainDir = sdCard + ANDROID_SEPARATOR + MAIN_DIR_NAME;
        execute(cmdBuilder.buildRemoveDir(deviceMainDir));
        String deviceNormalDir = deviceMainDir + ANDROID_SEPARATOR + NORMAL_DIR_NAME;
        String deviceUnifiedDir = deviceMainDir + ANDROID_SEPARATOR + UNIFIED_DIR_NAME;
        execute(cmdBuilder.buildMakeDir(deviceNormalDir));
        execute(cmdBuilder.buildMakeDir(deviceUnifiedDir));
        String localMainDir = Files.createTempDirectory(null).toString();
        String localUnifiedDir = localMainDir + File.separator + UNIFIED_DIR_NAME;
        return new DirectoriesBundle(deviceMainDir, deviceNormalDir, deviceUnifiedDir, localMainDir, localUnifiedDir);
    }

    private String execute(String command) throws IOException {
        return cmdExecutor.execute(command);
    }

}
