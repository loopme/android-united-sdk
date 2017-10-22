package com.loopme.request;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopme.BuildConfig;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.R;
import com.loopme.ad.LoopMeAd;
import com.loopme.LoopMeInterstitial;
import com.loopme.utils.StringUtils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class RequestUtils {

    private static final String VIDEO = "HTML - for usual MP4 video";
    private static final String VAST2 = "VAST2";
    private static final String VAST3 = "VAST3";
    private static final String VAST4 = "VAST4";
    private static final String VPAID1 = "VPAID1";
    private static final String VPAID2 = "VPAID2";
    private static final String MRAID2 = "MRAID2";
    private static final String V360 = "V360";
    private int[] mBattr;
    private String mAppBundle;
    private String mAppName;
    private String mAppVersion;
    private String mUa;
    private String mPn;
    private String mWn;
    private String mOr;
    private static String mIfa;
    private String mIp;
    private static String mDnt;
    private int mInstl;
    private int mConnectionType;
    private int mDeviceWidthPx;
    private int mDeviceHeightPx;
    private int mDeviceType;
    private int mJs;
    private int mWidth;
    private int mHeight;

    private LoopMeAd mLoopMeAd;

    public RequestUtils(Context context, LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            mLoopMeAd = loopMeAd;
            init(context);
        }
    }

    private void init(Context context) {
        if (context == null) {
            return;
        }
        getAdvertisingIdInfo(context);
        setBattery(context);
        setAppBundle(context);
        setAppName(context);
        setAppVersion(context);
        setDeviceType(context);
        setDeviceHeightPx(context);
        setDeviceWidthPx(context);
        setConnectionType(context);
        setJs(context);
        setUa(context);
        setWn(context);
        setOr(context);
        setAdSize(context);
        setInstl();
        setIp(context);
        setPn(context);
    }

    public String getAppBundle() {
        return mAppBundle;
    }

    public void setAppBundle(Context context) {
        this.mAppBundle = context.getPackageName();
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(Context context) {
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

    public String getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(Context context) {
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

    public String getUuId() {
        return UUID.randomUUID().toString();
    }

    public int getDeviceType() {
        return mDeviceType;
    }

    public void setDeviceType(Context context) {
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            mDeviceType = RequestConstants.DEVICE_TYPE_TABLET;
        } else {
            mDeviceType = RequestConstants.DEVICE_TYPE_PHONE;
        }
    }

    public int getDeviceWidthPx() {
        return mDeviceWidthPx;
    }

    public void setDeviceWidthPx(Context context) {
        mDeviceWidthPx = RequestParamsUtils.getDeviceWidthPx(context);
    }

    public int getDeviceHeightPx() {
        return mDeviceHeightPx;
    }

    public void setDeviceHeightPx(Context context) {
        mDeviceHeightPx = RequestParamsUtils.getDeviceHeightPx(context);
    }

    public int getJs() {
        return mJs;
    }

    public void setJs(Context context) {
        if (context != null && context instanceof Activity) {
            final Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean javaScriptEnabled = new WebView(activity).getSettings().getJavaScriptEnabled();
                    mJs = javaScriptEnabled ? 1 : 0;
                }
            });
        }
    }

    public static String getIfa() {
        return mIfa;
    }

    public String getOsv() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public int getConnectionType() {
        return mConnectionType;
    }

    public void setConnectionType(Context context) {
        mConnectionType = RequestParamsUtils.getConnectionType(context);
    }

    public String getOs() {
        return RequestConstants.ANDROID_OS;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        mIp = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getMake() {
        return Build.MANUFACTURER;
    }

    public String getHwv() {
        return Build.HARDWARE;
    }

    public String getUa() {
        return mUa;
    }

    public void setUa(Context context) {
        mUa = WebSettings.getDefaultUserAgent(context);
    }


    public String getModel() {
        return Build.MODEL;
    }

    public String getTimeZone() {
        return TimeZone.getDefault().getDisplayName();
    }

    public String getPhoneName() {
        return mPn;
    }

    public void setPn(Context context) {
        if (context != null) {
            String name = Settings.Secure.getString(context.getContentResolver(), RequestConstants.DEVICE_BLUETOOTH_NAME);
            name = StringUtils.getEncryptedString(name);
            mPn = StringUtils.getUrlEncodedString(name);
        } else {
            mPn = Build.UNKNOWN;
        }
    }

    public String getWifiName() {
        return mWn;
    }

    public void setWn(Context context) {
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
        int orientation = context.getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            mOr = "l";
        } else {
            mOr = "p";
        }
    }

    public String getChargeLevel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            return String.valueOf(level);
        } else {
            String[] batteryInfo = RequestParamsUtils.getBatteryInfo(context);
            return batteryInfo[0];
        }
    }

    public int getPlugin() {
        return -1;
    }

    public int getTmax() {
        return RequestConstants.MAX_TIME_TO_SUBMIT_BID;
    }

    public String[] getBcat() {
        return new String[]{RequestConstants.IAB25_3, RequestConstants.IAB25, RequestConstants.IAB26};
    }

    public int getSecure() {
        return RequestConstants.SECURE_IMPRESSION;
    }

    public String getTagId() {
        if (mLoopMeAd != null) {
            mLoopMeAd.getAppKey();
        }
        return Build.UNKNOWN;
    }


    public String getDisplayManagerVersion() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    public String getImpId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public int[] getApi() {
        return new int[]{RequestConstants.FRAMEWORK_MRAID_2, RequestConstants.FRAMEWORK_VIPAID_2_0};
    }

    public int getWidth() {
        return mWidth;
    }


    public int[] getBattery() {
        return mBattr;
    }

    public void setBattery(Context context) {
        mBattr = new int[]{3, 8};
    }

    public int getHeight() {
        return mHeight;
    }

    public String getIt() {
        return mLoopMeAd.getIntegrationType().getType();
    }

    public float getBidFloor() {
        return RequestConstants.BID_FLOOR_DEFAULT_VALUE;
    }


    public int getMaxDuration() {
        return RequestConstants.DEFAULT_MAX_DURATION;
    }

    public int[] getProtocols() {
        return new int[]{RequestConstants.PROTOCOLS_VAST_2_0, RequestConstants.PROTOCOLS_VAST_3_0};
    }


    public int getLinearity() {
        return RequestConstants.LINEAR_IN_STREAM;
    }


    public String[] getMimeTypes() {
        return new String[]{RequestConstants.VIDEO_MP4};
    }

    public int getStartDelay() {
        return RequestConstants.START_DELAY_DEFAULT_VALUE;
    }

    public int[] getDelivery() {
        return new int[]{RequestConstants.DELIVERY_METHOD_PROGRESSIVE};
    }

    public int getSequence() {
        return RequestConstants.SEQUENCE_DEFAULT_VALUE;
    }


    public int getMinDuration() {
        return RequestConstants.MIN_BITRATE_DEFAULT_VALUE;
    }

    public int getBoxingAllowed() {
        return RequestConstants.BOXING_DEFAULT;
    }

    public int getMaxBitrate() {
        return RequestConstants.MAX_BITRATE_DEFAULT_VALUE;
    }


    public String getDisplayManager() {
        return RequestConstants.LOOPME_SDK;
    }


    public int getInstl() {
        return mInstl;
    }

    public void setInstl() {
        boolean interstitial = false;
        if (mLoopMeAd instanceof LoopMeInterstitialGeneral) {
            interstitial = true;
        }
        mInstl = interstitial ? 1 : 0;
    }

    private void setAdSize(Context context) {
        int[] adSize = RequestParamsUtils.getAdSize(context, mLoopMeAd);
        mWidth = adSize[0];
        mHeight = adSize[1];
    }

    public static String getAdvertisingIdInfo(Context context) {
        RequestParamsUtils.getAdvertisingIdInfo(context, new AdvIdFetcher.Listener() {

            @Override
            public void onComplete(String googleAdId, boolean isDnt) {
                mDnt = isDnt ? "1" : "0";
                mIfa = googleAdId;
            }
        });
        return mIfa;
    }

    public String getDnt() {
        return mDnt;
    }

    public ArrayList getSupportedTechs() {
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

    public String getTrackersSupported() {
        return "[" + BuildConfig.SUPPORTED_TRACKERS + "]";
    }
}
