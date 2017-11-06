package com.loopme.controllers.display;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeMediaPlayer;
import com.loopme.ad.LoopMeAd;
import com.loopme.controllers.view.ViewControllerVast;
import com.loopme.models.Errors;
import com.loopme.models.Message;
import com.loopme.time.TimeUtils;
import com.loopme.time.TimerWithPause;
import com.loopme.utils.Utils;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.vast.TrackingEvent;

import java.util.ArrayList;
import java.util.List;


public class DisplayControllerVast extends VastVpaidBaseDisplayController implements
        LoopMeMediaPlayer.LoopMeMediaPlayerListener {

    private static final int DELAY_UNTIL_EXECUTE = 100;
    private static final int COUNTDOWN_INTERVAL = 100;

    private final ViewControllerVast mViewControllerVast;
    private List<TrackingEvent> mTrackingEventsList = new ArrayList<>();
    private LoopMeMediaPlayer mLoopMePlayer;
    private int mSkipTimeMillis;
    private View mAdView;
    private int mVideoDuration;
    private TimerWithPause mVideoTimer;
    private VastWebView mWebView;
    private OnPreparedListener mPrepareListener;

    public DisplayControllerVast(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mViewControllerVast = new ViewControllerVast(this, initViewControllerVastListener());
        VastVpaidEventTracker.addAllEvents(mLoopMeAd.getAdParams().getTrackingEventsList());
        mLogTag = DisplayControllerVast.class.getSimpleName();
        Logging.out(mLogTag);
    }

    private ViewControllerVast.ViewControllerVastListener initViewControllerVastListener() {
        return new ViewControllerVast.ViewControllerVastListener() {
            @Override
            public void onSurfaceTextureReady(Surface surface) {
                resumeMediaPlayer(surface);
            }
        };
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        initTrackers();
    }

    @Override
    public void prepare(OnPreparedListener listener) {
        mPrepareListener = listener;
        vast4Verification();
    }

    private void vast4Verification() {
        if (isVast4VerificationNeeded()) {
            configureWebView();
            super.onAdRegisterView(mLoopMeAd.getContext(), mWebView);
            vast4Verification(mWebView);
        } else {
            onVast4VerificationDoesNotNeed();
        }
    }

    @Override
    public void onVast4VerificationDoesNotNeed() {
        preparationFinished();
    }

    @Override
    public void onBuildVideoAdView(FrameLayout containerView) {
        mViewControllerVast.buildVideoAdView(containerView, mLoopMeAd.getContext(), mWebView);
        mAdView = containerView;
        setVerificationView(mAdView);
    }

    @Override
    public void onPlay(int position) {
        onAdRecordReady();


//        onAdResumedEvent();
//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                destroyMediaPlayer();
//                startMediaPlayer();
//            }
//        }, DELAY_UNTIL_EXECUTE);
//
//        onAdStartedEvent();
    }

    @Override
    public void onAdRegisterView(Activity activity, View view) {
        super.onAdRegisterView(activity, mWebView);
        onInject();
        onAdResumedEvent();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                destroyMediaPlayer();
                startMediaPlayer();
            }
        }, DELAY_UNTIL_EXECUTE);

        onAdStartedEvent();

    }

    private void startMediaPlayer() {
        mLoopMePlayer = new LoopMeMediaPlayer(mVideoUri, this);
    }

    private void resumeMediaPlayer(Surface surface) {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.seekTo(getPassedTime());
            mLoopMePlayer.setSurface(surface);
            mLoopMePlayer.start();
            resumeVideoTimer();
        }
    }

    private int getPassedTime() {
        if (mVideoTimer != null) {
            return (int) mVideoTimer.timePassed();
        }
        return 0;
    }

    private void resumeVideoTimer() {
        if (mVideoTimer != null) {
            mVideoTimer.resume();
        }
    }

    private Surface getSurface() {
        if (mViewControllerVast != null) {
            return mViewControllerVast.getSurface();
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseMediaPlayer();
        onMessage(Message.EVENT, EventConstants.PAUSE);
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeSdk24();
        onMessage(Message.EVENT, EventConstants.RESUME);
    }

    private void resumeSdk24() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N && !mViewControllerVast.isEndCard()) {
            resumeMediaPlayer(getSurface());
        }
    }

    private void prepareControls(final int duration) {
        initSkipTime(duration);
        createProgressPoints(duration);
        mViewControllerVast.setMaxProgress(duration);
    }

    private void setProgress(int duration) {
        if (mViewControllerVast != null) {
            mViewControllerVast.setProgress(duration);
        }
    }

    private void trackDurationsEvents(int doneMillis) {
        onAdDurationEvents(doneMillis, mLoopMePlayer.getVideoDuration());
        postViewableEvents(doneMillis);
        List<TrackingEvent> eventsToRemove = new ArrayList<>();
        for (TrackingEvent event : mTrackingEventsList) {
            if (doneMillis > event.timeMillis) {
                onMessage(Message.EVENT, event.url);
                eventsToRemove.add(event);
            }
        }
        mTrackingEventsList.removeAll(eventsToRemove);
    }

    private void showSkipButton(int doneMillis) {
        if (mSkipTimeMillis >= 0 && doneMillis > mSkipTimeMillis) {
            mViewControllerVast.showSkipButton();
            mSkipTimeMillis = -1;
        }
    }

    private void initSkipTime(int duration) {
        mSkipTimeMillis = TimeUtils.retrieveSkipTime(mAdParams.getSkipTime(), duration);
    }

    private void createProgressPoints(int duration) {
        mTrackingEventsList = Utils.createProgressPoints(duration, mAdParams);
    }

    @Override
    public void skipVideo() {
        skipVideo(true);
        onAdSkippedEvent();
    }

    @Override
    public void onVolumeMute(boolean mute) {
        muteVideo(mute, true);
    }

    @Override
    public boolean onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        if (isPlaying()) {
            url = mAdParams.getVideoRedirectUrl();
            for (String trackUrl : mAdParams.getVideoClicks()) {
                VastVpaidEventTracker.postEvent(trackUrl, getCurrentPositionAsString());
            }
        } else {
            url = mAdParams.getEndCardRedirectUrl();
            for (String trackUrl : mAdParams.getEndCardClicks()) {
                onMessage(Message.EVENT, trackUrl);
            }
        }
        onAdClicked();
        return super.onRedirect(url, mLoopMeAd);
    }

    private String getCurrentPositionAsString() {
        return mLoopMePlayer == null ? String.valueOf(0) : String.valueOf(mLoopMePlayer.getCurrentPosition() / 1000);
    }

    private boolean isPlaying() {
        return mLoopMePlayer != null && mLoopMePlayer.isPlaying();
    }

    @Override
    public void closeSelf() {
        VastVpaidEventTracker.postEvent(EventConstants.CLOSE, getCurrentPositionAsString());
        dismissAd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMediaPlayer();
        destroyVideoTimer();
        destroyWebView();
        mViewControllerVast.dismiss();
        mViewControllerVast.destroy();
    }

    private void destroyMediaPlayer() {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.destroyListeners();
            mLoopMePlayer.releasePlayer();
            mLoopMePlayer = null;
        }
    }

    private void skipVideo(boolean skipEvent) {
        pauseMediaPlayer();
        destroyVideoTimer();
        if (TextUtils.isEmpty(mImageUri)) {
            closeSelf();
        } else {
            mViewControllerVast.showEndCard(mImageUri);
        }
        if (skipEvent) {
            VastVpaidEventTracker.postEvent(EventConstants.SKIP, getCurrentPositionAsString());
        }
    }

    private void muteVideo(boolean mute, boolean postEvent) {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.muteVideo(mute);
        }
        postVolumeStateEvent(mute, postEvent);
    }

    private void postVolumeStateEvent(boolean mute, boolean postEvent) {
        if (mute) {
            if (postEvent) {
                onMessage(Message.EVENT, EventConstants.MUTE);
            }
        } else {
            if (postEvent) {
                onMessage(Message.EVENT, EventConstants.UNMUTE);
            }
        }
    }

    public View getView() {
        return mAdView;
    }

    private void pauseMediaPlayer() {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.pauseMediaPlayer();
            pauseVideoTimer();
        }
    }

    private void pauseVideoTimer() {
        if (mVideoTimer != null) {
            mVideoTimer.pause();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        skipVideo(false);
        onMessage(Message.EVENT, EventConstants.COMPLETE);
        onAdVideoDidReachEnd();
        onAdCompleteEvent();
    }

    private void createVideoTimer(final int duration) {
        mVideoTimer = initVideoTimer(duration);
        mVideoTimer.create();
    }

    private void destroyVideoTimer() {
        if (mVideoTimer != null) {
            mVideoTimer.pause();
            mVideoTimer.cancel();
            mVideoTimer = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        onInternalLoadFail(Errors.PROBLEM_DISPLAYING_MEDIAFILE);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoDuration = mp.getDuration();
        mViewControllerVast.adjustLayoutParams(mp.getVideoWidth(), mp.getVideoHeight());
        createVideoTimer(mVideoDuration);
        prepareControls(mp.getDuration());
        resumeMediaPlayer(getSurface());
        postEvents(mp);
        muteVideo(mViewControllerVast.isMute(), false);
    }

    private void postEvents(MediaPlayer mediaPlayer) {
        onAdPreparedEvent(mediaPlayer, mViewControllerVast.getPlayerView());
//        onAdImpressionEvent();
        onAdStartedEvent();
    }

    @Override
    public void onErrorOccurred(Exception e) {
        onInternalLoadFail(Errors.PROBLEM_DISPLAYING_MEDIAFILE);
        closeSelf();
    }

    @Override
    public void onVolumeChanged(double volume, int currentPosition) {
        onAdVolumeChangedEvent(volume, currentPosition);
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public void onAdShake() {

    }

    private TimerWithPause initVideoTimer(final int duration) {
        return new TimerWithPause(duration, COUNTDOWN_INTERVAL, true) {
            @Override
            public void onTick(long millisUntilFinished) {
                onDurationChanged(millisUntilFinished);
            }

            @Override
            public void onFinish() {
            }
        };
    }

    private void onDurationChanged(long millisUntilFinished) {
        int doneMillis = mVideoDuration - (int) millisUntilFinished + Constants.MILLIS_IN_SECOND;
        setProgress((int) millisUntilFinished);
        showSkipButton(doneMillis);
        trackDurationsEvents(doneMillis);
        muteVideo(mViewControllerVast.isMute(), false);
    }

    private void configureWebView() {
        mWebView = new VastWebView(mLoopMeAd.getContext(), new VastWebView.OnFinishLoadListener() {

            @Override
            public void onFinishLoad() {
                preparationFinished();
            }

            @Override
            public void onJsError(String message) {
                DisplayControllerVast.this.onPostWarning(Errors.VERIFICATION_UNIT_NOT_EXECUTED);
            }
        });
    }

    private void preparationFinished() {
        if (mPrepareListener != null) {
            mPrepareListener.onPrepared();
            mPrepareListener = null;
        }
    }

    private void destroyWebView() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
    }
}
