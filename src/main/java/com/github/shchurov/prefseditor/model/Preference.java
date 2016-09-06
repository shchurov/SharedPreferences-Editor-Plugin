package com.github.shchurov.prefseditor.model;

import java.util.Set;

public class Preference {

    private String key;
    private Type type;
    private Object value;

    public Preference(String key, Object value) {
        this.key = key;
        this.value = value;
        type = extractType(value);
    }

    private Type extractType(Object value) {
        if (value instanceof Integer) {
            return Type.INTEGER;
        } else if (value instanceof Boolean) {
            return Type.BOOLEAN;
        } else if (value instanceof Float) {
            return Type.FLOAT;
        } else if (value instanceof Long) {
            return Type.LONG;
        } else if (value instanceof String) {
            return Type.STRING;
        } else if (value instanceof Set) {
            return Type.STRING_SET;
        } else {
            throw new IllegalArgumentException(value.toString());
        }
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public enum Type {

        INTEGER("int"), BOOLEAN("boolean"), FLOAT("float"), LONG("long"), STRING("String"), STRING_SET("Set<String>");

        public final String name;

        Type(String name) {
            this.name = name;
        }

    }


}
