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
import static com.loopme.request.validation.ValidationRule.*;

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

   public ArrayList<Validation> prepare(JSONObject ortbRequest, AdRequestType adRequestType) {
        ArrayList<Validation> validations = new ArrayList<>();

        String appKey = null;
        try {
            appKey = ortbRequest.getJSONObject(APP).getString(APPKEY);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(new String[]{APP, DOT, APPKEY}, appKey, REQUIRED));
        }

        JSONObject sourceExtJson = null;
        String sourceExtOmidpn = null;
        try {
            sourceExtJson = ortbRequest.optJSONObject(SOURCE).getJSONObject(EXT);
            sourceExtOmidpn = sourceExtJson.getString(OMID_PARTNER_NAME);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(new String[]{SOURCE, DOT, EXT, DOT, OMID_PARTNER_NAME}, sourceExtOmidpn, REQUIRED));
        }

        String sourceExtOmidpv = null;
        try {
            sourceExtOmidpv = sourceExtJson.getString(OMID_PARTNER_VERSION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(new String[]{SOURCE, DOT, EXT, DOT, OMID_PARTNER_VERSION}, sourceExtOmidpv, REQUIRED));

        }

        JSONObject eventsExtJson = null;
        String eventsExtOmidpn = null;
        try {
            eventsExtJson = ortbRequest.getJSONObject(EVENTS).getJSONObject(EXT);
            eventsExtOmidpn = eventsExtJson.getString(OMID_PARTNER_NAME);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(new String[]{EVENTS, DOT, EXT, DOT, OMID_PARTNER_NAME}, eventsExtOmidpn, REQUIRED));
        }

        String eventsExtOmidpv = null;
        try {
            eventsExtOmidpv = eventsExtJson.getString(OMID_PARTNER_VERSION);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            validations.add(createValidation(new String[]{EVENTS, DOT, EXT, DOT, OMID_PARTNER_VERSION}, eventsExtOmidpv, REQUIRED));

        }

        JSONArray impression = null;
        JSONObject bannerJson = null;
        int bannerWidth = 0;
        if (adRequestType.isBanner()) {
            try {
                impression = ortbRequest.optJSONArray(IMP);
                bannerJson = impression.getJSONObject(0).getJSONObject(BANNER);
                bannerWidth = bannerJson.getInt(WIDTH);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(new String[]{IMP, ARRAY, BANNER, DOT, WIDTH}, String.valueOf(bannerWidth), REQUIRED, GREATER_THEN_ZERO));
            }

            int bannerHeight = 0;
            try {
                bannerHeight = bannerJson.getInt(HEIGHT);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(new String[]{IMP, ARRAY, BANNER, DOT, HEIGHT}, String.valueOf(bannerHeight), REQUIRED, GREATER_THEN_ZERO));
            }
        }
        if (adRequestType.isVideo() || adRequestType.isRewarded()) {
            JSONObject videoJson = null;
            int videoWidth = 0;
            try {
                videoJson = impression.getJSONObject(0).getJSONObject(VIDEO);
                videoWidth = videoJson.getInt(HEIGHT);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(new String[]{IMP, ARRAY, VIDEO, DOT, WIDTH}, String.valueOf(videoWidth), REQUIRED, GREATER_THEN_ZERO));

            }

            int videoHeight = 0;
            try {
                videoHeight = videoJson.getInt(HEIGHT);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                validations.add(createValidation(new String[]{IMP, ARRAY, VIDEO, DOT, HEIGHT}, String.valueOf(videoHeight), REQUIRED, GREATER_THEN_ZERO));
            }
        }
        return validations;
    }


    private static @NonNull Validation createValidation(String[] path, String value, ValidationRule... rules) {
        String pathS = "";
        for (String a : path) {
            pathS = pathS.concat(a);
        }
        return new Validation(pathS, value, rules);
    }
}
