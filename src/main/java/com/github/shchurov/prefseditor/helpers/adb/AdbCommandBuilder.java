package com.github.shchurov.prefseditor.helpers.adb;

public class AdbCommandBuilder {

    public String buildGetSdCardPath() {
        return "adb shell echo $EXTERNAL_STORAGE";
    }

    public String buildSetPrefsPermissions(String applicationId) {
        return "adb shell run-as " + applicationId + " chmod -R 777 " + getPrefsPath(applicationId);
    }

    private String getPrefsPath(String applicationId) {
        return "/data/data/" + applicationId + "/shared_prefs/";
    }

    public String buildClearDir(String dir) {
        return "adb shell rm -rf " + dir + "/*";
    }

    public String buildRemoveDir(String dir) {
        return "adb shell rm -rf " + dir;
    }

    public String buildMakeDir(String path) {
        return "adb shell mkdir -p " + path;
    }

    public String buildCopyPrefsToDir(String dir, String applicationId) {
        return "adb shell cp " + getPrefsPath(applicationId) + "/* " + dir;
    }

    public String buildGetDirFiles(String dir) {
        return "adb shell ls " + dir;
    }

    public String buildMoveFile(String src, String dst) {
        return "adb shell mv " + src + " " + dst;
    }

    public String buildPullFile(String src, String dst) {
        return "adb pull " + src + " " + dst;
    }

    public String buildPushFile(String src, String dst) {
        return "adb push " + src + " " + dst;
    }

    public String buildOverwritePrefs(String dir, String applicationId) {
        return "adb shell cp " + dir + "/* " + getPrefsPath(applicationId);
    }

    public String buildKillApp(String applicationId) {
        return "adb shell am force-stop " + applicationId;
    }

    public String buildStartApp(String applicationId, String defaultActivityName) {
        return "adb shell am start " + applicationId + "/" + defaultActivityName;
    }

    String buildStartServer() {
        return "adb start-server";
    }

}
