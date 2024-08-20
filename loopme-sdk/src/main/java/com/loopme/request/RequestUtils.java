package com.loopme.request;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.loopme.BuildConfig;
import com.loopme.IABPreferences;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.R;
import com.loopme.ad.LoopMeAd;
import com.loopme.gdpr.ConsentType;
import com.loopme.location.GoogleLocationService;
import com.loopme.network.HttpUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class RequestUtils {
    private static final String BLUETOOTH = "bluetooth";
    private static final String HEADPHONES = "headphones";
    private String mAppBundle;
    private String mAppName;
    private String mAppVersion = "0.0";
    private String mUa;
    private String mOr = "p";
    private Location location;
    private static String mIfa = "";
    private static String mDnt = "0";
    private int mInstl;
    private int mConnectionType;
    private int mDeviceWidthPx;
    private int mDeviceHeightPx;
    private int mWidth;
    private int mHeight;
    private int mDeviceType;
    private LoopMeAd mLoopMeAd;
    private int mSkippable;

    RequestUtils(Context context, LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            mLoopMeAd = loopMeAd;
            if (context != null) {
                init(context);
            }
        }
    }

    private void init(Context context) {
        mAppBundle = context.getPackageName();
        mDeviceWidthPx = RequestParamsUtils.getDeviceSize(context).x;
        mDeviceHeightPx = RequestParamsUtils.getDeviceSize(context).y;
        mConnectionType = HttpUtils.getConnectionType(context);
        location = GoogleLocationService.getLocation(context);
        mUa = WebSettings.getDefaultUserAgent(context);
        mInstl = mLoopMeAd instanceof LoopMeInterstitialGeneral ? 1 : 0;
        setAdvertisingIdInfo(context);
        setAppName(context);
        setAppVersion(context);
        setDeviceType(context);
        setOr(context);
        setAdSize(context);
        setSkippable();
    }

    String getAppBundle() { return mAppBundle; }
    String getAppName() { return mAppName; }
    String getAppVersion() { return mAppVersion; }
    String getUa() { return mUa; }
    String getDnt() { return mDnt; }
    int getDeviceType() { return mDeviceType; }
    int getDeviceWidthPx() { return mDeviceWidthPx; }
    int getDeviceHeightPx() { return mDeviceHeightPx; }
    public int getWidth() { return mWidth; }
    public int getHeight() { return mHeight; }
    public static String getIfa() { return mIfa; }
    int getConnectionType() { return mConnectionType; }
    public String getOrientation() { return mOr; }
    public int[] getApi() {
        LoopMeAd.Type adType = mLoopMeAd.getPreferredAdType();
        if (adType == LoopMeAd.Type.HTML) return RequestConstants.API_HTML;
        if (adType == LoopMeAd.Type.VIDEO) return RequestConstants.API_VIDEO;
        return RequestConstants.API_ALL;
    }
    int getInstl() { return mInstl; }
    Location getLocation() { return location; }
    int getSkippable() { return mSkippable; }
    String getUuId() { return UUID.randomUUID().toString(); }
    String getLanguage() { return Locale.getDefault().getLanguage(); }
    String getOsv() { return String.valueOf(Build.VERSION.RELEASE); }
    String getMake() { return Build.MANUFACTURER; }
    String getHwv() { return Build.HARDWARE; }
    public String getModel() { return Build.MODEL; }
    String getTimeZone() { return TimeZone.getDefault().getDisplayName(Locale.ENGLISH); }
    String getDisplayManagerVersion() {  return BuildConfig.VERSION_NAME; }
    String getImpId() { return String.valueOf(System.currentTimeMillis()); }
    public String getIt() { return mLoopMeAd.getIntegrationType().getType(); }
    JSONArray getTrackersSupported() { return new JSONArray(); }

    String getUserConsent(Context context) {
        return IABPreferences.getInstance(context).getGdprState();
    }

    int getConsentType(Context context) {
        return IABPreferences.getInstance(context).getConsentType();
    }

    String getUSPrivacyString(Context context) {
        return IABPreferences.getInstance(context).getUSPrivacyString();
    }

    String getIabTcfTcString(Context context) {
        return IABPreferences.getInstance(context).getIabTcfTcString();
    }

    boolean isIabTcfCmpSdkPresent(Context context) {
        return IABPreferences.getInstance(context).isIabTcfCmpSdkPresent();
    }

    int getIabTcfGdprApplies(Context context) {
        return IABPreferences.getInstance(context).getIabTcfGdprApplies();
    }

    int getCoppa(Context context) {
        return IABPreferences.getInstance(context).getCoppa() ? 1 : 0;
    }

    boolean isIabTcfGdprAppliesPresent(Context context) {
        return IABPreferences.getInstance(context).isIabTcfGdprAppliesPresent();
    }

    int getMusic(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager != null && manager.isMusicActive() ? 1 : 0;
    }

    private List<String> getAudioOutput(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        List<String> outputs = new ArrayList<>();
        if (manager == null) {
            return outputs;
        }
        // TODO: Change implementation after min API has been raised to at least 23
        if (manager.isWiredHeadsetOn()) {
            outputs.add(HEADPHONES);
        }
        if (manager.isBluetoothA2dpOn()) {
            outputs.add(BLUETOOTH);
        }
        return outputs;
    }

    JSONArray getAudioOutputJson(Context context) {
        JSONArray audioOutputs = new JSONArray();
        List<String> activeOutput = getAudioOutput(context);
        for (String output : activeOutput) {
            audioOutputs.put(output);
        }
        return audioOutputs;
    }

    String getChargeLevel(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batteryPercentage = (bm != null) ? bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) : 1;
        return String.valueOf(batteryPercentage);
    }


    int getBatteryLevel(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batteryPercentage = (bm != null) ? bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) : -1;

        if (batteryPercentage >= 85) return 8;
        if (batteryPercentage >= 70) return 7;
        if (batteryPercentage >= 55) return 6;
        if (batteryPercentage >= 40) return 5;
        if (batteryPercentage >= 25) return 4;
        if (batteryPercentage >= 10) return 3;
        if (batteryPercentage >= 5) return 2;
        return 1;
    }

    int getBatterySaverState(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return (powerManager != null && powerManager.isPowerSaveMode()) ? 1 : 0;
    }

    boolean isAnyAudioOutput(Context context) {
        return !getAudioOutput(context).isEmpty();
    }

    private void setAppName(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = packageManager.getApplicationInfo(mAppBundle, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            mAppName = Build.UNKNOWN;
            e.printStackTrace();
        }
        if (appInfo == null) {
            return;
        }
        mAppName = packageManager.getApplicationLabel(appInfo).toString();
        if (TextUtils.isEmpty(mAppName)) {
            mAppName = Build.UNKNOWN;
        }
    }

    private void setAppVersion(Context context) {
        try {
            mAppVersion = context
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .versionName;
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setDeviceType(Context context) {
        mDeviceType = context.getResources().getBoolean(R.bool.isTablet) ?
            RequestConstants.DEVICE_TYPE_TABLET : RequestConstants.DEVICE_TYPE_PHONE;
    }

    public void setOr(Context context) {
        if (context.getResources() == null) {
            return;
        }
        int orientation = context.getResources().getConfiguration().orientation;
        mOr = orientation == Configuration.ORIENTATION_LANDSCAPE ? "l" : "p";
    }

    private void setAdSize(Context context) {
        int[] adSize = RequestParamsUtils.getAdSize(context, mLoopMeAd);
        mWidth = adSize[0];
        mHeight = adSize[1];
    }

    private static void setAdvertisingIdInfo(final Context context) {
        RequestParamsUtils.AdvAdInfo advAdInfo = RequestParamsUtils.getAdvertisingIdInfo(context);
        mDnt = advAdInfo.getDoNotTrackAsString();
        mIfa = advAdInfo.getAdvId();
        if (advAdInfo.isUserSetDoNotTrack()) {
            IABPreferences
                .getInstance(context)
                .setGdprState(false, ConsentType.USER_RESTRICTED);
        }
    }

    private void setSkippable() {
        LoopMeAd.Type adType = mLoopMeAd.getPreferredAdType();
        if (adType == LoopMeAd.Type.ALL || adType == LoopMeAd.Type.VIDEO) {
            mSkippable = 1;
        }
    }

    boolean isFullscreenSize() {
        return mWidth >= 320 && mHeight >= 320;
    }
}
