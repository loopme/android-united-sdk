package com.loopme.controllers.display;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.CreativeParams;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.MraidBridge;
import com.loopme.bridges.vpaid.BridgeEventHandler;
import com.loopme.bridges.vpaid.VpaidBridge;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.MraidController;
import com.loopme.controllers.interfaces.VastVpaidDisplayController;
import com.loopme.controllers.view.ViewControllerVpaid;
import com.loopme.models.Errors;
import com.loopme.time.SimpleTimer;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.utils.UiUtils;
import com.loopme.utils.Utils;
import com.loopme.views.MraidView;
import com.loopme.views.webclient.AdViewChromeClient;
import com.loopme.xml.Tracking;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

public class DisplayControllerVpaid extends VastVpaidBaseDisplayController implements
        BridgeEventHandler,
        VastVpaidDisplayController,
        AdViewChromeClient.OnErrorFromJsCallbackVpaid,
        Vast4WebViewClient.OnPageLoadedListener, Observer {

    private static final String LOG_TAG = DisplayControllerVpaid.class.getSimpleName();

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

    private Timers mTimer;

    public DisplayControllerVpaid(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mTimer = new Timers(this);
        mVpaidBridge = new VpaidBridge(
            this,
            new CreativeParams(mLoopMeAd.getAdSpotDimensions(), mLoopMeAd.getAdParams().getAdParams())
        );
        mViewControllerVpaid = new ViewControllerVpaid(this);
        mVideoDuration = mAdParams.getDuration();
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (!(observable instanceof Timers) || !(arg instanceof TimersType)) {
            return;
        }
        if (arg == TimersType.PREPARE_VPAID_JS_TIMER) {
            if (mTimer != null) mTimer.stopTimer(TimersType.PREPARE_VPAID_JS_TIMER);
            mLoopMeAd.onInternalLoadFail(Errors.JS_LOADING_TIMEOUT);
        }
    }

    //region DisplayController methods

    private StringBuilder readAssets(AssetManager assetManager) {
        try (InputStream inputStream = assetManager.open("loopmeAd.html")) {
            StringBuilder out = new StringBuilder();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.append(new String(buffer, 0, bytesRead));
            }
            return out;
        } catch (IOException ignored) {
            return new StringBuilder();
        }
    }

    @Override
    public void prepare() {
        super.prepare();
        if (mTimer != null) mTimer.startTimer(TimersType.PREPARE_VPAID_JS_TIMER);
        StringBuilder htmlBuilder = readAssets(mContext.getAssets());
        onAdInjectJsVpaid(htmlBuilder);
        mIsWaitingForWebView = true;
        ((MraidView) getWebView()).loadHtml(
            htmlBuilder.toString().replace("[VPAID_CREATIVE_URL]", mAdParams.getVpaidJsUrl())
        );
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected WebView createWebView() {
        MraidView wv = new MraidView(mLoopMeAd.getContext(), mLoopMeAd);
        MraidController mraidController = new MraidController(mLoopMeAd, wv);
        wv.setWebViewClient(new MraidBridge(mraidController, () -> { }));
        wv.setWebChromeClient(new AdViewChromeClient(this, mLoopMeAd));
        Vast4WebViewClient vast4WebViewClient = new Vast4WebViewClient();
        vast4WebViewClient.setOnPageLoadedListener(this);
        wv.setWebViewClient(vast4WebViewClient);
        wv.addJavascriptInterface(mVpaidBridge, "android");
        return wv;
    }

    @Override
    public void onBuildVideoAdView(FrameLayout containerView) {
        mViewControllerVpaid.buildVideoAdView(containerView, getWebView(), mLoopMeAd.getContext());
    }

    @Override
    public void onPlay(int position) {
        mIsStarted = true;
        runOnUiThread(mVpaidBridge::startAd);
        onStartWebMeasuringDelayed();
        onAdResumedEvent();
    }

    @Override
    public void onPause() {
        if (mIsStarted) {
            super.onPause();
            runOnUiThread(mVpaidBridge::pauseAd);
        }
        mViewControllerVpaid.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        runOnUiThread(mVpaidBridge::resumeAd);
        mViewControllerVpaid.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.destroy();
            mTimer = null;
        }
        runOnUiThread(() -> {
            mVpaidBridge.stopAd();
            destroyWebView();
        });
        mViewControllerVpaid.destroy();
    }

    @Override
    public void onAdReady() {
        super.onAdReady();
        if (mTimer != null) mTimer.stopTimer(TimersType.PREPARE_VPAID_JS_TIMER);
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
    public void runOnUiThread(Runnable runnable) { mLoopMeAd.runOnUiThread(runnable); }

    //region BridgeEventHandler methods
    @Override
    public void callJsMethod(final String url) {
        runOnUiThread(() -> {
            WebView wv = getWebView();
            if (wv != null)
                wv.loadUrl("javascript:" + url);
        });
    }

    @Override
    public void onPrepared() {
        Logging.out(LOG_TAG, "vpaidAdLoaded");
        onAdReady();
    }

    @Override
    public void onAdSkipped() {
        Logging.out(LOG_TAG, "vpaidAdSkipped");
        if (!mIsStarted) return;
        mIsWaitingForSkippableState = true;
        runOnUiThread(mVpaidBridge::getAdSkippableState);
    }

    @Override
    public void onAdStopped() {
        Logging.out(LOG_TAG, "vpaidAdStopped");
        if (!mIsStarted) return;
        runOnUiThread(() -> {
            postVideoEvent(EventConstants.CLOSE, mCurrentVideoTime);
            skipVideo();
        });
    }

    @Override
    public void setSkippableState(boolean skippable) {
        Logging.out(LOG_TAG, "vpaidAdSetSkippableState: " + skippable);
        if (!mIsStarted) return;
        if (!mIsWaitingForSkippableState || !skippable) return;
        mIsWaitingForSkippableState = false;
        postVideoEvent(EventConstants.SKIP, mCurrentVideoTime);
        skipVideo();
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        Logging.out(LOG_TAG, "vpaidAdClickThru " + url);
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
        Logging.out(LOG_TAG, "vpaidAdError " + message);
        if (TextUtils.isEmpty(message)) return;
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
        LoopMeError error = new LoopMeError(901, "Error from vpaid js: trackError(): " + message, Constants.ErrorType.VPAID);
        mLoopMeAd.onInternalLoadFail(error);
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
    public void onDurationChanged() { Logging.out(LOG_TAG, "vpaidAdDurationChanged"); }
    @Override
    public void onAdLinearChange() { Logging.out(LOG_TAG, "vpaidAdLinearChange"); }
    @Override
    public void onAdVolumeChange() { Logging.out(LOG_TAG, "vpaidAdVolumeChanged"); }

    @Override
    public void postEvent(String eventType) {
        Logging.out(LOG_TAG, "vpaidAd: " + eventType);
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
            mLoopMeAd.onSendPostWarning(error);
        } else {
            mLoopMeAd.onInternalLoadFail(error);
        }
    }

    @Override
    public void onVideoSource(String source) {
        Logging.out(LOG_TAG, "Video source event received");
        if (mIsFirstLaunch) {
            mViewControllerVpaid.enableCloseButton(false);
            mIsFirstLaunch = false;
        }

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
        mLoopMeAd.onSendPostWarning(error);
    }

    @Override
    public void onPageLoaded() {
        if (!mIsWaitingForWebView) return;
        Logging.out(LOG_TAG, "Init webView done");
        runOnUiThread(mVpaidBridge::prepare);
        mIsWaitingForWebView = false;
    }

    @Override
    public void closeSelf() {
        mIsWaitingForWebView = false;
        runOnUiThread(mVpaidBridge::stopAd);
        dismissAd();
    }

    //endregion
    @Override
    public void onAdImpression() {
        Logging.out(LOG_TAG, "vpaidAdImpression");
        mImpressionTimer.stop();
        for (String url : mAdParams.getImpressionsList()) {
            postVideoEvent(url);
            Logging.out(LOG_TAG, "mAdParams.getImpressionsList() " + url);
        }
        setVerificationView(getWebView());
        postViewableEvents(ViewableImpressionTracker.IMPRESSION_TIME_NATIVE_VIDEO);
    }

    @Override
    public void resizeAd() {
        runOnUiThread(() -> mVpaidBridge.resizeAd(
            Utils.getScreenWidthInDp(), Utils.getScreenHeightInDp(), "'fullscreen'"
        ));
    }

    @Override
    public void adStarted() {
        Logging.out(LOG_TAG, "vpaidAdStarted");
        mImpressionTimer = new SimpleTimer(2000, this::onAdImpression);
        mImpressionTimer.start();
        if (mCreativeType != CreativeType.VIDEO_OTHER_TYPE) {
            return;
        }
        runOnUiThread(() -> {
            int duration = mVideoDuration * 1000;
            Logging.out(LOG_TAG, "mVideoDuration " + duration);
            mViewControllerVpaid.startCloseButtonTimer(duration);
        });
    }

    @Override
    public void setVideoTime(int time) {
        mCurrentVideoTime = String.valueOf(mVideoDuration - time);
    }

    private void cancelExtraCloseButton() {
        mViewControllerVpaid.cancelCloseButtonTimer();
        mViewControllerVpaid.enableCloseButton(false);
    }
}