package com.loopme.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.request.AES;
import com.loopme.vast.TrackingEvent;
import com.loopme.xml.Tracking;

import java.util.ArrayList;
import java.util.List;

// TODO. Refactor.
public class Utils {
    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final int ZERO = 0;
    private static Resources sResources;
    private static AudioManager sAudioManager;
    private static WindowManager sWindowManager;
    private static PackageManager sPackageManager;
    public static String sUserAgent;

    public static void init(Context context) {
        if (context == null) {
            Log.d(LOG_TAG, "Could not init utils, context is null");
            return;
        }
        sUserAgent = WebSettings.getDefaultUserAgent(context);
        sResources = context.getResources();
        sPackageManager = context.getPackageManager();
        sAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static void reset() {
        sResources = null;
        sPackageManager = null;
        sAudioManager = null;
        sWindowManager = null;
    }

    public static void setDimensions(AdSpotDimensions adSpotDimensions) {
        DisplayMetrics dm = getDisplayMetrics();
        boolean isPortrait = dm.heightPixels > dm.widthPixels;
        int width = isPortrait ? dm.widthPixels / 2 : dm.widthPixels / 3;
        int height = width * 2 / 3;
        adSpotDimensions.setWidth(width);
        adSpotDimensions.setHeight(height);
    }

    public static List<String> getInstalledPackagesAsStringsList() {
        // TODO: getInstalledPackages - As of Android 11, this method no longer returns information about all apps;
        List<PackageInfo> installedPackages = sPackageManager == null ?
                new ArrayList<>() : sPackageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();
        for (PackageInfo packageInfo : installedPackages) {
            packageNames.add(packageInfo.packageName);
        }
        return packageNames;
    }

    public static boolean isPackageInstalled(List<String> packageIds) {
        List<String> installedList = getInstalledPackagesAsStringsList();
        for (String packageName : installedList) {
            for (int i = 0; i < packageIds.size(); i++) {
                if (packageIds.get(i).equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    // TODO: Refactor - Remove side effects.
    private static float countPercentOfBlackArea(
        @NonNull FrameLayout.LayoutParams layoutParams,
        int videoWidth, int videoHeight,
        int resizeWidth, int resizeHeight
    ) {
        int blackArea;
        float percent = 0;
        int HUNDRED_PERCENTS = 100;
        if (videoWidth > videoHeight) {
            layoutParams.width = resizeWidth;
            layoutParams.height = (int) ((float) videoHeight / (float) videoWidth * (float) resizeWidth);
            if (layoutParams.height != 0) {
                blackArea = resizeHeight - layoutParams.height;
                percent = (float) (blackArea * HUNDRED_PERCENTS) / layoutParams.height;
            }
        } else {
            layoutParams.height = resizeHeight;
            layoutParams.width = (int) ((float) videoWidth / (float) videoHeight * (float) resizeHeight);
            if (layoutParams.width != 0) {
                blackArea = resizeWidth - layoutParams.width;
                percent = (float) (blackArea * HUNDRED_PERCENTS) / layoutParams.width;
            }
        }
        return percent;
    }

    public static FrameLayout.LayoutParams calculateNewLayoutParams(
        @NonNull FrameLayout.LayoutParams layoutParams,
        int videoWidth, int videoHeight,
        int resizeWidth, int resizeHeight,
        Constants.StretchOption stretchOption
    ) {
        layoutParams.gravity = Gravity.CENTER;
        switch (stretchOption) {
            case NONE:
                float percent = countPercentOfBlackArea(layoutParams, videoWidth, videoHeight, resizeWidth, resizeHeight);
                int DEFAULT_THRESHOLD = 11;
                if (percent < DEFAULT_THRESHOLD) {
                    layoutParams.width = resizeWidth;
                    layoutParams.height = resizeHeight;
                }
                break;
            case STRETCH:
                layoutParams.width = resizeWidth;
                layoutParams.height = resizeHeight;
                break;
        }
        return layoutParams;
    }

    public static List<TrackingEvent> createProgressPoints(int duration, AdParams adParams) {
        List<TrackingEvent> trackingEventsList = new ArrayList<>();

        for (String url : adParams.getImpressionsList()) {
            trackingEventsList.add(new TrackingEvent(url));
        }

        for (Tracking tracking : adParams.getTrackingEventsList()) {
            if (tracking == null) {
                continue;
            }
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.isCreativeViewEvent()) {
                event.timeMillis = 0;
                trackingEventsList.add(event);
            }
            if (tracking.isStartEvent()) {
                event.timeMillis = 0;
                trackingEventsList.add(event);
            }
            if (tracking.isFirstQuartileEvent()) {
                event.timeMillis = duration / 4;
                trackingEventsList.add(event);
            }
            if (tracking.isMidpointEvent()) {
                event.timeMillis = duration / 2;
                trackingEventsList.add(event);
            }
            if (tracking.isThirdQuartileEvent()) {
                event.timeMillis = duration * 3 / 4;
                trackingEventsList.add(event);
            }
            if (tracking.isProgressEvent()) {
                if (tracking.getOffset() != null) {
                    if (tracking.getOffset().contains("%")) {
                        event.timeMillis = duration * Utils.parsePercent(adParams.getSkipTime()) / 100;
                    } else {
                        event.timeMillis = Utils.parseDuration(tracking.getOffset()) * 1000;
                    }
                    trackingEventsList.add(event);
                }
            }
        }
        return trackingEventsList;
    }

    /**
     * @param duration in format hh:mm:ss
     * @return in seconds
     */
    public static int parseDuration(String duration) {
        try {
            String DIVIDER = ":";
            String[] time = duration.split(DIVIDER);
            int hours = Integer.parseInt(time[0]);
            int minutes = Integer.parseInt(time[1]);
            int seconds = Integer.parseInt(time[2]);
            int SECONDS_IN_MINUTE = 60;
            int SECONDS_IN_HOUR = 3600;
            return seconds + SECONDS_IN_MINUTE * minutes + SECONDS_IN_HOUR * hours;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ZERO;
        }
    }

    public static int parsePercent(String duration) {
        try {
            return Integer.parseInt(duration.replace("%", "").trim());
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }
        return ZERO;
    }

    // : ------------------------------------ do not test --------------------------------------------

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

    public static int getScreenWidth() {
        if (sWindowManager == null) {
            return ZERO;
        }
        Display display = sWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight() {
        if (sWindowManager == null) {
            return ZERO;
        }
        Display display = sWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static float getSystemVolume() {
        if (sAudioManager == null) {
            return 1.0f;
        }
        int volumeLevel = sAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int max = sAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (float) Math.round((float) (volumeLevel * 100) / max) / 100;
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (sWindowManager != null) {
            sWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    private static float getDensity() {
        float DEFAULT_DENSITY = 1;
        return sResources != null ? sResources.getDisplayMetrics().density : DEFAULT_DENSITY;
    }

    public static int getHeight() {
        return (int) (getDisplayMetrics().heightPixels / getDensity());
    }

    public static int getWidth() {
        return (int) (getDisplayMetrics().widthPixels / getDensity());
    }

    public static int convertDpToPixel(float dp) {
        if (sResources == null) {
            return ZERO;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, sResources.getDisplayMetrics());
    }

    public static int convertDpToPixel(float dp, Context ctx) {
        if (ctx == null)
            return ZERO;

        Resources res = ctx.getResources();
        if (res == null)
            return ZERO;

        DisplayMetrics dm = res.getDisplayMetrics();
        if (dm == null)
            return ZERO;

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    public static ViewGroup.LayoutParams createMatchParentLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        return params;
    }

    public static String getUserAgent() {
        return sUserAgent;
    }
}
