package com.loopme.bridges.vpaid;

import android.support.annotation.Keep;
import android.webkit.JavascriptInterface;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.*;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.CreativeParams;

@Keep
@SuppressWarnings("unused")
public class VpaidBridgeImpl implements VpaidBridge {

    private static final String LOG_TAG = VpaidBridgeImpl.class.getSimpleName();
    private static final String ENVIRONMENT_VARS = "{ " +
            "slot: document.getElementById('loopme-slot'), " +
            "videoSlot: document.getElementById('loopme-videoslot'), " +
            "videoSlotCanAutoPlay: true }";
    private final BridgeEventHandler mBridge;
    private final CreativeParams mCreativeParams;
    private volatile int mPreviousValue;

    public VpaidBridgeImpl(BridgeEventHandler eventHandler, AdParams adParams) {
        mBridge = eventHandler;
        mCreativeParams = createCreativeParams(adParams);
    }

    //region VpaidBridge methods
    @Override
    public void prepare() {
        Logging.out(LOG_TAG, "call initVpaidWrapper()");
        callJsMethod("initVpaidWrapper()");
    }

    @Override
    public void startAd() {
        Logging.out(LOG_TAG, "call startAd()");
        callWrapper("startAd()");
    }

    @Override
    public void stopAd() {
        Logging.out(LOG_TAG, "call stopAd()");
        callWrapper("stopAd()");
    }

    @Override
    public void pauseAd() {
        Logging.out(LOG_TAG, "call pauseAd()");
        callWrapper("pauseAd()");
    }

    @Override
    public void resumeAd() {
        Logging.out(LOG_TAG, "call resumeAd()");
        callWrapper("resumeAd()");
    }

    @Override
    public void getAdSkippableState() {
        Logging.out(LOG_TAG, "call getAdSkippableState()");
        callWrapper("getAdSkippableState()");
    }

    @Override
    public void resizeAd(int width, int height, String viewMode) {
        callWrapper("resizeAd(" + width + ", " + height + ", " + viewMode + ")");
    }

    //endregion

    //region Helpers
    private void runOnUiThread(Runnable runnable) {
        mBridge.runOnUiThread(runnable);
    }

    private void callJsMethod(final String url) {
        mBridge.callJsMethod(url);
    }

    private void callWrapper(String method) {
        callJsMethod("loopMeVPAIDWrapperInstance." + method);
    }
    //endregion

    //region JsCallbacks
    @JavascriptInterface
    public void wrapperReady() {
        initAd();
    }

    private void initAd() {
        Logging.out(LOG_TAG, "JS: call initAd()");
        String requestTemplate = "initAd(" +
                "%1$d," + // width
                "%2$d," + // height
                "%3$s," + // viewMode
                "%4$s," + // desiredBitrate
                "%5$s," + // creativeData
                "%6$s)"; // environmentVars
        String requestFinal = String.format(requestTemplate,
                mCreativeParams.getWidth(),
                mCreativeParams.getHeight(),
                mCreativeParams.getViewMode(),
                mCreativeParams.getDesiredBitrate(),
                mCreativeParams.getCreativeData(),
                mCreativeParams.getEnvironmentVars()
        );
        callWrapper(requestFinal);
    }

    @JavascriptInterface
    public String handshakeVersionResult(String result) {
        return result;
    }

    @JavascriptInterface
    public void vpaidAdLoaded() {
        Logging.out(LOG_TAG, "JS: vpaidAdLoaded");
        mBridge.onPrepared();
    }

    @JavascriptInterface
    public void vpaidAdStarted() {
        mBridge.adStarted();
        Logging.out(LOG_TAG, "JS: vpaidAdStarted");
    }

    @JavascriptInterface
    public void initAdResult() {
        Logging.out(LOG_TAG, "JS: Init ad done");
    }

    @JavascriptInterface
    public void vpaidAdError(String message) {
        Logging.out(LOG_TAG, "JS: vpaidAdError" + message);
        mBridge.trackError(message);
    }

    @JavascriptInterface
    public void vpaidAdLog(String message) {
        Logging.out(LOG_TAG, "JS: vpaidAdLog " + message);
    }

    @JavascriptInterface
    public void vpaidAdUserAcceptInvitation() {
        Logging.out(LOG_TAG, "JS: vpaidAdUserAcceptInvitation");
    }

    @JavascriptInterface
    public void vpaidAdUserMinimize() {
        Logging.out(LOG_TAG, "JS: vpaidAdUserMinimize");
    }

    @JavascriptInterface
    public void vpaidAdUserClose() {
        Logging.out(LOG_TAG, "JS: vpaidAdUserClose");
    }

    @JavascriptInterface
    public void vpaidAdSkippableStateChange() {
        Logging.out(LOG_TAG, "JS: vpaidAdSkippableStateChange");
    }

    @JavascriptInterface
    public void vpaidAdExpandedChange() {
        Logging.out(LOG_TAG, "JS: vpaidAdExpandedChange");
    }

    @JavascriptInterface
    public void getAdExpandedResult(String result) {
        Logging.out(LOG_TAG, "JS: getAdExpandedResult");
    }

