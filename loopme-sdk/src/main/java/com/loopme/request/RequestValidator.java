package com.loopme.request;

import static com.loopme.request.RequestBuilder.BANNER;
import static com.loopme.request.RequestBuilder.EVENTS;
import static com.loopme.request.RequestBuilder.EXT;
import static com.loopme.request.RequestBuilder.HEIGHT;
import static com.loopme.request.RequestBuilder.IMP;
import static com.loopme.request.RequestBuilder.OMID_PARTNER_NAME;
import static com.loopme.request.RequestBuilder.OMID_PARTNER_VERSION;
import static com.loopme.request.RequestBuilder.SOURCE;
import static com.loopme.request.RequestBuilder.WIDTH;
import static com.loopme.request.RequestBuilder.VIDEO;

import android.util.Log;

import com.loopme.request.validation.Validation;
import com.loopme.request.validation.ValidationRule;

import org.json.JSONObject;

import java.util.ArrayList;

public class RequestValidator {

    private static final String LOG_TAG = "RequestValidator";
    private static final String NOT_PRESENT = "not present or null";
    private static final String NOT_PRESENT_OR_INVALID = "not present or invalid";
    private static final String BLANK = "is blank";
    private static final String FALLBACK = "fallback";
    private static final String VALUE_0 = "is 0";
    private static final String SOURCE_EXT_PV = SOURCE + "." + EXT + "." + OMID_PARTNER_VERSION;
    private static final String SOURCE_EXT_PN = SOURCE + "." + EXT + "." + OMID_PARTNER_NAME;
    private static final String EVENTS_EXT_PV = EVENTS + "." + EXT + "." + OMID_PARTNER_VERSION;
    private static final String EVENTS_EXT_PN = EVENTS + "." + EXT + "." + OMID_PARTNER_NAME;
    private static final String BANNER_HEIGHT = IMP + "." + BANNER + "." + HEIGHT;
    private static final String BANNER_WIDTH = IMP + "." + BANNER + "." + WIDTH;
    private static final String VIDEO_HEIGHT = IMP + "." + VIDEO + "." + HEIGHT;
    private static final String VIDEO_WIDTH = IMP + "." + VIDEO + "." + WIDTH;
    private static final String IMP_VIDEO = IMP + "." + VIDEO;
    private static final String IMP_BANNER = IMP + "." + BANNER;


//    private boolean isValid = true;

