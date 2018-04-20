package com.loopme.controllers.display;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.VastVpaidDisplayController;
import com.loopme.loaders.VastVpaidAssetsResolver;
import com.loopme.models.Errors;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.vast.VastVpaidEventTracker;

import java.util.Observable;
import java.util.Observer;

public abstract class VastVpaidBaseDisplayController extends BaseTrackableController
        implements VastVpaidDisplayController, Observer {
    private static final String LOG_TAG = VastVpaidBaseDisplayController.class.getSimpleName();
    protected String mVideoUri;
    protected String mImageUri;
    protected AdParams mAdParams;
    protected LoopMeAd mLoopMeAd;
    private Timers mTimer;
    private Context mContext;
    private Vast4Tracker mVast4Tracker;
    private final VastVpaidAssetsResolver mVastVpaidAssetsResolver;
    private VastVpaidEventTracker mEventTracker;
    private boolean mIsVpaidEventTracked;

    public VastVpaidBaseDisplayController(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mLoopMeAd = loopMeAd;
        mAdParams = loopMeAd.getAdParams();
        mContext = loopMeAd.getContext();
        mVastVpaidAssetsResolver = new VastVpaidAssetsResolver();
        mTimer = new Timers(this);
        LoopMeTracker.initVastErrorUrl(mAdParams.getErrorUrlList());
        mEventTracker = new VastVpaidEventTracker(mLoopMeAd.getAdParams().getTrackingEventsList());
    }

    public void postVideoEvent(String event, String addMessage) {
        if (mEventTracker != null) {
            mEventTracker.postEvent(event, addMessage);
        }
    }

    public void postVideoEvent(String eventsUrlOrType) {
        postVideoEvent(eventsUrlOrType, "");
        postVpaidDidReachEnd(eventsUrlOrType);
    }

    protected void postVideoClicks(String currentPosition) {
        for (String trackUrl : mAdParams.getVideoClicks()) {
            postVideoEvent(trackUrl, currentPosition);
        }
    }

    protected void postEndCardClicks() {
        for (String trackUrl : mAdParams.getEndCardClicks()) {
            postVideoEvent(trackUrl);
        }
    }

    protected void onEndCardAppears() {
        for (String trackUrl : mAdParams.getCompanionCreativeViewEvents()) {
            postVideoEvent(trackUrl);
        }
    }

    private void postVpaidDidReachEnd(String eventType) {
        if (!mIsVpaidEventTracked) {
            if (this instanceof DisplayControllerVpaid && TextUtils.equals(eventType, EventConstants.COMPLETE)) {
                onAdVideoDidReachEnd();
                mIsVpaidEventTracked = true;
            }
        }
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        startTimer(TimersType.PREPARE_ASSETS_TIMER);
        loadAssets();
    }

    protected void loadAssets() {
        mVastVpaidAssetsResolver.resolve(mAdParams, mContext, createAssetsLoadListener());
    }

    protected void vast4Verification() {
        if (isVast4VerificationNeeded()) {
            mVast4Tracker.loadVerificationJavaScripts();
        }
    }

    protected void initVast4Tracker(WebView webView) {
        if (isVast4VerificationNeeded() && mVast4Tracker == null) {
            mVast4Tracker = new Vast4Tracker(mLoopMeAd.getAdParams(), webView);
        }
    }

    protected String addVerificationScripts(String html) {
        return mVast4Tracker != null ? mVast4Tracker.addVerificationScripts(html) : html;
    }

    protected void setVerificationView(View view) {
        if (mVast4Tracker != null) {
            mVast4Tracker.setAdView(view);
        }
    }

    protected void postViewableEvents(int doneMillis) {
        if (mVast4Tracker != null) {
            mVast4Tracker.postViewableEvents(doneMillis);
        }
    }

    protected boolean isVast4VerificationNeeded() {
        return mAdParams != null && mAdParams.isVast4VerificationNeeded();
    }

    private VastVpaidAssetsResolver.OnAssetsLoaded createAssetsLoadListener() {
        return new VastVpaidAssetsResolver.OnAssetsLoaded() {
            @Override
            public void onAssetsLoaded(String videoFilePath, String endCardFilePath) {
                mVideoUri = videoFilePath;
                mImageUri = endCardFilePath;
                stopTimer(TimersType.PREPARE_ASSETS_TIMER);
                prepareAd();
            }

            @Override
            public void onError(LoopMeError info) {
                onInternalLoadFail(info);
                stopTimer(TimersType.PREPARE_ASSETS_TIMER);
            }

            @Override
            public void onPostWarning(LoopMeError error) {
                postWarning(error);
            }
        };
    }

    private void postWarning(LoopMeError error) {
        if (mLoopMeAd != null) {
            mLoopMeAd.onSendPostWarning(error);
        }
    }

    private void prepareAd() {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                startTimer(TimersType.PREPARE_VPAID_JS_TIMER);
                prepare();
            }
        });
    }

    @Override
    public void closeSelf() {
        onAdUserCloseEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyTimers();
        breakAssetsLoading();
        LoopMeTracker.clear();
    }

    protected void onAdLoadSuccess() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.onAdLoadSuccess();
                }
            });
        }
    }

    protected void dismissAd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.dismiss();
                }
            });
        }
    }

    protected void onAdClicked() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.onAdClicked();
                }
            });
        }
    }

    protected void onAdVideoDidReachEnd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoopMeAd.onAdVideoDidReachEnd();
                }
            });
        }
    }

    protected AssetManager getAssetsManager() {
        return mContext.getAssets();
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable != null && observable instanceof Timers
                && arg != null && arg instanceof TimersType) {
            switch ((TimersType) arg) {
                case PREPARE_VPAID_JS_TIMER: {
                    onPrepareJsTimeout();
                    break;
                }
                case PREPARE_ASSETS_TIMER: {
                    onPrepareAssetsTimeout();
                    break;
                }
            }
        }
    }

    private void onPrepareAssetsTimeout() {
        stopTimer(TimersType.PREPARE_ASSETS_TIMER);
        breakAssetsLoading();
        onInternalLoadFail(Errors.TIMEOUT_ON_MEDIA_FILE_URI);
    }

    private void onPrepareJsTimeout() {
        stopTimer(TimersType.PREPARE_VPAID_JS_TIMER);
        Logging.out(LOG_TAG, "Js loading timeout");
        if (this instanceof DisplayControllerVpaid) {
            onInternalLoadFail(Errors.JS_LOADING_TIMEOUT);
        }
    }

    private void breakAssetsLoading() {
        if (mVastVpaidAssetsResolver != null) {
            mVastVpaidAssetsResolver.stop();
        }
    }

    private void destroyTimers() {
        if (mTimer != null) {
            mTimer.destroy();
            mTimer = null;
        }
    }

    @Override
    public boolean isFullScreen() {
        return mLoopMeAd != null && mLoopMeAd.isInterstitial();
    }


    private void startTimer(TimersType timersType) {
        if (mTimer != null) {
            mTimer.startTimer(timersType);
        }
    }

    private void stopTimer(TimersType timersType) {
        if (mTimer != null) {
            mTimer.stopTimer(timersType);
        }
    }

    protected void onAdReady() {
        stopTimer(TimersType.PREPARE_VPAID_JS_TIMER);
        onAdLoadSuccess();
    }
}