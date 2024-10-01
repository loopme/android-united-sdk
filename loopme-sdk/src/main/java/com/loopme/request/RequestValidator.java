package com.loopme.request;

import android.util.Log;

import com.loopme.request.validation.Invalidation;
import com.loopme.request.validation.Validation;
import com.loopme.request.validation.ValidationRule;

import java.util.ArrayList;

public class RequestValidator {

    private static final String LOG_TAG = "RequestValidator";

    /**
     * If empty list returned - request is valid
     */
    public ArrayList<Invalidation> validate(ArrayList<Validation> rules) {
        ArrayList<Invalidation> invalidations = new ArrayList<>();
        for (Validation validation : rules) {
            for (ValidationRule rule : validation.getRules()) {
                switch (rule) {
                    case REQUIRED:
                        if (!validateRequired(validation)) {
                            invalidations.add(new Invalidation(validation.getPath(), ValidationRule.REQUIRED));
                        }
                        break;
                    case GREATER_THEN_ZERO:
                        if (!validateGreaterThanZero(validation)) {
                            invalidations.add(new Invalidation(validation.getPath(), ValidationRule.GREATER_THEN_ZERO));
                        }
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown validation rule");
                        break;
                }
            }

        }
        return invalidations;
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
