package com.loopme.request;

import static org.junit.Assert.assertEquals;

import com.loopme.request.validation.Invalidation;
import com.loopme.request.validation.Validation;
import com.loopme.request.validation.ValidationRule;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class RequestValidatorTest {

    private RequestValidator validator;
    private ArrayList<Validation> dummyRules;

    @Before
    public void setUp() {
        validator = new RequestValidator();
        dummyRules = new ArrayList<>();
    }

    @Test
    public void emptyRequired() {
        String path = "app.id";
        dummyRules.add(new Validation(path, "", new ValidationRule[]{ValidationRule.REQUIRED}));
        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(1, result.size());
        Invalidation invalidation = result.get(0);
        assertEquals(path, invalidation.getPath());
        assertEquals(ValidationRule.REQUIRED, invalidation.getRule());
    }

    @Test
    public void blankRequired() {
        String path = "app.id";
        dummyRules.add(new Validation(path, "  ", new ValidationRule[]{ValidationRule.REQUIRED}));
        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(1, result.size());
        Invalidation invalidation = result.get(0);
        assertEquals(path, invalidation.getPath());
        assertEquals(ValidationRule.REQUIRED, invalidation.getRule());
    }

    @Test
    public void validRequired() {
        String path = "app.id";
        dummyRules.add(new Validation(path, "adasfsdfas89080", new ValidationRule[]{ValidationRule.REQUIRED}));
        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(0, result.size());
    }

    @Test
    public void blankValue() {
        String path = "imp[].banner.w";
        dummyRules.add(new Validation(path, "  ", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(2, result.size());
        Invalidation invalidation1 = result.get(0);
        assertEquals(path, invalidation1.getPath());
        assertEquals(ValidationRule.REQUIRED, invalidation1.getRule());
        Invalidation invalidation2 = result.get(1);
        assertEquals(path, invalidation2.getPath());
        assertEquals(ValidationRule.GREATER_THEN_ZERO, invalidation2.getRule());
    }

    @Test
    public void notGreaterThanZero() {
        String path = "imp[].banner.w";
        dummyRules.add(new Validation(path, "0", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(1, result.size());
        Invalidation invalidation = result.get(0);
        assertEquals(path, invalidation.getPath());
        assertEquals(ValidationRule.GREATER_THEN_ZERO, invalidation.getRule());
    }

    @Test
    public void validRequiredAndGTZ() {
        String path = "imp[].banner.w";
        dummyRules.add(new Validation(path, "480", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(0, result.size());
    }

    @Test
    public void oneValidOneNot() {
        String appIdPath = "app.id";
        dummyRules.add(new Validation(appIdPath, "adasfsdfas89080", new ValidationRule[]{ValidationRule.REQUIRED}));
        String bannerWidthPath = "imp[].banner.w";
        dummyRules.add(new Validation(bannerWidthPath, "0", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));

        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(1, result.size());

        Invalidation invalidation = result.get(0);
        assertEquals(bannerWidthPath, invalidation.getPath());
        assertEquals(ValidationRule.GREATER_THEN_ZERO, invalidation.getRule());
    }

    @Test
    public void oneNotValidOneAndOneValid() {
        String bannerWidthPath = "imp[].banner.w";
        dummyRules.add(new Validation(bannerWidthPath, "0", new ValidationRule[]{ValidationRule.REQUIRED, ValidationRule.GREATER_THEN_ZERO}));
        String appIdPath = "app.id";
        dummyRules.add(new Validation(appIdPath, "adasfsdfas89080", new ValidationRule[]{ValidationRule.REQUIRED}));

        ArrayList<Invalidation> result = validator.validate(dummyRules);
        assertEquals(1, result.size());

        Invalidation invalidation = result.get(0);
        assertEquals(bannerWidthPath, invalidation.getPath());
        assertEquals(ValidationRule.GREATER_THEN_ZERO, invalidation.getRule());
    }
}