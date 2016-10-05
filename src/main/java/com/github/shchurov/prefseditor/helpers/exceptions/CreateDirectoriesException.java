package com.github.shchurov.prefseditor.helpers.exceptions;

public class CreateDirectoriesException extends RuntimeException {

    public CreateDirectoriesException(Throwable cause) {
        super("Error while creating temporary directories", cause);
    }

}
