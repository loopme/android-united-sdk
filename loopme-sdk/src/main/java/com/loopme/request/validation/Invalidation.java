package com.loopme.request.validation;

import androidx.annotation.NonNull;

public class Invalidation {
    private final String path;
    private final ValidationRule rule;

    public Invalidation(String path, ValidationRule rule) {
        this.path = path;
        this.rule = rule;
    }

    public String getPath() {
        return path;
    }

    public ValidationRule getRule() {
        return rule;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s : %s", path, rule);
    }
}

