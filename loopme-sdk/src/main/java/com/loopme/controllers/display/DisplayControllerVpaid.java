package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

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
import com.loopme.utils.IOUtils;
import com.loopme.utils.UiUtils;
import com.loopme.utils.Utils;
import com.loopme.views.LoopMeWebView;
import com.loopme.views.webclient.AdViewChromeClient;
import com.loopme.xml.Tracking;

import java.io.IOException;
import java.io.InputStream;

public class DisplayControllerVpaid extends VastVpaidBaseDisplayController implements
        BridgeEventHandler,
        VastVpaidDisplayController,
        AdViewChromeClient.OnErrorFromJsCallbackVpaid,
        Vast4WebViewClient.OnPageLoadedListener {

    private static final String LOG_TAG = DisplayControllerVpaid.class.getSimpleName();
    private static final String HTML_SOURCE_FILE = "loopmeAd.html";
    private static final String VPAID_CREATIVE_URL_STRING = "[VPAID_CREATIVE_URL]";
    private static final String ANDROID_JS_INTERFACE = "android";
    private static final int IMPRESSION_TIMER = 2000;

    private final VpaidBridge mVpaidBridge;
    private final ViewControllerVpaid mViewControllerVpaid;

    private boolean mIsWaitingForSkippableState;
    private boolean mIsWaitingForWebView;
    private boolean mIsStarted;
    private SimpleTimer mImpressionTimer;
    private volatile String mCurrentVideoTime;
    private final int mVideoDuration;
    private boolean mIsFirstLaunch = true;

    private enum CreativeType { VIDEO_MP4_WEBM, VIDEO_OTHER_TYPE, NONE_VIDEO }
    private CreativeType mCreativeType = CreativeType.NONE_VIDEO;

    public DisplayControllerVpaid(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mVpaidBridge = new VpaidBridgeImpl(this, mLoopMeAd.getAdParams(), mLoopMeAd.getAdSpotDimensions());
        mViewControllerVpaid = new ViewControllerVpaid(this);
        mVideoDuration = mAdParams.getDuration();
        mLogTag = DisplayControllerVast.class.getSimpleName();
        Logging.out(mLogTag);
    }

    //region DisplayController methods

    private StringBuilder readAssets(AssetManager assetManager, String filename) {
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(filename);
            StringBuilder out = new StringBuilder();
            byte[] bytes = new byte[4096];
            int numberBytesRead;
            while ((numberBytesRead = inputStream.read(bytes)) != -1) {
                out.append(new String(bytes, 0, numberBytesRead));
            }
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return new StringBuilder();
    }

    @Override
    public void prepare() {
        super.prepare();
        StringBuilder htmlBuilder = readAssets(getAssetsManager(), HTML_SOURCE_FILE);
        onAdInjectJsVpaid(htmlBuilder);
        String html = htmlBuilder.toString().replace(VPAID_CREATIVE_URL_STRING, mAdParams.getVpaidJsUrl());
        mIsWaitingForWebView = true;
        ((LoopMeWebView) getWebView()).loadHtml(html);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected WebView createWebView() {
        WebView wv = new LoopMeWebView(mLoopMeAd.getContext());
        wv.setWebChromeClient(new AdViewChromeClient(this));
        Vast4WebViewClient vast4WebViewClient = new Vast4WebViewClient();
        vast4WebViewClient.setOnPageLoadedListener(this);
        wv.setWebViewClient(vast4WebViewClient);
        wv.addJavascriptInterface(mVpaidBridge, ANDROID_JS_INTERFACE);
        return wv;
    }

    @Override
    public void onBuildVideoAdView(FrameLayout containerView) {
        mViewControllerVpaid.buildVideoAdView(
            containerView, getWebView(), mLoopMeAd.getContext()
        );
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
        if (mViewControllerVpaid != null)
            mViewControllerVpaid.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        callBridgeMethod(BridgeMethods.VPAID_RESUME_AD);
        if (mViewControllerVpaid != null)
            mViewControllerVpaid.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnUiThread(() -> {
            callBridgeMethod(BridgeMethods.VPAID_STOP_AD);
            destroyWebView();
        });
        if (mViewControllerVpaid != null)
            mViewControllerVpaid.destroy();
    }

    @Override
    public void onVolumeMute(boolean mute) { }

    @Override
    public void skipVideo() {
        onAdSkipped();
        mIsStarted = false;
        runOnUiThread(this::dismissAd);
    }
    //endregion

    @Override
    public void runOnUiThread(Runnable runnable) { onUiThread(runnable); }

    //region BridgeEventHandler methods
    @Override
    public void callJsMethod(final String url) {
        onUiThread(() -> {
            WebView wv = getWebView();
            if (wv != null)
                wv.loadUrl("javascript:" + url);
        });
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
        if (!mIsWaitingForSkippableState || !skippable) {
            return;
        }
        mIsWaitingForSkippableState = false;
        postVideoEvent(EventConstants.SKIP, mCurrentVideoTime);
        skipVideo();
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
        runOnUiThread(() -> DisplayControllerVpaid.super.onRedirect(finalUrl, mLoopMeAd));
    }

    @Override
    public void trackError(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
        LoopMeError error = new LoopMeError(901, "Error from vpaid js: trackError(): " + message, Constants.ErrorType.VPAID);
        onInternalLoadFail(new LoopMeError(error));
    }

    @Override
    public void postEvent(String eventType, int value) {
        for (Tracking tracking : mAdParams.getTrackingEventsList()) {
            if (tracking.isProgressEvent() && tracking.getOffset() != null) {
                int currentEventTime = mVideoDuration - value;
                int eventTime = Utils.parseDuration(tracking.getOffset());
                if (eventTime == currentEventTime) {
                    postVideoEvent(tracking.getText());
                }
            }
        }
    }

    @Override
    public void onDurationChanged() { }
    @Override
    public void onAdLinearChange() { }
    @Override
    public void onAdVolumeChange() { }

    @Override
    public void postEvent(String eventType) {
        postVideoEvent(eventType);
        if (TextUtils.equals(eventType, EventConstants.FIRST_QUARTILE) && mCreativeType == CreativeType.VIDEO_OTHER_TYPE) {
            Logging.out(LOG_TAG, "Event video 25% is posted. Dismiss extra close button timer");
            cancelExtraCloseButton();
        }
    }
    //endregion

    //region other methods

    @Override
    public void onErrorFromJs(String message) {
        Logging.out(LOG_TAG, "Error from JS " + message);
        LoopMeError error = mIsStarted ? new LoopMeError(Errors.GENERAL_VPAID_ERROR) : new LoopMeError(Errors.VPAID_FILE_NOT_FOUND);
        error.addToMessage(message);
        if (mIsStarted) {
            onPostWarning(error);
        } else {
            onInternalLoadFail(error);
        }
    }

    @Override
    public void onVideoSource(String source) { videoSourceEventOccurred(source); }

    @Override
    public void onPageLoaded() {
        if (!mIsWaitingForWebView) {
            return;
        }
        Logging.out(LOG_TAG, "Init webView done");
        callBridgeMethod(BridgeMethods.VPAID_PREPARE_AD);
        mIsWaitingForWebView = false;
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
        setVerificationView(getWebView());
        postViewableEvents(ViewableImpressionTracker.IMPRESSION_TIME_NATIVE_VIDEO);
    }


    @Override
    public void resizeAd() {
        callBridgeMethod(BridgeMethods.VPAID_RESIZE_AD);
    }

    @Override
    public void adStarted() {
        mImpressionTimer = new SimpleTimer(IMPRESSION_TIMER, this::onAdImpression);
        mImpressionTimer.start();
        if (mCreativeType != CreativeType.VIDEO_OTHER_TYPE) {
            return;
        }
        runOnUiThread(() -> {
            if (mViewControllerVpaid == null) {
                return;
            }
            int duration = mVideoDuration * 1000;
            Logging.out(LOG_TAG, "mVideoDuration " + duration);
            mViewControllerVpaid.startCloseButtonTimer(duration);
        });
    }

    @Override
    public void setVideoTime(int time) {
        mCurrentVideoTime = String.valueOf(mVideoDuration - time);
    }

    @Override
    public void onAdShake() {
        Logging.out(LOG_TAG, "onAdShake stub");
    }

    @Override
    public void setFullScreen(boolean isFullScreen) { }

    private void callBridgeMethod(BridgeMethods method) {
        onUiThread(new CallBridgeRunnable(method));
    }

    private class CallBridgeRunnable implements Runnable {
        private final BridgeMethods mMethod;
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
                    mVpaidBridge.resizeAd(Utils.getWidth(), Utils.getHeight(), "'fullscreen'");
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

        int lastIndex = source.lastIndexOf("/");
        String fileName = TextUtils.isEmpty(source) || lastIndex < 0 ? "" : source.substring(lastIndex);
        boolean isUsualFormat = fileName.contains(Constants.MP4_FORMAT_EXT) || fileName.contains(Constants.WEBM_FORMAT_EXT);
        if (isUsualFormat) {
            cancelExtraCloseButton();
            mCreativeType = CreativeType.VIDEO_MP4_WEBM;
            return;
        }
        mCreativeType = CreativeType.VIDEO_OTHER_TYPE;
        LoopMeError error = new LoopMeError(Errors.UNUSUAL_VIDEO_FORMAT);
        error.addToMessage(source);
        onPostWarning(error);
    }

    private void hideCloseButtonOnce() {
        if (!mIsFirstLaunch || mViewControllerVpaid == null) {
            return;
        }
        mViewControllerVpaid.enableCloseButton(false);
        mIsFirstLaunch = false;
    }

    private void cancelExtraCloseButton() {
        if (mViewControllerVpaid == null) {
            return;
        }
        mViewControllerVpaid.cancelCloseButtonTimer();
        mViewControllerVpaid.enableCloseButton(false);
    }
}