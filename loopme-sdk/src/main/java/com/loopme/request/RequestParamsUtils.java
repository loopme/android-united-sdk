package com.loopme.request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
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

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by vynnykiakiv on 6/14/17.
 */

public class RequestParamsUtils {

    private static final String UNKNOWN_SSID = "unknown ssid";
    private static final String SSID_VALUE = "0x";
    private static final String NCHRG = "NCHRG";
    private static final String AC = "AC";
    private static final String USB = "USB";
    private static final String WL = "WL";
    private static final String CHRG = "CHRG";

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

    public static String[] getBatteryInfo(final Context context) {

        final Object monitor = new Object();
        final String[] result = new String[2];
        final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                synchronized (monitor) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level / (float) scale;
                    result[0] = String.valueOf(batteryPct);

                    int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    switch (status) {
                        case 0:
                            result[1] = NCHRG;
                            break;
                        case BatteryManager.BATTERY_PLUGGED_AC:
                            result[1] = AC;
                            break;
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            result[1] = USB;
                            break;
                        case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                            result[1] = WL;
                            break;
                        default:
                            result[1] = CHRG;
                    }
                    monitor.notifyAll();
                }
            }
        };

        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryReceiver, batteryLevelFilter);

        // on some devices broadcast does not sent sometimes
        // in this case continue by timeout
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                synchronized (monitor) {
                    if (result[0] == null) {
                        result[0] = "3";
                        result[1] = "8";
                    }
                    monitor.notifyAll();
                }
            }
        }, 3, TimeUnit.MILLISECONDS);

        synchronized (monitor) {
            try {
                if (result[0] == null) {
                    monitor.wait();
                }
            } catch (InterruptedException e) {
                // do nothing
            }
            try {
                context.unregisterReceiver(batteryReceiver);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        return result;
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
