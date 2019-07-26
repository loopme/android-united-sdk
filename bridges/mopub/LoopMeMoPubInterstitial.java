package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.loopme.IntegrationType;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

public class LoopMeMoPubInterstitial extends CustomEventInterstitial implements LoopMeInterstitial.Listener {

    private static final String LOG_TAG = LoopMeMoPubInterstitial.class.getSimpleName();

    private LoopMeInterstitial mInterstitial;

    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    public void loadInterstitial(Context context,
                                 CustomEventInterstitialListener mInterstitialListener,
                                 Map<String, Object> localExtras, Map<String, String> serverExtras) {

        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, LOG_TAG, "Bridge loadInterstitial");

        this.mInterstitialListener = mInterstitialListener;
        Activity mActivity = null;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            // You may also pass in an Activity Context in the localExtras map and retrieve it here.
        }

        String mLoopMeAppId = serverExtras.get("app_key");

        mInterstitial = LoopMeInterstitial.getInstance(mLoopMeAppId, mActivity);
        mInterstitial.setListener(this);
        mInterstitial.load(IntegrationType.MOPUB);
    }

    @Override
    public void showInterstitial() {
        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, LOG_TAG, "Bridge showInterstitial");
        if (mInterstitial.isReady()) {
            mInterstitial.show();
        }
    }

    @Override
    public void onInvalidate() {
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial arg0) {
        mInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial arg0) {
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial arg0) {
        mInterstitialListener.onInterstitialDismissed();
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial arg0) {
        mInterstitialListener.onLeaveApplication();
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial arg0) {
        mInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial loopMeInterstitial, LoopMeError i) {
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial arg0) {
        mInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
    }
}