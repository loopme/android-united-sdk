package com.loopme.tracker;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;

public class EventManager implements AdEvents {
    private boolean mQuarter25Tracked;
    private boolean mQuarter50Tracked;
    private boolean mQuarter75Tracked;

    private final TrackerManager mTrackerManager;

    public EventManager(@NonNull LoopMeAd loopMeAd) {
        // TODO: Currently TrackerManager is simple track logger
        mTrackerManager = new TrackerManager(loopMeAd);
    }

    @Override
    public void onInitTracker(AdType type) { }
    @Override
    public void onAdResumedEvent() { mTrackerManager.track(Event.PLAYING); }
    @Override
    public void onAdPausedEvent() { mTrackerManager.track(Event.PAUSED); }
    @Override
    public void onAdErrorEvent(String message) { mTrackerManager.track(Event.ERROR, message); }
    @Override
    public void onAdLoadedEvent() { mTrackerManager.track(Event.LOADED); }
    @Override
    public void onAdStoppedEvent() { mTrackerManager.track(Event.VIDEO_STOPPED); }
    @Override
    public void onAdClickedEvent() { mTrackerManager.track(Event.CLICKED); }
    @Override
    public void onAdUserMinimizeEvent() { mTrackerManager.track(Event.USER_MINIMIZE); }
    @Override
    public void onAdUserCloseEvent() { mTrackerManager.track(Event.USER_CLOSE); }
    @Override
    public void onAdEnteredFullScreenEvent() { mTrackerManager.track(Event.ENTERED_FULLSCREEN); }
    @Override
    public void onAdExitedFullScreenEvent() { mTrackerManager.track(Event.EXITED_FULLSCREEN); }
    @Override
    public void onAdSkippedEvent() { mTrackerManager.track(Event.SKIPPED); }
    @Override
    public void onAdUserAcceptInvitationEvent() { mTrackerManager.track(Event.USER_ACCEPT_INVITATION); }
    @Override
    public void onAdImpressionEvent() { mTrackerManager.track(Event.IMPRESSION); }
    @Override
    public void onStartWebMeasuringDelayed() { mTrackerManager.track(Event.START_MEASURING); }
    @Override
    public void onAdRecordReady() { mTrackerManager.track(Event.RECORD_READY); }
    @Override
    public void onAdRegisterView(Activity activity, View view) { mTrackerManager.track(Event.REGISTER, activity, view); }
    @Override
    public void onAdInjectJs(LoopMeAd loopMeAd) { mTrackerManager.track(Event.INJECT_JS_WEB, loopMeAd); }
    @Override
    public void onAdInjectJsVpaid(StringBuilder html) { mTrackerManager.track(Event.INJECT_JS_VPAID, html); }
    @Override
    public void onAdRecordAdClose() { mTrackerManager.track(Event.CLOSE); }
    @Override
    public void onNewActivity(Activity activity) { mTrackerManager.track(Event.NEW_ACTIVITY, activity); }

    @Override
    public void onAdDestroyedEvent() {
        mTrackerManager.track(Event.STOP);
        mTrackerManager.track(Event.END_SESSION);
    }
    @Override
    public void onAdStartedEvent() {
        mTrackerManager.track(Event.START);
        mTrackerManager.track(Event.VIDEO_STARTED);
    }
    @Override
    public void onAdCompleteEvent() {
        mTrackerManager.track(Event.COMPLETE);
        mTrackerManager.track(Event.VIDEO_COMPLETE);
        mTrackerManager.track(Event.VIDEO_STOPPED);
    }
    @Override
    public void onAdStartedEvent(WebView webView, MediaPlayer mediaPlayer) {
        mTrackerManager.track(Event.START, mediaPlayer, webView);
    }
    @Override
    public void onAdVolumeChangedEvent(float volume, int currentPosition) {
        mTrackerManager.track(Event.VOLUME_CHANGE, volume, currentPosition);
    }
    @Override
    public void onAdPreparedEvent(MediaPlayer mediaPlayer, View playerView) {
        mTrackerManager.track(Event.PREPARE, mediaPlayer, playerView);
    }
    @Override
    public void onAdDurationEvents(int position, int videoDuration) {
        String adDuration = String.valueOf(position);
        String adRemainingTime = String.valueOf(videoDuration - position);
        mTrackerManager.track(Event.DURATION_CHANGED, adDuration, adRemainingTime);

        int quarter25 = videoDuration / 4;
        int quarter50 = videoDuration / 2;
        int quarter75 = quarter25 + quarter50;

        if (position > quarter25 && !mQuarter25Tracked) {
            mQuarter25Tracked = true;
            mTrackerManager.track(Event.FIRST_QUARTILE);
        } else if (position > quarter50 && !mQuarter50Tracked) {
            mQuarter50Tracked = true;
            mTrackerManager.track(Event.MIDPOINT);
        } else if (position > quarter75 && !mQuarter75Tracked) {
            mQuarter75Tracked = true;
            mTrackerManager.track(Event.THIRD_QUARTILE);
        }
    }

}
