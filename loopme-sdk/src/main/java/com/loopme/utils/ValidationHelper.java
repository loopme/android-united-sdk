package com.loopme.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.IntegrationType;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by katerina on 5/15/17.
 */

public class ValidationHelper {

    private static final String LOG_TAG = ValidationHelper.class.getSimpleName();
    private OnValidationHelperListener mOnValidationHelperListener;

    public ValidationHelper(OnValidationHelperListener onValidationHelperListener) {
        this.mOnValidationHelperListener = onValidationHelperListener;
    }

    public interface OnValidationHelperListener {
        void onError(LoopMeError error);

        void onSuccess();
    }

    public static boolean isValidResponse(JSONObject result) {
        return !TextUtils.isEmpty(result.toString());
    }

    public static boolean isValidFormat(String format) {
        return format != null
                && (format.equalsIgnoreCase(Constants.BANNER_TAG)
                || format.equalsIgnoreCase(Constants.INTERSTITIAL_TAG));
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
        } else if (!InternetUtils.isOnline(loopMeAd.getContext())) {
            error = "No connection";
            Logging.out(LOG_TAG, error);
            loopMeAd.onAdLoadFail(new LoopMeError(error));
            return false;
        } else if (loopMeAd.isCustomBannerHtml() || loopMeAd.isExpandBannerVideo()) {
            error = "Container size is not valid for chosen ad type";
            Logging.out(LOG_TAG, error);
            loopMeAd.onAdLoadFail(new LoopMeError(error));
            return false;
        }
        return true;
    }

    private void onSuccess() {
        if (mOnValidationHelperListener != null) {
            mOnValidationHelperListener.onSuccess();
        }
    }

    private void onError(LoopMeError error) {
        if (mOnValidationHelperListener != null) {
            mOnValidationHelperListener.onError(error);
        }
    }

    private static boolean isCorrectIntegrationType(IntegrationType integrationType) {
        return Arrays.asList(IntegrationType.values()).contains(integrationType);
    }
}
