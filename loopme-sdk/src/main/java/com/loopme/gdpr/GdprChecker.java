package com.loopme.gdpr;

import android.app.Activity;

import com.loopme.Logging;
import com.loopme.Preferences;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprChecker implements GdprDialog.OnGdprDialogListener, DntFetcher.OnDntFetcherListener, GdprHttpUtils.Callback {
    private Activity mActivity;
    private OnConsentListener mListener;
    private static final String LOG_TAG = GdprChecker.class.getSimpleName();

    public GdprChecker(Activity activity, OnConsentListener listener) {
        this.mActivity = activity;
        mListener = listener;
    }

    public void check() {
        new DntFetcher(mActivity, this).start();
    }

    @Override
    public void onDntFetched(boolean isLimited, String advId) {
        if (isLimited) {
            onComplete();
        } else {
            GdprHttpUtils.getInstance().setListener(this).checkNeedConsent(advId);
        }
    }

    @Override
    public void onSuccess(GdprResponse response) {
        if (response.needShowDialog()) {
            new GdprDialog().show(mActivity, this);
        } else {
            onComplete();
        }
        Logging.out(LOG_TAG, "need consent: " + response.getNeedConsent());
    }

    @Override
    public void onFail(String message) {
        Logging.out(LOG_TAG, message);
        onComplete();
    }

    @Override
    public void onGotGdprConsent(boolean isAccepted) {
        Preferences.getInstance(mActivity).setGdprState(isAccepted, ConsentType.LOOPME);
        onComplete();
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
