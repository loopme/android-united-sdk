package com.loopme.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Vibrator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.request.AES;
import com.loopme.vast.TrackingEvent;
import com.loopme.xml.Tracking;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO. Refactor.
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

    public static StringBuilder getStringBuilderFromStream(InputStream inputStream) {
        int numberBytesRead;
        StringBuilder out = new StringBuilder();
        byte[] bytes = new byte[4096];

        if (inputStream == null) {
            return out;
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
        return out;
    }

    public static boolean isPackageInstalled(List<String> packageIds, List<String> installedList) {
        for (String packageName : installedList) {
            for (int i = 0; i < packageIds.size(); i++) {
                if (packageIds.get(i).equalsIgnoreCase(packageName)) {
                    return true;
                }
            }
        }
        return false;
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

    // : ------------------------------------ do not test --------------------------------------------
    public static StringBuilder readAssets(AssetManager assetManager, String filename) {
        try {
            return getStringBuilderFromStream(assetManager.open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new StringBuilder();
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

    public static float getSystemVolume() {
        if (sAudioManager != null) {
            int volume_level = sAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int max = sAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int percent = Math.round((float) (volume_level * HUNDRED_PERCENTS) / max);
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
        return sResources != null ? (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, sResources.getDisplayMetrics()) : ZERO;
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

    public static ViewGroup.LayoutParams createMatchParentLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    private static float getDensity() {
        return sResources != null ? sResources.getDisplayMetrics().density : DEFAULT_DENSITY;
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
                percent = (float) (blackArea * HUNDRED_PERCENTS) / layoutParams.height;
            }
        } else {
            layoutParams.height = resizeHeight;
            layoutParams.width = (int) ((float) videoWidth / (float) videoHeight * (float) resizeHeight);

            blackArea = resizeWidth - layoutParams.width;
            if (layoutParams.width != 0) {
                percent = (float) (blackArea * HUNDRED_PERCENTS) / layoutParams.width;
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

    private static String getFileNameFromUrl(String source) {
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        int lastIndexOfSlash = source.lastIndexOf("/");
        return source.substring(lastIndexOfSlash);
    }

    public static void removeParent(ViewGroup view) {
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    public static boolean isCustomBannerSize(int width, int height) {
        return !(isExpandBanner(width, height) || isMpuBanner(width, height));
    }

    public static boolean isExpandBanner(int width, int height) {
        AdSpotDimensions expandBanner = new AdSpotDimensions(Constants.Banner.EXPAND_BANNER_WIDTH, Constants.Banner.EXPAND_BANNER_HEIGHT);
        AdSpotDimensions customSize = new AdSpotDimensions(width, height);
        return expandBanner.equals(customSize);
    }

    public static boolean isMpuBanner(int width, int height) {
        AdSpotDimensions mpuBanner = new AdSpotDimensions(Constants.Banner.MPU_BANNER_WIDTH, Constants.Banner.MPU_BANNER_HEIGHT);
        AdSpotDimensions customSize = new AdSpotDimensions(width, height);
        return mpuBanner.equals(customSize);
    }

    public static boolean isWithinAcceptableLimits(int sizeToCheck, int currentSize) {
        int minSizeToCheck = sizeToCheck - Constants.Banner.SIZE_DISCREPANCY;
        int maxSizeToCheck = sizeToCheck + Constants.Banner.SIZE_DISCREPANCY;

        return minSizeToCheck <= currentSize && currentSize <= maxSizeToCheck;
    }

    public static void roundBannersSize(int[] currentSizeArray, int[] bannerSizeToCheck) {
        int currentWidth = currentSizeArray[0];
        int currentHeight = currentSizeArray[1];

        int bannerWidth = bannerSizeToCheck[0];
        int bannerHeight = bannerSizeToCheck[1];

        if (Utils.isWithinAcceptableLimits(bannerWidth, currentWidth) && Utils.isWithinAcceptableLimits(bannerHeight, currentHeight)) {
            currentSizeArray[0] = bannerWidth;
            currentSizeArray[1] = bannerHeight;
        }
    }

    public static void adjustLayoutParams(ViewGroup.LayoutParams paramFrom, ViewGroup.LayoutParams paramTo) {
        paramTo.width = paramFrom.width;
        paramTo.height = paramFrom.height;
    }

    public static boolean isNotNull(Object[] args) {
        if (args != null && args.length >= 1) {
            for (Object object : args) {
                if (object == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasInteger(Object[] args) {
        if (isNotNull(args)) {
            for (Object object : args) {
                if (!(object instanceof Integer)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasStrings(Object[] args) {
        if (isNotNull(args)) {
            for (Object object : args) {
                if (!(object instanceof String)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isFirstQuartile(int adDuration, int currentPosition) {
        int event25 = adDuration / 4;
        return currentPosition >= event25;
    }

    public static boolean isMidpoint(int adDuration, int currentPosition) {
        int event50 = adDuration / 2;
        return currentPosition >= event50;
    }

    public static boolean isThirdQuartile(int adDuration, int currentPosition) {
        int event75 = adDuration * 3 / 4;
        return currentPosition >= event75;
    }

    public static boolean isFloat(Object obj) {
        return obj instanceof Float;
    }

    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<\\s*script\\b[^>]*>");

    public static String addMraidScript(String html) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }

        Matcher m = SCRIPT_TAG_PATTERN.matcher(html);
        if (!m.find()) {
            Logging.out(LOG_TAG, "Couldn't find <script>");
            return html;
        }

        // TODO. Performance?
        return new StringBuilder(html).insert(m.start(), Constants.MRAID_SCRIPT).toString();
    }

    public static String getUserAgent() {
        return sUserAgent;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
