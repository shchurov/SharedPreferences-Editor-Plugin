package com.github.shchurov.prefseditor.helpers.adb;

import com.github.shchurov.prefseditor.helpers.ProgressManagerUtils;
import com.github.shchurov.prefseditor.helpers.exceptions.StartAdbServerException;
import com.intellij.openapi.project.Project;

import java.io.IOException;

public class AdbServerStarter {

    private Project project;
    private AdbCommandBuilder cmdBuilder;
    private AdbCommandExecutor cmdExecutor;

    public AdbServerStarter(Project project, AdbCommandBuilder cmdBuilder, AdbCommandExecutor cmdExecutor) {
        this.project = project;
        this.cmdBuilder = cmdBuilder;
        this.cmdExecutor = cmdExecutor;
    }

    public void start() {
        ProgressManagerUtils.runWithProgressDialog(project, "Starting ADB Server", this::performStart);
    }

    private Void performStart() {
        try {
            cmdExecutor.execute(cmdBuilder.buildStartServer());
        } catch (IOException e) {
            throw new StartAdbServerException(e);
        }
        return null;
    }


}
