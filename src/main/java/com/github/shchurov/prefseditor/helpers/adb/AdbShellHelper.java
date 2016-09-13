package com.github.shchurov.prefseditor.helpers.adb;

public class AdbShellHelper {

    private AdbCommandExecutor cmdExecutor;
    private String shellPrefix;

    public AdbShellHelper(AdbCommandExecutor cmdExecutor, String deviceId) {
        this.cmdExecutor = cmdExecutor;
        shellPrefix = "adb -s " + deviceId + " shell ";
    }

    public String getSdCardPath() {
        return exec(shellPrefix + "echo $EXTERNAL_STORAGE");
    }

    private String exec(String command) {
        return cmdExecutor.execute(command);
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
        exec(shellPrefix + "cp " + getPrefsPath(applicationId) + "/* " + dir);
    }

    public String getDirFiles(String dir) {
        return exec(shellPrefix + "ls " + dir);
    }

    public void moveFile(String src, String dst) {
        exec(shellPrefix + "mv " + src + " " + dst);
    }

    public void pullFile(String src, String dst) {
        exec("adb pull " + src + " " + dst);
    }

    public void pushFile(String src, String dst) {
        exec("adb push " + src + " " + dst);
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

}
