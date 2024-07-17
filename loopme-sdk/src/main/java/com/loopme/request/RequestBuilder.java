package com.loopme.request;

import android.content.Context;
import android.location.Location;

import com.loopme.AdTargetingData;
import com.loopme.JSONBuilder;
import com.loopme.LoopMeInterstitialGeneral;
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
    private static final String METRIC = "metric";
    private static final String SKIP = "skip";
    private static final String SKIP_MIN = "skipmin";
    private static final String SKIP_AFTER = "skipafter";
    private static final String PLACEMENT_TYPE = "placementType";
    private static final String VIDEO_TYPE = "videotype";
    private static final String RWDD = "rwdd";
    private static final String REWARDED = "rewarded";
    private static final String PARAM_CONSENT_TYPE = "consent_type";
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

    public static JSONObject buildRequestJson(Context context, LoopMeAd loopMeAd) throws JSONException {
        RequestUtils requestUtils = new RequestUtils(context, loopMeAd);
        Location location = requestUtils.getLocation();
        AdTargetingData data = loopMeAd.getAdTargetingData();
        LoopMeAd.Type adType = loopMeAd.getPreferredAdType();
        boolean isBanner = LoopMeAd.Type.ALL == adType || LoopMeAd.Type.HTML == adType;
        boolean isFullscreenSize = requestUtils.isFullscreenSize();
        boolean isVideo = isFullscreenSize && (LoopMeAd.Type.ALL == adType || LoopMeAd.Type.VIDEO == adType);
        boolean isRewarded = loopMeAd instanceof LoopMeInterstitialGeneral && ((LoopMeInterstitialGeneral) loopMeAd).isRewarded();

        JSONObject video = null;
        return new JSONBuilder()
            .put(TMAX, RequestConstants.MAX_TIME_TO_SUBMIT_BID)
            .put(BCAT, new JSONArray(RequestConstants.BCAT))
            .put(ID, requestUtils.getUuId())
            .put(APP, new JSONBuilder()
                .put(BUNDLE, requestUtils.getAppBundle())
                .put(NAME, requestUtils.getAppName())
                .put(VERSION, requestUtils.getAppVersion())
                .put(APPKEY, loopMeAd.getAppKey())
                .build()
            )
            .put(SOURCE, new JSONBuilder()
                .put(EXT, new JSONBuilder()
                    .put(OMID_PARTNER_NAME, OmidHelper.getPartnerName())
                    .put(OMID_PARTNER_VERSION, OmidHelper.getPartnerVersion())
                    .build())
                .build()
            )
            .put(EVENTS, new JSONBuilder()
                .put(APIS, new JSONArray(new int[]{RequestConstants.FRAMEWORK_OMID_1}))
                .put(EXT, new JSONBuilder()
                    .put(OMID_PARTNER_NAME, OmidHelper.getPartnerName())
                    .put(OMID_PARTNER_VERSION, OmidHelper.getPartnerVersion())
                    .build())
                .build()
            )
            .put(REGS, new JSONBuilder()
                .put(COPPA, requestUtils.getCoppa(context))
                .put(EXT, new JSONBuilder()
                    .put(US_PRIVACY, requestUtils.getUSPrivacyString(context))
                    .put(GDPR, requestUtils.isIabTcfGdprAppliesPresent(context) ?
                        requestUtils.getIabTcfGdprApplies(context) : null
                    )
                    .build())
                .build()
            )
            .put(USER, new JSONBuilder()
                .put(GENDER, data.getGender())
                .put(KEYWORDS, data.getKeywords())
                .put(YOB, data.getYob() != 0 ? data.getYob() : null)
                .put(CONSENT, requestUtils.isIabTcfCmpSdkPresent(loopMeAd.getContext()) ?
                    requestUtils.getIabTcfTcString(loopMeAd.getContext()) : null
                )
                .put(EXT, !requestUtils.isIabTcfCmpSdkPresent(loopMeAd.getContext()) ?
                    new JSONBuilder()
                        .put(PARAM_CONSENT_TYPE, requestUtils.getConsentType(loopMeAd.getContext()))
                        .put(CONSENT, requestUtils.getUserConsent(loopMeAd.getContext()))
                        .build() : null
                )
                .build()
            )
            .put(DEVICE, new JSONBuilder()
                .put(JS, RequestConstants.JS_SUPPORTED)
                .put(OS, RequestConstants.ANDROID_OS)
                .put(DEVICE_TYPE, requestUtils.getDeviceType())
                .put(WIDTH, requestUtils.getDeviceWidthPx())
                .put(HEIGHT, requestUtils.getDeviceHeightPx())
                .put(IFA, RequestUtils.getIfa())
                .put(OSV, requestUtils.getOsv())
                .put(CONNECTION_TYPE, requestUtils.getConnectionType())
                .put(LANGUAGE, requestUtils.getLanguage())
                .put(MAKE, requestUtils.getMake())
                .put(HWV, requestUtils.getHwv())
                .put(UA, requestUtils.getUa())
                .put(DNT, requestUtils.getDnt())
                .put(MODEL, requestUtils.getModel())
                .put(GEO, (location != null) ? new JSONBuilder()
                    .put(LAT, (float) location.getLatitude())
                    .put(LON, (float) location.getLongitude())
                    .put(GEO_TYPE, 1 /*gps/location services*/)
                    .build() : null
                )
                .put(EXT, new JSONBuilder()
                    .put(TIMEZONE, requestUtils.getTimeZone())
                    .put(ORIENTATION, requestUtils.getOrientation())
                    .put(CHARGE_LEVEL, requestUtils.getChargeLevel(context))
                    .put(PLUGIN, RequestConstants.PLUGIN)
                    .put(OUTPUT, LiveDebug.isDebugOn() && requestUtils.isAnyAudioOutput(context) ?
                        requestUtils.getAudioOutputJson(context) : null
                    )
                    .put(MUSIC, LiveDebug.isDebugOn() ? requestUtils.getMusic(context) : null)
                    .build()
                )
                .build()
            )
            .put(IMP, new JSONArray(new JSONObject[]{ new JSONBuilder()
                .put(BANNER, isBanner ? new JSONBuilder()
                    .put(ID, 1)
                    .put(BATTR, new JSONArray(RequestConstants.BATTERY_INFO))
                    .put(WIDTH, requestUtils.getWidth())
                    .put(HEIGHT, requestUtils.getHeight())
                    .put(API, new JSONArray(requestUtils.getApi()))
                    .build() : null
                )
                .put(VIDEO, isVideo ? getVideo(isRewarded, requestUtils) : null)
                .put(SECURE, RequestConstants.SECURE_IMPRESSION)
                .put(BID_FLOOR, RequestConstants.BID_FLOOR_DEFAULT_VALUE)
                .put(DISPLAY_MANAGER, RequestConstants.LOOPME_SDK)
                .put(DISPLAY_MANAGER_VERSION, requestUtils.getDisplayManagerVersion())
                .put(ID, requestUtils.getImpId())
                .put(METRIC, requestUtils.getTrackersSupported())
                .put(INSTL, requestUtils.getInstl())
                .put(EXT, new JSONBuilder()
                    .put(IT, requestUtils.getIt())
                    .build()
                )
                .build()
            }))
            .put(EXT, isRewarded ? new JSONBuilder()
                .put(PLACEMENT_TYPE, REWARDED)
                .build() : null
            )
            .build();
    }

    private static JSONObject getVideo(boolean isRewarded, RequestUtils requestUtils) throws JSONException {
        return !isRewarded ?
            new JSONBuilder()
                .put(MAX_DURATION, RequestConstants.DEFAULT_MAX_DURATION)
                .put(LINEARITY, RequestConstants.LINEAR_IN_STREAM)
                .put(BOXING_ALLOWED, RequestConstants.BOXING_DEFAULT)
                .put(START_DELAY, RequestConstants.START_DELAY_DEFAULT_VALUE)
                .put(SEQUENCE, RequestConstants.SEQUENCE_DEFAULT_VALUE)
                // TODO: Why min duration is set to MIN_BITRATE_DEFAULT_VALUE?
                .put(MIN_DURATION, RequestConstants.MIN_BITRATE_DEFAULT_VALUE)
                .put(MAX_BITRATE, RequestConstants.MAX_BITRATE_DEFAULT_VALUE)
                .put(PROTOCOLS, new JSONArray(RequestConstants.PROTOCOLS))
                .put(BATTR, new JSONArray(RequestConstants.BATTERY_INFO))
                .put(MIME_TYPE, new JSONArray(RequestConstants.MIME_TYPES))
                .put(DELIVERY, new JSONArray(RequestConstants.DELIVERY_METHODS))
                .put(WIDTH, requestUtils.getWidth())
                .put(HEIGHT, requestUtils.getHeight())
                .put(API, new JSONArray(requestUtils.getApi()))
                .put(SKIP, 1)
                .put(SKIP_AFTER, 5)
                .put(EXT, new JSONBuilder()
                    .put(REWARDED, 0)
                    .build()
                )
                .build() :
            new JSONBuilder()
                .put(MAX_DURATION, RequestConstants.DEFAULT_MAX_DURATION)
                .put(LINEARITY, RequestConstants.LINEAR_IN_STREAM)
                .put(BOXING_ALLOWED, RequestConstants.BOXING_DEFAULT)
                .put(START_DELAY, RequestConstants.START_DELAY_DEFAULT_VALUE)
                .put(SEQUENCE, RequestConstants.SEQUENCE_DEFAULT_VALUE)
                // TODO: Why min duration is set to MIN_BITRATE_DEFAULT_VALUE?
                .put(MIN_DURATION, RequestConstants.MIN_BITRATE_DEFAULT_VALUE)
                .put(MAX_BITRATE, RequestConstants.MAX_BITRATE_DEFAULT_VALUE)
                .put(PROTOCOLS, new JSONArray(RequestConstants.PROTOCOLS))
                .put(BATTR, new JSONArray(RequestConstants.BATTERY_INFO))
                .put(MIME_TYPE, new JSONArray(RequestConstants.MIME_TYPES))
                .put(DELIVERY, new JSONArray(RequestConstants.DELIVERY_METHODS))
                .put(WIDTH, requestUtils.getWidth())
                .put(HEIGHT, requestUtils.getHeight())
                .put(API, new JSONArray(requestUtils.getApi()))
                .put(RWDD, 1)
                .put(SKIP, 0)
                .put(SKIP_MIN, 0)
                .put(SKIP_AFTER, 0)
                .put(EXT, new JSONBuilder()
                    .put(REWARDED, 1)
                    .put(VIDEO_TYPE, REWARDED)
                    .build()
                )
                .build();
    }
}
