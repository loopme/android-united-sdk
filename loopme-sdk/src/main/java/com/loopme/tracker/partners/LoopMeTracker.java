package com.loopme.tracker.partners;

import static com.loopme.debugging.Params.CID;
import static com.loopme.debugging.Params.CRID;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.REQUEST_ID;

import android.os.Build;
import android.text.TextUtils;

import com.loopme.BidManager;
import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.debugging.LiveDebug;
import com.loopme.debugging.Params;
import com.loopme.network.HttpUtils;
import com.loopme.request.RequestUtils;
import com.loopme.utils.ExecutorHelper;
import com.loopme.utils.StringUtils;
import com.loopme.utils.Utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoopMeTracker {
    private static final String LOG_TAG = LoopMeTracker.class.getSimpleName();

    private static String sPackageId;
    private static String sAppKey;
    private static final Set<String> sVastErrorUrlSet = new HashSet<>();

    private LoopMeTracker() { }

    public static void init(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            return;
        }
        sAppKey = loopMeAd.getAppKey();
        sPackageId = loopMeAd.getContext().getPackageName();
    }

    public static void initVastErrorUrl(List<String> errorUrlList) {
        sVastErrorUrlSet.addAll(errorUrlList);
    }

    public static void post(HashMap<String, String> errorInfo) {
        Logging.out(LOG_TAG, errorInfo.toString());
        Map<String, String> params = new HashMap<>(getGeneralInfo());
        params.putAll(errorInfo);
        if (!params.containsKey(ERROR_TYPE)) {
            params.put(ERROR_TYPE, Constants.ErrorType.CUSTOM);
        }
        proceedBuildEvent(obtainRequestString(params));
    }

    private static void sendDataToServer(final String errorUrl, final String request) {
        ExecutorHelper.getExecutor().submit(() -> HttpUtils.simpleRequest(errorUrl, request));
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
        if (Utils.isPackageInstalled(packageIds)) {
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
        if (!LiveDebug.isDebugOn()) {
            return;
        }
        Logging.out(LOG_TAG, param + "=" + value);
        proceedBuildEvent(buildDebugRequest(param, value));
    }

    private static String buildDebugRequest(String param, String value) {
        Map<String, String> params = new HashMap<>(getGeneralInfo());
        params.put(ERROR_TYPE, Constants.ErrorType.CUSTOM);
        params.put(param, value);
        return obtainRequestString(params);
    }

    private static Map<String, String> getGeneralInfo() {
        Map<String, String> params = new HashMap<>();
        params.put(Params.DEVICE_OS, Constants.ADNROID_DEVICE_OS);
        params.put(Params.SDK_TYPE, Constants.LOOPME_SDK_TYPE);
        params.put(Params.SDK_VERSION, BuildConfig.VERSION_NAME);
        params.put(Params.DEVICE_OS_VERSION, Build.VERSION.RELEASE);
        params.put(Params.DEVICE_MODEL, Build.MODEL);
        params.put(Params.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        params.put(Params.DEVICE_ID, RequestUtils.getIfa());
        params.put(Params.APP_KEY, sAppKey);
        params.put(Params.PACKAGE_ID, sPackageId);
        params.put(REQUEST_ID, BidManager.getInstance().getRequestId());
        params.put(CID, BidManager.getInstance().getCurrentCid());
        params.put(CRID, BidManager.getInstance().getCurrentCrid());
        params.put(Params.MSG, Constants.SDK_ERROR_MSG);
        return params;
    }

    private static void proceedBuildEvent(String request) {
        sendDataToServer(Constants.ERROR_URL, request);
    }

    public static void post(LoopMeError error) {
        if (!shouldTrack(error)) {
            return;
        }
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(ERROR_MSG, error.getMessage());
        errorInfo.put(ERROR_TYPE, error.getErrorType());
        post(errorInfo);
        if (isVastError(error.getErrorType())) {
            postVastError(String.valueOf(error.getErrorCode()));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                } else {
                    result.append(URLEncoder.encode(entry.getKey()));
                }
                result.append("=");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                } else {
                    result.append(URLEncoder.encode(entry.getValue()));
                }
            }
        } catch (NullPointerException e) {
            Logging.out("HttpUtil", "UnsupportedEncoding: UTF-8");
        }
        return String.valueOf(result);
    }
}
