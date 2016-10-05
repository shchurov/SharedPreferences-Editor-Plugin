package com.github.shchurov.prefseditor.helpers.exceptions;

public class ParsePreferencesException extends RuntimeException {

    public ParsePreferencesException(Throwable cause) {
        super("Error while parsing SharedPreferences file", cause);
    }

}
