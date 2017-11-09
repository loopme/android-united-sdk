package com.loopme.ad;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.LoopMeExpandableBannerGeneral;
import com.loopme.LoopMeInterstitialGeneral;

import java.util.HashMap;
import java.util.Map;

public class LoopMeAdHolder {

    private static final Map<Integer, LoopMeInterstitialGeneral> mNewImplInterstitialMap = new HashMap<>();
    private static final Map<Integer, LoopMeBannerGeneral> mNewImplBannerMap = new HashMap<>();
    private static final Map<Integer, LoopMeExpandableBannerGeneral> mNewImplExpandableBannerMap = new HashMap<>();

    private LoopMeAdHolder() {
    }

    public static void putAd(LoopMeAd loopMeAd) {
        int id = loopMeAd.getAdId();
        if (loopMeAd.getAdFormat() == Constants.AdFormat.INTERSTITIAL) {
            mNewImplInterstitialMap.put(id, (LoopMeInterstitialGeneral) loopMeAd);
        } else if (loopMeAd.getAdFormat() == Constants.AdFormat.EXPANDABLE_BANNER) {
            mNewImplExpandableBannerMap.put(id, (LoopMeExpandableBannerGeneral) loopMeAd);
        } else {
            mNewImplBannerMap.put(id, (LoopMeBannerGeneral) loopMeAd);
        }
    }

    public static LoopMeInterstitialGeneral createInterstitial(String appKey, Activity activity) {
        if (activity == null || TextUtils.isEmpty(appKey)) {
            return null;
        } else {
            LoopMeInterstitialGeneral interstitial = new LoopMeInterstitialGeneral(activity, appKey);
            mNewImplInterstitialMap.put(interstitial.getAdId(), interstitial);
            return interstitial;
        }
    }

    private static LoopMeInterstitialGeneral findInterstitial(int adId) {
        if (mNewImplInterstitialMap.containsKey(adId)) {
            return mNewImplInterstitialMap.get(adId);
        } else {
            return null;
        }
    }

    public static LoopMeBannerGeneral createBanner(String appKey, Activity activity) {
        if (activity == null || TextUtils.isEmpty(appKey)) {
            return null;
        } else {
            LoopMeBannerGeneral banner = new LoopMeBannerGeneral(activity, appKey);
            mNewImplBannerMap.put(banner.getAdId(), banner);
            return banner;
        }
    }

    private static LoopMeBannerGeneral findBanner(int adId) {
        if (mNewImplBannerMap.containsKey(adId)) {
            return mNewImplBannerMap.get(adId);
        } else {
            return null;
        }
    }

    public static LoopMeExpandableBannerGeneral createExpandableBanner(String appKey, Activity activity) {
        if (activity == null || TextUtils.isEmpty(appKey)) {
            return null;
        } else {
            LoopMeExpandableBannerGeneral banner = new LoopMeExpandableBannerGeneral(activity, appKey);
            mNewImplExpandableBannerMap.put(banner.getAdId(), banner);
            return banner;
        }
    }

    private static LoopMeExpandableBannerGeneral findExpandableBanner(int adId) {
        if (mNewImplExpandableBannerMap.containsKey(adId)) {
            return mNewImplExpandableBannerMap.get(adId);
        } else {
            return null;
        }
    }
    public static void removeAd(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            mNewImplInterstitialMap.remove(loopMeAd.getAdId());
            mNewImplBannerMap.remove(loopMeAd.getAdId());
            mNewImplExpandableBannerMap.remove(loopMeAd.getAdId());
        }
    }

    public static LoopMeAd getAd(Intent intent) {
        if (intent == null) {
            return null;
        }
        int adId = intent.getIntExtra(Constants.AD_ID_TAG, Constants.DEFAULT_AD_ID);
        int format = intent.getIntExtra(Constants.FORMAT_TAG, Constants.DEFAULT_AD_ID);
        if (format == Constants.AdFormat.BANNER) {
            return LoopMeAdHolder.findBanner(adId);
        } else if (format == Constants.AdFormat.EXPANDABLE_BANNER) {
            return LoopMeAdHolder.findExpandableBanner(adId);
        } else {
            return LoopMeAdHolder.findInterstitial(adId);
        }
    }
}