    /**
     * After calling validateOrtbRequest function, this field should be populated. If empty hashmap, it means, that request is valid
     */
//    private final Map<String, String> violations = new HashMap<>();
    public boolean validate(ArrayList<Validation> rules) {
//        ArrayList output = new ArrayList<ValidationOutput>();
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

    //app.id
    //source.ext.omidpv {source, ext, omidpv}
    //imp[0].banner.w  {imp, 0, banner, w}
// how to make it?

    private JSONObject extractByPath(String path) {
        return null;
    }

    //how to make it better?
//    public Map<String, String> validateOrtbRequest(JSONObject ortbRequest, AdRequestType adRequestType) {
//        Map<String, String> violations = new HashMap<>();
//        ArrayList<ValidationOutput> validationOutput = new ArrayList<>();
////        boolean isValid = true;
//
//        //app.id
//        String appKey = ortbRequest.optJSONObject(APP) != null ? ortbRequest.optJSONObject(APP).optString(APPKEY, FALLBACK) : null;
//        if (appKey == null || appKey.equals(FALLBACK)) {
//            violate(APPKEY, NOT_PRESENT);
//        } else if (appKey.isBlank()) {
//            violate(APPKEY, BLANK);
//        }
//
//        //source.ext.omidpv
//        JSONObject sourceExtJson = null;
//        try {
//            sourceExtJson = ortbRequest.optJSONObject(SOURCE).optJSONObject(EXT);
//            String sourceExtOmidpn = sourceExtJson.getString(OMID_PARTNER_NAME);
//            if (sourceExtOmidpn.isBlank()) {
//                violations.put(SOURCE_EXT_PN, BLANK);
//            }
//        } catch (Exception e) {
//            violations.put(SOURCE_EXT_PN, NOT_PRESENT);
//        }
//
//        try {
//            String sourceExtOmidpv = sourceExtJson.optString(OMID_PARTNER_VERSION, FALLBACK);
//            if (sourceExtOmidpv == null || sourceExtOmidpv.equals(FALLBACK)) {
//                violate(SOURCE_EXT_PV, NOT_PRESENT);
//            } else if (sourceExtOmidpv.isBlank()) {
//                violate(SOURCE_EXT_PV, BLANK);
//            }
//        } catch (Exception e) {
//            violations.put(SOURCE_EXT_PV, NOT_PRESENT);
//
//        }
//
//        //events.ext.omidpv
//        JSONObject eventsExtJson;
//        try {
//            eventsExtJson = ortbRequest.getJSONObject(EVENTS).getJSONObject(EXT);
//            String eventsExtOmidpn = eventsExtJson.getString(OMID_PARTNER_NAME);
//            if (eventsExtOmidpn.isBlank()) {
//                validationOutput.add(new ValidationOutput(false, new Validation(EVENTS_EXT_PN, ValidationRule.NOT_BLANK)));
//            }
//        } catch (Exception e) {
//            validationOutput.add(new ValidationOutput(false, new Validation(EVENTS_EXT_PN, ValidationRule.NOT_BLANK)));
//
//        }
//
//
//        String eventsExtOmidpv = eventsExtJson != null ? eventsExtJson.optString(OMID_PARTNER_VERSION, FALLBACK) : null;
//        if (eventsExtOmidpv == null || eventsExtOmidpv.equals(FALLBACK)) {
//            violate(EVENTS_EXT_PV, NOT_PRESENT);
//        } else if (eventsExtOmidpv.isBlank()) {
//            violate(EVENTS_EXT_PV, BLANK);
//        }
//
//
//        //imp[0].banner.w
//        JSONArray impression = ortbRequest.optJSONArray(IMP);
//
//        if (adRequestType.isBanner()) {
//            JSONObject bannerJson = impression != null ? ortbRequest.optJSONArray(IMP).optJSONObject(0).optJSONObject(BANNER) : null;
//            if (bannerJson == null) {
//                violate(IMP_BANNER, NOT_PRESENT);
//            } else {
//                int bannerWidth = bannerJson.optInt(WIDTH, -1);
//                if (bannerWidth == -1) {
//                    violate(BANNER_WIDTH, NOT_PRESENT_OR_INVALID);
//                } else if (bannerWidth == 0) {
//                    violate(BANNER_WIDTH, VALUE_0);
//                }
//
//                int bannerHeight = bannerJson.optInt(HEIGHT, -1);
//                if (bannerHeight == -1) {
//                    violate(BANNER_HEIGHT, NOT_PRESENT_OR_INVALID);
//                } else if (bannerHeight == 0) {
//                    violate(BANNER_HEIGHT, VALUE_0);
//                }
//            }
//        }
//
//        if (adRequestType.isVideo() || adRequestType.isRewarded()) {
//            JSONObject videoJson = ortbRequest.optJSONArray(IMP) != null ? ortbRequest.optJSONArray(IMP).optJSONObject(0).optJSONObject(VIDEO) : null;
//            if (videoJson == null) {
//                violate(IMP_VIDEO, NOT_PRESENT);
//            } else {
//                int videoWidth = videoJson.optInt(WIDTH, -1);
//                if (videoWidth == -1) {
//                    violate(VIDEO_WIDTH, NOT_PRESENT_OR_INVALID);
//                } else if (videoWidth == 0) {
//                    violate(VIDEO_WIDTH, VALUE_0);
//                }
//
//                int videoHeight = videoJson.optInt(HEIGHT, -1);
//                if (videoHeight == -1) {
//                    violate(VIDEO_HEIGHT, NOT_PRESENT_OR_INVALID);
//                } else if (videoHeight == 0) {
//                    violate(VIDEO_HEIGHT, VALUE_0);
//                }
//            }
//        }
//
//        return violations;
//    }

    private boolean violate(String fieldName, String issue) {
//        isValid = false;
//        violations.put(fieldName, issue);
        return false;
    }


}
