package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.vpaid.BridgeEventHandler;
import com.loopme.bridges.vpaid.VpaidBridge;
import com.loopme.bridges.vpaid.VpaidBridgeImpl;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.VastVpaidDisplayController;
import com.loopme.controllers.view.ViewControllerVpaid;
import com.loopme.models.BridgeMethods;
import com.loopme.models.Errors;
import com.loopme.models.Message;
import com.loopme.time.SimpleTimer;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.utils.Utils;
import com.loopme.vast.TrackingEvent;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.views.LoopMeWebView;
import com.loopme.views.webclient.AdViewChromeClient;
import com.loopme.xml.Tracking;

public class DisplayControllerVpaid extends VastVpaidBaseDisplayController implements
        BridgeEventHandler,
        VastVpaidDisplayController {

    private static final String HTML_SOURCE_FILE = "loopmeAd.html";
    private static final String VPAID_CREATIVE_URL_STRING = "[VPAID_CREATIVE_URL]";
    private static final String ANDROID_JS_INTERFACE = "android";
    private static final int IMPRESSION_TIMER = 2000;
    private static final String NO_VAST_RESPONSE = "no VAST tag response";

    private VpaidBridge mVpaidBridge;
    private ViewControllerVpaid mViewControllerVpaid;

    private volatile OnPreparedListener mOnPreparedListener;
    private LoopMeWebView mWebView;

    private boolean mIsWaitingForSkippableState;
    private boolean mIsWaitingForWebView;
    private volatile boolean mIsAdVerificationLoaded;
    private boolean mIsStarted;
    private SimpleTimer mImpressionTimer;
    private volatile String mCurrentVideoTime;
    private int mVideoDuration;

    public DisplayControllerVpaid(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mVpaidBridge = new VpaidBridgeImpl(this, mLoopMeAd.getAdParams());
        mViewControllerVpaid = new ViewControllerVpaid(this);
        VastVpaidEventTracker.addAllEvents(mLoopMeAd.getAdParams().getTrackingEventsList());
        mVideoDuration = mAdParams.getDuration();
        mLogTag = DisplayControllerVast.class.getSimpleName();
        Logging.out(mLogTag);
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        initTrackers();
    }

    //region DisplayController methods
    @Override
    public void prepare(VastVpaidDisplayController.OnPreparedListener listener) {
        mOnPreparedListener = listener;
        initWebView();
        vast4Verification(mWebView);
    }

    @Override
    public void onVast4VerificationDoesNotNeed() {
        loadHtml(prepareHtml());
    }

    private String prepareHtml() {
        String html = Utils.readAssets(getAssetsManager(), HTML_SOURCE_FILE);
        return html.replace(VPAID_CREATIVE_URL_STRING, mAdParams.getVpaidJsUrl());
    }

    @Override
    public void onBuildVideoAdView(FrameLayout containerView) {
        mViewControllerVpaid.buildVideoAdView(containerView, mWebView, mLoopMeAd.getContext());
    }

    @Override
    public void onPlay(int position) {
        mIsStarted = true;
        callBridgeMethod(BridgeMethods.VPAID_START_AD);
        onStartWebMeasuringDelayed();
    }

    @Override
    public void onPause() {
        if (mIsStarted) {
            super.onPause();
            callBridgeMethod(BridgeMethods.VPAID_PAUSE_AD);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        callBridgeMethod(BridgeMethods.VPAID_RESUME_AD);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                destroy();
            }
        });
    }

    private void destroy() {
        callBridgeMethod(BridgeMethods.VPAID_STOP_AD);
        destroyWebView();
    }

    private void destroyWebView() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }

    public View getView() {
        return mWebView;
    }

    @Override
    public void onVolumeMute(boolean mute) {
    }

    @Override
    public void skipVideo() {
        mIsStarted = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissAd();
            }
        });
    }
    //endregion

    @Override
    public void runOnUiThread(Runnable runnable) {
        onUiThread(runnable);
    }

    //region BridgeEventHandler methods
    @Override
    public void callJsMethod(final String url) {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                loadUrlToWebView(url);
            }
        });
    }

    private void loadUrlToWebView(String url) {
        if (mWebView != null) {
            mWebView.loadUrl("javascript:" + url);
        }
    }

    @Override
    public void onPrepared() {
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared();
        }
    }

    @Override
    public void onAdSkipped() {
        if (!mIsStarted) {
            return;
        }
        mIsWaitingForSkippableState = true;
        callBridgeMethod(BridgeMethods.VPAID_AD_SKIPPABLE_STATE);
    }

    @Override
    public void onAdStopped() {
        if (!mIsStarted) {
            return;
        }
        VastVpaidEventTracker.postEvent(EventConstants.CLOSE, mCurrentVideoTime);
        skipVideo();
    }

    @Override
    public void setSkippableState(boolean skippable) {
        if (!mIsStarted) {
            return;
        }
        if (mIsWaitingForSkippableState && skippable) {
            mIsWaitingForSkippableState = false;
            VastVpaidEventTracker.postEvent(EventConstants.SKIP, mCurrentVideoTime);
            skipVideo();
        }
    }

    @Override
    public boolean onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        for (String trackUrl : mAdParams.getVideoClicks()) {
            onMessage(Message.EVENT, trackUrl);
        }
        if (TextUtils.isEmpty(url)) {
            url = mAdParams.getVideoRedirectUrl();
        }
        onAdClicked();
        return super.onRedirect(url, mLoopMeAd);
    }


    @Override
    public void trackError(String message) {
        onMessage(Message.ERROR, Errors.VastError.VPAID.getValue());
        handleCriticalError(message);
    }

    private void handleCriticalError(String message) {
        if (!TextUtils.isEmpty(message) && message.equalsIgnoreCase(NO_VAST_RESPONSE)) {
            onInternalLoadFail(new LoopMeError(message));
        }
    }

    @Override
    public void postEvent(String eventType, int value) {
        for (Tracking tracking : mAdParams.getTrackingEventsList()) {
            TrackingEvent event = new TrackingEvent(tracking.getText());
            if (tracking.getEvent().equalsIgnoreCase(EventConstants.PROGRESS)) {
                if (tracking.getOffset() == null) {
                    continue;
                }
                int sendEventTime = mVideoDuration - value;
                if (Utils.parseDuration(tracking.getOffset()) == sendEventTime) {
                    onMessage(Message.EVENT, event.url);
                }
            }
        }
    }

    @Override
    public void onDurationChanged() {

    }

    @Override
    public void onAdLinearChange() {

    }

    @Override
    public void onAdVolumeChange() {

    }

    @Override
    public void postEvent(String eventType) {
        onMessage(Message.EVENT, eventType);
    }
    //endregion

    //region other methods
    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = new LoopMeWebView(mLoopMeAd.getContext());
        mWebView.setWebChromeClient(new AdViewChromeClient(initOnErrorFromJsListener()));
        mWebView.setOnPageLoadedCallback(initOnPageLoadedCallback());
        mWebView.setWebViewClient(mWebView.getVpaidWebViewClient());
        mIsWaitingForWebView = true;
        mWebView.addJavascriptInterface(mVpaidBridge, ANDROID_JS_INTERFACE);
    }

    private AdViewChromeClient.OnErrorFromJsCallback initOnErrorFromJsListener() {
        return new AdViewChromeClient.OnErrorFromJsCallback() {

            @Override
            public void onErrorFromJs(String message) {
                if (isVast4VerificationNeeded() && !mIsAdVerificationLoaded) {
                    DisplayControllerVpaid.this.onPostWarning(Errors.VERIFICATION_UNIT_NOT_EXECUTED);
                } else {
                    onPostWarning(Errors.GENERAL_VPAID_ERROR);
                }
            }
        };
    }

    private LoopMeWebView.OnPageLoadedCallback initOnPageLoadedCallback() {
        return new LoopMeWebView.OnPageLoadedCallback() {
            @Override
            public void onPageLoaded() {
                if (mIsWaitingForWebView) {
                    if (isVast4VerificationNeeded() && !mIsAdVerificationLoaded) {
                        loadHtml(prepareHtml());
                        mIsAdVerificationLoaded = true;
                    } else {
                        onPageFinished();
                        mIsWaitingForWebView = false;
                    }
                }
            }
        };
    }

    private void loadHtml(String html) {
        if (mWebView != null) {
            mWebView.loadHtml(html);
        }
    }

    private void onPageFinished() {
        callBridgeMethod(BridgeMethods.VPAID_PREPARE_AD);
    }

    @Override
    public void closeSelf() {
        mIsWaitingForWebView = false;
        callBridgeMethod(BridgeMethods.VPAID_STOP_AD);
        dismissAd();
    }


    //endregion
    @Override
    public void onAdImpression() {
        mImpressionTimer.cancel();
        for (String url : mAdParams.getImpressionsList()) {
            onMessage(Message.EVENT, url);
            onMessage(Message.LOG, "mAdParams.getImpressionsList() " + url);
        }

        setVerificationView(mWebView);
        postViewableEvents(Vast4Tracker.IMPRESSION_TIME_NATIVE_VIDEO);
    }


    @Override
    public void resizeAd() {
        callBridgeMethod(BridgeMethods.VPAID_RESIZE_AD);
    }

    @Override
    public void adStarted() {
        mImpressionTimer = new SimpleTimer(IMPRESSION_TIMER, new SimpleTimer.Listener() {
            @Override
            public void onFinish() {
                onAdImpression();
            }
        });
        mImpressionTimer.start();
    }

    @Override
    public void setVideoTime(int time) {
        mCurrentVideoTime = String.valueOf(mVideoDuration - time);
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public void onAdShake() {

    }

    @Override
    public void setFullScreen(boolean isFullScreen) {

    }

    private void callBridgeMethod(BridgeMethods method) {
        onUiThread(new CallBridgeRunnable(method));
    }

    private class CallBridgeRunnable implements Runnable {
        private BridgeMethods mMethod;

        private CallBridgeRunnable(BridgeMethods method) {
            mMethod = method;
        }

        @Override
        public void run() {
            if (mVpaidBridge == null) {
                return;
            }
            switch (mMethod) {
                case VPAID_PREPARE_AD: {
                    mVpaidBridge.prepare();
                    break;
                }
                case VPAID_START_AD: {
                    mVpaidBridge.startAd();
                    break;
                }
                case VPAID_PAUSE_AD: {
                    mVpaidBridge.pauseAd();
                    break;
                }
                case VPAID_RESUME_AD: {
                    mVpaidBridge.resumeAd();
                    break;
                }
                case VPAID_RESIZE_AD: {
                    mVpaidBridge.resizeAd(Utils.getWidth(),
                            Utils.getHeight(), "\'fullscreen\'");
                    break;
                }
                case VPAID_STOP_AD: {
                    mVpaidBridge.stopAd();
                    break;
                }
                case VPAID_AD_SKIPPABLE_STATE: {
                    mVpaidBridge.getAdSkippableState();
                    break;
                }
            }
        }
    }
}