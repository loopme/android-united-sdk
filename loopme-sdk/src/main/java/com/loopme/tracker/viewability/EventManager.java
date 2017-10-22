package com.loopme.tracker.viewability;

import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebView;

import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.interfaces.AdEvents;

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
            mTrackerManager.track(Event.END_SESSION);
            mTrackerManager.track(Event.STOP);
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
            mTrackerManager.track(Event.STARTED);
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
        mTrackerManager.track(Event.COMPLETE);
        mTrackerManager.track(Event.VIDEO_COMPLETE);
        mTrackerManager.track(Event.VIDEO_STOPPED);
    }

    @Override
    public void onAdVolumeChangedEvent(double volume, int currentPosition) {
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
}
