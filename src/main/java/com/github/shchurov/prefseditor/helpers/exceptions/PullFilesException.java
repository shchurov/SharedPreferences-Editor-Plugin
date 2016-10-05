package com.github.shchurov.prefseditor.helpers.exceptions;

public class PullFilesException extends RuntimeException {

    public PullFilesException(Throwable cause) {
        super("Error while pulling SharedPreferences files", cause);
    }

}
