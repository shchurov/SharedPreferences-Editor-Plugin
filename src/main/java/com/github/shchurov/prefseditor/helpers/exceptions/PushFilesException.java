package com.github.shchurov.prefseditor.helpers.exceptions;

public class PushFilesException extends RuntimeException {

    public PushFilesException(Throwable cause) {
        super("Error while pushing SharedPreferences files", cause);
    }

}
