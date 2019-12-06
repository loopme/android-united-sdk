package com.loopme.request;

import android.content.Context;
import android.location.Location;

import com.loopme.AdTargetingData;
import com.loopme.ad.LoopMeAd;
import com.loopme.debugging.LiveDebug;
import com.loopme.om.OmidHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by katerina on 6/11/17.
 */

public class RequestBuilder implements Serializable {

    private static final String APP = "app";
    private static final String ID = "id";
    private static final String BUNDLE = "bundle";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String APPKEY = "id";
    private static final String DEVICE = "device";
    private static final String DEVICE_TYPE = "devicetype";
    private static final String WIDTH = "w";
    private static final String HEIGHT = "h";
    private static final String JS = "js";
    private static final String IFA = "ifa";
    private static final String OSV = "osv";
    private static final String CONNECTION_TYPE = "connectiontype";
    private static final String OS = "os";
    private static final String LANGUAGE = "language";
    private static final String MAKE = "make";
    private static final String HWV = "hwv";
    private static final String UA = "ua";
    private static final String DNT = "dnt";
    private static final String MODEL = "model";
    private static final String EXT = "ext";
    private static final String TIMEZONE = "timezone";
    private static final String PHONE_NAME = "phonename";
    private static final String WIFI_NAME = "wifiname";
    private static final String ORIENTATION = "orientation";
    private static final String CHARGE_LEVEL = "chargelevel";
    private static final String PLUGIN = "plugin";
    private static final String TMAX = "tmax";
    private static final String BCAT = "bcat";
    private static final String SOURCE = "source";
    private static final String EVENTS = "events";
    private static final String OMID_PARTNER_NAME = "omidpn";
    private static final String OMID_PARTNER_VERSION = "omidpv";
    private static final String IMP = "imp";
    private static final String SECURE = "secure";
    private static final String DISPLAY_MANAGER_VERSION = "displaymanagerver";
    private static final String BANNER = "banner";
    private static final String API = "api";
    private static final String APIS = "apis";
    private static final String BATTR = "battr";
    private static final String IT = "it";
    private static final String BID_FLOOR = "bidfloor";
    private static final String VIDEO = "video";
    private static final String MAX_DURATION = "maxduration";
    private static final String PROTOCOLS = "protocols";
    private static final String LINEARITY = "linearity";
    private static final String BOXING_ALLOWED = "boxingallowed";
    private static final String MIME_TYPE = "mimes";
    private static final String START_DELAY = "startdelay";
    private static final String DELIVERY = "delivery";
    private static final String SEQUENCE = "sequence";
    private static final String MIN_DURATION = "minduration";
    private static final String MAX_BITRATE = "maxbitrate";
    private static final String DISPLAY_MANAGER = "displaymanager";
    private static final String INSTL = "instl";
    private static final String SUPPORTED_TECS = "supported_techs";
    private static final String METRIC = "metric";
    private static final String SKIP = "skip";
    private static final String PARAM_CONSENT_TYPE = "consent_type";
    private static final String EXP_DIR = "expdir";
    private static final String USER = "user";
    private static final String REGS = "regs";
    private static final String COPPA = "coppa";
    private static final String GDPR = "gdpr";
    private static final String US_PRIVACY = "us_privacy";
    private static final String GENDER = "gender";
    private static final String YOB = "yob";
    private static final String KEYWORDS = "keywords";
    private static final String CONSENT = "consent";
    private static final String OUTPUT = "audio_output";
    private static final String MUSIC = "music";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String GEO_TYPE = "type";
    private static final String GEO = "geo";

