package com.loopme.request;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.ad.LoopMeAd;
import com.loopme.utils.Utils;

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

    public static int[] getAdSize(Context context, LoopMeAd baseAd) {
        int[] adSizeArray = new int[]{0, 0};
        if (context == null || baseAd == null) {
            return adSizeArray;
        }
        
        final String appKey = baseAd.getAppKey();
        Resources resources = context.getResources();
        if (baseAd instanceof LoopMeInterstitialGeneral) {
            int widthInDp = convertPixelToDp(resources, getDeviceSize(context).x);
            int heightInDp = convertPixelToDp(resources, getDeviceSize(context).y);
            adSizeArray[0] = widthInDp;
            adSizeArray[1] = heightInDp;
        } else if (baseAd instanceof LoopMeBannerGeneral banner) {
            final FrameLayout bannerView = banner.getBannerView();
            if (bannerView != null) {
                bannerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int widthInDp = convertPixelToDp(resources, bannerView.getWidth());
                        int heightInDp = convertPixelToDp(resources, bannerView.getHeight());
                        if (widthInDp <= 0 || heightInDp <= 0) {
                            return;
                        }
                        adSizeArray[0] = widthInDp;
                        adSizeArray[1] = heightInDp;

                        // Round the size before updating the map
                        Utils.roundBannersSize(adSizeArray, Constants.Banner.EXPANDABLE_BANNER_SIZE);
                        Utils.roundBannersSize(adSizeArray, Constants.Banner.MPU_BANNER_SIZE);
                        adSizes.put(appKey, adSizeArray);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bannerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            bannerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
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
