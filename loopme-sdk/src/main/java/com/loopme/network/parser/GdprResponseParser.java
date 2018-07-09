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

        int needConsent = getInt(jsonObject, PARAM_NEED_CONSENT, true);
        if (needConsent == 0) {
            int userConsent = getInt(jsonObject, PARAM_USER_CONSENT, true);
            response.setUserConsent(userConsent);
        } else {
            String consentUrl = getString(jsonObject, PARAM_CONSENT_URL, true);
            response.setConsentUrl(consentUrl);
        }
        response.setNeedConsent(needConsent);
        return response;
    }
}
