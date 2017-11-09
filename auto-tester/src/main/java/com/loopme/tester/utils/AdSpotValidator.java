package com.loopme.tester.utils;

import android.text.TextUtils;

import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.model.AdSpot;

import java.io.File;

public class AdSpotValidator {

    public static boolean isValidParentDirectory(File file) {
        return file != null && file.isDirectory();
    }

    public static boolean isAdSpotValid(AdSpot adSpot) {
        return adSpot != null && validateSdk(adSpot) && validateAdType(adSpot);
    }

    private static boolean validateAdType(AdSpot adSpot) {
        return adSpot != null && adSpot.getType() != null
                && (TextUtils.equals(adSpot.getType().toString(), AdType.INTERSTITIAL.toString())
                || TextUtils.equals(adSpot.getType().toString(), AdType.BANNER.toString())
                || TextUtils.equals(adSpot.getType().toString(), AdType.EXPANDABLE_BANNER.toString()));
    }

    private static boolean validateSdk(AdSpot adSpot) {
        return adSpot != null && adSpot.getSdk() != null
                && (TextUtils.equals(adSpot.getSdk().toString(), AdSdk.LMVPAID.toString())
                || TextUtils.equals(adSpot.getSdk().toString(), AdSdk.LOOPME.toString())
                || TextUtils.equals(adSpot.getSdk().toString(), AdSdk.MOPUB.toString()));
    }

}
