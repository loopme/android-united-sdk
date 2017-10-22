package com.loopme.tracker.partners;

import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.HttpUtil;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.debugging.Params;
import com.loopme.request.RequestUtils;
import com.loopme.utils.StringUtils;
import com.loopme.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoopMeTracker {
    private static final String LOG_TAG = LoopMeTracker.class.getSimpleName();

    private static String sPackageId;
    private static String sAppKey;
    private static Set<String> sVastErrorUrlSet = new HashSet<>();
    private static ExecutorService sExecutor;

    private LoopMeTracker() {
    }

    public static void init(LoopMeAd loopMeAd) {
        sExecutor = Executors.newCachedThreadPool();
        if (loopMeAd != null) {
            sAppKey = loopMeAd.getAppKey();
            sPackageId = loopMeAd.getContext().getPackageName();
        }
    }

    public static void initVastErrorUrl(List<String> errorUrlList) {
        sVastErrorUrlSet.addAll(errorUrlList);
    }

    public static void sendError(String error) {
        LoopMeTracker.post(error);
    }

    public static void postServerError(String error) {
        post(error, Constants.ErrorType.SERVER);
    }

    public static void post(String errorMessage) {
        post(errorMessage, Constants.ErrorType.CUSTOM);
    }

    public static void post(String errorMessage, String errorType) {
        Logging.out(LOG_TAG, errorType + " - " + errorMessage);
        String url = buildServerIssueUrl();
        String request = buildRequest(errorType, errorMessage);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        sendDataToServer(url, headers, request);
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
        params.put(Params.DEVICE_OS, Constants.ADNROID_DEVICE_OS);
        params.put(Params.SDK_TYPE, BuildConfig.SDK_TYPE);
        params.put(Params.SDK_VERSION, BuildConfig.VERSION_NAME);
        params.put(Params.DEVICE_ID, RequestUtils.getIfa());
        params.put(Params.APP_KEY, sAppKey);
        params.put(Params.PACKAGE_ID, sPackageId);
        params.put(Params.MSG, Constants.SDK_ERROR_MSG);
        params.put(Params.ERROR_TYPE, errorType);
        params.put(Params.ERROR_MSG, errorMessage);
        return HttpUtil.obtainRequestString(params);
    }

    private static void sendDataToServer(final String errorUrl, final Map<String, String> headers, final String request) {
        sExecutor.submit(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendRequest(errorUrl, headers, request);
            }
        });
    }

    public static synchronized void postVastError(String vastErrorCode) {
        for (String url : sVastErrorUrlSet) {
            String urlWithCode = StringUtils.setErrorCode(url, vastErrorCode);
            sendDataToServer(urlWithCode, null, null);
            Logging.out(LOG_TAG, urlWithCode);
        }
    }

    public static void clear() {
        sVastErrorUrlSet.clear();
    }

    public static void trackSdkFeedBack(List<String> packageIds, String token) {
        if (Utils.isPackageInstalled(packageIds)) {
            sendDataToServer(buildSdkFeedBackUrl(token), null, null);
        }
    }

    private static String buildSdkFeedBackUrl(String token) {
        String url = Constants.OLD_TRACK_FEEDBACK_URL + "?";
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(Params.ERROR_TYPE, Params.SDK_FEEDBACK);
        requestParams.put(Params.R, "1");
        requestParams.put(Params.ID, token);
        return url + HttpUtil.obtainRequestString(requestParams);
    }
}
