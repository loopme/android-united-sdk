package com.loopme.tracker.partners;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.integralads.avid.library.loopme.session.AvidAdSessionManager;
import com.integralads.avid.library.loopme.session.AvidDisplayAdSession;
import com.integralads.avid.library.loopme.session.AvidManagedVideoAdSession;
import com.integralads.avid.library.loopme.session.ExternalAvidAdSessionContext;
import com.integralads.avid.library.loopme.video.AvidVideoPlaybackListener;
import com.loopme.BuildConfig;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.interfaces.Tracker;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.utils.ArrayUtils;

public class IasTracker implements Tracker {
    private static final String LOG_TAG = IasTracker.class.getSimpleName();
    private static AvidVideoPlaybackListener sAvidVideoPlaybackListener;
    private static AvidManagedVideoAdSession mVideoAdSession;

    public IasTracker(AdType adType) {

    }

    public static void startSdk(LoopMeAd loopMeAd) {
        //todo does IAS need to startSdk sdk?
    }

    public static class Web {

        public static void init(LoopMeAd loopMeAd) {
            WebView webView = loopMeAd.getDisplayController().getWebView();
            Context context = loopMeAd.getContext();
            ExternalAvidAdSessionContext adSessionContext = createAvidAdSessionContext();
            AvidDisplayAdSession avidDisplayAdSession = AvidAdSessionManager.startAvidDisplayAdSession(context, adSessionContext);
            registerAdView(avidDisplayAdSession, context, webView);
        }

        private static void registerAdView(AvidDisplayAdSession avidDisplayAdSession, Context context, WebView containerView) {
            if (context != null && context instanceof Activity) {
                Activity activity = (Activity) context;
                avidDisplayAdSession.registerAdView(containerView, activity);
            } else {
                Logging.out(LOG_TAG, "Context should be instance of Activity!");
            }
        }

    }

    public static class Native {

        public static void init(LoopMeAd loopMeAd) {
            if (loopMeAd == null) {
                Logging.out(LOG_TAG, "LoopMeAd should not be null!");
                return;
            }
            FrameLayout containerView = loopMeAd.getContainerView();
            Context context = loopMeAd.getContext();

            ExternalAvidAdSessionContext adSessionContext = createAvidAdSessionContext();
            mVideoAdSession = AvidAdSessionManager.startAvidManagedVideoAdSession(context, adSessionContext);
            registerAdView(mVideoAdSession, context, containerView);
            sAvidVideoPlaybackListener = mVideoAdSession.getAvidVideoPlaybackListener();
        }

        private static void registerAdView(AvidManagedVideoAdSession videoAdSession, Context context, FrameLayout containerView) {
            if (context != null && context instanceof Activity) {
                Activity activity = (Activity) context;
                videoAdSession.registerAdView(containerView, activity);
            } else {
                Logging.out(LOG_TAG, "Context should not be null and must be instance of Activity!");
            }
        }
    }

    private static ExternalAvidAdSessionContext createAvidAdSessionContext() {
        String partnerVersion = getPartnerVersion();
        return new ExternalAvidAdSessionContext(partnerVersion);
    }

