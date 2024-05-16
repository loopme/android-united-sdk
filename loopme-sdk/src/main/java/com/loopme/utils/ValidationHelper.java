package com.loopme.utils;

import android.app.Activity;

import com.loopme.IntegrationType;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;

import java.util.Arrays;

/**
 * Created by katerina on 5/15/17.
 */

public class ValidationHelper {

    private static final String LOG_TAG = ValidationHelper.class.getSimpleName();

    public ValidationHelper() {
    }

    public static boolean isCouldLoadAd(LoopMeAd loopMeAd) {
        String error;
        if (loopMeAd == null || loopMeAd.getContext() == null || !(loopMeAd.getContext() instanceof Activity)) {
            Logging.out(LOG_TAG, "Context should not be null and should be instance of Activity");
            return false;
        }
        if (loopMeAd.isLoading() || loopMeAd.isShowing()) {
            Logging.out(LOG_TAG, "Ad is already loading or showing");
            return false;
        }
        if (!isCorrectIntegrationType(loopMeAd.getIntegrationType())) {
            error = "Incorrect integration type. Please use one from list";
            Logging.out(LOG_TAG, error);
            loopMeAd.onAdLoadFail(new LoopMeError(error));
            return false;
        }
        if (!InternetUtils.isOnline(loopMeAd.getContext())) {
            error = "No connection";
            Logging.out(LOG_TAG, error);
            loopMeAd.onAdLoadFail(new LoopMeError(error));
            return false;
        }
        if (loopMeAd.isCustomBannerHtml() || loopMeAd.isExpandBannerVideo()) {
            error = "Container size is not valid for chosen ad type";
            Logging.out(LOG_TAG, error);
            loopMeAd.onAdLoadFail(new LoopMeError(error));
            return false;
        }
        return true;
    }

    private static boolean isCorrectIntegrationType(IntegrationType integrationType) {
        return Arrays.asList(IntegrationType.values()).contains(integrationType);
    }
}
