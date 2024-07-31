package com.loopme.controllers;

import static com.loopme.debugging.Params.CID;
import static com.loopme.debugging.Params.CRID;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.REQUEST_ID;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;

import com.loopme.BidManager;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeMediaPlayer;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.views.AdView;

import java.util.HashMap;
import java.util.Map;

public class VideoController implements LoopMeMediaPlayer.LoopMeMediaPlayerListener {


    private static final String LOG_TAG = VideoController.class.getSimpleName();
    private static final int HALF = 2;
    private static final int FOURTH = 4;
    private static final long DELAY_TIME = 50;

    private static final int BUFFERING_MILLIS_IN_FUTURE = 2000;
    private static final int BUFFERING_COUNTDOWN_INTERVAL = 500;
    private static final int START = 10;

    private final AdView mAdView;
    private Surface mSurface;
    private Runnable mCurrentTimePoster;
    private Callback mCallback;
    private final Constants.AdFormat mAdFormat;
    private CountDownTimer mBufferingTimer;
    private volatile LoopMeMediaPlayer mLoopMePlayer;
    private final Map<String, Integer> mQuartileEventsMap;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private String mFileRest;
    private int mVideoDuration;
    private int mQuarter25;
    private int mQuarter50;
    private int mQuarter75;
    private int mCurrentPosition;
    private int mVideoPositionWhenError;
    private boolean mWasError;
    private boolean mWaitForVideo;
    private boolean mMuteState = false;
    private boolean mIsSurfaceTextureAvailable;

    public VideoController(AdView adView, Callback callback, Constants.AdFormat adFormat) {
        mAdView = adView;
        mCallback = callback;
        mAdFormat = adFormat;
        initCurrentTimePoster();
        mQuartileEventsMap = new HashMap<>();
    }

    public void playVideo(int time) {
        if (isReadyToStartPlay() || isVideoStateComplete() || isVideoStateIdle()) {
            playVideoInternal(time);
            return;
        }
        if (isVideoStatePaused()) {
            resumeVideoInternal(time);
        }
    }

    private boolean isReadyToStartPlay() {
        return isVideoStateReady() && !isPostponePlay();
    }

    public void resumeVideo() {
        resumeVideoInternal(mCurrentPosition);
    }

    private void resumeVideoInternal(int time) {
        if (!isVideoStatePaused()) {
            return;
        }
        playVideoInternal(time == START ? START : mCurrentPosition);
    }

    public void pauseVideo(boolean isSkip) {
        if (!isPlaying()) {
            return;
        }
        pauseVideoInternal();
        setVideoState(isSkip);
    }

    private void setVideoState(boolean isSkip) {
        if (!isInterstitial()) {
            setVideoState(Constants.VideoState.PAUSED);
            return;
        }
        if (isSkip) {
            setVideoState(Constants.VideoState.IDLE);
        } else {
            setVideoState(Constants.VideoState.PAUSED);
        }
    }

    private boolean isInterstitial() {
        return mAdFormat == Constants.AdFormat.INTERSTITIAL;
    }

    public void destroy() {
        Logging.out(LOG_TAG, "destroy");
        removeCallbacks();
        stopBuffering();
        releasePlayer();
        mCurrentTimePoster = null;
        mCallback = null;
        mSurface = null;
    }

    private void runProgressAgain() {
        if (mCurrentPosition < mVideoDuration) {
            startCurrentTimePoster();
        }
    }

    private void setCurrentPosition() {
        mCurrentPosition = getCurrentPosition();
    }

    private int getCurrentPosition() {
        return mLoopMePlayer == null ? 0 : mLoopMePlayer.getCurrentPosition();
    }

    public void setSurface(Surface surface) {
        Logging.out(LOG_TAG, "setSurfaceTexture " + surface);
        mSurface = surface;
        if (mLoopMePlayer != null) {
            mLoopMePlayer.setSurface(surface);
        }
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        Surface surface = new Surface(surfaceTexture);
        setSurface(surface);
    }