    @JavascriptInterface
    public void vpaidAdSizeChange() {
        Logging.out(LOG_TAG, "JS: vpaidAdSizeChange");
    }

    @JavascriptInterface
    public void vpaidAdDurationChange() {
        Logging.out(LOG_TAG, "JS: vpaidAdDurationChange");
        callWrapper("getAdDurationResult");
        mBridge.onDurationChanged();
    }

    @JavascriptInterface
    public void vpaidAdRemainingTimeChange() {
        callWrapper("getAdRemainingTime()");
    }

    @JavascriptInterface
    public void vpaidAdLinearChange() {
        Logging.out(LOG_TAG, "JS: vpaidAdLinearChange");
        mBridge.onAdLinearChange();
    }

    @JavascriptInterface
    public void vpaidAdPaused() {
        Logging.out(LOG_TAG, "JS: vpaidAdPaused");
        mBridge.postEvent(EventConstants.PAUSE);
    }

    @JavascriptInterface
    public void vpaidAdVideoStart() {
        Logging.out(LOG_TAG, "JS: vpaidAdVideoStart");
        mBridge.resizeAd();
        mBridge.postEvent(EventConstants.START);
    }

    @JavascriptInterface
    public void vpaidAdPlaying() {
        Logging.out(LOG_TAG, "JS: vpaidAdPlaying");
        mBridge.postEvent(EventConstants.RESUME);
    }

    @JavascriptInterface
    public void vpaidAdClickThruIdPlayerHandles(String url, String id, boolean playerHandles) {
        if (playerHandles) {
            mBridge.onRedirect(url, null);
        }
    }

    @JavascriptInterface
    public void vpaidAdVideoFirstQuartile() {
        Logging.out(LOG_TAG, "vpaidAdVideoFirstQuartile");
        mBridge.postEvent(EventConstants.FIRST_QUARTILE);
    }

    @JavascriptInterface
    public void vpaidAdVideoMidpoint() {
        Logging.out(LOG_TAG, "JS: vpaidAdVideoMidpoint");
        mBridge.postEvent(EventConstants.MIDPOINT);
    }

    @JavascriptInterface
    public void vpaidAdVideoThirdQuartile() {
        Logging.out(LOG_TAG, "JS: vpaidAdVideoThirdQuartile");
        mBridge.postEvent(EventConstants.THIRD_QUARTILE);
    }

    @JavascriptInterface
    public void vpaidAdVideoComplete() {
        mBridge.postEvent(EventConstants.COMPLETE);
        Logging.out(LOG_TAG, "JS: vpaidAdVideoComplete");
    }

    @JavascriptInterface
    public void getAdSkippableStateResult(boolean value) {
        Logging.out(LOG_TAG, "JS: SkippableState: " + value);
        mBridge.setSkippableState(value);
    }

    @JavascriptInterface
    public void getAdRemainingTimeResult(int value) {
        if (value == mPreviousValue && value != 0) {
            return;
        }
        mPreviousValue = value;
        if (value == 0) {
            mBridge.postEvent(EventConstants.COMPLETE);
        } else {
            mBridge.postEvent(EventConstants.PROGRESS, value);
            mBridge.setVideoTime(value);
        }
    }

    @JavascriptInterface
    public void getAdDurationResult(int value) {
        Logging.out(LOG_TAG, "JS: getAdDurationResult: " + value);
    }

    @JavascriptInterface
    public void getAdLinearResult(boolean value) {
        Logging.out(LOG_TAG, "getAdLinearResult: " + value);
    }

    @JavascriptInterface
    public void vpaidAdSkipped() {
        Logging.out(LOG_TAG, "JS: vpaidAdSkipped");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBridge.onAdSkipped();
            }
        });
    }

    @JavascriptInterface
    public void vpaidAdStopped() {
        Logging.out(LOG_TAG, "JS: vpaidAdStopped");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBridge.onAdStopped();
            }
        });
    }

    @JavascriptInterface
    public void vpaidAdImpression() {
        Logging.out(LOG_TAG, "JS: vpaidAdImpression");
        mBridge.onAdImpression();
    }

    @JavascriptInterface
    public void vpaidAdInteraction(Object id) {
        Logging.out(LOG_TAG, "JS: vpaidAdInteraction" + id);
    }

    @JavascriptInterface
    public void vpaidAdVolumeChanged() {
        Logging.out(LOG_TAG, "JS: vpaidAdVolumeChanged");
        mBridge.onAdVolumeChange();
    }

    @JavascriptInterface
    public void getAdVolumeResult() {
        Logging.out(LOG_TAG, "JS: getAdVolumeResult");
    }

    private CreativeParams createCreativeParams(AdParams adParams) {
        int width = Constants.getAdSpotDimensions().getWidth();
        int height = Constants.getAdSpotDimensions().getHeight();
        CreativeParams result = new CreativeParams(width, height, "normal", 720);
        result.setAdParameters("{'AdParameters':'" + adParams.getAdParams() + "'}");
        result.setEnvironmentVars(ENVIRONMENT_VARS);
        return result;
    }

}
