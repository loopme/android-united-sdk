package com.loopme.request;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.loopme.BuildConfig;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.IABPreferences;
import com.loopme.R;
import com.loopme.ad.LoopMeAd;
import com.loopme.gdpr.ConsentType;
import com.loopme.location.GoogleLocationService;
import com.loopme.utils.ApiLevel;
import com.loopme.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static android.content.Context.BATTERY_SERVICE;

public class RequestUtils {

    private static final String VIEWABILITY_VENDOR = "viewability";
    private static final String MOAT = "moat";
    private static final String IAS = "ias";
    private static final String TYPE_KEY = "type";
    private static final String VENDOR_KEY = "vendor";
    private static final String VIDEO = "HTML - for usual MP4 video";
    private static final String VAST2 = "VAST2";
    private static final String VAST3 = "VAST3";
    private static final String VAST4 = "VAST4";
    private static final String VPAID1 = "VPAID1";
    private static final String VPAID2 = "VPAID2";
    private static final String MRAID2 = "MRAID2";
    private static final String V360 = "V360";
    private static final String BLUETOOTH = "bluetooth";
    private static final String HEADPHONES = "headphones";
    private static final int[] EXPANDABLE_DIRECTION_FULLSCREEN = new int[]{5};
    private int[] mBattr;
    private String mAppBundle;
    private String mAppName;
    private String mAppVersion;
    private String mUa;
    private String mPn;
    private String mWn;
    private String mOr;
    private Location location;
    private static String mIfa = "";
    private static String mDnt = "0";
    private int mInstl;
    private int mConnectionType;
    private int mDeviceWidthPx;
    private int mDeviceHeightPx;
    private int mDeviceType;
    private int mJs = 1;
    private int mWidth;
    private int mHeight;

    private LoopMeAd mLoopMeAd;
    private int mSkippable;
    private int[] mApi;

