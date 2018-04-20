package com.loopme.tracker;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebView;

import com.loopme.tracker.constants.AdType;

public interface AdEvents {
    void onAdDurationEvents(int currentPosition, int videoDuration);

    void onAdDestroyedEvent();

    void onAdResumedEvent();

    void onAdStartedEvent();

    void onAdStartedEvent(WebView webView, MediaPlayer mediaPlayer);

    void onAdPausedEvent();

    void onAdErrorEvent(String message);

    void onAdLoadedEvent();

    void onAdStoppedEvent();

    void onAdClickedEvent();

    void onAdCompleteEvent();

    void onAdVolumeChangedEvent(double volume, int currentPosition);

    void onAdUserMinimizeEvent();

    void onAdUserCloseEvent();

    void onAdEnteredFullScreenEvent();

    void onAdExitedFullScreenEvent();

    void onAdSkippedEvent();

    void onAdUserAcceptInvitationEvent();

    void onAdImpressionEvent();

    void onAdPreparedEvent(MediaPlayer mediaPlayer, View playerView);

    void onInitTracker(AdType type);

    void onStartWebMeasuringDelayed();

    void onAdRecordReady();

    void onAdRegisterView(Activity activity, View view);

    void onAdInject();

    void onAdRecordAdLoaded();

    void onAdRecordAdClose();
}