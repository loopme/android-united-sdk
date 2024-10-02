package com.loopme.request;

import static com.loopme.request.ValidationDataExtractor.APP_APPKEY;
import static com.loopme.request.ValidationDataExtractor.BANNER_HEIGHT;
import static com.loopme.request.ValidationDataExtractor.BANNER_WIDTH;
import static com.loopme.request.ValidationDataExtractor.EVENTS_EXT_PN;
import static com.loopme.request.ValidationDataExtractor.EVENTS_EXT_PV;
import static com.loopme.request.ValidationDataExtractor.SOURCE_EXT_PN;
import static com.loopme.request.ValidationDataExtractor.SOURCE_EXT_PV;
import static com.loopme.request.ValidationDataExtractor.VIDEO_HEIGHT;
import static com.loopme.request.ValidationDataExtractor.VIDEO_WIDTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.loopme.loaders.AdRequestType;
import com.loopme.request.validation.Validation;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ValidationDataExtractorTest {


    private ValidationDataExtractor dataExtractor;
    private AdRequestType dummyRequestType;

    @Before
    public void setUp() {
        dataExtractor = new ValidationDataExtractor();
    }

    @Test
    public void everythingMissingBannerAndVideo() throws JSONException {
        JSONObject request = new JSONObject(convertFileToString("all_required_not_present"));
        dummyRequestType = new AdRequestType(true, true, true);
        ArrayList<Validation> validations = dataExtractor.prepare(request, dummyRequestType);
        assertEquals(9, validations.size());
        for (Validation validation : validations) {
            assertNull(validation.getValue());
        }
    }

    @Test
    public void everythingMissingBannerOnly() throws JSONException {
        JSONObject request = new JSONObject(convertFileToString("all_required_not_present"));
        dummyRequestType = new AdRequestType(true, false, false);
        ArrayList<Validation> validations = dataExtractor.prepare(request, dummyRequestType);
        assertEquals(7, validations.size());
        for (Validation validation : validations) {
            assertNull(validation.getValue());
        }
        assertTrue(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(BANNER_HEIGHT));
        assertTrue(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(BANNER_WIDTH));
        assertFalse(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(VIDEO_HEIGHT));
        assertFalse(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(VIDEO_WIDTH));
    }

    @Test
    public void everythingMissingVideoOnly() throws JSONException {
        JSONObject request = new JSONObject(convertFileToString("all_required_not_present"));
        dummyRequestType = new AdRequestType(false, true, false);
        ArrayList<Validation> validations = dataExtractor.prepare(request, dummyRequestType);
        assertEquals(7, validations.size());
        for (Validation validation : validations) {
            assertNull(validation.getValue());
        }
        assertTrue(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(VIDEO_HEIGHT));
        assertTrue(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(VIDEO_WIDTH));
        assertFalse(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(BANNER_HEIGHT));
        assertFalse(validations.stream().map(Validation::getPath).collect(Collectors.toList()).contains(BANNER_WIDTH));
    }

    @Test
    public void everythingPresentBannerAndVideo() throws JSONException {
        JSONObject request = new JSONObject(convertFileToString("proper_ad_request"));
        dummyRequestType = new AdRequestType(true, true, true);
        ArrayList<Validation> validations = dataExtractor.prepare(request, dummyRequestType);
        assertEquals(9, validations.size());
        assertEquals(APP_APPKEY, validations.get(0).getPath());
        assertEquals(SOURCE_EXT_PN, validations.get(1).getPath());
        assertEquals(SOURCE_EXT_PV, validations.get(2).getPath());
        assertEquals(EVENTS_EXT_PN, validations.get(3).getPath());
        assertEquals(EVENTS_EXT_PV, validations.get(4).getPath());
        assertEquals(BANNER_WIDTH, validations.get(5).getPath());
        assertEquals(BANNER_HEIGHT, validations.get(6).getPath());
        assertEquals(VIDEO_WIDTH, validations.get(7).getPath());
        assertEquals(VIDEO_HEIGHT, validations.get(8).getPath());

        assertEquals("dafa602ab1", validations.get(0).getValue());
        assertEquals("Loopme", validations.get(1).getValue());
        assertEquals("9.0.6", validations.get(2).getValue());
        assertEquals("Loopme", validations.get(3).getValue());
        assertEquals("9.0.6", validations.get(4).getValue());
        assertEquals("320", validations.get(5).getValue());
        assertEquals("480", validations.get(6).getValue());
        assertEquals("320", validations.get(7).getValue());
        assertEquals("480", validations.get(8).getValue());
    }

    private String convertFileToString(String fileName) {
        return new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(fileName)
                )
        ).lines().collect(Collectors.joining("\n"));
    }

}