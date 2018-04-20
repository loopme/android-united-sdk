package com.loopme.request;

import android.content.Context;

import com.loopme.ad.LoopMeAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.stream.Stream;

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
    private static final String IP = "ip";
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
    private static final String IMP = "imp";
    private static final String SECURE = "secure";
    private static final String DISPLAY_MANAGER_VERSION = "displaymanagerver";
    private static final String BANNER = "banner";
    private static final String API = "api";
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
    private static final String TRACKERS = "trackers";
    private static final String INSTL = "instl";
    private static final String SUPPORTED_TECS = "supported_techs";
    private static final String METRIC = "metric";
    private static final String SKIP = "skip";

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
            deviceObj.put(IFA, requestUtils.getIfa());
            deviceObj.put(OSV, requestUtils.getOsv());
            deviceObj.put(CONNECTION_TYPE, requestUtils.getConnectionType());
            deviceObj.put(OS, requestUtils.getOs());
//            deviceObj.put(IP, requestUtils.getIp());
            deviceObj.put(LANGUAGE, requestUtils.getLanguage());
            deviceObj.put(MAKE, requestUtils.getMake());
            deviceObj.put(HWV, requestUtils.getHwv());
            deviceObj.put(UA, requestUtils.getUa());
            deviceObj.put(DNT, requestUtils.getDnt());
            deviceObj.put(MODEL, requestUtils.getModel());

            JSONObject extObj = new JSONObject();
            extObj.put(TIMEZONE, requestUtils.getTimeZone());
            extObj.put(PHONE_NAME, requestUtils.getPhoneName());
            extObj.put(WIFI_NAME, requestUtils.getWifiName());
            extObj.put(ORIENTATION, requestUtils.getOrientation());
            extObj.put(CHARGE_LEVEL, requestUtils.getChargeLevel(context));
            extObj.put(PLUGIN, requestUtils.getPlugin());

            deviceObj.put(EXT, extObj);
            requestObj.put(DEVICE, deviceObj);
            requestObj.put(TMAX, requestUtils.getTmax());

            JSONArray bcatArray = new JSONArray(requestUtils.getBcat());
            requestObj.put(BCAT, bcatArray);

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

            JSONArray trackersArray = new JSONArray().put(requestUtils.getTrackersSupported());
            impObj.put(METRIC, trackersArray);


            impObj.put(EXT, extImpObj);
            impObj.put(BID_FLOOR, requestUtils.getBidFloor());


            impObj.put(DISPLAY_MANAGER, requestUtils.getDisplayManager());
            impObj.put(INSTL, requestUtils.getInstl());

            impArray.put(impObj);
            requestObj.put(IMP, impArray);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return requestObj;
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
