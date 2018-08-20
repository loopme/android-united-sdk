package com.loopme.tracker.partners;

import android.text.TextUtils;

import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.debugging.LiveDebug;
import com.loopme.debugging.Params;
import com.loopme.network.HttpUtils;
import com.loopme.request.RequestUtils;
import com.loopme.utils.StringUtils;
import com.loopme.utils.Utils;
import com.loopme.utils.ExecutorHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoopMeTracker {
    private static final String LOG_TAG = LoopMeTracker.class.getSimpleName();

    private static String sPackageId;
    private static String sAppKey;
    private static Set<String> sVastErrorUrlSet = new HashSet<>();

    private LoopMeTracker() {
    }

    public static void init(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            sAppKey = loopMeAd.getAppKey();
            sPackageId = loopMeAd.getContext().getPackageName();
        }
    }

    public static void initVastErrorUrl(List<String> errorUrlList) {
        sVastErrorUrlSet.addAll(errorUrlList);
    }

    public static void post(String errorMessage) {
        post(errorMessage, Constants.ErrorType.CUSTOM);
    }

    public static void post(String errorMessage, String errorType) {
        Logging.out(LOG_TAG, errorType + " - " + errorMessage);
        String request = buildRequest(errorType, errorMessage);
        proceedBuildEvent(request);
    }

    private static String buildServerIssueUrl() {
        String url = Constants.ERROR_URL;
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        return url;
    }

    private static String buildRequest(String errorType, String errorMessage) {
        Map<String, String> params = new HashMap<>();
        params.putAll(getGeneralInfo());
        params.put(Params.MSG, Constants.SDK_ERROR_MSG);
        params.put(Params.ERROR_TYPE, errorType);
        params.put(Params.ERROR_MSG, errorMessage);
        return obtainRequestString(params);
    }

    private static void sendDataToServer(final String errorUrl, final String request) {
        ExecutorHelper.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                HttpUtils.simpleRequest(errorUrl, request);
            }
        });
    }

    private static synchronized void postVastError(String vastErrorCode) {
        for (String url : sVastErrorUrlSet) {
            String urlWithCode = StringUtils.setErrorCode(url, vastErrorCode);
            sendDataToServer(urlWithCode, null);
            Logging.out(LOG_TAG, urlWithCode);
        }
    }

    public static void clear() {
        sVastErrorUrlSet.clear();
    }

    public static void trackSdkFeedBack(List<String> packageIds, String token) {
        if (Utils.isPackageInstalled(packageIds, Utils.getInstalledPackagesAsStringsList())) {
            sendDataToServer(buildSdkFeedBackUrl(token), null);
        }
    }

    private static String buildSdkFeedBackUrl(String token) {
        String url = Constants.BASE_EVENT_URL + "?";
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(Params.EVENT_TYPE, Params.SDK_FEEDBACK);
        requestParams.put(Params.R, "1");
        requestParams.put(Params.ID, token);
        return url + obtainRequestString(requestParams);
    }

    public static void postDebugEvent(String param, String value) {
        if (LiveDebug.isDebugOn()) {
            Logging.out(LOG_TAG, param + "=" + value);
            String request = buildDebugRequest(param, value);
            proceedBuildEvent(request);
        }
    }

    private static String buildDebugRequest(String param, String value) {
        Map<String, String> params = new HashMap<>();
        params.putAll(getGeneralInfo());
        params.put(Params.ERROR_TYPE, Constants.ErrorType.CUSTOM);
        params.put(param, value);
        return obtainRequestString(params);
    }

    private static Map<String, String> getGeneralInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(Params.DEVICE_OS, Constants.ADNROID_DEVICE_OS);
        params.put(Params.SDK_TYPE, BuildConfig.SDK_TYPE);
        params.put(Params.SDK_VERSION, BuildConfig.VERSION_CODE + "." + BuildConfig.VERSION_NAME);
        params.put(Params.DEVICE_ID, RequestUtils.getIfa());
        params.put(Params.APP_KEY, sAppKey);
        params.put(Params.PACKAGE_ID, sPackageId);
        return params;
    }

    private static void proceedBuildEvent(String request) {
        String url = buildServerIssueUrl();
        sendDataToServer(url, request);
    }

    public static void post(LoopMeError error) {
        if (shouldTrack(error)) {
            post(error.getMessage(), error.getErrorType());
            if (isVastError(error.getErrorType())) {
                postVastError(String.valueOf(error.getErrorCode()));
            }
        }
    }

    private static boolean shouldTrack(LoopMeError error) {
        return error != null && !TextUtils.isEmpty(error.getErrorType()) && !error.getErrorType().equalsIgnoreCase(Constants.ErrorType.DO_NOT_TRACK);
    }

    private static boolean isVastError(String errorType) {
        return TextUtils.equals(errorType, Constants.ErrorType.VAST) ||
                TextUtils.equals(errorType, Constants.ErrorType.VPAID);
    }

    public static String obtainRequestString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        try {
            boolean firstTime = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException | NullPointerException e) {
            Logging.out("HttpUtil", "UnsupportedEncoding: UTF-8");
        }
        return String.valueOf(result);
    }
}
