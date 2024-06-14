package com.loopme.controllers.display;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.iab.omid.library.loopme.adsession.AdEvents;
import com.iab.omid.library.loopme.adsession.AdSession;
import com.iab.omid.library.loopme.adsession.VerificationScriptResource;
import com.iab.omid.library.loopme.adsession.media.MediaEvents;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeMediaPlayer;
import com.loopme.ad.LoopMeAd;
import com.loopme.controllers.view.ViewControllerVast;
import com.loopme.models.Errors;
import com.loopme.om.OmidEventTrackerWrapper;
import com.loopme.om.OmidHelper;
import com.loopme.time.TimeUtils;
import com.loopme.time.TimerWithPause;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.utils.ApiLevel;
import com.loopme.utils.Utils;
import com.loopme.vast.TrackingEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayControllerVast extends VastVpaidBaseDisplayController implements
        LoopMeMediaPlayer.LoopMeMediaPlayerListener,
        ViewControllerVast.ViewControllerVastListener {

    private static final String LOG_TAG = DisplayControllerVast.class.getSimpleName();

    private static final int DELAY_UNTIL_EXECUTE = 100;
    private static final int COUNTDOWN_INTERVAL = 100;

    private final ViewControllerVast mViewControllerVast;
    private List<TrackingEvent> mTrackingEventsList = new ArrayList<>();
    private LoopMeMediaPlayer mLoopMePlayer;
    private int mSkipTimeMillis;
    private int mVideoDuration;
    private TimerWithPause mVideoTimer;

    private boolean mIsAdSkipped;
    // TODO. Refactor.
    private boolean playerPrepared;
    private boolean controllerPrepared;
    private boolean needWaitOmidAdSessionStart;

    private AdSession omidAdSession;
    private OmidEventTrackerWrapper omidEventTrackerWrapper;

    public DisplayControllerVast(LoopMeAd loopMeAd) {
        super(loopMeAd);
        mViewControllerVast = new ViewControllerVast(this, this);
        mLogTag = DisplayControllerVast.class.getSimpleName();
        Logging.out(mLogTag);
    }

    @Override
    public void onSurfaceTextureReady(Surface surface) {
        resumeMediaPlayer(surface);
    }

    @Override
    public void prepare() {
        super.prepare();
        controllerPrepared = true;
        onAdReady();
    }

    @Override
    protected void onAdReady() {
        if (needWaitOmidAdSessionStart || !controllerPrepared)
            return;
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendLoaded(mAdParams.getSkipTime(), mAdParams.getDuration());
        super.onAdReady();
    }

    @Override
    protected void onTryCreateOmidTracker(Map<String, Verification> omidVerificationMap) {
        if (omidVerificationMap == null || omidVerificationMap.isEmpty())
            return;
        needWaitOmidAdSessionStart = true;
        OmidHelper.createNativeVideoAdSessionAsync(
            mLoopMeAd.getContext().getApplicationContext(),
            createOmidVerificationScriptResourceList(omidVerificationMap),
            new OmidHelper.AdSessionListener() {
                @Override
                public void onReady(AdSession adSession) {
                    onOmidNativeVideoAdSessionCreated(adSession, null);
                }
                @Override
                public void onError(String error) {
                    onOmidNativeVideoAdSessionCreated(null, error);
                }
            }
        );
    }

    private void onOmidNativeVideoAdSessionCreated(AdSession adSession, String error) {
        if (adSession == null) {
            Logging.out(LOG_TAG, error);
        } else {
            try {
                omidEventTrackerWrapper = new OmidEventTrackerWrapper(
                    AdEvents.createAdEvents(adSession),
                    MediaEvents.createMediaEvents(adSession)
                );
                // Starting adSession.
                omidAdSession = adSession;
                omidAdSession.start();
            } catch (Exception e) {
                Logging.out(LOG_TAG, e.toString());
            }
        }
        needWaitOmidAdSessionStart = false;
        onAdReady();
    }

    // TODO. Refactor.
    private static List<VerificationScriptResource> createOmidVerificationScriptResourceList(
        Map<String, Verification> omidVerificationMap
    ) {
        List<VerificationScriptResource> vsrList = new ArrayList<>();
        if (omidVerificationMap == null)
            return vsrList;
        for (Verification v : omidVerificationMap.values()) {
            if (v == null)
                continue;
            try {
                String vendor = v.getVendor();
                URL url = new URL(v.getJavaScriptResourceUrl());
                String vp = v.getVerificationParameters();
                VerificationScriptResource vsr = TextUtils.isEmpty(vp) ?
                    VerificationScriptResource.createVerificationScriptResourceWithoutParameters(
                        vendor, url
                    ) :
                    VerificationScriptResource.createVerificationScriptResourceWithParameters(
                        vendor, url, vp
                    );
                vsrList.add(vsr);
            } catch (Exception e) {
                Logging.out(LOG_TAG, e.toString());
                // TODO. Refactor. This piece of code isn't about creating data.
                postVerificationNotExecutedEvent(
                    v.getVerificationNotExecutedUrlList(), VAST_MACROS_REASON_LOAD_ERROR
                );
            }
        }
        return vsrList;
    }

    @Override
    protected WebView createWebView() {
        return isTrackerAvailable() ? new VastWebView(mLoopMeAd.getContext()) : null;
    }

    @Override
    public void onBuildVideoAdView(FrameLayout containerView) {
        mViewControllerVast.buildVideoAdView(
            containerView, mLoopMeAd.getContext(), getWebView()
        );
        setVerificationView(containerView);
        try {
            if (omidAdSession != null)
                omidAdSession.registerAdView(containerView);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    @Override
    public void onPlay(int position) {
        mIsAdSkipped = false;
        postDelayed(() -> {
            destroyMediaPlayer();
            startMediaPlayer();
        }, DELAY_UNTIL_EXECUTE);
        onAdResumedEvent();
    }

    private void startMediaPlayer() {
        mLoopMePlayer = new LoopMeMediaPlayer(mVideoUri, this);
    }

    // TODO. Refactor.
    private void resumeMediaPlayer(final Surface surface) {
        if (mLoopMePlayer == null)
            return;
        mLoopMePlayer.setSurface(surface);
        if (!playerPrepared)
            return;
        if (omidEventTrackerWrapper != null) {
            omidEventTrackerWrapper.sendOneTimeImpression();
            omidEventTrackerWrapper.sendOneTimeStartEvent(
                mAdParams.getDuration(), mViewControllerVast.isMute()
            );
        }
        mLoopMePlayer.start();
        resumeVideoTimer();
    }

    private void resumeVideoTimer() {
        if (mVideoTimer != null) {
            mVideoTimer.resume();
        }
    }

    private Surface getSurface() {
        return mViewControllerVast == null ? null : mViewControllerVast.getSurface();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseMediaPlayer();
        if (mIsAdSkipped)
            return;
        postVideoEvent(EventConstants.PAUSE);
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeSdk24AndAbove();
        if (mIsAdSkipped)
            return;
        postVideoEvent(EventConstants.RESUME);
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendResume();
    }

    private void resumeSdk24AndAbove() {
        if (ApiLevel.isApi24AndHigher() && !mViewControllerVast.isEndCard()) {
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
        if (mLoopMePlayer != null)
            onAdDurationEvents(doneMillis, mLoopMePlayer.getVideoDuration());
        postViewableEvents(doneMillis);
        List<TrackingEvent> eventsToRemove = new ArrayList<>();
        for (TrackingEvent event : mTrackingEventsList) {
            if (doneMillis > event.timeMillis) {
                postVideoEvent(event.url);
                eventsToRemove.add(event);
            }
        }
        mTrackingEventsList.removeAll(eventsToRemove);
        // TODO. Refactor.
        // Omid progress events tracking.
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendOneTimeProgressEvent(
                doneMillis / 1000f, mAdParams.getDuration()
            );
    }

    private void showSkipButton(int doneMillis) {
        if (mSkipTimeMillis < 0 || doneMillis <= mSkipTimeMillis) {
            return;
        }
        mViewControllerVast.showSkipButton();
        mSkipTimeMillis = -1;
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
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendOneTimeSkipEvent();
    }

    @Override
    public void onVolumeMute(boolean mute) {
        muteVideo(mute, true);
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendVolume(mute);
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        if (isPlaying()) {
            url = mAdParams.getVideoRedirectUrl();
            postVideoClicks(getCurrentPositionAsString());
        } else {
            url = mAdParams.getEndCardRedirectUrl();
            postEndCardClicks();
        }
        onAdClicked();
        super.onRedirect(url, mLoopMeAd);
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendClicked();
    }

    private String getCurrentPositionAsString() {
        return String.valueOf(getPassedTime() / Constants.MILLIS_IN_SECOND);
    }

    private int getPassedTime() {
        return mVideoTimer != null ? (int) mVideoTimer.timePassed() : 0;
    }

    private boolean isPlaying() {
        return mLoopMePlayer != null && mLoopMePlayer.isPlaying();
    }

    @Override
    public void closeSelf() {
        onAdUserCloseEvent();
        postVideoEvent(EventConstants.CLOSE, getCurrentPositionAsString());
        dismissAd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyOmid();
        destroyMediaPlayer();
        destroyVideoTimer();
        destroyWebView();
        mViewControllerVast.dismiss();
        mViewControllerVast.destroy();
    }

    private void destroyOmid() {
        new Handler(Looper.getMainLooper())
            .postDelayed(new OmidFinisher(omidAdSession), OmidHelper.FINISH_AD_SESSION_DELAY_MILLIS);
        omidAdSession = null;
        omidEventTrackerWrapper = null;
    }

    public class OmidFinisher implements Runnable {
        private final AdSession adSession;

        public OmidFinisher(AdSession adSession) {
            this.adSession = adSession;
        }

        public AdSession getAdSession() {
            return adSession;
        }

        @Override
        public void run() {
            if (adSession != null)
                adSession.finish();
        }
    }

    private void destroyMediaPlayer() {
        playerPrepared = false;
        if (mLoopMePlayer == null) {
            return;
        }
        mLoopMePlayer.destroyListeners();
        mLoopMePlayer.releasePlayer();
        mLoopMePlayer = null;
    }

    private void skipVideo(boolean skipEvent) {
        mIsAdSkipped = true;
        pauseMediaPlayer();
        destroyVideoTimer();
        if (TextUtils.isEmpty(mImageUri)) {
            closeSelf();
        } else {
            mViewControllerVast.showEndCard(mImageUri);
            onEndCardAppears();
        }
        if (skipEvent) {
            postVideoEvent(EventConstants.SKIP, getCurrentPositionAsString());
        }
    }

    private void muteVideo(boolean mute, boolean postEvent) {
        if (mLoopMePlayer != null) {
            mLoopMePlayer.muteVideo(mute);
        }
        postVolumeStateEvent(mute, postEvent);
    }

    private void postVolumeStateEvent(boolean mute, boolean postEvent) {
        if (postEvent) {
            postVideoEvent(mute ? EventConstants.MUTE : EventConstants.UNMUTE);
        }
    }

    private void pauseMediaPlayer() {
        if (mLoopMePlayer == null) {
            return;
        }
        mLoopMePlayer.pauseMediaPlayer();
        pauseVideoTimer();
    }

    private void pauseVideoTimer() {
        if (mVideoTimer != null) {
            mVideoTimer.pause();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        skipVideo(false);
        postVideoEvent(EventConstants.COMPLETE);
        onAdVideoDidReachEnd();
        onAdCompleteEvent();

        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendOneTimeCompleteEvent();
    }

    private void createVideoTimer(final int duration) {
        mVideoTimer = initVideoTimer(duration);
        mVideoTimer.create();
    }

    private void destroyVideoTimer() {
        if (mVideoTimer == null) {
            return;
        }
        mVideoTimer.pause();
        mVideoTimer.cancel();
        mVideoTimer = null;
    }

    // TODO. Refactor.
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logging.out(mLogTag, "MediaPlayer onError():  what - " + what + "; extra - " + extra);
        onErrorOccurred(null);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playerPrepared = true;
        mVideoDuration = mp.getDuration();
        mViewControllerVast.adjustLayoutParams(mp.getVideoWidth(), mp.getVideoHeight(), mLoopMeAd.isBanner());
        createVideoTimer(mVideoDuration);
        prepareControls(mVideoDuration);
        resumeMediaPlayer(getSurface());
        postEvents(mp);
        muteVideo(mViewControllerVast.isMute(), false);
    }

    private void postEvents(MediaPlayer mediaPlayer) {
        onAdPreparedEvent(mediaPlayer, mViewControllerVast.getPlayerView());
        onAdStartedEvent();
    }

    // TODO. Refactor.
    @Override
    public void onErrorOccurred(Exception e) {
        if (e != null)
            Logging.out(mLogTag, e.toString());
        // TODO. Destroying beforehand to get rid of unnecessary callbacks like onCompletion.
        destroyMediaPlayer();
        onInternalLoadFail(Errors.PROBLEM_DISPLAYING_MEDIAFILE);
        closeSelf();
    }

    @Override
    public void onVolumeChanged(float volume, int currentPosition) {
        onAdVolumeChangedEvent(volume, currentPosition);
    }

    @Override
    public void onAdShake() { }

    @Override
    public void setFullScreen(boolean isFullScreen) { }

    private TimerWithPause initVideoTimer(final int duration) {
        return new TimerWithPause(duration, COUNTDOWN_INTERVAL, true) {
            @Override
            public void onTick(long millisUntilFinished) {
                onDurationChanged(millisUntilFinished);
            }
            @Override
            public void onFinish() { }
        };
    }

    private void onDurationChanged(long millisUntilFinished) {
        int doneMillis = mVideoDuration - (int) millisUntilFinished + Constants.MILLIS_IN_SECOND;
        setProgress((int) millisUntilFinished);
        showSkipButton(doneMillis);
        trackDurationsEvents(doneMillis);
        muteVideo(mViewControllerVast.isMute(), false);
    }
}