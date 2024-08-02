package com.loopme.controllers.display;

import static com.loopme.debugging.Params.ERROR_MSG;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.DisplayController;
import com.loopme.models.Message;
import com.loopme.tracker.AdEvents;
import com.loopme.tracker.EventManager;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

public abstract class BaseTrackableController implements DisplayController, AdEvents {
    protected String mLogTag;
    private final EventManager mEventManager;
    private final LoopMeAd mLoopMeAd;
    private boolean mIsImpressionTracked;
    private final String mOrientation;

    public BaseTrackableController(@NonNull LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
        mOrientation = mLoopMeAd.getAdParams().getAdOrientation();
        mEventManager = new EventManager(mLoopMeAd);
    }

    protected void initTrackers() {
        boolean isNativeAd = !mLoopMeAd.isMraidAd() && !mLoopMeAd.isVpaidAd() && mLoopMeAd.isVastAd();
        onInitTracker(isNativeAd ? AdType.NATIVE : AdType.WEB);
    }

    protected void onInternalLoadFail(final LoopMeError error) {
        onUiThread(() -> mLoopMeAd.onInternalLoadFail(error));
    }

    protected void onPostWarning(final LoopMeError error) {
        onUiThread(() -> mLoopMeAd.onSendPostWarning(error));
    }

    protected void onUiThread(Runnable runnable) {
        mLoopMeAd.runOnUiThread(runnable);
    }

    protected void postDelayed(Runnable action, long delayMillis) {
        mLoopMeAd.runOnUiThreadDelayed(action, delayMillis);
    }

    @Override
    public void onMessage(Message type, String message) {
        if (type != Message.ERROR) {
            Log.d(mLogTag, message);
            return;
        }
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(ERROR_MSG, message);
        LoopMeTracker.post(errorInfo);
        onAdErrorEvent(message);
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        onAdClickedEvent();
        onMessage(Message.LOG, "Handle url");
        if (AdUtils.tryStartCustomTabs(loopMeAd.getContext(), url))
            loopMeAd.onAdLeaveApp();
    }

    // events region
    @Override
    public void onStartLoad() { }
    @Override
    public void onInitTracker(AdType type) { mEventManager.onInitTracker(type); }
    @Override
    public void onResume() { onAdResumedEvent(); }
    @Override
    public void onPause() { onAdPausedEvent(); }
    @Override
    public void onDestroy() { onAdDestroyedEvent(); }
    @Override
    public void onAdDestroyedEvent() { mEventManager.onAdDestroyedEvent(); }
    @Override
    public void onAdResumedEvent() { mEventManager.onAdResumedEvent(); }
    @Override
    public void onAdStartedEvent() { mEventManager.onAdStartedEvent(); }
    @Override
    public void onAdPausedEvent() { mEventManager.onAdPausedEvent(); }
    @Override
    public void onAdErrorEvent(String message) { mEventManager.onAdErrorEvent(message); }
    @Override
    public void onAdLoadedEvent() { mEventManager.onAdLoadedEvent(); }
    @Override
    public void onAdStoppedEvent() { mEventManager.onAdStoppedEvent(); }
    @Override
    public void onAdClickedEvent() { mEventManager.onAdClickedEvent(); }
    @Override
    public void onAdCompleteEvent() { mEventManager.onAdCompleteEvent(); }
    @Override
    public void onAdUserMinimizeEvent() { mEventManager.onAdUserMinimizeEvent(); }
    @Override
    public void onAdUserCloseEvent() { mEventManager.onAdUserCloseEvent(); }
    @Override
    public void onAdEnteredFullScreenEvent() { mEventManager.onAdEnteredFullScreenEvent(); }
    @Override
    public void onAdExitedFullScreenEvent() { mEventManager.onAdExitedFullScreenEvent(); }
    @Override
    public void onAdSkippedEvent() { mEventManager.onAdSkippedEvent(); }
    @Override
    public void onAdUserAcceptInvitationEvent() { mEventManager.onAdUserAcceptInvitationEvent(); }
    @Override
    public void onAdImpressionEvent() { mEventManager.onAdImpressionEvent(); }
    @Override
    public void onAdRecordReady() { mEventManager.onAdRecordReady(); }
    @Override
    public void onAdInjectJs(LoopMeAd loopMeAd) { mEventManager.onAdInjectJs(loopMeAd); }
    @Override
    public void onAdInjectJsVpaid(StringBuilder html) { mEventManager.onAdInjectJsVpaid(html); }
    @Override
    public void onAdRecordAdClose() { mEventManager.onAdRecordAdClose(); }
    @Override
    public void onNewActivity(Activity activity) { mEventManager.onNewActivity(activity); }

    @Override
    public void onAdDurationEvents(int currentPosition, int videoDuration) {
        mEventManager.onAdDurationEvents(currentPosition, videoDuration);
    }
    @Override
    public void onAdStartedEvent(WebView webView, MediaPlayer mediaPlayer) {
        mEventManager.onAdStartedEvent(webView, mediaPlayer);
    }
    @Override
    public void onAdVolumeChangedEvent(float volume, int currentPosition) {
        mEventManager.onAdVolumeChangedEvent(volume, currentPosition);
    }
    @Override
    public void onAdPreparedEvent(MediaPlayer mediaPlayer, View playerView) {
        mEventManager.onAdPreparedEvent(mediaPlayer, playerView);
    }
    @Override
    public void onStartWebMeasuringDelayed() {
        mLoopMeAd.getContainerView().postDelayed(mEventManager::onStartWebMeasuringDelayed, 100);
    }
    @Override
    public void onAdRegisterView(Activity activity, View view) {
        mEventManager.onAdRegisterView(activity, view);
    }

    public void postImpression() {
        if (mIsImpressionTracked) {
            return;
        }
        onAdRecordReady();
        onAdLoadedEvent();
        onAdImpressionEvent();
        mIsImpressionTracked = true;
    }

    @Override
    public int getOrientation() { return getOrientationFromAdParams(); }

    protected int getOrientationFromAdParams() {
        if (TextUtils.equals(mOrientation, Constants.ORIENTATION_PORT)) {
            return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        }
        if (TextUtils.equals(mOrientation, Constants.ORIENTATION_LAND)) {
            return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    protected boolean isTrackerAvailable() { return mEventManager != null; }

}
