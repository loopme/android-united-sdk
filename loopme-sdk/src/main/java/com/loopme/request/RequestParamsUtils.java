package com.loopme.request;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.loopme.Constants;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.ad.LoopMeAd;
import com.loopme.utils.ConnectionUtils;
import com.loopme.utils.Utils;

/**
 * Created by vynnykiakiv on 6/14/17.
 */

public class RequestParamsUtils {

    private static final String UNKNOWN_SSID = "unknown ssid";
    private static final String SSID_VALUE = "0x";

    public static String getWifiName(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return "";
            }
            if (!wifiManager.isWifiEnabled()) {
                return "";
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            // remove extra quotes if needed
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            if (TextUtils.isEmpty(ssid) || ssid.contains(UNKNOWN_SSID) || ssid.equals(SSID_VALUE)) {
                return "";
            }
            return ssid;
        } catch (Exception e) {
            return "";
        }
    }

    public static int getConnectionType(Context context) {
        return ConnectionUtils.getConnectionType(context);
    }

    public static int convertPixelToDp(Context context, int pixels) {
        Resources resources = context.getResources();
        if (resources != null) {
            return (int) (pixels / resources.getDisplayMetrics().density);
        } else {
            return 0;
        }
    }

    public static ViewGroup.LayoutParams getParamsSafety(LoopMeBannerGeneral banner) {
        try {
            return banner.getBannerView().getLayoutParams();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int[] getAdSize(Context context, LoopMeAd baseAd) {
        int[] adSizeArray = new int[]{0, 0};
        if (context == null || baseAd == null) {
            return adSizeArray;
        }

        if (baseAd instanceof LoopMeInterstitialGeneral) {
            int widthInDp = RequestParamsUtils.convertPixelToDp(context, getDeviceWidthPx(context));
            int heightInDp = RequestParamsUtils.convertPixelToDp(context, getDeviceHeightPx(context));
            adSizeArray[0] = widthInDp;
            adSizeArray[1] = heightInDp;

        } else if (baseAd instanceof LoopMeBannerGeneral) {
            LoopMeBannerGeneral banner = (LoopMeBannerGeneral) baseAd;
            ViewGroup.LayoutParams params = RequestParamsUtils.getParamsSafety(banner);
            int currentWidthInDp = 0;
            int currentHeightInDp = 0;
            if (params != null) {
                currentWidthInDp = RequestParamsUtils.convertPixelToDp(context, params.width);
                currentHeightInDp = RequestParamsUtils.convertPixelToDp(context, params.height);
            }

            adSizeArray[0] = currentWidthInDp;
            adSizeArray[1] = currentHeightInDp;

            Utils.roundBannersSize(adSizeArray, Constants.Banner.EXPANDABLE_BANNER_SIZE);
            Utils.roundBannersSize(adSizeArray, Constants.Banner.MPU_BANNER_SIZE);
        }
        return adSizeArray;
    }

    public static int getDeviceWidthPx(Context context) {
        WindowManager windowManager;
        if (context != null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) {
                return 0;
            }
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        } else {
            return 0;
        }
    }

    public static int getDeviceHeightPx(Context context) {
        WindowManager windowManager;
        if (context != null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) {
                return 0;
            }
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.y;
        } else {
            return 0;
        }
    }

    public static AdvAdInfo getAdvertisingIdInfo(Context context) {
        return new AdvAdInfo(AdvertisingIdClient.getAdvertisingIdInfo(context));
    }

    public static class AdvAdInfo {
        private String mAdvId;
        private boolean mIsDoNotTrack;

        private AdvAdInfo(AdvertisingIdClient.AdInfo adInfo) {
            mIsDoNotTrack = adInfo.isLimitAdTrackingEnabled();
            mAdvId = adInfo.getId();
        }

        public String getAdvId() {
            return mAdvId;
        }

        public boolean isUserSetDoNotTrack() {
            return mIsDoNotTrack;
        }

        public String getDoNotTrackAsString() {
            return mIsDoNotTrack ? "1" : "0";
        }
    }
}