    public static JSONObject buildRequestJson(Context context, LoopMeAd loopMeAd) {
        RequestUtils requestUtils = new RequestUtils(context, loopMeAd);
        JSONObject requestObj = new JSONObject();
        try {
            JSONObject appObj = new JSONObject();
            appObj.put(BUNDLE, requestUtils.getAppBundle());
            appObj.put(NAME, requestUtils.getAppName());
            appObj.put(VERSION, requestUtils.getAppVersion());
            appObj.put(APPKEY, loopMeAd.getAppKey());

            requestObj.put(APP, appObj);
            requestObj.put(ID, requestUtils.getUuId());

            JSONObject deviceObj = new JSONObject();
            deviceObj.put(DEVICE_TYPE, requestUtils.getDeviceType());
            deviceObj.put(WIDTH, requestUtils.getDeviceWidthPx());
            deviceObj.put(HEIGHT, requestUtils.getDeviceHeightPx());
            deviceObj.put(JS, requestUtils.getJs());
            deviceObj.put(IFA, RequestUtils.getIfa());
            deviceObj.put(OSV, requestUtils.getOsv());
            deviceObj.put(CONNECTION_TYPE, requestUtils.getConnectionType());
            deviceObj.put(OS, requestUtils.getOs());

            deviceObj.put(LANGUAGE, requestUtils.getLanguage());
            deviceObj.put(MAKE, requestUtils.getMake());
            deviceObj.put(HWV, requestUtils.getHwv());
            deviceObj.put(UA, requestUtils.getUa());
            deviceObj.put(DNT, requestUtils.getDnt());
            deviceObj.put(MODEL, requestUtils.getModel());

            tryAddGeo(deviceObj, requestUtils);

            JSONObject extObj = new JSONObject();
            extObj.put(TIMEZONE, requestUtils.getTimeZone());
            extObj.put(PHONE_NAME, requestUtils.getPhoneName());
            extObj.put(WIFI_NAME, requestUtils.getWifiName());
            extObj.put(ORIENTATION, requestUtils.getOrientation());
            extObj.put(CHARGE_LEVEL, requestUtils.getChargeLevel(context));
            extObj.put(PLUGIN, requestUtils.getPlugin());
            addDebugInfo(extObj, requestUtils, context);
            deviceObj.put(EXT, extObj);
            requestObj.put(DEVICE, deviceObj);
            requestObj.put(TMAX, requestUtils.getTmax());

            JSONArray bcatArray = new JSONArray(requestUtils.getBcat());
            requestObj.put(BCAT, bcatArray);

            JSONObject sourceObj = new JSONObject();
            requestObj.put(SOURCE, sourceObj);

            JSONObject sourceExtObj = new JSONObject();
            sourceObj.put(EXT, sourceExtObj);

            sourceExtObj.put(OMID_PARTNER_NAME, OmidHelper.getPartnerName());
            sourceExtObj.put(OMID_PARTNER_VERSION, OmidHelper.getPartnerVersion());

            JSONObject eventsObject = new JSONObject();
            requestObj.put(EVENTS, eventsObject);

            eventsObject.put(APIS, new JSONArray(new int[]{RequestConstants.FRAMEWORK_OMID_1}));

            JSONObject eventsExtObj = new JSONObject();
            eventsObject.put(EXT, eventsExtObj);

            eventsExtObj.put(OMID_PARTNER_NAME, OmidHelper.getPartnerName());
            eventsExtObj.put(OMID_PARTNER_VERSION, OmidHelper.getPartnerVersion());

            JSONArray impArray = new JSONArray();
            JSONObject impObj = new JSONObject();

            impObj.put(SECURE, requestUtils.getSecure());
            impObj.put(DISPLAY_MANAGER_VERSION, requestUtils.getDisplayManagerVersion());
            impObj.put(ID, requestUtils.getImpId());

            JSONObject bannerObj = createBannerObject(requestUtils);
            JSONObject videoObj = createVideoObject(requestUtils);

            switch (loopMeAd.getPreferredAdType()) {
                case ALL: {
                    impObj.put(BANNER, bannerObj);
                    impObj.put(VIDEO, videoObj);
                    break;
                }
                case HTML: {
                    impObj.put(BANNER, bannerObj);
                    break;
                }
                case VIDEO: {
                    impObj.put(VIDEO, videoObj);
                    break;
                }
            }

            JSONObject extImpObj = new JSONObject();
            extImpObj.put(IT, requestUtils.getIt());
            JSONArray supportedTechsArray = new JSONArray(requestUtils.getSupportedTechs());
            extImpObj.put(SUPPORTED_TECS, supportedTechsArray);

            JSONArray trackersArray = requestUtils.getTrackersSupported();
            impObj.put(METRIC, trackersArray);

            impObj.put(EXT, extImpObj);
            impObj.put(BID_FLOOR, requestUtils.getBidFloor());

            impObj.put(DISPLAY_MANAGER, requestUtils.getDisplayManager());
            impObj.put(INSTL, requestUtils.getInstl());

            impArray.put(impObj);
            requestObj.put(IMP, impArray);
            requestObj.put(USER, createUserObj(requestUtils, loopMeAd));
            requestObj.put(REGS, createRegsObj(requestUtils, context));
        } catch (JSONException | ClassCastException ex) {
            ex.printStackTrace();
        }
        return requestObj;
    }

    private static void tryAddGeo(JSONObject deviceObj, RequestUtils requestUtils)
            throws JSONException {

        if (deviceObj == null || requestUtils == null)
            return;

        Location location = requestUtils.getLocation();
        if (location == null)
            return;

        JSONObject geoObj = new JSONObject();
        geoObj.put(LAT, (float) location.getLatitude());
        geoObj.put(LON, (float) location.getLongitude());
        geoObj.put(GEO_TYPE, 1 /*gps/location services*/);

        deviceObj.put(GEO, geoObj);
    }

