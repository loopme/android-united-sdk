package com.loopme.tracker;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebView;

import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;

public class EventManager implements AdEvents {
    private static final String LOG_TAG = EventManager.class.getSimpleName();
    private static final int HALF_DURATION = 2;
    private static final int FOURTH_DURATION = 4;

    private boolean mQuarter25Tracked;
    private boolean mQuarter50Tracked;
    private boolean mQuarter75Tracked;

    private TrackerManager mTrackerManager;

    public EventManager(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            mTrackerManager = new TrackerManager(loopMeAd);
            mTrackerManager.startSdk();
        } else {
            Logging.out(LOG_TAG, "LoopMeAd is null!");
        }
    }

    @Override
    public void onInitTracker(AdType type) {
        if (mTrackerManager != null) {
            mTrackerManager.onInitTracker(type);
        }
    }

    @Override
    public void onAdDestroyedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.STOP);
            mTrackerManager.track(Event.END_SESSION);
        }
    }

    @Override
    public void onAdResumedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.PLAYING);
        }
    }

    @Override
    public void onAdStartedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.START);
            mTrackerManager.track(Event.VIDEO_STARTED);
        }
    }

    @Override
    public void onAdStartedEvent(WebView webView, MediaPlayer mediaPlayer) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.START, mediaPlayer, webView);
        }
    }

    @Override
    public void onAdPausedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.PAUSED);
        }
    }

    @Override
    public void onAdErrorEvent(String message) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.ERROR, message);
        }
    }

    @Override
    public void onAdLoadedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.LOADED);
        }
    }

    @Override
    public void onAdStoppedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.VIDEO_STOPPED);
        }
    }

    @Override
    public void onAdClickedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.CLICKED);
        }
    }


    @Override
    public void onAdCompleteEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.COMPLETE);
            mTrackerManager.track(Event.VIDEO_COMPLETE);
            mTrackerManager.track(Event.VIDEO_STOPPED);
        }
    }

    @Override
    public void onAdVolumeChangedEvent(float volume, int currentPosition) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.VOLUME_CHANGE, volume, currentPosition);
        }
    }

    @Override
    public void onAdUserMinimizeEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.USER_MINIMIZE);
        }
    }

    @Override
    public void onAdUserCloseEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.USER_CLOSE);
        }
    }

    @Override
    public void onAdEnteredFullScreenEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.ENTERED_FULLSCREEN);
        }
    }

    @Override
    public void onAdExitedFullScreenEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.EXITED_FULLSCREEN);
        }
    }

    @Override
    public void onAdSkippedEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.SKIPPED);
        }
    }

    @Override
    public void onAdUserAcceptInvitationEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.USER_ACCEPT_INVITATION);
        }
    }

    @Override
    public void onAdImpressionEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.IMPRESSION);
        }
    }

    @Override
    public void onAdPreparedEvent(MediaPlayer mediaPlayer, View playerView) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.PREPARE, mediaPlayer, playerView);
        }
    }

    @Override
    public void onStartWebMeasuringDelayed() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.START_MEASURING);
        }
    }

    @Override
    public void onAdRecordReady() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.RECORD_READY);
        }
    }

    @Override
    public void onAdRegisterView(Activity activity, View view) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.REGISTER, activity, view);
        }
    }

    @Override
    public void onAdInjectJs(LoopMeAd loopMeAd) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.INJECT_JS_WEB, loopMeAd);
        }
    }

    @Override
    public void onAdInjectJsVpaid(StringBuilder html) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.INJECT_JS_VPAID, html);
        }
    }

    @Override
    public void onAdDurationEvents(int position, int videoDuration) {
        onAdDurationChangedEvent(position, videoDuration);
        handleProgressDurationEvents(position, videoDuration);
    }

    private void onAdVideoFirstQuartileEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.FIRST_QUARTILE);
        }
    }

    private void onAdVideoMidpointEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.MIDPOINT);
        }
    }

    private void onAdVideoThirdQuartileEvent() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.THIRD_QUARTILE);
        }
    }

    private void onAdDurationChangedEvent(int position, int videoDuration) {
        if (mTrackerManager != null) {
            String adDuration = "" + position;
            String adRemainingTime = "" + (videoDuration - position);
            mTrackerManager.track(Event.DURATION_CHANGED, adDuration, adRemainingTime);
        }
    }

    private void handleProgressDurationEvents(int position, int videoDuration) {
        int quarter25 = videoDuration / FOURTH_DURATION;
        int quarter50 = videoDuration / HALF_DURATION;
        int quarter75 = quarter25 + quarter50;

        if (position > quarter25 && !mQuarter25Tracked) {
            mQuarter25Tracked = true;
            onAdVideoFirstQuartileEvent();
        } else if (position > quarter50 && !mQuarter50Tracked) {
            mQuarter50Tracked = true;
            onAdVideoMidpointEvent();
        } else if (position > quarter75 && !mQuarter75Tracked) {
            mQuarter75Tracked = true;
            onAdVideoThirdQuartileEvent();
        }
    }

    @Override
    public void onAdRecordAdClose() {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.CLOSE);
        }
    }

    @Override
    public void onNewActivity(Activity activity) {
        if (mTrackerManager != null) {
            mTrackerManager.track(Event.NEW_ACTIVITY, activity);
        }
    }
}
