package com.loopme.gdpr;

import android.app.Activity;

import com.loopme.Logging;
import com.loopme.Preferences;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprChecker implements
        GdprDialog.OnGdprDialogListener,
        DntFetcher.OnDntFetcherListener,
        LoopMeGdprServiceImpl.Callback {
    private Activity mActivity;
    private OnConsentListener mListener;
    private static boolean sIsNeedCheckUserConsent = true;
    private static boolean sIsDialogWasShown = false;
    private static final String LOG_TAG = GdprChecker.class.getSimpleName();
    private String mAdvId;

    public GdprChecker(Activity activity, OnConsentListener listener) {
        this.mActivity = activity;
        mListener = listener;
    }

    public void check() {
        new DntFetcher(mActivity, this).start();
    }

    @Override
    public void onDntFetched(boolean isLimited, String advId) {
        mAdvId = advId;
        if (isLimited) {
            onComplete();
        } else {
            new LoopMeGdprServiceImpl(advId, this).start();
        }
    }

    @Override
    public void onSuccess(GdprResponse response) {
        if (response.needShowDialog()) {
            showDialogOnlyFirstTime(response);
        } else {
            saveUserConsent(response.getUserConsent());
            onComplete();
        }
        Logging.out(LOG_TAG, "need consent: " + response.getNeedConsent());
    }

    private void showDialogOnlyFirstTime(GdprResponse response) {
        if (!sIsDialogWasShown) {
            new GdprDialog(mActivity, this).show(buildConsentUrl(response));
            sIsNeedCheckUserConsent = true;
        } else {
            setGdprState(false, ConsentType.FAILED_SERVICE);
            onComplete();
        }
    }

    private String buildConsentUrl(GdprResponse response) {
        return response.getConsentUrl() + "?device_id=" + mAdvId + "&is_sdk=true";
    }

    private void saveUserConsent(int userConsent) {
        boolean isAccepted = userConsent == 1;
        setGdprState(isAccepted, ConsentType.LOOPME);
    }

    @Override
    public void onFail(String message) {
        Logging.out(LOG_TAG, message);
        setGdprState(false, ConsentType.FAILED_SERVICE);
        onComplete();
    }

    private void setGdprState(boolean isAccepted, ConsentType consentType) {
        Preferences.getInstance(mActivity).setGdprState(isAccepted, consentType);
    }

    @Override
    public void onCloseDialog() {
        if (sIsNeedCheckUserConsent) {
            sIsNeedCheckUserConsent = false;
            sIsDialogWasShown = true;
            check();
        }
    }

    private void onComplete() {
        if (mListener != null) {
            mListener.onComplete();
        }
    }

    public interface OnConsentListener {
        void onComplete();
    }
}
