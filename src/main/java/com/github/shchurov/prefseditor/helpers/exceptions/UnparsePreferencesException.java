package com.github.shchurov.prefseditor.helpers.exceptions;

public class UnparsePreferencesException extends RuntimeException {

    public UnparsePreferencesException(Throwable cause) {
        super("Error while un-parsing SharedPreferences file", cause);
    }

}
