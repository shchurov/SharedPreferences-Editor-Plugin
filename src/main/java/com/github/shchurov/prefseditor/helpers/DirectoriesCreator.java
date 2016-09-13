package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.helpers.adb.AdbShellHelper;
import com.github.shchurov.prefseditor.helpers.exceptions.CreateDirectoriesException;
import com.github.shchurov.prefseditor.helpers.exceptions.ExecuteAdbCommandException;
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
    private AdbShellHelper shellHelper;

    public DirectoriesCreator(Project project, AdbShellHelper shellHelper) {
        this.project = project;
        this.shellHelper = shellHelper;
    }

    public DirectoriesBundle createDirectories() throws CreateDirectoriesException {
        return Utils.runWithProgressDialog(project, "Creating Directories", () -> {
            try {
                return performCreateDirectories();
            } catch (IOException | ExecuteAdbCommandException e) {
                throw new CreateDirectoriesException(e);
            }
        });
    }

    private DirectoriesBundle performCreateDirectories() throws IOException {
        String sdCard = shellHelper.getSdCardPath();
        String deviceMainDir = sdCard + "/" + MAIN_DIR_NAME;
        String deviceNormalDir = deviceMainDir + "/" + NORMAL_DIR_NAME;
        String deviceUnifiedDir = deviceMainDir + "/" + UNIFIED_DIR_NAME;
        shellHelper.makeDir(deviceNormalDir);
        shellHelper.makeDir(deviceUnifiedDir);
        String localMainDir = Files.createTempDirectory(null).toString();
        String localUnifiedDir = localMainDir + File.separator + UNIFIED_DIR_NAME;
        return new DirectoriesBundle(deviceMainDir, deviceNormalDir, deviceUnifiedDir, localMainDir, localUnifiedDir);
    }

}
