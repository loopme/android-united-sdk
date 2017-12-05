package com.loopme.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ResourceInfo;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.request.AES;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.vast.TrackingEvent;
import com.loopme.xml.Tracking;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Utils {
    private static final int ZERO = 0;
    private static final float DEFAULT_DENSITY = 1;
    private static final float DEFAULT_VOLUME = 1.0f;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int HUNDRED_PERCENTS = 100;
    private static final int DEFAULT_THRESHOLD = 11;
    private static final int HUNDREDS = 100;
    private static final long MILLIS_IN_SECOND = 1000;
    private static final int VIBRATE_MILLISECONDS = 500;

    private static final String CHROME = "Chrome";
    private static final String EMPTY_STRING = "";
    private static final String PERCENT_SYMBOL = "%";
    private static final String CHROME_SHORTCUT = "Chrm";
    private static final String DOUBLE_POINTS_SYMBOL = ":";
    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static Resources sResources;
    private static AudioManager sAudioManager;
    private static WindowManager sWindowManager;
    private static PackageManager sPackageManager;
    public static String sUserAgent;

    public static void init(Context context) {
        if (context != null) {
            sUserAgent = WebSettings.getDefaultUserAgent(context);
            sResources = context.getResources();
            sPackageManager = context.getPackageManager();
            sAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            sWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        } else {
            Logging.out(LOG_TAG, "Could not init utils, context is null");
        }
    }

    public static void reset() {
        sResources = null;
        sPackageManager = null;
        sAudioManager = null;
        sWindowManager = null;
    }

    public static int[] getPositionsOnScreen(RecyclerView recyclerView) {
        int[] positionArray = {-1, -1};
        if (recyclerView == null) {
            return positionArray;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            positionArray[0] = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            positionArray[1] = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

        } else if (layoutManager instanceof GridLayoutManager) {
            positionArray[0] = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            positionArray[1] = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] firsts;
            int[] lasts;
            try {
                firsts = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                lasts = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            } catch (NullPointerException e) {
                return positionArray;
            }

            List<Integer> firstList = new ArrayList<Integer>(firsts.length);
            for (int first : firsts) {
                firstList.add(first);
            }

            List<Integer> lastList = new ArrayList<Integer>(lasts.length);
            for (int last : lasts) {
                lastList.add(last);
            }

            positionArray[0] = Collections.min(firstList);
            positionArray[1] = Collections.max(lastList);
        }
        return positionArray;
    }

    public static String getSourceUrl(String message) {
        String result = "";
        if (message != null) {
            String[] tokens = message.split(":");
            if (tokens != null && tokens.length >= 3) {
                return tokens[tokens.length - 1];
            }
        }
        return result;
    }

    public static boolean isUsualFormat(String source) {
        try {
            String fileName = getFileNameFromUrl(source);
            return !TextUtils.isEmpty(fileName)
                    && (fileName.contains(Constants.MP4_FORMAT_EXT)
                    || fileName.contains(Constants.WEBM_FORMAT_EXT));
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String formatTime(double time) {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(time);
    }

    public static void setDimensions(AdSpotDimensions adSpotDimensions) {
        if (adSpotDimensions != null) {
            DisplayMetrics dm = getDisplayMetrics();
            int height;
            int width;
            // portrait mode
            if (dm.heightPixels > dm.widthPixels) {
                width = dm.widthPixels / 2;
            } else { //landscape mode
                width = dm.widthPixels / 3;
            }
            height = width * 2 / 3;

            adSpotDimensions.setWidth(width);
            adSpotDimensions.setHeight(height);
        }
    }

    public static String getStringFromStream(InputStream inputStream) {
        int numberBytesRead;
        StringBuilder out = new StringBuilder();
        byte[] bytes = new byte[4096];

        if (inputStream == null) {
            return "";
        }
        try {
            while ((numberBytesRead = inputStream.read(bytes)) != -1) {
                out.append(new String(bytes, 0, numberBytesRead));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return out.toString();
    }

    public static boolean isPackageInstalled(List<String> packageIds) {
        List<PackageInfo> packages = getInstalledPackages();

        for (PackageInfo packageInfo : packages) {
            for (int i = 0; i < packageIds.size(); i++) {
                if (packageIds.get(i).equalsIgnoreCase(packageInfo.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getScreenOrientation() {
        if (sWindowManager == null || sWindowManager.getDefaultDisplay() == null) {
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        DisplayMetrics displayMetrics = getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int orientation;

        int rotation = sWindowManager.getDefaultDisplay().getRotation();
        if (isDeviceNaturalOrientationPortrait(rotation, width, height)) {
            orientation = getOrientationForRectangleScreens(rotation);
        } else {
            orientation = getOrientationForSquareScreens(rotation);
        }
        return orientation;
    }

    public static int getOrientationForSquareScreens(int rotation) {
        int orientation;
        switch (rotation) {
            case Surface.ROTATION_0: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            }
            case Surface.ROTATION_90: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            }
            case Surface.ROTATION_180: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            }
            case Surface.ROTATION_270: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            }
            default: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            }
        }
        return orientation;
    }

    public static FrameLayout.LayoutParams calculateNewLayoutParams(
            FrameLayout.LayoutParams layoutParams,
            int videoWidth, int videoHeight,
            int resizeWidth, int resizeHeight,
            Constants.StretchOption stretchOption) {

        layoutParams.gravity = Gravity.CENTER;

        switch (stretchOption) {
            case NONE: {
                float percent = countPercentOfBlackArea(layoutParams, videoWidth, videoHeight, resizeWidth, resizeHeight);
                if (percent < DEFAULT_THRESHOLD) {
                    setNewSize(layoutParams, resizeWidth, resizeHeight);
                }
                break;
            }

            case STRETCH: {
                setNewSize(layoutParams, resizeWidth, resizeHeight);
                break;
            }
        }
        return layoutParams;
    }

    public static List<TrackingEvent> createProgressPoints(int duration, AdParams adParams) {
        List<TrackingEvent> trackingEventsList = new ArrayList<>();

        trackingEventsList.clear();
        for (String url : adParams.getImpressionsList()) {
            trackingEventsList.add(new TrackingEvent(url));
        }

        for (Tracking tracking : adParams.getTrackingEventsList()) {
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.CREATIVE_VIEW)) {
                event.timeMillis = 0;
                trackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.START)) {
                event.timeMillis = 0;
                trackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.FIRST_QUARTILE)) {
                event.timeMillis = duration / 4;
                trackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.MIDPOINT)) {
                event.timeMillis = duration / 2;
                trackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.THIRD_QUARTILE)) {
                event.timeMillis = duration * 3 / 4;
                trackingEventsList.add(event);
            }
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.PROGRESS)) {
                if (tracking.getOffset() == null) {
                    continue;
                }
                if (tracking.getOffset().contains("%")) {
                    event.timeMillis = duration * Utils.parsePercent(adParams.getSkipTime()) / 100;
                } else {
                    event.timeMillis = Utils.parseDuration(tracking.getOffset()) * 1000;
                }
                trackingEventsList.add(event);
            }
        }
        return trackingEventsList;
    }

    public static String makeChromeShortCut(String userString) {
        if (!TextUtils.isEmpty(userString) && userString.contains(CHROME)) {
            return userString.replace(CHROME, CHROME_SHORTCUT);
        } else {
            return userString;
        }
    }


    /**
     * @param duration in format hh:mm:ss
     * @return in seconds
     */
    public static int parseDuration(String duration) {
        try {
            String[] data = duration.split(DOUBLE_POINTS_SYMBOL);
            int hours = Integer.parseInt(data[0]);
            int minutes = Integer.parseInt(data[1]);
            int seconds = Integer.parseInt(data[2]);
            return seconds + SECONDS_IN_MINUTE * minutes + SECONDS_IN_HOUR * hours;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ZERO;
        }
    }

    public static int parsePercent(String duration) {
        try {
            String progress = duration.replace(PERCENT_SYMBOL, EMPTY_STRING).trim();
            return Integer.parseInt(progress);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ZERO;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return ZERO;
    }

    public static String createTimeStamp(int progress) {
        int timeLeft = progress / Constants.MILLIS_IN_SECOND;
        int minutes = timeLeft / Constants.SECONDS_IN_MINUTE;
        int seconds = timeLeft % Constants.SECONDS_IN_MINUTE;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    public static boolean isUrl(String event) {
        return !TextUtils.isEmpty(event) && event.contains(Constants.HTTP_PROTOCOL);
    }

    public static boolean isBooleanString(String value) {
        return !TextUtils.isEmpty(value) && (value.equalsIgnoreCase(Boolean.TRUE.toString()) || value.equalsIgnoreCase(Boolean.FALSE.toString()));
    }

    public static int getInteger(String value) {
        if (!TextUtils.isEmpty(value)) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static ResourceInfo getResourceInfo(String url) {
        if (!TextUtils.isEmpty(url)) {
            int lastIndexOfSlash = url.lastIndexOf("/");
            String baseUrl = url.substring(0, lastIndexOfSlash + 1);
            String resourceName = url.substring(lastIndexOfSlash + 1, url.length());
            return new ResourceInfo(baseUrl, resourceName);
        }
        return new ResourceInfo();
    }

    // TODO: ------------------------------------ do not test --------------------------------------------
    public static String readAssets(AssetManager assetManager, String filename) {
        try {
            return getStringFromStream(assetManager.open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getEncryptedString(String stringToEncrypt) {
        if (!TextUtils.isEmpty(stringToEncrypt)) {
            AES.setDefaultKey();
            AES.encrypt(stringToEncrypt);
            return Utils.deleteLastCharacter(AES.getEncryptedString());
        } else {
            return stringToEncrypt;
        }
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (sWindowManager != null) {
            sWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    public static String getPackageInstalledEncrypted() {
        String installedPackages = getInstalledPackagesAsString();
        return Utils.getEncryptedString(installedPackages);
    }

    public static void clearCache(Context context) {
        FileUtils.clearCache(context);
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

    public static List<PackageInfo> getInstalledPackages() {
        if (sPackageManager != null) {
            return sPackageManager.getInstalledPackages(0);
        }
        return new ArrayList<>();
    }

    public static void animateAppear(View view) {
        if (view != null) {
            view.animate().setDuration(500).alpha(1.0f);
        }
    }

    public static float getSystemVolume() {
        if (sAudioManager != null) {
            int volume_level = sAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int max = sAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int percent = Math.round(volume_level * HUNDRED_PERCENTS / max);
            return (float) percent / HUNDRED_PERCENTS;
        } else {
            return DEFAULT_VOLUME;
        }
    }

    public static int getHeight() {
        float density = getDensity();
        return (int) (getDisplayMetrics().heightPixels / density);
    }

    public static int getWidth() {
        float density = getDensity();
        return (int) (getDisplayMetrics().widthPixels / density);
    }

    public static void vibrate(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VIBRATE_MILLISECONDS);
            }
        } catch (Exception e) {
            Logging.out(LOG_TAG, "Missing permission for vibrate");
        }
    }

    public static List<String> getInstalledPackagesAsStringsList() {
        List<PackageInfo> installedPackages = getInstalledPackages();
        List<String> packagesAsString = new ArrayList<>();
        for (PackageInfo packageInfo : installedPackages) {
            packagesAsString.add(packageInfo.packageName);
        }
        return packagesAsString;
    }

    public static MediaPlayer convertToMediaPlayer(Object object) {
        if (object instanceof MediaPlayer) {
            return (MediaPlayer) object;
        }
        return null;
    }

    public static View convertToView(Object object) {
        if (object instanceof View) {
            return (View) object;
        }
        return null;
    }

    private static String getInstalledPackagesAsString() {
        List<String> packages = getInstalledPackagesAsStringsList();
        StringBuilder builder = new StringBuilder();
        for (String packageName : packages) {
            builder.append(packageName);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static int convertDpToPixel(float dp) {
        if (sResources != null) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                    sResources.getDisplayMetrics());
        } else {
            return ZERO;
        }
    }

    public static int roundNumberToHundredth(int number) {
        return (number / HUNDREDS) * HUNDREDS;
    }

    public static long toSeconds(long millisUntilFinished) {
        return millisUntilFinished / MILLIS_IN_SECOND;
    }

    public static boolean isActivityResolved(Intent intent, Context context) {
        return context != null && intent != null && context.getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY) != null;
    }

    public static boolean isApi19() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    }

    public static ViewGroup.LayoutParams createMatchParentLayoutParams() {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private static float getDensity() {
        if (sResources != null) {
            return sResources.getDisplayMetrics().density;
        } else {
            return DEFAULT_DENSITY;
        }
    }

    private static float countPercentOfBlackArea(FrameLayout.LayoutParams layoutParams,
                                                 int videoWidth, int videoHeight,
                                                 int resizeWidth, int resizeHeight) {
        if (layoutParams == null) {
            return ZERO;
        }
        int blackArea;
        float percent = 0;

        if (videoWidth > videoHeight) {
            layoutParams.width = resizeWidth;
            layoutParams.height = (int) ((float) videoHeight / (float) videoWidth * (float) resizeWidth);

            blackArea = resizeHeight - layoutParams.height;
            if (layoutParams.height != 0) {
                percent = blackArea * HUNDRED_PERCENTS / layoutParams.height;
            }
        } else {
            layoutParams.height = resizeHeight;
            layoutParams.width = (int) ((float) videoWidth / (float) videoHeight * (float) resizeHeight);

            blackArea = resizeWidth - layoutParams.width;
            if (layoutParams.width != 0) {
                percent = blackArea * HUNDRED_PERCENTS / layoutParams.width;
            }
        }
        return percent;
    }

    private static void setNewSize(FrameLayout.LayoutParams layoutParams, int resizeWidth, int resizeHeight) {
        if (layoutParams != null) {
            layoutParams.width = resizeWidth;
            layoutParams.height = resizeHeight;
        }
    }

    private static String deleteLastCharacter(String string) {
        if (!TextUtils.isEmpty(string)) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    private static int getOrientationForRectangleScreens(int rotation) {
        int orientation;
        switch (rotation) {
            case Surface.ROTATION_0: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            }
            case Surface.ROTATION_90: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            }
            case Surface.ROTATION_180: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            }
            case Surface.ROTATION_270: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            }
            default: {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            }
        }
        return orientation;
    }

    private static boolean isDeviceNaturalOrientationPortrait(int rotation, int width, int height) {
        return (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width
                || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height;
    }

    private static String getFileNameFromUrl(String source) {
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        int lastIndexOfSlash = source.lastIndexOf("/");
        return source.substring(lastIndexOfSlash, source.length());
    }

    public static void removeParent(ViewGroup view) {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static boolean isLandscape() {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == getScreenOrientation();
    }
}
