package com.loopme.debugging;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.network.HttpUtils;
import com.loopme.request.RequestUtils;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiveDebug {

    private static final String LOG_TAG = LiveDebug.class.getSimpleName();

    // Default debug time is 3 minutes.
    private static final int DEBUG_TIME = 3 * 60 * 1000;

    private static LogDbHelper sLogDbHelper;
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();

    private static CountDownTimer sDebugTimer;

    private static boolean sIsDebugOn;
    public static boolean isDebugOn() { return sIsDebugOn; }

    public static void init(Context context) { sLogDbHelper = new LogDbHelper(context); }

    public static void setLiveDebug(String packageId, final boolean debug, final String appKey) {
        Logging.out(LOG_TAG, "setLiveDebug " + debug);
        if (sIsDebugOn == debug || !debug) return;
        sIsDebugOn = true;
        new Handler(Looper.getMainLooper()).post(() -> {
            if (sDebugTimer != null) return;
            sDebugTimer = new CountDownTimer(DEBUG_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() {
                    sIsDebugOn = false;
                    sDebugTimer = null;
                    if (sLogDbHelper == null) return;
                    String logs = String.join("\n", sLogDbHelper.getLogs());
                    Executors.newCachedThreadPool().submit(() ->
                        HttpUtils.track(
                            Constants.ERROR_URL,
                            LoopMeTracker.obtainRequestString(getParams(packageId, appKey, logs))
                        )
                    );
                    sLogDbHelper.clear();
                }
            };
            Logging.out(LOG_TAG, "start debug timer");
            sDebugTimer.start();
        });
    }

    /**
     * @param forceSave TODO. Refactor. For handling cases when sIsDebugOn isn't set yet.
     */
    public static void handle(String logTag, String text, boolean forceSave) {
        if (sLogDbHelper != null && (sIsDebugOn || forceSave)) {
            boolean isUiThread = Looper.getMainLooper() == Looper.myLooper();
            String log = (isUiThread ? "ui" : "bg") + ": " + logTag + ": " + text;
            sExecutor.submit(() -> sLogDbHelper.putLog(log));
        }
    }

    private static Map<String, String> getParams(String packageId, String appKey, String debugLogs) {
         return new HashMap<String, String>() {{
            put(Params.DEVICE_OS, Constants.ADNROID_DEVICE_OS);
            put(Params.SDK_TYPE, Constants.LOOPME_SDK_TYPE);
            put(Params.SDK_VERSION, BuildConfig.VERSION_NAME);
            put(Params.DEVICE_ID, RequestUtils.getIfa());
            put(Params.PACKAGE_ID, packageId);
            put(Params.APP_KEY, appKey);
            put(Params.MSG, Constants.SDK_DEBUG_MSG);
            put(Params.DEBUG_LOGS, debugLogs);
            put(Params.APP_IDS, Utils.getPackageInstalledEncrypted());
        }};
    }
}