    private void releasePlayer() {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.releasePlayer();
        }
    }

    private void waitForVideo() {
        if (mWaitForVideo) {
            releasePlayer();
            initPlayerFromFile(mFileRest);
            setSurface(mSurface);
            startMediaPlayer();
        }
        seekMediaPlayerTo(mVideoPositionWhenError);
        setVideoState(Constants.VideoState.PLAYING);
        startCurrentTimePoster();
        Logging.out(LOG_TAG, "waitForVideo mHandler.startCurrentTimePoster");
    }

    public void initPlayerFromFile(String filePath) {
        mLoopMePlayer = new LoopMeMediaPlayer(filePath, this);
    }

    public void muteVideo(boolean muted) {
        mMuteState = muted;
        if (mLoopMePlayer != null) {
            mLoopMePlayer.muteVideo(muted);
        }
    }

    private void updateCurrentVolume() {
        if (mLoopMePlayer != null && !mMuteState) {
            mLoopMePlayer.setSystemVolume();
        }
    }

    public void setSurfaceTextureAvailable(boolean isAvailable) {
        mIsSurfaceTextureAvailable = isAvailable;
    }

    private void playVideoInternal(int time) {
        if (isPlaying()) {
            return;
        }
        muteVideo(mMuteState);
        seekMediaPlayerTo(time);
        startMediaPlayer();
        startCurrentTimePoster();
        setVideoState(Constants.VideoState.PLAYING);
        Logging.out(LOG_TAG, "Play video " + time);
    }

    private void startCurrentTimePoster() {
        mHandler.postDelayed(mCurrentTimePoster, DELAY_TIME);
    }

    private boolean isPostponePlay() {
        return !mWasError && !mIsSurfaceTextureAvailable;
    }

    private void pauseVideoInternal() {
        setCurrentPosition();
        removeCallbacks();
        pauseMediaPlayer();
        setVideoState(Constants.VideoState.PAUSED);
        Logging.out(LOG_TAG, "Pause video");
    }

    private boolean isPlaying() {
        return !mWasError && isLmPlayerPlaying();
    }

    private boolean isLmPlayerPlaying() {
        return mLoopMePlayer != null && mLoopMePlayer.isPlaying();
    }

    private void pauseMediaPlayer() {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.pauseMediaPlayer();
        }
    }

    private void setFileRest(String filePath) {
        mFileRest = filePath;
    }

    private boolean handleError(MediaPlayer mediaPlayer, int extra) {
        if (extra == MediaPlayer.MEDIA_ERROR_IO) {
            handleMediaIoError(mediaPlayer);
            return true;
        }
        if (isErrorByLoading()) {
            onFail();
        } else {
            handelPlayBackFinishWithError();
        }
        return false;
    }

    private void handelPlayBackFinishWithError() {
        setWebViewState(Constants.WebviewState.HIDDEN);
        setVideoState(Constants.VideoState.PAUSED);
        onPlaybackFinishedWithError();
        mWasError = true;
    }

    private void setWebViewState(Constants.WebviewState webViewState) {
        if (mAdView != null) {
            mAdView.setWebViewState(webViewState);
        }
    }

    private boolean isErrorByLoading() {
        return isVideoStateBroken() || isVideoStateIdle();
    }

    private boolean isVideoStateIdle() {
        return mAdView != null && mAdView.getCurrentVideoState() == Constants.VideoState.IDLE;
    }

    private boolean isVideoStateBroken() {
        return mAdView != null && mAdView.getCurrentVideoState() == Constants.VideoState.BROKEN;
    }

    private boolean isVideoStateComplete() {
        return mAdView != null && mAdView.getCurrentVideoState() == Constants.VideoState.COMPLETE;
    }

    private boolean isVideoStatePaused() {
        return mAdView != null && mAdView.getCurrentVideoState() == Constants.VideoState.PAUSED;
    }

    private boolean isVideoStateReady() {
        return mAdView != null && mAdView.getCurrentVideoState() == Constants.VideoState.READY;
    }

    private void handleMediaIoError(MediaPlayer mediaPlayer) {
        Logging.out(LOG_TAG, "end of preview file");
        setVideoPositionWhenError(mediaPlayer);
        if (!TextUtils.isEmpty(mFileRest)) {
            restartMediaPlayer();
            return;
        }
        mWaitForVideo = true;
        setVideoState(Constants.VideoState.BUFFERING);
        startBuffering();
    }

    private void restartMediaPlayer() {
        releasePlayer();
        initPlayerFromFile(mFileRest);
        setSurface(mSurface);
        seekMediaPlayerTo(mVideoPositionWhenError);
        startMediaPlayer();
        setVideoState(Constants.VideoState.PLAYING);
        startCurrentTimePoster();
    }

    private void seekMediaPlayerTo(int position) {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.seekTo(position);
        }
    }

    private void setVideoPositionWhenError(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mVideoPositionWhenError = mediaPlayer.getCurrentPosition();
        }
    }

    private void removeCallbacks() {
        mHandler.removeCallbacks(mCurrentTimePoster);
    }

    private void postVideoCurrentTimeToWebView(int currentPosition) {
        if (mAdView != null) {
            mAdView.setVideoCurrentTime(currentPosition);
        }
    }

    private void startBuffering() {
        mBufferingTimer = new CountDownTimer(BUFFERING_MILLIS_IN_FUTURE, BUFFERING_COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                Logging.out(LOG_TAG, "Buffering " + (millisUntilFinished / 1000) + " second...");
            }
            @Override
            public void onFinish() {
                HashMap<String, String> errorInfo = new HashMap<>();
                errorInfo.put(ERROR_MSG, "Buffering 2 seconds");
                errorInfo.put(REQUEST_ID, BidManager.getInstance().getRequestId());
                errorInfo.put(CID, BidManager.getInstance().getCurrentCid());
                errorInfo.put(CRID, BidManager.getInstance().getCurrentCrid());
                LoopMeTracker.post(errorInfo);
            }
        };
        mBufferingTimer.start();
    }

    public void fullVideoLoaded(String filePath, boolean preload, boolean adShown) {
        if (!preload) {
            initPlayerFromFile(filePath);
            return;
        }
        if (adShown) {
            setFileRest(mFileRest);
            waitForVideo();
            return;
        }
        releasePlayer();
        initPlayerFromFile(filePath);
    }

    private void stopBuffering() {
        if (mBufferingTimer != null) {
            mBufferingTimer.cancel();
        }
    }

    private void extractVideoInfo(int videoDuration, int videoWidth, int videoHeight) {
        setVideoDuration(videoDuration);
        setQuarters(mVideoDuration);
        fillQuartileEventsMap();
        onVideoSizeChanged(videoWidth, videoHeight);
    }

    private void setVideoState(Constants.VideoState state) {
        if (mAdView != null) {
            mAdView.setVideoState(state);
        }
    }

    private void setVideoDuration(int videoDuration) {
        mVideoDuration = videoDuration;
        if (mAdView != null) {
            mAdView.setVideoDuration(mVideoDuration);
        }
    }

    private int roundNumberToHundredth(int number) {
        return (number / 100) * 100;
    }

    private void setQuarters(int mVideoDuration) {
        mQuarter25 = roundNumberToHundredth(mVideoDuration / FOURTH);
        mQuarter50 = roundNumberToHundredth(mVideoDuration / HALF);
        mQuarter75 = mQuarter25 + mQuarter50;
    }

    private void fillQuartileEventsMap() {
        mQuartileEventsMap.clear();
        mQuartileEventsMap.put(Constants.EVENT_VIDEO_25, mQuarter25);
        mQuartileEventsMap.put(Constants.EVENT_VIDEO_50, mQuarter50);
        mQuartileEventsMap.put(Constants.EVENT_VIDEO_75, mQuarter75);
    }

    public void surfaceTextureDestroyed() {
        setSurfaceTextureAvailable(false);
        setSurface(null);
    }

    private void startMediaPlayer() {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.start();
        }
    }

    private void destroyListeners() {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.destroyListeners();
        }
    }

    private void initCurrentTimePoster() {
        mCurrentTimePoster = () -> {
            int currentPosition = getCurrentPosition();
            postVideoCurrentTimeToWebView(currentPosition);
            onDurationChangedEvent(currentPosition, mVideoDuration);
            updateCurrentVolume();
            runProgressAgain();
        };
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        stopBuffering();
        extractVideoInfo(mediaPlayer.getDuration(), mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        setVideoState(Constants.VideoState.READY);
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Logging.out(LOG_TAG, "onError: " + extra);
        removeCallbacks();
        destroyListeners();
        if (handleError(mediaPlayer, extra)) {
            return true;
        }
        releasePlayer();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isVideoStateComplete()) {
            return;
        }
        removeCallbacks();
        postVideoCurrentTimeToWebView(mp.getCurrentPosition());
        setVideoState(Constants.VideoState.COMPLETE);
        onVideoReachEnd();
    }

    @Override
    public void onTimeUpdate(int currentTime, int duration) {
        // TODO: onDurationChangedEvent or onTimeUpdate?
    }

    @Override
    public void onErrorOccurred(Exception e) {
        setVideoState(Constants.VideoState.BROKEN);
    }

    @Override
    public void onVolumeChanged(float volume, int currentPosition) {
        onVolumeChangedEvent(volume, currentPosition);
    }

    private void onVolumeChangedEvent(float volume, int currentPosition) {
        if (mCallback != null) {
            mCallback.onVolumeChangedEvent(volume, currentPosition);
        }
    }

    private void onPlaybackFinishedWithError() {
        if (mCallback != null) {
            mCallback.onPlaybackFinishedWithError();
        }
    }

    private void onFail() {
        if (mCallback != null) {
            mCallback.onFail(Errors.ERROR_DURING_VIDEO_LOADING);
        }
    }

    private void onVideoSizeChanged(int videoWidth, int videoHeight) {
        if (mCallback != null) {
            mCallback.onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

    private void onVideoReachEnd() {
        if (mCallback != null) {
            mCallback.onVideoReachEnd();
        }
    }

    private void onDurationChangedEvent(int currentPosition, int adDuration) {
        if (mCallback != null) {
            mCallback.onDurationChangedEvent(currentPosition, adDuration);
        }
    }

    public interface Callback {
        void onVideoReachEnd();
        void onFail(LoopMeError error);
        void onVideoSizeChanged(int width, int height);
        void onPlaybackFinishedWithError();
        void onVolumeChangedEvent(float volume, int currentPosition);
        void onDurationChangedEvent(int currentPosition, int adDuration);
    }

}