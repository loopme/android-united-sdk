package com.loopme.controllers.display;

import static com.loopme.Constants.SKIP_DELAY_INTERSTITIAL;
import static com.loopme.Constants.SKIP_DELAY_REWARDED;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.iab.omid.library.loopme.adsession.AdEvents;
import com.iab.omid.library.loopme.adsession.AdSession;
import com.iab.omid.library.loopme.adsession.VerificationScriptResource;
import com.iab.omid.library.loopme.adsession.media.MediaEvents;
import com.loopme.Logging;
import com.loopme.LoopMeInterstitialGeneral;
import com.loopme.LoopMeMediaPlayer;
import com.loopme.ad.LoopMeAd;
import com.loopme.controllers.view.ViewControllerVast;
import com.loopme.models.Errors;
import com.loopme.om.OmidEventTrackerWrapper;
import com.loopme.om.OmidHelper;
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
    private final ViewControllerVast mViewControllerVast;
    private List<TrackingEvent> mTrackingEventsList = new ArrayList<>();
    private LoopMeMediaPlayer mLoopMePlayer;
    private int mSkipTimeMillis;

    private boolean mIsAdSkipped;
    // TODO. Refactor.
    private boolean playerPrepared;
    private boolean controllerPrepared;
    private boolean needWaitOmidAdSessionStart;

    private AdSession omidAdSession;
    private OmidEventTrackerWrapper omidEventTrackerWrapper;

    public DisplayControllerVast(@NonNull LoopMeAd loopMeAd) {
        super(loopMeAd);
        mViewControllerVast = new ViewControllerVast(this, this);
        mLogTag = DisplayControllerVast.class.getSimpleName();
        Logging.out(mLogTag);
    }

    @Override
    public void onSurfaceTextureReady(Surface surface) { resumeMediaPlayer(surface); }

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

    // TODO. Refactor.
    private static List<VerificationScriptResource> createOmidVerificationScriptResourceList(
        @NonNull Map<String, Verification> omidVerificationMap
    ) {
        List<VerificationScriptResource> vsrList = new ArrayList<>();
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
    protected void onTryCreateOmidTracker(Map<String, Verification> omidVerificationMap) {
        if (omidVerificationMap == null || omidVerificationMap.isEmpty())
            return;
        needWaitOmidAdSessionStart = true;
        try {
            if (OmidHelper.isInitialized()) onOmidNativeVideoAdSessionCreated(
                OmidHelper.createAdSession(
                    createOmidVerificationScriptResourceList(omidVerificationMap)
                ), null
            );
            else onOmidNativeVideoAdSessionCreated(null, "Can't create Video AdSession: OMSDK not initialized");
        } catch (Exception e) {
            onOmidNativeVideoAdSessionCreated(null, "Can't create Video AdSession: " + e);
        }
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
            mLoopMePlayer = new LoopMeMediaPlayer(mVideoUri, DisplayControllerVast.this);
        }, 100);
        onAdResumedEvent();
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLoopMePlayer != null) {
            mLoopMePlayer.pauseMediaPlayer();
        }
        if (mIsAdSkipped)
            return;
        postVideoEvent(EventConstants.PAUSE);
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApiLevel.isApi24AndHigher() && !mViewControllerVast.isEndCard()) {
            resumeMediaPlayer(mViewControllerVast.getSurface());
        }
        if (mIsAdSkipped)
            return;
        postVideoEvent(EventConstants.RESUME);
        if (omidEventTrackerWrapper != null)
            omidEventTrackerWrapper.sendResume();
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
        if (mLoopMePlayer != null && mLoopMePlayer.isPlaying()) {
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
        // TODO: replace 0 with value;
        return String.valueOf(0);
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
        new Handler(Looper.getMainLooper())
            .postDelayed(new OmidFinisher(omidAdSession), OmidHelper.FINISH_AD_SESSION_DELAY_MILLIS);
        omidAdSession = null;
        omidEventTrackerWrapper = null;
        destroyMediaPlayer();
        destroyWebView();
        mViewControllerVast.dismiss();
        mViewControllerVast.destroy();
    }

    private class OmidFinisher implements Runnable {
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
        if (mLoopMePlayer != null) {
            mLoopMePlayer.pauseMediaPlayer();
        }
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
        if (postEvent) {
            postVideoEvent(mute ? EventConstants.MUTE : EventConstants.UNMUTE);
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
        mViewControllerVast.adjustLayoutParams(mp.getVideoWidth(), mp.getVideoHeight(), mLoopMeAd.isBanner());
        int duration = mp.getDuration();
        boolean isRewarded = mLoopMeAd instanceof LoopMeInterstitialGeneral &&
                ((LoopMeInterstitialGeneral) mLoopMeAd).isRewarded();
        mSkipTimeMillis = isRewarded ? SKIP_DELAY_REWARDED : SKIP_DELAY_INTERSTITIAL;
        mTrackingEventsList = Utils.createProgressPoints(duration, mAdParams);
        mViewControllerVast.setMaxProgress(duration);
        resumeMediaPlayer(mViewControllerVast.getSurface());
        onAdPreparedEvent(mp, mViewControllerVast.getPlayerView());
        onAdStartedEvent();
        muteVideo(mViewControllerVast.isMute(), false);
    }

    @Override
    public void onTimeUpdate(int currentTime, int duration) {
        int doneMillis = duration - currentTime;
        if (mViewControllerVast != null) {
            mViewControllerVast.setProgress(doneMillis);
        }
        if (mSkipTimeMillis >= 0 && currentTime > mSkipTimeMillis) {
            mViewControllerVast.showSkipButton();
            mSkipTimeMillis = -1;
        }
        if (mLoopMePlayer != null) {
            onAdDurationEvents(currentTime, mLoopMePlayer.getVideoDuration());
        }
        postViewableEvents(currentTime);
        List<TrackingEvent> eventsToRemove = new ArrayList<>();
        for (TrackingEvent event : mTrackingEventsList) {
            if (currentTime > event.timeMillis) {
                postVideoEvent(event.url);
                eventsToRemove.add(event);
            }
        }
        mTrackingEventsList.removeAll(eventsToRemove);
        // TODO. Refactor.
        // Omid progress events tracking.
        if (omidEventTrackerWrapper != null) {
            omidEventTrackerWrapper.sendOneTimeProgressEvent(currentTime / 1000f, mAdParams.getDuration());
        }
        muteVideo(mViewControllerVast.isMute(), false);
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
}