    RequestUtils(Context context, LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            mLoopMeAd = loopMeAd;
            init(context);
        }
    }

    private void init(Context context) {
        if (context == null) {
            return;
        }
        setAdvertisingIdInfo(context);
        setBattery();
        setAppBundle(context);
        setAppName(context);
        setAppVersion(context);
        setDeviceType(context);
        setDeviceHeightPx(context);
        setDeviceWidthPx(context);
        setConnectionType(context);
        setJs(true);
        setUa(context);
        setWn(context);
        setOr(context);
        setAdSize(context);
        setInstl();
        setPn(context);
        setSkippable();
        setApi();
        setLocation(context);
    }

    String getAppBundle() {
        return mAppBundle;
    }

    private void setAppBundle(Context context) {
        this.mAppBundle = context.getPackageName();
    }

    String getAppName() {
        return mAppName;
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
        if (appInfo != null) {
            mAppName = packageManager.getApplicationLabel(appInfo).toString();
            if (TextUtils.isEmpty(mAppName)) {
                mAppName = Build.UNKNOWN;
            }
        }
    }

    String getAppVersion() {
        return mAppVersion;
    }

    private void setAppVersion(Context context) {
        mAppVersion = "0.0";
        if (context != null) {
            try {
                mAppVersion = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    String getUuId() {
        return UUID.randomUUID().toString();
    }

    int getDeviceType() {
        return mDeviceType;
    }

    private void setDeviceType(Context context) {
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            mDeviceType = RequestConstants.DEVICE_TYPE_TABLET;
        } else {
            mDeviceType = RequestConstants.DEVICE_TYPE_PHONE;
        }
    }

    int getDeviceWidthPx() {
        return mDeviceWidthPx;
    }

    private void setDeviceWidthPx(Context context) {
        mDeviceWidthPx = RequestParamsUtils.getDeviceWidthPx(context);
    }

    int getDeviceHeightPx() {
        return mDeviceHeightPx;
    }

    private void setDeviceHeightPx(Context context) {
        mDeviceHeightPx = RequestParamsUtils.getDeviceHeightPx(context);
    }

    public int getJs() {
        return mJs;
    }

    public void setJs(boolean enabled) {
        if (enabled) {
            mJs = 1;
        }
    }

    public static String getIfa() {
        return mIfa;
    }

    String getOsv() {
        return String.valueOf(Build.VERSION.RELEASE);
    }

    int getConnectionType() {
        return mConnectionType;
    }

    private void setConnectionType(Context context) {
        mConnectionType = RequestParamsUtils.getConnectionType(context);
    }

    public String getOs() {
        return RequestConstants.ANDROID_OS;
    }

    String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    String getMake() {
        return Build.MANUFACTURER;
    }

    String getHwv() {
        return Build.HARDWARE;
    }

    String getUa() {
        return mUa;
    }

    private void setUa(Context context) {
        mUa = WebSettings.getDefaultUserAgent(context);
    }


    public String getModel() {
        return Build.MODEL;
    }

    String getTimeZone() {
        return TimeZone.getDefault().getDisplayName();
    }

    String getPhoneName() {
        return mPn;
    }

    private void setPn(Context context) {
        if (context != null) {
            String name = Settings.Secure.getString(context.getContentResolver(), RequestConstants.DEVICE_BLUETOOTH_NAME);
            name = StringUtils.getEncryptedString(name);
            mPn = StringUtils.getUrlEncodedString(name);
        } else {
            mPn = Build.UNKNOWN;
        }
    }

    String getWifiName() {
        return mWn;
    }

    private void setWn(Context context) {
        mWn = RequestParamsUtils.getWifiName(context);
    }

    public String getOrientation() {
        return mOr;
    }

    public void setOr(Context context) {
        if (context == null || context.getResources() == null) {
            mOr = "p";
            return;
        }

        mOr = context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE
                ? "l"
                : "p";
    }

    private boolean isReverseOrientation() {
        return mLoopMeAd != null && mLoopMeAd.isReverseOrientationRequest();
    }

    String getChargeLevel(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int level = 1;
        if (bm != null) {
            level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return String.valueOf(level);
    }

    int getPlugin() {
        return -1;
    }

    int getTmax() {
        return RequestConstants.MAX_TIME_TO_SUBMIT_BID;
    }

    String[] getBcat() {
        return new String[]{RequestConstants.IAB25_3, RequestConstants.IAB25, RequestConstants.IAB26};
    }

    int getSecure() {
        return RequestConstants.SECURE_IMPRESSION;
    }

    String getDisplayManagerVersion() {
        return BuildConfig.VERSION_NAME;
    }

    String getImpId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void setApi() {
        LoopMeAd.Type adType = mLoopMeAd.getPreferredAdType();
        switch (adType) {
            case ALL: {
                mApi = new int[]{
                        RequestConstants.FRAMEWORK_MRAID_2,
                        RequestConstants.FRAMEWORK_VIPAID_2_0,
                        RequestConstants.FRAMEWORK_OMID_1
                };
                break;
            }
            case HTML: {
                mApi = new int[]{
                        RequestConstants.FRAMEWORK_MRAID_2,
                        RequestConstants.FRAMEWORK_OMID_1
                };
                break;
            }
            case VIDEO: {
                mApi = new int[]{
                        RequestConstants.FRAMEWORK_VIPAID_2_0,
                        RequestConstants.FRAMEWORK_OMID_1
                };
            }
        }
    }

    public int[] getApi() {
        return mApi;
    }

    public int getWidth() {
        return mWidth;
    }


    int[] getBattery() {
        return mBattr;
    }

    private void setBattery() {
        mBattr = new int[]{3, 8};
    }

    public int getHeight() {
        return mHeight;
    }

    public String getIt() {
        return mLoopMeAd.getIntegrationType().getType();
    }

    float getBidFloor() {
        return RequestConstants.BID_FLOOR_DEFAULT_VALUE;
    }


    int getMaxDuration() {
        return RequestConstants.DEFAULT_MAX_DURATION;
    }

    int[] getProtocols() {
        return new int[]{RequestConstants.PROTOCOLS_VAST_2_0,
                RequestConstants.PROTOCOLS_VAST_3_0,
                RequestConstants.PROTOCOLS_VAST_4_0,
                RequestConstants.PROTOCOLS_VAST_4_0_WRAPPER};
    }


    int getLinearity() {
        return RequestConstants.LINEAR_IN_STREAM;
    }


    String[] getMimeTypes() {
        return new String[]{RequestConstants.VIDEO_MP4};
    }

    int getStartDelay() {
        return RequestConstants.START_DELAY_DEFAULT_VALUE;
    }

    int[] getDelivery() {
        return new int[]{RequestConstants.DELIVERY_METHOD_PROGRESSIVE};
    }

    int getSequence() {
        return RequestConstants.SEQUENCE_DEFAULT_VALUE;
    }


    int getMinDuration() {
        return RequestConstants.MIN_BITRATE_DEFAULT_VALUE;
    }

    int getBoxingAllowed() {
        return RequestConstants.BOXING_DEFAULT;
    }

    int getMaxBitrate() {
        return RequestConstants.MAX_BITRATE_DEFAULT_VALUE;
    }


    String getDisplayManager() {
        return RequestConstants.LOOPME_SDK;
    }


    int getInstl() {
        return mInstl;
    }

    private void setInstl() {
        boolean interstitial = false;
        if (mLoopMeAd instanceof LoopMeInterstitialGeneral) {
            interstitial = true;
        }
        mInstl = interstitial ? 1 : 0;
    }

    private void setAdSize(Context context) {
        int[] adSize = RequestParamsUtils.getAdSize(context, mLoopMeAd);
        if (isReverseOrientation()) {
            mWidth = adSize[1];
            mHeight = adSize[0];
        } else {
            mWidth = adSize[0];
            mHeight = adSize[1];
        }
    }

    private static void setAdvertisingIdInfo(final Context context) {
        RequestParamsUtils.AdvAdInfo advAdInfo = RequestParamsUtils.getAdvertisingIdInfo(context);
        mDnt = advAdInfo.getDoNotTrackAsString();
        mIfa = advAdInfo.getAdvId();
        if (advAdInfo.isUserSetDoNotTrack()) {
            IABPreferences.getInstance(context).setGdprState(false, ConsentType.USER_RESTRICTED);
        }
    }

    String getDnt() {
        return mDnt;
    }

    ArrayList getSupportedTechs() {
        ArrayList<String> techsList = new ArrayList<>();
        techsList.add(VIDEO);
        techsList.add(VAST2);
        techsList.add(VAST3);
        techsList.add(VAST4);
        techsList.add(VPAID1);
        techsList.add(VPAID2);
        techsList.add(MRAID2);
        techsList.add(V360);
        return techsList;
    }

    JSONArray getTrackersSupported() {
        JSONArray arr = new JSONArray();
        try {
            arr.put(new JSONObject().put(TYPE_KEY, VIEWABILITY_VENDOR).put(VENDOR_KEY, MOAT));
            arr.put(new JSONObject().put(TYPE_KEY, VIEWABILITY_VENDOR).put(VENDOR_KEY, IAS));
            return arr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    int getSkippable() {
        return mSkippable;
    }

    private void setSkippable() {
        LoopMeAd.Type adType = mLoopMeAd.getPreferredAdType();
        switch (adType) {
            case ALL:
            case VIDEO: {
                mSkippable = 1;
                break;
            }
        }
    }

    String getUserConsent(Context context) {
        return IABPreferences.getInstance(context).getGdprState();
    }


    int getConsentType(Context context) {
        return IABPreferences.getInstance(context).getConsentType();
    }

    int[] getExpDir() {
        return EXPANDABLE_DIRECTION_FULLSCREEN;
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
        boolean coppa = IABPreferences.getInstance(context).getCoppa();
        return coppa ? 1 : 0;
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
        if (manager != null) {
            boolean wiredHeadsetOn = manager.isWiredHeadsetOn();
            if (wiredHeadsetOn) {
                outputs.add(HEADPHONES);
            }
            boolean bluetoothA2dpOn = manager.isBluetoothA2dpOn();
            if (bluetoothA2dpOn) {
                outputs.add(BLUETOOTH);
            }
        }
        return outputs;
    }

    JSONArray getAudioOutputJson(Context context) {
        JSONArray array = new JSONArray();
        List<String> activeOutput = getAudioOutput(context);
        for (String output : activeOutput) {
            array.put(output);
        }
        return array;
    }

    boolean isAnyAudioOutput(Context context) {
        List<String> activeOutput = getAudioOutput(context);
        return !activeOutput.isEmpty();
    }

    private void setLocation(Context context) {
        location = GoogleLocationService.getLocation(context);
    }

    Location getLocation() {
        return location;
    }
}
