package com.loopme.request.validation;

public class Validation {
    private String path;
    private String value;
    private ValidationRule[] rules;

    public Validation(String path, String value, ValidationRule[] rules){
        this.path = path;
        this.value = value;
        this.rules = rules;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    public ValidationRule[] getRules() {
        return rules;
    }
}