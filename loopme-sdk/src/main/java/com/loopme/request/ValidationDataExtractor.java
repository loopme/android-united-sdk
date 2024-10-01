package com.loopme.request;

import static com.loopme.request.RequestBuilder.APP;
import static com.loopme.request.RequestBuilder.APPKEY;
import static com.loopme.request.RequestBuilder.BANNER;
import static com.loopme.request.RequestBuilder.EVENTS;
import static com.loopme.request.RequestBuilder.EXT;
import static com.loopme.request.RequestBuilder.HEIGHT;
import static com.loopme.request.RequestBuilder.IMP;
import static com.loopme.request.RequestBuilder.OMID_PARTNER_NAME;
import static com.loopme.request.RequestBuilder.OMID_PARTNER_VERSION;
import static com.loopme.request.RequestBuilder.SOURCE;
import static com.loopme.request.RequestBuilder.VIDEO;
import static com.loopme.request.RequestBuilder.WIDTH;
import static com.loopme.request.validation.ValidationRule.GREATER_THEN_ZERO;
import static com.loopme.request.validation.ValidationRule.REQUIRED;

import android.util.Log;

import androidx.annotation.NonNull;

import com.loopme.loaders.AdRequestType;
import com.loopme.request.validation.Validation;
import com.loopme.request.validation.ValidationRule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ValidationDataExtractor {

    private static final String LOG_TAG = "ValidationDataExtractor";
    private static final String DOT = ".";
    private static final String ARRAY = "[]";
    static final String APP_APPKEY = APP + DOT + APPKEY;
    static final String SOURCE_EXT_PV = SOURCE + DOT + EXT + DOT + OMID_PARTNER_VERSION;
    static final String SOURCE_EXT_PN = SOURCE + DOT + EXT + DOT + OMID_PARTNER_NAME;
    static final String EVENTS_EXT_PV = EVENTS + DOT + EXT + DOT + OMID_PARTNER_VERSION;
    static final String EVENTS_EXT_PN = EVENTS + DOT + EXT + DOT + OMID_PARTNER_NAME;
    private static final String IMP_BANNER = IMP + ARRAY + BANNER;
    static final String BANNER_HEIGHT = IMP_BANNER + DOT + HEIGHT;
    static final String BANNER_WIDTH = IMP_BANNER + DOT + WIDTH;
    static final String IMP_VIDEO = IMP + ARRAY + VIDEO;
    static final String VIDEO_HEIGHT = IMP_VIDEO + DOT + HEIGHT;
    static final String VIDEO_WIDTH = IMP_VIDEO + DOT + WIDTH;

    public ArrayList<Validation> prepare(JSONObject ortbRequest, AdRequestType adRequestType) {
        ArrayList<Validation> validations = new ArrayList<>();

        String appKey = null;
        try {
            appKey = ortbRequest.getJSONObject(APP).getString(APPKEY);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(APP_APPKEY, appKey, REQUIRED));
        }

        JSONObject sourceExtJson = null;
        String sourceExtOmidpn = null;
        try {
            sourceExtJson = ortbRequest.optJSONObject(SOURCE).getJSONObject(EXT);
            sourceExtOmidpn = sourceExtJson.getString(OMID_PARTNER_NAME);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(SOURCE_EXT_PN, sourceExtOmidpn, REQUIRED));
        }

        String sourceExtOmidpv = null;
        try {
            sourceExtOmidpv = sourceExtJson.getString(OMID_PARTNER_VERSION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(SOURCE_EXT_PV, sourceExtOmidpv, REQUIRED));

        }

        JSONObject eventsExtJson = null;
        String eventsExtOmidpn = null;
        try {
            eventsExtJson = ortbRequest.getJSONObject(EVENTS).getJSONObject(EXT);
            eventsExtOmidpn = eventsExtJson.getString(OMID_PARTNER_NAME);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(EVENTS_EXT_PN, eventsExtOmidpn, REQUIRED));
        }

        String eventsExtOmidpv = null;
        try {
            eventsExtOmidpv = eventsExtJson.getString(OMID_PARTNER_VERSION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(EVENTS_EXT_PV, eventsExtOmidpv, REQUIRED));

        }

        JSONArray impression = null;
        JSONObject bannerJson = null;
        String bannerWidth = null;
        if (adRequestType.isBanner()) {
            try {
                impression = ortbRequest.optJSONArray(IMP);
                bannerJson = impression.getJSONObject(0).getJSONObject(BANNER);
                bannerWidth = String.valueOf(bannerJson.getInt(WIDTH));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(BANNER_WIDTH, bannerWidth, REQUIRED, GREATER_THEN_ZERO));
            }

            String bannerHeight = null;
            try {
                bannerHeight = String.valueOf(bannerJson.getInt(HEIGHT));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(BANNER_HEIGHT, bannerHeight, REQUIRED, GREATER_THEN_ZERO));
            }
        }
        if (adRequestType.isVideo() || adRequestType.isRewarded()) {
            JSONObject videoJson = null;
            String videoWidth = null;
            try {
                videoJson = impression.getJSONObject(0).getJSONObject(VIDEO);
                videoWidth = String.valueOf(videoJson.getInt(WIDTH));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(VIDEO_WIDTH, videoWidth, REQUIRED, GREATER_THEN_ZERO));

            }

            String videoHeight = null;
            try {
                videoHeight = String.valueOf(videoJson.getInt(HEIGHT));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(VIDEO_HEIGHT, videoHeight, REQUIRED, GREATER_THEN_ZERO));
            }
        }
        return validations;
    }

    private static @NonNull Validation createValidation(String path, String value, ValidationRule... rules) {
        return new Validation(path, value, rules);
    }

}
