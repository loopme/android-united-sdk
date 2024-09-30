package com.loopme.request;

import static org.junit.Assert.*;

import com.loopme.request.validation.Validation;
import com.loopme.request.validation.ValidationRule;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class RequestValidatorTest2 {

    private RequestValidator validator;
    private ArrayList<Validation> dummyRules;

    @Before
    public void setUp() {
        validator = new RequestValidator();
        dummyRules = new ArrayList<>();
    }

    @Test
    public void emptyRequired() {
        dummyRules.add(new Validation("app.id", "", new ValidationRule[]{ValidationRule.REQUIRED}));
        boolean result = validator.validate(dummyRules);
        assertFalse(result);
    }

    @Test
    public void blankRequired() {
        dummyRules.add(new Validation("app.id", "   ", new ValidationRule[]{ValidationRule.REQUIRED}));
        boolean result = validator.validate(dummyRules);
        assertFalse(result);
    }

    @Test
    public void validRequired() {
        dummyRules.add(new Validation("app.id", "abcdefgjklta89", new ValidationRule[]{ValidationRule.REQUIRED}));
        boolean result = validator.validate(dummyRules);
        assertTrue(result);
    }

    @Test
    public void blankValue() {
        dummyRules.add(new Validation("imp[].banner.w", "  ", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        boolean result = validator.validate(dummyRules);
        assertFalse(result);
    }

    @Test
    public void notGreaterThanZero() {
        dummyRules.add(new Validation("imp[].banner.w", "0", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        boolean result = validator.validate(dummyRules);
        assertFalse(result);
    }

    @Test
    public void validRequiredAndGTZ() {
        dummyRules.add(new Validation("imp[].banner.w", "480", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        boolean result = validator.validate(dummyRules);
        assertTrue(result);
    }

    @Test
    public void oneValidOneNot() {
        dummyRules.add(new Validation("app.id", "abcdefgjklta89", new ValidationRule[]{ValidationRule.REQUIRED}));
        dummyRules.add(new Validation("imp[].banner.w", "0", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        boolean result = validator.validate(dummyRules);
        assertFalse(result);
    }

    @Test
    public void oneNotValidOneAndOneValid() {
        dummyRules.add(new Validation("imp[].banner.w", "0", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        dummyRules.add(new Validation("app.id", "abcdefgjklta89", new ValidationRule[]{ValidationRule.REQUIRED}));
        boolean result = validator.validate(dummyRules);
        assertFalse(result);
    }




}