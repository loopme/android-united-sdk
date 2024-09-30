package com.loopme.request;

import android.util.Log;

import com.loopme.request.validation.Validation;
import com.loopme.request.validation.ValidationRule;

import java.util.ArrayList;

public class RequestValidator {

    private static final String LOG_TAG = "RequestValidator";

    /**
     * After calling validateOrtbRequest function, this field should be populated. If empty hashmap, it means, that request is valid
     */
    public boolean validate(ArrayList<Validation> rules) {
        boolean isValid = true;
        for (Validation validation : rules) {
            for (ValidationRule rule : validation.getRules()) {
                switch (rule) {
                    case REQUIRED:
                        if (isValid) {
                            isValid = validateRequired(validation);
                        } else {
                            validateRequired(validation);
                        }
                        break;
                    case GREATER_THEN_ZERO:
                        if (isValid) {
                            isValid = validateGreaterThanZero(validation);
                        } else {
                            validateGreaterThanZero(validation);
                        }
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown validation rule");
                        break;
                }
            }

        }
        return isValid;
    }

    private boolean validateRequired(Validation validation) {
        boolean isValid = validation.getValue() != null && !validation.getValue().isBlank();
        if (!isValid) {
            Log.e(LOG_TAG, String.format("Validation failed: %s is required and missing or empty.", validation.getPath()));
        }
        return isValid;
    }

    private boolean validateGreaterThanZero(Validation validation) {
        boolean isValid;
        try {
            isValid = Integer.parseInt(validation.getValue()) > 0;
        } catch (Exception e) {
            isValid = false;
            Log.e(LOG_TAG, e.getMessage());
        }
        if (!isValid) {
            Log.e(LOG_TAG, String.format("Validation failed: %s must be greater than 0.", validation.getPath()));
        }
        return isValid;
    }
}
