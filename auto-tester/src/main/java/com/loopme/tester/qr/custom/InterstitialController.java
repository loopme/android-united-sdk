package com.loopme.tester.qr.custom;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.tester.Constants;
import com.loopme.tester.qr.listener.InterstitialListenerAdapter;
import com.loopme.tester.tracker.AppEventTracker;

public class InterstitialController {
    private Activity mActivity;
    private final Listener mListener;
    private LoopMeInterstitial mInterstitial;

    public InterstitialController(Activity activity, Listener listener) {
        mActivity = activity;
        mListener = listener;
    }

    public void destroy() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    public void show() {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    public void load(String url) {
        init();
        loadInternal(url);
    }

    private void loadInternal(String url) {
        if (mInterstitial != null && !mInterstitial.isLoading()) {
            mInterstitial.load(url);
            if (mListener != null) {
                mListener.onAdLoading();
            }
        }
    }

    private void init() {
        if (mInterstitial == null) {
            mInterstitial = new LoopMeInterstitial(mActivity, Constants.MOCK_APP_KEY);
            mInterstitial.setAutoLoading(false);
            mInterstitial.setListener(mAdListener);
            mActivity = null;
        }
    }

    public boolean isLoading() {
        return mInterstitial != null && mInterstitial.isLoading();
    }

    private InterstitialListenerAdapter mAdListener = new InterstitialListenerAdapter() {
        @Override
        public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
            show();
            track(AppEventTracker.Event.QR_SUCCESS);
        }

        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
            onAdFail(error.getMessage());
            track(AppEventTracker.Event.QR_FAIL);
        }

        @Override
        public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
            if (mListener != null) {
                mListener.onAdShow();
            }
        }

        @Override
        public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
            if (mListener != null) {
                mListener.onAdHide();
            }
        }
    };

    private void track(AppEventTracker.Event event) {
        if (mListener != null) {
            mListener.track(event);
        }
    }

    private void onAdFail(String message) {
        if (mListener != null) {
            mListener.onAdFail(message);
        }
    }

    public interface Listener {
        void onAdFail(String message);

        void onAdShow();

        void onAdHide();

        void onAdLoading();

        void track(@NonNull AppEventTracker.Event event);
    }
}
