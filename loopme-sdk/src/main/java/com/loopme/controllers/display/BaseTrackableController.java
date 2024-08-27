package com.loopme.controllers.display;

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
import com.loopme.ad.LoopMeAd;
import com.loopme.controllers.interfaces.DisplayController;
import com.loopme.tracker.AdEvents;
import com.loopme.tracker.EventManager;
import com.loopme.tracker.constants.AdType;

public abstract class BaseTrackableController implements DisplayController, AdEvents {
    protected String mLogTag;
    private final EventManager mEventManager;
    private final LoopMeAd mLoopMeAd;
    private boolean mIsImpressionTracked;

    public BaseTrackableController(@NonNull LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
        mEventManager = new EventManager(mLoopMeAd);
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        onAdClickedEvent();
        Log.d(mLogTag, "Handle url: " + url);
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
    public void onAdRegisterView(Activity activity, View view) {
        mEventManager.onAdRegisterView(activity, view);
    }
    @Override
    public void onStartWebMeasuringDelayed() {
        mLoopMeAd.getContainerView().postDelayed(mEventManager::onStartWebMeasuringDelayed, 100);
    }
    @Override
    public int getOrientation() {
        String orientation = mLoopMeAd.getAdParams().getAdOrientation();
        if (TextUtils.equals(orientation, Constants.ORIENTATION_PORT)) {
            return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        }
        if (TextUtils.equals(orientation, Constants.ORIENTATION_LAND)) {
            return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public void postImpression() {
        if (mIsImpressionTracked) return;
        onAdRecordReady();
        onAdLoadedEvent();
        onAdImpressionEvent();
        mIsImpressionTracked = true;
    }
}
