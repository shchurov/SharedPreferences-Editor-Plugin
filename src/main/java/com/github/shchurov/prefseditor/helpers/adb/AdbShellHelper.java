package com.github.shchurov.prefseditor.helpers.adb;

public class AdbShellHelper {

    private AdbCommandExecutor cmdExecutor;
    private String adbPath;
    private String shellPrefix;
    private StringBuilder shellLogs = new StringBuilder();

    public AdbShellHelper(AdbCommandExecutor cmdExecutor, String deviceId, String adbPath) {
        this.adbPath = adbPath;
        this.cmdExecutor = cmdExecutor;
        shellPrefix = adbPath + " -s " + deviceId + " shell ";
    }

    public String getSdCardPath() {
        return exec(shellPrefix + "echo $EXTERNAL_STORAGE");
    }

    private String exec(String command) {
        String result = cmdExecutor.execute(command);
        addToLogs(command, result);
        return result;
    }

    private void addToLogs(String command, String result) {
        shellLogs.append("\nCommand: ")
                .append(command)
                .append("\n")
                .append(result)
                .append("\n");
    }

    public void setPrefsPermissions(String applicationId) {
        exec(shellPrefix + "run-as " + applicationId + " chmod -R 777 " + getPrefsPath(applicationId));
    }

    private String getPrefsPath(String applicationId) {
        return "/data/data/" + applicationId + "/shared_prefs/";
    }

    public void clearDir(String dir) {
        exec(shellPrefix + "rm -rf " + dir + "/*");
    }

    public void removeDir(String dir) {
        exec(shellPrefix + "rm -rf " + dir);
    }

    public void makeDir(String path) {
        exec(shellPrefix + "mkdir -p " + path);
    }

    public void copyPrefsToDir(String dir, String applicationId) {
        exec(shellPrefix + "cp " + getPrefsPath(applicationId) + "* " + dir);
    }

    public String getDirFiles(String dir) {
        return exec(shellPrefix + "for f in " + dir + "/*; do echo $f; done");
    }

    public void moveFile(String src, String dst) {
        exec(shellPrefix + "mv " + src + " " + dst);
    }

    public void pullFile(String src, String dst) {
        exec(adbPath + " pull " + src + " " + dst);
    }

    public void pushFile(String src, String dst) {
        exec(adbPath + " push " + src + " " + dst);
    }

    public void overwritePrefs(String dir, String applicationId) {
        exec(shellPrefix + "cp " + dir + "/* " + getPrefsPath(applicationId));
    }

    public void killApp(String applicationId) {
        exec(shellPrefix + "am force-stop " + applicationId);
    }

    public void startApp(String applicationId, String defaultActivityName) {
        exec(shellPrefix + "am start " + applicationId + "/" + defaultActivityName);
    }

    public String getShellLogs() {
        return shellLogs.toString();
    }

}
