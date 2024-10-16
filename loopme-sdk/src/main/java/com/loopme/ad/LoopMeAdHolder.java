package com.loopme.ad;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.LoopMeInterstitialGeneral;

import java.util.HashMap;
import java.util.Map;

public class LoopMeAdHolder {

    private static final Map<Integer, LoopMeInterstitialGeneral> mNewImplInterstitialMap = new HashMap<>();
    private static final Map<Integer, LoopMeBannerGeneral> mNewImplBannerMap = new HashMap<>();

    private LoopMeAdHolder() { }

    public static void putAd(LoopMeAd loopMeAd) {
        int id = loopMeAd.getAdId();
        if (loopMeAd.getAdFormat() == Constants.AdFormat.INTERSTITIAL) {
            mNewImplInterstitialMap.put(id, (LoopMeInterstitialGeneral) loopMeAd);
        } else {
            mNewImplBannerMap.put(id, (LoopMeBannerGeneral) loopMeAd);
        }
    }

    public static void removeAd(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            return;
        }
        mNewImplInterstitialMap.remove(loopMeAd.getAdId());
        mNewImplBannerMap.remove(loopMeAd.getAdId());
    }

    public static LoopMeAd getAd(@NonNull Intent intent) {
        int adId = intent.getIntExtra(Constants.AD_ID_TAG, Constants.DEFAULT_AD_ID);
        int format = intent.getIntExtra(Constants.FORMAT_TAG, Constants.DEFAULT_AD_ID);
        if (Constants.AdFormat.fromInt(format) == Constants.AdFormat.BANNER) {
            return mNewImplBannerMap.get(adId);
        } else {
            return mNewImplInterstitialMap.get(adId);
        }
    }
}
