package com.github.shchurov.prefseditor.helpers.exceptions;

public class PreferencesFilesNotFoundException extends RuntimeException {

    public PreferencesFilesNotFoundException() {
        super("SharedPreferences files not found");
    }

}
