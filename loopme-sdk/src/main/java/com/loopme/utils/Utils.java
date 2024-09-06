package com.loopme.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.webkit.WebSettings;
import kotlin.Lazy;


import androidx.annotation.NonNull;

import com.loopme.Logging;
import com.loopme.request.AES;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// TODO. Refactor.
public class Utils {
    private static Context sContext;
    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final Lazy<Resources> sResources = kotlin.LazyKt.lazy(() -> sContext.getResources());
    private static final Lazy<AudioManager> sAudioManager = kotlin.LazyKt.lazy(() -> (AudioManager) sContext.getSystemService(Context.AUDIO_SERVICE));
    private static final Lazy<WindowManager> sWindowManager = kotlin.LazyKt.lazy(() -> (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE));
    private static final Lazy<PackageManager> sPackageManager = kotlin.LazyKt.lazy(() -> sContext.getPackageManager());
    private static final Lazy<String> sPackageName = kotlin.LazyKt.lazy(() -> sContext.getPackageName());
    private static final Lazy<String> sUserAgent = kotlin.LazyKt.lazy(() -> WebSettings.getDefaultUserAgent(sContext));
    private static final Deque<Integer> initializationTimeMillisQueue = new ArrayDeque<>(5);

    private static boolean isInitialized = false;

    public static void init(@NonNull Context context) {
        if (!isInitialized) {
            isInitialized = true;
            sContext = context.getApplicationContext();
        }
    }

    public static void setInitializationTimeMillisQueue(int timeInMillis){
        initializationTimeMillisQueue.offerLast(timeInMillis);
    }

    public static int getInitializationTimeMillisQueue() {
        Integer oldestValue = initializationTimeMillisQueue.pollFirst();
        return oldestValue == null ? 0 : oldestValue;
    }

    public static String getUserAgent() { return sUserAgent.getValue(); }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (sWindowManager.getValue() != null) {
            sWindowManager.getValue().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    public static int getScreenWidthInPixels() {
        if (sWindowManager.getValue() == null) return 0;
        Point size = new Point();
        sWindowManager.getValue().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeightInPixels() {
        if (sWindowManager.getValue() == null) return 0;
        Point size = new Point();
        sWindowManager.getValue().getDefaultDisplay().getSize(size);
        return size.y;
    }

    public static int getScreenHeightInDp() {
        float density = sResources.getValue() != null ? sResources.getValue().getDisplayMetrics().density : 1;
        return (int) (getDisplayMetrics().heightPixels / density);
    }

    public static int getScreenWidthInDp() {
        float density = sResources.getValue() != null ? sResources.getValue().getDisplayMetrics().density : 1;
        return (int) (getDisplayMetrics().widthPixels / density);
    }

    public static String getPackageName() {
        return sPackageName.getValue();
    }

    private static List<String> getInstalledPackagesAsStringsList() {
        // TODO: getInstalledPackages - As of Android 11, this method no longer returns information about all apps;
        List<PackageInfo> installedPackages = sPackageManager.getValue() == null ?
                new ArrayList<>() : sPackageManager.getValue().getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();
        for (PackageInfo packageInfo : installedPackages) {
            packageNames.add(packageInfo.packageName);
        }
        return packageNames;
    }

    public static boolean isPackageInstalled(List<String> packageIds) {
        for (String packageName : getInstalledPackagesAsStringsList()) {
            for (int i = 0; i < packageIds.size(); i++) {
                if (packageIds.get(i).equalsIgnoreCase(packageName)) return true;
            }
        }
        return false;
    }

    public static String getPackageInstalledEncrypted() {
        List<String> packages = getInstalledPackagesAsStringsList();
        StringBuilder builder = new StringBuilder();
        for (String packageName : packages) {
            builder.append(packageName).append(",");
        }
        String stringToEncrypt = builder.deleteCharAt(builder.length() - 1).toString();
        if (TextUtils.isEmpty(stringToEncrypt)) {
            return stringToEncrypt;
        }
        AES.setDefaultKey();
        AES.encrypt(stringToEncrypt);
        String encryptedString = AES.getEncryptedString();
        return TextUtils.isEmpty(encryptedString) ?
            encryptedString : encryptedString.substring(0, encryptedString.length() - 1);
    }

    /**
     * @param duration in format hh:mm:ss
     * @return in seconds
     */
    public static int parseDuration(String duration) {
        try {
            String[] time = duration.split(":");
            int hours = Integer.parseInt(time[0]);
            int minutes = Integer.parseInt(time[1]);
            int seconds = Integer.parseInt(time[2]);
            return seconds + 60 * minutes + 60 * 60 * hours;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int parsePercent(String duration) {
        try {
            return Integer.parseInt(duration.replace("%", "").trim());
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    public static float getSystemVolume() {
        if (sAudioManager.getValue() == null) return 1.0f;
        int volumeLevel = sAudioManager.getValue().getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = sAudioManager.getValue().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (float) Math.round((float) (volumeLevel * 100) / max) / 100;
    }

    public static int convertDpToPixel(float dp) {
        if (sResources.getValue() == null) return 0;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, sResources.getValue().getDisplayMetrics());
    }

    public interface RetrieveOperation<T> {
        T execute() throws Exception;
    }

    public static <T> T safelyRetrieve(RetrieveOperation<T> operation, T defaultValue) {
        try {
            return operation.execute();
        } catch (Exception ex) {
            Logging.out(LOG_TAG, ex.getMessage());
            return defaultValue;
        }
    }
}
