package com.loopme.network.parser;

import com.loopme.gdpr.GdprResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class GdprResponseParser extends BaseJSONParser {
    private static final String PARAM_NEED_CONSENT = "need_consent";
    private static final String PARAM_CONSENT_URL = "consent_url";
    private static final String PARAM_USER_CONSENT = "user_consent";

    public GdprResponse parse(byte[] body) throws JSONException {
        GdprResponse response = new GdprResponse();
        String responseAsString = new String(body);
        JSONObject jsonObject = new JSONObject(responseAsString);

        if (jsonObject.has(PARAM_NEED_CONSENT))
            response.setNeedConsent(getInt(jsonObject, PARAM_NEED_CONSENT, true));

        if (jsonObject.has(PARAM_USER_CONSENT))
            response.setUserConsent(getInt(jsonObject, PARAM_USER_CONSENT, true));

        if (jsonObject.has(PARAM_CONSENT_URL))
            response.setConsentUrl(getString(jsonObject, PARAM_CONSENT_URL, true));

        return response;
    }
}