    private static void addDebugInfo(JSONObject extObj, RequestUtils requestUtils, Context context) throws JSONException {
        if (LiveDebug.isDebugOn() && requestUtils != null && context != null) {
            if (requestUtils.isAnyAudioOutput(context)) {
                extObj.put(OUTPUT, requestUtils.getAudioOutputJson(context));
            }
            extObj.put(MUSIC, requestUtils.getMusic(context));
        }
    }

    private static JSONObject createRegsObj(RequestUtils requestUtils, Context context) throws JSONException {
        JSONObject regs = new JSONObject();

        regs.put(COPPA, requestUtils.getCoppa());

        JSONObject ext = new JSONObject();
        regs.put(EXT, ext);

        ext.put(US_PRIVACY, requestUtils.getUSPrivacyString(context));

        if (requestUtils.isSubjectToGdprPresent(context))
            ext.put(GDPR, requestUtils.getIabConsentSubjectToGdpr(context));

        return regs;
    }

    private static JSONObject createUserObj(RequestUtils requestUtils, LoopMeAd loopMeAd) throws JSONException {
        JSONObject userObj = new JSONObject();

        AdTargetingData data = loopMeAd.getAdTargetingData();
        String gender = data.getGender();
        String keywords = data.getKeywords();
        int yob = data.getYob();

        if (gender != null) {
            userObj.put(GENDER, gender);
        }
        if (yob != 0) {
            userObj.put(YOB, yob);
        }
        if (keywords != null) {
            userObj.put(KEYWORDS, keywords);
        }

        JSONObject ext = new JSONObject();
        if (requestUtils.isIabConsentCmpPresent(loopMeAd.getContext())) {
            ext.put(CONSENT, requestUtils.getIabConsentString(loopMeAd.getContext()));
        } else {
            ext.put(PARAM_CONSENT_TYPE, requestUtils.getConsentType(loopMeAd.getContext()));
            ext.put(CONSENT, requestUtils.getUserConsent(loopMeAd.getContext()));
        }
        userObj.put(EXT, ext);
        return userObj;
    }

    private static JSONObject createBannerObject(RequestUtils requestUtils) throws JSONException {
        JSONObject bannerObj = new JSONObject();

        JSONArray apiArray = new JSONArray(requestUtils.getApi());
        bannerObj.put(API, apiArray);

        bannerObj.put(WIDTH, requestUtils.getWidth());
        bannerObj.put(HEIGHT, requestUtils.getHeight());
        bannerObj.put(ID, 1);

        JSONArray battrArray = new JSONArray(requestUtils.getBattery());
        bannerObj.put(BATTR, battrArray);

        JSONArray expDirArray = new JSONArray(requestUtils.getExpDir());
        bannerObj.put(EXP_DIR, expDirArray);
        return bannerObj;
    }

    private static JSONObject createVideoObject(RequestUtils requestUtils) throws JSONException {
        JSONObject videoObj = new JSONObject();

        JSONArray apiArray = new JSONArray(requestUtils.getApi());
        videoObj.put(API, apiArray);

        videoObj.put(SKIP, requestUtils.getSkippable());

        JSONArray protocolsArray = new JSONArray(requestUtils.getProtocols());
        videoObj.put(PROTOCOLS, protocolsArray);

        videoObj.put(MAX_DURATION, requestUtils.getMaxDuration());

        JSONArray battrArray = new JSONArray(requestUtils.getBattery());
        videoObj.put(BATTR, battrArray);
        videoObj.put(WIDTH, requestUtils.getWidth());
        videoObj.put(HEIGHT, requestUtils.getHeight());
        videoObj.put(LINEARITY, requestUtils.getLinearity());
        videoObj.put(BOXING_ALLOWED, requestUtils.getBoxingAllowed());

        JSONArray mimeArray = new JSONArray(requestUtils.getMimeTypes());
        videoObj.put(MIME_TYPE, mimeArray);
        videoObj.put(START_DELAY, requestUtils.getStartDelay());

        JSONArray deliveryArray = new JSONArray(requestUtils.getDelivery());
        videoObj.put(DELIVERY, deliveryArray);
        videoObj.put(SEQUENCE, requestUtils.getSequence());
        videoObj.put(MIN_DURATION, requestUtils.getMinDuration());
        videoObj.put(MAX_BITRATE, requestUtils.getMaxBitrate());
        return videoObj;
    }
}
