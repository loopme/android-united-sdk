package com.loopme.request.validation;

public class ValidationOutput {
    private Validation validation;
    private boolean isValid;

    public ValidationOutput(boolean isValid, Validation validation) {
        this.isValid = isValid;
        this.validation = validation;
    }
}
