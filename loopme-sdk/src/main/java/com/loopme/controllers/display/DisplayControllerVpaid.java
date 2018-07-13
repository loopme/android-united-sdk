package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.loopme.Constants;
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
import com.loopme.utils.UiUtils;
import com.loopme.utils.Utils;
import com.loopme.views.LoopMeWebView;
import com.loopme.views.webclient.AdViewChromeClient;
import com.loopme.xml.Tracking;

public class DisplayControllerVpaid extends VastVpaidBaseDisplayController implements
        BridgeEventHandler,
        VastVpaidDisplayController,
        AdViewChromeClient.OnErrorFromJsCallbackVpaid,
        Vast4Tracker.OnAdVerificationListener,
        Vast4WebViewClient.OnPageLoadedListener {

    private static final String LOG_TAG = DisplayControllerVpaid.class.getSimpleName();
    private static final double VIDEO_25_WITH_SPARE_TIME_COEFFICIENT = 0.35;
    private static final String HTML_SOURCE_FILE = "loopmeAd.html";
    private static final String VPAID_CREATIVE_URL_STRING = "[VPAID_CREATIVE_URL]";
    private static final String ANDROID_JS_INTERFACE = "android";
    private static final int IMPRESSION_TIMER = 2000;

    private VpaidBridge mVpaidBridge;
    private ViewControllerVpaid mViewControllerVpaid;

    private LoopMeWebView mWebView;

    private boolean mIsWaitingForSkippableState;
    private boolean mIsWaitingForWebView;
    private boolean mIsStarted;
    private SimpleTimer mImpressionTimer;
    private volatile String mCurrentVideoTime;
    private int mVideoDuration;
    private boolean mIsFirstLaunch = true;
    private CreativeType mCreativeType = CreativeType.NONE_VIDEO;

    public DisplayControllerVpaid(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mVpaidBridge = new VpaidBridgeImpl(this, mLoopMeAd.getAdParams(), mLoopMeAd.getAdSpotDimensions());
        mViewControllerVpaid = new ViewControllerVpaid(this);
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
    public void prepare() {
        initWebView();
        onAdRegisterView(mLoopMeAd.getContext(), mWebView);
        initVast4Tracker(mWebView);
        loadHtml(prepareHtml());
    }

    private String prepareHtml() {
        StringBuilder htmlBuilder = Utils.readAssets(getAssetsManager(), HTML_SOURCE_FILE);
        addVerificationScripts(htmlBuilder);
        onAdInjectJsVpaid(htmlBuilder);
        return htmlBuilder.toString().replace(VPAID_CREATIVE_URL_STRING, mAdParams.getVpaidJsUrl());
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
        onAdResumedEvent();
    }

    @Override
    public void onPause() {
        if (mIsStarted) {
            super.onPause();
            callBridgeMethod(BridgeMethods.VPAID_PAUSE_AD);
        }
        pauseViewController();
    }

    @Override
    public void onResume() {
        super.onResume();
        callBridgeMethod(BridgeMethods.VPAID_RESUME_AD);
        resumeViewController();
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
        destroyViewController();
    }

    private void pauseViewController() {
        if (mViewControllerVpaid != null) {
            mViewControllerVpaid.pause();
        }
    }

    private void resumeViewController() {
        if (mViewControllerVpaid != null) {
            mViewControllerVpaid.resume();
        }
    }

    private void destroyViewController() {
        if (mViewControllerVpaid != null) {
            mViewControllerVpaid.destroy();
        }
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
        onAdSkipped();
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
        onAdReady();
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
        postVideoEvent(EventConstants.CLOSE, mCurrentVideoTime);
        skipVideo();
    }

    @Override
    public void setSkippableState(boolean skippable) {
        if (!mIsStarted) {
            return;
        }
        if (mIsWaitingForSkippableState && skippable) {
            mIsWaitingForSkippableState = false;
            postVideoEvent(EventConstants.SKIP, mCurrentVideoTime);
            skipVideo();
        }
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        for (String trackUrl : mAdParams.getVideoClicks()) {
            postVideoEvent(trackUrl);
        }
        if (TextUtils.isEmpty(url)) {
            url = mAdParams.getVideoRedirectUrl();
        }
        onAdClicked();
        final String finalUrl = url;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DisplayControllerVpaid.super.onRedirect(finalUrl, mLoopMeAd);
            }
        });
    }

    @Override
    public void trackError(String message) {
        if (!TextUtils.isEmpty(message)) {
            UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
            LoopMeError error = new LoopMeError(901, "Error from vpaid js: trackError(): " + message, Constants.ErrorType.VPAID);
            onInternalLoadFail(new LoopMeError(error));
        }
    }

    @Override
    public void postEvent(String eventType, int value) {
        for (Tracking tracking : mAdParams.getTrackingEventsList()) {
            if (tracking.isProgressEvent()) {
                if (tracking.getOffset() != null) {
                    int currentEventTime = mVideoDuration - value;
                    int eventTime = Utils.parseDuration(tracking.getOffset());
                    if (eventTime == currentEventTime) {
                        postVideoEvent(tracking.getText());
                    }
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
        postVideoEvent(eventType);
        cancelExtraCloseButtonIfFirstQuartile(eventType);
    }
    //endregion

    //region other methods
    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        mWebView = new LoopMeWebView(mLoopMeAd.getContext());
        mWebView.setWebChromeClient(new AdViewChromeClient(this));
        mWebView.setWebViewClient(initVast4WebViewClient());
        mIsWaitingForWebView = true;
        mWebView.addJavascriptInterface(mVpaidBridge, ANDROID_JS_INTERFACE);
    }

    private WebViewClient initVast4WebViewClient() {
        Vast4WebViewClient vast4WebViewClient = new Vast4WebViewClient(this);
        vast4WebViewClient.setOnPageLoadedListener(this);
        return vast4WebViewClient;
    }

    @Override
    public void onErrorFromJs(String message) {
        postVpaidError(message);
    }

    @Override
    public void onVideoSource(String source) {
        videoSourceEventOccurred(source);
    }

    @Override
    public void onVerificationJsLoaded() {
        Logging.out(LOG_TAG, "verification script successfully loaded");
    }

    @Override
    public void onVerificationJsFailed(String message) {
        LoopMeError error = new LoopMeError(Errors.VERIFICATION_UNIT_NOT_EXECUTED);
        error.addToMessage(message);
        onPostWarning(error);
    }

    @Override
    public void onPageLoaded() {
        if (mIsWaitingForWebView) {
            Logging.out(LOG_TAG, "Init webView done");
            callBridgeMethod(BridgeMethods.VPAID_PREPARE_AD);
            mIsWaitingForWebView = false;
        }
    }

    private void postVpaidError(String message) {
        Logging.out(LOG_TAG, "Error from JS " + message);
        LoopMeError error = mIsStarted ? new LoopMeError(Errors.GENERAL_VPAID_ERROR) : new LoopMeError(Errors.VPAID_FILE_NOT_FOUND);
        error.addToMessage(message);

        if (mIsStarted) {
            onPostWarning(error);
        } else {
            onInternalLoadFail(error);
        }
    }

    private void loadHtml(String html) {
        if (mWebView != null) {
            mWebView.loadHtml(html);
        }
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
        mImpressionTimer.stop();
        for (String url : mAdParams.getImpressionsList()) {
            postVideoEvent(url);
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

        startCloseButtonTimerOnUiThread();
    }

    private void startCloseButtonTimerOnUiThread() {
        if (mCreativeType == CreativeType.VIDEO_OTHER_TYPE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int duration = mVideoDuration * 1000;
                    Logging.out(LOG_TAG, "mVideoDuration " + duration);
                    if (mViewControllerVpaid != null) {
                        mViewControllerVpaid.startCloseButtonTimer(duration);
                    }
                }
            });
        }
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
        Logging.out(LOG_TAG, "onAdShake stub");
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

    private void videoSourceEventOccurred(String source) {
        Logging.out(LOG_TAG, "Video source event received");
        hideCloseButtonOnce();
        checkVideoFormat(source);
    }

    private void hideCloseButtonOnce() {
        if (mIsFirstLaunch && mViewControllerVpaid != null) {
            mViewControllerVpaid.enableCloseButton(false);
            mIsFirstLaunch = false;
        }
    }

    private void checkVideoFormat(String source) {
        if (Utils.isUsualFormat(source)) {
            cancelExtraCloseButton();
            mCreativeType = CreativeType.VIDEO_MP4_WEBM;
        } else {
            mCreativeType = CreativeType.VIDEO_OTHER_TYPE;
            LoopMeError error = new LoopMeError(Errors.UNUSUAL_VIDEO_FORMAT);
            error.addToMessage(source);
            onPostWarning(error);
        }
    }

    private void cancelExtraCloseButtonIfFirstQuartile(String eventType) {
        if (TextUtils.equals(eventType, EventConstants.FIRST_QUARTILE) && mCreativeType == CreativeType.VIDEO_OTHER_TYPE) {
            Logging.out(LOG_TAG, "Event video 25% is posted. Dismiss extra close button timer");
            cancelExtraCloseButton();
        }
    }

    private void cancelExtraCloseButton() {
        if (mViewControllerVpaid != null) {
            mViewControllerVpaid.cancelCloseButtonTimer();
            mViewControllerVpaid.enableCloseButton(false);
        }
    }

    private enum CreativeType {
        VIDEO_MP4_WEBM, VIDEO_OTHER_TYPE, NONE_VIDEO
    }
}