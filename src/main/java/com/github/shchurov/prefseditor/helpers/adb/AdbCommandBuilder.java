package com.github.shchurov.prefseditor.helpers.adb;

public class AdbCommandBuilder {

    private String shellPrefix;

    public AdbCommandBuilder(String deviceId) {
        shellPrefix = "adb -s " + deviceId + " shell ";
    }

    public String buildGetSdCardPath() {
        return shellPrefix + "echo $EXTERNAL_STORAGE";
    }

    public String buildSetPrefsPermissions(String applicationId) {
        return shellPrefix + "run-as " + applicationId + " chmod -R 777 " + getPrefsPath(applicationId);
    }

    private String getPrefsPath(String applicationId) {
        return "/data/data/" + applicationId + "/shared_prefs/";
    }

    public String buildClearDir(String dir) {
        return shellPrefix + "rm -rf " + dir + "/*";
    }

    public String buildRemoveDir(String dir) {
        return shellPrefix + "rm -rf " + dir;
    }

    public String buildMakeDir(String path) {
        return shellPrefix + "mkdir -p " + path;
    }

    public String buildCopyPrefsToDir(String dir, String applicationId) {
        return shellPrefix + "cp " + getPrefsPath(applicationId) + "/* " + dir;
    }

    public String buildGetDirFiles(String dir) {
        return shellPrefix + "ls " + dir;
    }

    public String buildMoveFile(String src, String dst) {
        return shellPrefix + "mv " + src + " " + dst;
    }

    public String buildPullFile(String src, String dst) {
        return "adb pull " + src + " " + dst;
    }

    public String buildPushFile(String src, String dst) {
        return "adb push " + src + " " + dst;
    }

    public String buildOverwritePrefs(String dir, String applicationId) {
        return shellPrefix + "cp " + dir + "/* " + getPrefsPath(applicationId);
    }

    public String buildKillApp(String applicationId) {
        return shellPrefix + "am force-stop " + applicationId;
    }

    public String buildStartApp(String applicationId, String defaultActivityName) {
        return shellPrefix + "am start " + applicationId + "/" + defaultActivityName;
    }

}
