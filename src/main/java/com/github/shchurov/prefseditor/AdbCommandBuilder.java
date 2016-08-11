package com.github.shchurov.prefseditor;

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

    public String buildMakeDir(String path) {
        return "adb shell mkdir -p " + path;
    }

    public String buildCopyPrefsToDir(String dirPath, String applicationId) {
        return "adb shell cp " + getPrefsPath(applicationId) + "/* " + dirPath;
    }

    public String buildGetDirFiles(String dirPath) {
        return "adb shell ls " + dirPath;
    }

    public String buildMoveFile(String src, String dst) {
        return "adb shell mv " + src + " " + dst;
    }

    public String buildPullFile(String src, String dst) {
        return "adb pull " + src + " " + dst;
    }

    // 0. Делаем shared_prefs chmod 777
    // 1. Копируем файлы из shared_prefs в /sdcard/SharedPreferencesEditor
    // 2. Создаем мэп имен файлов
    // 3. Переименовываем файлы
    // 4. Пулим файлы из SharedPreferencesEditor
    // 5. Пушим файлы в SharedPreferencesEditor
    // 6. Переименовываем файлы назад
    // 4. Копируем файлы из SharedPreferencesEditor в shared_prefs

}
