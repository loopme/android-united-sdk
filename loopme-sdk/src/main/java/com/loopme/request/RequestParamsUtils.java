package com.loopme.request;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.ad.LoopMeAd;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vynnykiakiv on 6/14/17.
 */

public class RequestParamsUtils {
    // Map to store the sizes of each LoopMeAd by mAppKey
    private static final Map<String, int[]> adSizes = new HashMap<>();

    private static int convertPixelToDp(Resources resources, int pixels) {
        return resources == null ? 0 : (int) (pixels / resources.getDisplayMetrics().density);
    }

    private static boolean isWithinAcceptableLimits(int sizeToCheck, int currentSize) {
        int minSizeToCheck = sizeToCheck - Constants.Banner.SIZE_DISCREPANCY;
        int maxSizeToCheck = sizeToCheck + Constants.Banner.SIZE_DISCREPANCY;
        return minSizeToCheck <= currentSize && currentSize <= maxSizeToCheck;
    }

    private static void roundBannersSize(int[] currentSizeArray, int[] bannerSizeToCheck) {
        int currentWidth = currentSizeArray[0];
        int currentHeight = currentSizeArray[1];
        int bannerWidth = bannerSizeToCheck[0];
        int bannerHeight = bannerSizeToCheck[1];
        if (
            isWithinAcceptableLimits(bannerWidth, currentWidth) &&
            isWithinAcceptableLimits(bannerHeight, currentHeight)
        ) {
            currentSizeArray[0] = bannerWidth;
            currentSizeArray[1] = bannerHeight;
        }
    }

    public static int[] getAdSize(Context context, LoopMeAd baseAd) {
        int[] adSizeArray = new int[]{0, 0};
        if (context == null || baseAd == null) {
            return adSizeArray;
        }
        
        final String appKey = baseAd.getAppKey();
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        boolean isTablet = configuration.smallestScreenWidthDp >= 600; // 600dp is a common breakpoint for tablets
        boolean isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (baseAd instanceof LoopMeInterstitialGeneral) {
            if (isTablet) {
                if (isLandscape) {
                    adSizeArray[0] = 1024;
                    adSizeArray[1] = 768;
                } else {
                    adSizeArray[0] = 768;
                    adSizeArray[1] = 1024;
                }
            } else {
                if (isLandscape) {
                    adSizeArray[0] = 480;
                    adSizeArray[1] = 320;
                } else {
                    adSizeArray[0] = 320;
                    adSizeArray[1] = 480;
                }
            }
        } else if (baseAd instanceof LoopMeBannerGeneral banner) {
            final FrameLayout bannerView = banner.getBannerView();
            if (bannerView != null) {
                adSizeArray[0] = banner.getWidth();
                adSizeArray[1] = banner.getHeight();
                roundBannersSize(adSizeArray, Constants.Banner.EXPANDABLE_BANNER_SIZE);
                roundBannersSize(adSizeArray, Constants.Banner.MPU_BANNER_SIZE);
                adSizes.put(appKey, adSizeArray);
            }

            // Get the size from the map
            if (adSizes.containsKey(appKey)) {
                return adSizes.get(appKey);
            }
        }
        return adSizeArray;
    }

    public static Point getDeviceSize(Context context) {
        WindowManager windowManager;
        Point size = new Point(0, 0);
        if (context == null) {
            return size;
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return size;
        }
        windowManager.getDefaultDisplay().getSize(size);
        return size;
    }

    public static AdvAdInfo getAdvertisingIdInfo(Context context) {
        return new AdvAdInfo(AdvertisingIdClient.getAdvertisingIdInfo(context));
    }

    public static class AdvAdInfo {
        private final String mAdvId;
        private final boolean mIsDoNotTrack;

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