    private static String getPartnerVersion() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    @Override
    public void track(Event event, Object... args) {
        switch (event) {
            case IMPRESSION: {
                recordAdImpressionEvent();
                break;
            }
            case STARTED: {
                recordAdStartedEvent();
                break;
            }
            case LOADED: {
                recordAdLoadedEvent();
                break;
            }
            case VIDEO_STARTED: {
                recordAdVideoStartEvent();
                break;
            }
            case VIDEO_STOPPED: {
                recordAdStoppedEvent();
                break;
            }
            case COMPLETE: {
                recordAdCompleteEvent();
                break;
            }
            case CLICKED: {
                recordAdUserCloseEvent();
                break;
            }
            case FIRST_QUARTILE: {
                recordAdVideoFirstQuartileEvent();
                break;
            }
            case MIDPOINT: {
                recordAdVideoMidpointEvent();
                break;
            }
            case THIRD_QUARTILE: {
                recordAdVideoThirdQuartileEvent();
                break;
            }
            case EXPANDED_CHANGE: {
                recordAdExpandedChangeEvent();
                break;
            }
            case PAUSED: {
                recordAdPausedEvent();
                break;
            }
            case PLAYING: {
                recordAdPlayingEvent();
                break;
            }
            case USER_MINIMIZE: {
                recordAdUserMinimizeEvent();
                break;
            }
            case USER_ACCEPT_INVITATION: {
                recordAdUserAcceptInvitationEvent();
                break;
            }
            case USER_CLOSE: {
                recordAdUserCloseEvent();
                break;
            }
            case SKIPPED: {
                recordAdSkippedEvent();
                break;
            }
            case VOLUME_CHANGE: {
                recordAdVolumeChangeEvent(args);
                break;
            }
            case ENTERED_FULLSCREEN: {
                recordAdEnteredFullscreenEvent();
                break;
            }
            case EXITED_FULLSCREEN: {
                recordAdExitedFullscreenEvent();
                break;
            }
            case DURATION_CHANGED: {
                recordAdDurationChangeEvent(args);
                break;
            }
            case ERROR: {
                recordAdError(args);
                break;
            }
            case END_SESSION: {
                endSession();
                break;
            }
        }
    }

    private static void endSession() {
        if (mVideoAdSession != null) {
            mVideoAdSession.endSession();
            mVideoAdSession = null;
        }
    }

    private static void recordAdImpressionEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdImpressionEvent();
        }
    }

    private static void recordAdStartedEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdStartedEvent();
        }
    }

    private static void recordAdLoadedEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdLoadedEvent();
        }
    }

    private static void recordAdVideoStartEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdVideoStartEvent();
        }
    }

    private static void recordAdStoppedEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdStoppedEvent();
        }
    }

    private static void recordAdCompleteEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdCompleteEvent();
        }
    }

    private void recordAdClickThruEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdClickThruEvent();
        }
    }

    private static void recordAdVideoFirstQuartileEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdVideoFirstQuartileEvent();
        }
    }

    private static void recordAdVideoMidpointEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdVideoMidpointEvent();
        }
    }

    private static void recordAdVideoThirdQuartileEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdVideoThirdQuartileEvent();
        }
    }

    private static void recordAdPausedEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdPausedEvent();
        }
    }

    private static void recordAdPlayingEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdPlayingEvent();
        }
    }

    private static void recordAdExpandedChangeEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdExpandedChangeEvent();
        }
    }

    private static void recordAdUserMinimizeEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdUserMinimizeEvent();
        }
    }

    private static void recordAdUserAcceptInvitationEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdUserAcceptInvitationEvent();
        }
    }

    private static void recordAdUserCloseEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdUserCloseEvent();
        }
    }

    private static void recordAdSkippedEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdSkippedEvent();
        }
    }

    private static void recordAdVolumeChangeEvent(Object[] args) {
        if (ArrayUtils.isArrayContainsInteger(args)) {
            Integer volume = (Integer) args[0];
            if (sAvidVideoPlaybackListener != null) {
                sAvidVideoPlaybackListener.recordAdVolumeChangeEvent(volume);
            }
        }
    }

    private static void recordAdEnteredFullscreenEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdEnteredFullscreenEvent();
        }
    }

    private static void recordAdExitedFullscreenEvent() {
        if (sAvidVideoPlaybackListener != null) {
            sAvidVideoPlaybackListener.recordAdExitedFullscreenEvent();
        }
    }

    private static void recordAdDurationChangeEvent(Object[] args) {
        if (ArrayUtils.isArrayContainsStrings(args) && args.length >= 2) {
            String adDuration = (String) args[0];
            String adRemainingTime = (String) args[1];
            if (sAvidVideoPlaybackListener != null) {
                sAvidVideoPlaybackListener.recordAdDurationChangeEvent(adDuration, adRemainingTime);
            }
        }
    }

    private static void recordAdError(Object[] args) {
        if (ArrayUtils.isArrayContainsStrings(args)) {
            String error = (String) args[0];
            if (sAvidVideoPlaybackListener != null) {
                sAvidVideoPlaybackListener.recordAdError(error);
            }
        }
    }

}