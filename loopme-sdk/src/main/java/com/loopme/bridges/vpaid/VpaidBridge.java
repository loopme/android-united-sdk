package com.loopme.bridges.vpaid;

import android.webkit.JavascriptInterface;

import androidx.annotation.Keep;

import com.loopme.CreativeParams;
import com.loopme.Logging;
import com.loopme.tracker.constants.EventConstants;

import java.util.Locale;

@Keep
@SuppressWarnings("unused")
public class VpaidBridge {
    private static final String LOG_TAG = VpaidBridge.class.getSimpleName();
    private final BridgeEventHandler mBridge;
    private final CreativeParams mCreativeParams;
    private volatile int mPreviousValue;

    public VpaidBridge(BridgeEventHandler bridgeEventHandler, CreativeParams creativeParams) {
        mBridge = bridgeEventHandler;
        mCreativeParams = creativeParams;
    }

    private void jsLog(String message) { Logging.out(LOG_TAG, "JS: " + message); }

    private void callJsMethod(final String method) {
        Logging.out(LOG_TAG, "call " + method);
        mBridge.callJsMethod(method);
    }
    private void callWrapper(String method) { callJsMethod("loopMeVPAIDWrapperInstance." + method); }

    public void prepare() { callJsMethod("initVpaidWrapper()"); }
    public void startAd() { callWrapper("startAd()"); }
    public void stopAd() { callWrapper("stopAd()"); }
    public void pauseAd() { callWrapper("pauseAd()"); }
    public void resumeAd() { callWrapper("resumeAd()"); }
    public void getAdSkippableState() { callWrapper("getAdSkippableState()"); }
    public void resizeAd(int width, int height, String viewMode) {
        callWrapper("resizeAd(" + width + ", " + height + ", " + viewMode + ")");
    }

    //region JsCallbacks
    @JavascriptInterface
    public void wrapperReady() {
        callWrapper(String.format(Locale.US,
            "initAd(%d, %d, %s, %s, %s, %s);",
            mCreativeParams.getDimension().getWidth(),
            mCreativeParams.getDimension().getHeight(),
            mCreativeParams.getViewMode(),
            mCreativeParams.getDesiredBitrate(),
            mCreativeParams.getCreativeData(),
            mCreativeParams.getEnvironmentVars()
        ));
    }

    @JavascriptInterface
    public String handshakeVersionResult(String result) { return result; }
    @JavascriptInterface
    public void initAdResult() { jsLog("Init ad done"); }
    @JavascriptInterface
    public void vpaidAdLog(String message) { jsLog("vpaidAdLog " + message); }
    @JavascriptInterface
    public void vpaidAdUserAcceptInvitation() { jsLog("vpaidAdUserAcceptInvitation"); }
    @JavascriptInterface
    public void vpaidAdUserMinimize() { jsLog("vpaidAdUserMinimize"); }
    @JavascriptInterface
    public void vpaidAdUserClose() { jsLog("vpaidAdUserClose"); }
    @JavascriptInterface
    public void vpaidAdSkippableStateChange() { jsLog("vpaidAdSkippableStateChange"); }
    @JavascriptInterface
    public void vpaidAdExpandedChange() { jsLog("vpaidAdExpandedChange"); }
    @JavascriptInterface
    public void getAdExpandedResult(String result) { jsLog("getAdExpandedResult"); }
    @JavascriptInterface
    public void vpaidAdSizeChange() { jsLog("vpaidAdSizeChange"); }
    @JavascriptInterface
    public void vpaidAdRemainingTimeChange() { callWrapper("getAdRemainingTime()"); }
    @JavascriptInterface
    public void getAdDurationResult(int value) { jsLog("getAdDurationResult: " + value); }
    @JavascriptInterface
    public void getAdLinearResult(boolean value) { jsLog("getAdLinearResult: " + value); }
    @JavascriptInterface
    public void vpaidAdInteraction(Object id) { jsLog("vpaidAdInteraction " + id); }
    @JavascriptInterface
    public void getAdVolumeResult() { jsLog("getAdVolumeResult"); }

    @JavascriptInterface
    public void vpaidAdLoaded() { mBridge.onPrepared(); }
    @JavascriptInterface
    public void vpaidAdStarted() { mBridge.adStarted(); }
    @JavascriptInterface
    public void vpaidAdError(String message) { mBridge.trackError(message); }
    @JavascriptInterface
    public void vpaidAdLinearChange() { mBridge.onAdLinearChange(); }
    @JavascriptInterface
    public void getAdSkippableStateResult(boolean value) { mBridge.setSkippableState(value); }
    @JavascriptInterface
    public void vpaidAdImpression() { mBridge.onAdImpression(); }
    @JavascriptInterface
    public void vpaidAdVolumeChanged() { mBridge.onAdVolumeChange(); }
    @JavascriptInterface
    public void vpaidAdPaused() { mBridge.postEvent(EventConstants.PAUSE); }
    @JavascriptInterface
    public void vpaidAdPlaying() { mBridge.postEvent(EventConstants.RESUME); }
    @JavascriptInterface
    public void vpaidAdVideoFirstQuartile() { mBridge.postEvent(EventConstants.FIRST_QUARTILE); }
    @JavascriptInterface
    public void vpaidAdVideoMidpoint() { mBridge.postEvent(EventConstants.MIDPOINT); }
    @JavascriptInterface
    public void vpaidAdVideoThirdQuartile() { mBridge.postEvent(EventConstants.THIRD_QUARTILE); }
    @JavascriptInterface
    public void vpaidAdVideoComplete() { mBridge.postEvent(EventConstants.COMPLETE); }
    @JavascriptInterface
    public void vpaidAdSkipped() { mBridge.onAdSkipped(); }
    @JavascriptInterface
    public void vpaidAdStopped() { mBridge.onAdStopped(); }

    @JavascriptInterface
    public void vpaidAdDurationChange() {
        callWrapper("getAdDurationResult");
        mBridge.onDurationChanged();
    }

    @JavascriptInterface
    public void vpaidAdVideoStart() {
        mBridge.resizeAd();
        mBridge.postEvent(EventConstants.START);
    }

    @JavascriptInterface
    public void vpaidAdClickThruIdPlayerHandles(String url, String id, boolean playerHandles) {
        if (playerHandles) mBridge.onRedirect(url, null);
    }

    @JavascriptInterface
    public void getAdRemainingTimeResult(int value) {
        if (value == mPreviousValue && value != 0) {
            return;
        }
        mPreviousValue = value;
        if (value == 0) {
            mBridge.postEvent(EventConstants.COMPLETE);
            return;
        }
        mBridge.postEvent(EventConstants.PROGRESS, value);
        mBridge.setVideoTime(value);
    }
}
