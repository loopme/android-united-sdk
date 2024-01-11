package com.loopme.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by katerina on 5/4/17.
 */

public class InternetUtils {

    private static final String LOG_TAG = InternetUtils.class.getSimpleName();
    private static final String PING_COMMAND = "/system/bin/ping -c 1 -s 1 8.8.8.8";

    private static final long CACHE_TIME_RANGE = 5000;
    private static final long MILLIS_TO_WAIT = 1400;

    private static long mLastCallTime;
    private static boolean mLastCallResult;

    public static boolean isReallyInternetAvailable(Context context) {
        if (isMobileInternetAvailable(context)) {
            return true;
        }

        if (!isConnectionAvailable(context)) {
            return false;
        }

        if (isInTimeRange()) {
            return mLastCallResult;
        }

        try {
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                boolean online = isOnlineOnUIThread();
                setNewResult(online);
                return online;
            } else {
                boolean online = isOnline();
                setNewResult(online);
                return online;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unexpected exception");
            setNewResult(false);
            e.printStackTrace();
        }

        return false;
    }

    private static boolean isOnlineOnUIThread() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isOnline();
            }
        });
        executorService.shutdown();
        try {
            boolean isOk = executorService.awaitTermination(MILLIS_TO_WAIT, TimeUnit.MILLISECONDS);
            if (isOk) {
                return result.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec(PING_COMMAND);
            int exitValue = ipProcess.waitFor();
            return exitValue == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void setNewResult(boolean value) {
        mLastCallResult = value;
        mLastCallTime = System.currentTimeMillis();
    }

    private static boolean isInTimeRange() {
        return System.currentTimeMillis() - mLastCallTime < CACHE_TIME_RANGE;
    }

    private static boolean isMobileInternetAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //TODO: Use ConnectivityManager.NetworkCallback instead of deprecated method
        return mobileInfo != null && mobileInfo.isConnected() && (wifiInfo == null || !wifiInfo.isConnected());
    }

    public static boolean isConnectionAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        NetworkInfo info = cm.getActiveNetworkInfo();

        //TODO: See above
        return info != null && info.isConnected();
    }

    public static boolean isOnline(Context context) {
        boolean isOnline;
        try {
            final ConnectivityManager conMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr == null) {
                return false;
            }

            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

            //TODO: See above
            return activeNetwork != null && activeNetwork.isConnected()
                    && activeNetwork.isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
            isOnline = false;
        }
        return isOnline;
    }

}
