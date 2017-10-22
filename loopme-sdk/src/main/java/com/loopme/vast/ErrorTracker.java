//package com.loopme.vast;
//
//import com.loopme.Constants;
//import com.loopme.Logging;
//import com.loopme.HttpUtil;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class ErrorTracker {
//
//    public static final String UNEXPECTED_ERROR = "Unexpected top level error: ";
//    public static final String HTTP_UTIL = "HTTP util: ";
//    public static final String TIMEOUT = "timeout";
//
//    public static final String SERVER_CODE = "Server code ";
//
//    public static void sendError(String error) {
//        ErrorTracker.post(error);
//    }
//
//    public static void postServerError(String error) {
//        post(ErrorTracker.ErrorType.SERVER, error);
//    }
//
//    public enum ErrorType {
//        SERVER("server"), BAD_ASSET("bad_asset"), JS("js"), CUSTOM("custom"), VPAID("vpaid");
//
//        private final String mName;
//
//        ErrorType(String name) {
//            mName = name;
//        }
//    }
//
//    private static final String LOG_TAG = ErrorTracker.class.getSimpleName();
//
//    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
//
//    private ErrorTracker() {
//    }
//
//    public static void post(String errorMessage) {
//        post(ErrorType.CUSTOM, errorMessage);
//    }
//
//    public static void post(ErrorType errorType, String errorMessage) {
//        Logging.out(LOG_TAG, errorType + " - " + errorMessage);
//        String url = buildServerIssueUrl();
//        String request = buildRequest(errorType.mName, errorMessage);
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/x-www-form-urlencoded");
//        sendDataToServer(url, headers, request);
//    }
//
//    private static String buildServerIssueUrl() {
//        String url = Constants.ERROR_URL;
//        if (!url.startsWith("http")) {
//            url = "https://" + url;
//        }
//        return url;
//    }
//
//    private static String buildRequest(String errorType, String errorMessage) {
//        Map<String, String> params = new HashMap<>();
//        params.put("device_os", "android");
//        params.put("sdk_type", "vast");
//        params.put("sdk_version", "5.1.7");
////        params.put("device_id", RequestParametersProvider.getInstance().getViewerToken());
////        params.put("app_key", RequestParametersProvider.getInstance().getAppKey());
////        params.put("package", Utils.getPackageName());
//        params.put("msg", "sdk_error");
//        params.put("error_type", errorType);
//        params.put("error_msg", errorMessage);
//        return HttpUtil.obtainRequestString(params);
//    }
//
//    private static void sendDataToServer(final String errorUrl, final Map<String, String> headers, final String request) {
//        sExecutor.submit(new Runnable() {
//            @Override
//            public void run() {
//                HttpUtil.sendRequest(errorUrl, headers, request);
//            }
//        });
//    }
//}
