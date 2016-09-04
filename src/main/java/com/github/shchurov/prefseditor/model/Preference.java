package com.github.shchurov.prefseditor.model;

public class Preference {

    private String key;
    private Object value;

    public Preference(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
