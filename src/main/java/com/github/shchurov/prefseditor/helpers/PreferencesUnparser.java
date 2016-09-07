package com.github.shchurov.prefseditor.helpers;

import com.github.shchurov.prefseditor.model.Preference;

import java.util.List;

public class PreferencesUnparser {

    public void unparse(List<Preference> preferences, String filePath) throws UnparseException {
        for (Preference p : preferences) {
            System.out.println(p.getKey() + ": " + p.getValue());
        }
    }

    public static class UnparseException extends Exception {
        UnparseException(Throwable cause) {
            super(cause);
        }
    }

}
