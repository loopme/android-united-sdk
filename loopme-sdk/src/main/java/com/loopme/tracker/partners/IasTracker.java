package com.loopme.tracker.partners;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.integralads.avid.library.loopme.deferred.AvidDeferredAdSessionListener;
import com.integralads.avid.library.loopme.session.AbstractAvidAdSession;
import com.integralads.avid.library.loopme.session.AvidAdSessionManager;
import com.integralads.avid.library.loopme.session.AvidDisplayAdSession;
import com.integralads.avid.library.loopme.session.AvidManagedVideoAdSession;
import com.integralads.avid.library.loopme.session.AvidVideoAdSession;
import com.integralads.avid.library.loopme.session.ExternalAvidAdSessionContext;
import com.integralads.avid.library.loopme.video.AvidVideoPlaybackListener;
import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.interfaces.Tracker;
import com.loopme.utils.Utils;

public class IasTracker implements Tracker {
    private final String LOG_TAG = IasTracker.class.getSimpleName();
    private Tracker mTracker;
    private boolean mDeferred;
    private boolean mIsInFullScreenMode;
    private boolean mBackFromFullScreen;
    private ExternalAvidAdSessionContext mAdSessionContext;
    private int mAdFormat;

    public IasTracker(LoopMeAd loopMeAd, AdType type) {
        mAdFormat = loopMeAd.getAdFormat();
        if (loopMeAd == null) {
            Logging.out(LOG_TAG, "BaseAd should not be null!");
            return;
        }
        mDeferred = true;
        mAdSessionContext = createAvidAdSessionContext();
        init(type, loopMeAd.getContext());
    }

    private void init(AdType type, Context context) {
        if (type == AdType.NATIVE) {
            mTracker = new IasNativeTracker(context);
        } else if (type == AdType.WEB_VIDEO) {
            mTracker = new IasWebVideoTracker(context);
        } else if (type == AdType.WEB_IMAGE) {
            mTracker = new IasWebImageTracker(context);
        }
    }

    @Override
    public void track(Event event, Object... args) {
        if (mTracker != null) {
            mTracker.track(event, args);
        }
    }

    private ExternalAvidAdSessionContext createAvidAdSessionContext() {
        String partnerVersion = getPartnerVersion();
        return new ExternalAvidAdSessionContext(partnerVersion, mDeferred);
    }

    private String getPartnerVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private class IasNativeTracker implements Tracker {
        private AvidVideoPlaybackListener mAvidVideoPlaybackListener;
        private AvidManagedVideoAdSession mVideoAdSession;
        private boolean mVideoStarted;
        private boolean mIsFirstQuartileTracked;
        private boolean mIsMidPointTracked;
        private boolean mIsThirdQuartileTracked;
        private View mPlayerView;

        private IasNativeTracker(Context context) {
            init(context);
        }

        public void init(Context context) {
            if (context == null) {
                Logging.out(LOG_TAG, "Context should not be null!");
                return;
            }

            mVideoAdSession = AvidAdSessionManager.startAvidManagedVideoAdSession(context, mAdSessionContext);
            mAvidVideoPlaybackListener = mVideoAdSession.getAvidVideoPlaybackListener();
            Logging.out(LOG_TAG, "init " + mVideoAdSession);
            Logging.out(LOG_TAG, "init listener " + mAvidVideoPlaybackListener);
        }

        @Override
        public void track(Event event, Object... args) {
            switch (event) {
                case REGISTER: {
                    registerView(args);
                    break;
                }
                case REGISTER_FRIENDLY_VIEW: {
                    registerFriendlyView(args);
                }
                case RECORD_READY: {
                    recordReady();
                    break;
                }
                case IMPRESSION: {
                    recordAdImpressionEvent();
                    break;
                }
                case STOPPED: {
                    recordAdStoppedEvent();
                    break;
                }
                case COMPLETE: {
                    recordAdCompleteEvent();
                    break;
                }
                case CLICKED: {
                    recordAdClickThruEvent();
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
                case PAUSED: {
                    recordAdPausedEvent();
                    break;
                }
                case PLAYING: {
                    recordStartOrResume();
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
                case EXPANDED_CHANGE: {
                    recordAdExpandedChangeEvent(args);
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
                    endSession(args);
                    break;
                }
            }
        }

        private void registerFriendlyView(Object[] args) {
            if (Utils.isNotNull(args)
                    && args[0] instanceof View
                    && mVideoAdSession != null) {

                View view = (View) args[0];
                mVideoAdSession.registerFriendlyObstruction(view);
            }
        }

        private void registerView(Object[] args) {
            if (Utils.isNotNull(args)
                    && args.length >= 2
                    && args[0] instanceof Activity
                    && args[1] instanceof View
                    && mVideoAdSession != null) {

                Activity activity = (Activity) args[0];
                mPlayerView = (View) args[1];
                mVideoAdSession.registerAdView(mPlayerView, activity);
                Logging.out(LOG_TAG, "register view " + mPlayerView);
            }
        }

        private void recordReady() {
            if (mDeferred && mVideoAdSession != null) {
                AvidDeferredAdSessionListener avidDeferredAdSessionListener = mVideoAdSession.getAvidDeferredAdSessionListener();
                avidDeferredAdSessionListener.recordReadyEvent();
                Logging.out(LOG_TAG, "recordReadyEvent");
            }
        }

        private void endSession(Object[] args) {
            if (Utils.isNotNull(args) && mVideoAdSession != null) {
                mVideoAdSession.unregisterAdView(mPlayerView);
                Logging.out(LOG_TAG, "unregisterAdView()");
                mVideoAdSession.endSession();
                mVideoAdSession = null;
                Logging.out(LOG_TAG, "endSession()");
            }
            mDeferred = false;
        }

        private void recordAdImpressionEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdImpressionEvent();
                Logging.out(LOG_TAG, "recordAdImpressionEvent");
            }
        }

        private void recordStartOrResume() {
            if (!mVideoStarted) {
                recordAdVideoStartEvent();
                mVideoStarted = true;
            } else {
                recordAdPlayingEvent();
            }
        }

        private void recordAdVideoStartEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoStartEvent();
                Logging.out(LOG_TAG, "recordAdVideoStartEvent");
            }
        }

        private void recordAdStoppedEvent() {
            if (mAvidVideoPlaybackListener != null && mVideoStarted) {
                mAvidVideoPlaybackListener.recordAdStoppedEvent();
                Logging.out(LOG_TAG, "recordAdStoppedEvent");
            }
        }

        private void recordAdCompleteEvent() {
            if (mAvidVideoPlaybackListener != null) {
//                mAvidVideoPlaybackListener.recordAdCompleteEvent();
//                Logging.out(LOG_TAG, "recordAdCompleteEvent");
            }
        }

        private void recordAdVideoFirstQuartileEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoFirstQuartileEvent();
                Logging.out(LOG_TAG, "recordAdVideoFirstQuartileEvent");
            }
        }

        private void recordAdVideoMidpointEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoMidpointEvent();
                Logging.out(LOG_TAG, "recordAdVideoMidpointEvent");
            }
        }

        private void recordAdVideoThirdQuartileEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoThirdQuartileEvent();
                Logging.out(LOG_TAG, "recordAdVideoThirdQuartileEvent");
            }
        }

        private void recordAdPausedEvent() {
            if (isInterstitial()) {
                pause();
            } else if (!isBannerGoingToFullscreen()) {
                pause();
            }
        }

        private void pause() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdPausedEvent();
                Logging.out(LOG_TAG, "recordAdPausedEvent");
            }
        }

        private void recordAdPlayingEvent() {
            if (isInterstitial()) {
                resume();
            } else if (!mBackFromFullScreen) {
                resume();
            }
        }


        private void resume() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdPlayingEvent();
                Logging.out(LOG_TAG, "recordAdPlayingEvent");
            }
        }

        private void recordAdUserCloseEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdUserCloseEvent();
                Logging.out(LOG_TAG, "recordAdUserCloseEvent");
            }
        }

        private void recordAdClickThruEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdClickThruEvent();
                Logging.out(LOG_TAG, "recordAdClickThruEvent");
            }
        }

        private void recordAdSkippedEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdSkippedEvent();
                Logging.out(LOG_TAG, "recordAdSkippedEvent");
            }
        }

        private void recordAdVolumeChangeEvent(Object[] args) {
            if (Utils.hasInteger(args)) {
                Integer volume = (Integer) args[0];
                if (mAvidVideoPlaybackListener != null) {
                    mAvidVideoPlaybackListener.recordAdVolumeChangeEvent(volume);
                    Logging.out(LOG_TAG, "recordAdVolumeChangeEvent");
                }
            }
        }

        private void recordAdExpandedChangeEvent(Object[] args) {
            if (Utils.isNotNull(args) && args[0] instanceof Boolean) {
                boolean isFullScreen = (boolean) args[0];
                if (isFullScreen) {
                    recordAdEnteredFullscreenEvent();
                    mIsInFullScreenMode = true;
                    mBackFromFullScreen = false;
                } else if (mIsInFullScreenMode) {
                    recordAdExitedFullscreenEvent();
                    mBackFromFullScreen = true;
                    mIsInFullScreenMode = false;
                }
            }
        }

        private void recordAdEnteredFullscreenEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdEnteredFullscreenEvent();
                Logging.out(LOG_TAG, "recordAdEnteredFullscreenEvent");
            }
        }

        private void recordAdExitedFullscreenEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdExitedFullscreenEvent();
                Logging.out(LOG_TAG, "recordAdExitedFullscreenEvent");
            }
        }

        private void recordAdDurationChangeEvent(Object[] args) {
            if (Utils.hasInteger(args) && args.length >= 2) {
                int adDuration = (int) args[0];
                int currentPosition = (int) args[1];

                if (Utils.isFirstQuartile(adDuration, currentPosition) && !mIsFirstQuartileTracked) {
                    recordAdVideoFirstQuartileEvent();
                    mIsFirstQuartileTracked = true;

                } else if (Utils.isMidpoint(adDuration, currentPosition) && !mIsMidPointTracked) {
                    recordAdVideoMidpointEvent();
                    mIsMidPointTracked = true;

                } else if (Utils.isThirdQuartile(adDuration, currentPosition) && !mIsThirdQuartileTracked) {
                    recordAdVideoThirdQuartileEvent();
                    mIsThirdQuartileTracked = true;

                }

                // do we need to send this 100500 times per second?
//                int adRemainingTime = adDuration - currentPosition;
//                if (mAvidVideoPlaybackListener != null) {
//                    mAvidVideoPlaybackListener.recordAdDurationChangeEvent(String.valueOf(adDuration), String.valueOf(adRemainingTime));
//                }
            }
        }


        private void recordAdError(Object[] args) {
            if (Utils.hasStrings(args)) {
                String error = (String) args[0];
                if (mAvidVideoPlaybackListener != null) {
                    mAvidVideoPlaybackListener.recordAdError(error);
                    Logging.out(LOG_TAG, "recordAdError");
                }
            }
        }
    }

    private class IasWebVideoTracker extends IasWebTracker {

        private IasWebVideoTracker(Context context) {
            init(context);
        }

        @Override
        public void init(Context context) {
            if (context == null) {
                Logging.out(LOG_TAG, "Context should not be null!");
                return;
            }
            AvidVideoAdSession avidVideoAdSession = AvidAdSessionManager.startAvidVideoAdSession(context, mAdSessionContext);
            setAbstractAvidAdSession(avidVideoAdSession);
            Logging.out(LOG_TAG, "init " + avidVideoAdSession);
        }
    }

    private class IasWebImageTracker extends IasWebTracker {

        private IasWebImageTracker(Context context) {
            init(context);
        }

        @Override
        public void init(Context context) {
            if (context == null) {
                Logging.out(LOG_TAG, "Context should not be null!");
                return;
            }
            AvidDisplayAdSession avidDisplayAdSession = AvidAdSessionManager.startAvidDisplayAdSession(context, mAdSessionContext);
            setAbstractAvidAdSession(avidDisplayAdSession);
            Logging.out(LOG_TAG, "init " + avidDisplayAdSession);
        }
    }

    private abstract class IasWebTracker implements Tracker {
        private AbstractAvidAdSession<WebView> mAbstractAvidAdSession;
        private WebView mWebView;

        public abstract void init(Context context);

        @Override
        public void track(Event event, Object... args) {
            switch (event) {
                case REGISTER: {
                    registerView(args);
                    break;
                }

                case REGISTER_FRIENDLY_VIEW: {
                    registerFriendlyView(args);
                }

                case RECORD_READY: {
                    recordReady();
                    break;
                }

                case END_SESSION: {
                    endSession();
                    break;
                }
            }
        }

        protected void setAbstractAvidAdSession(AbstractAvidAdSession<WebView> mAbstractAvidAdSession) {
            this.mAbstractAvidAdSession = mAbstractAvidAdSession;
        }

        private void registerFriendlyView(Object[] args) {
            if (Utils.isNotNull(args)
                    && args[0] instanceof View
                    && mAbstractAvidAdSession != null) {

                View view = (View) args[0];
                mAbstractAvidAdSession.registerFriendlyObstruction(view);
            }
        }

        private void registerView(Object[] args) {
            if (Utils.isNotNull(args)
                    && args.length >= 2
                    && args[0] instanceof Activity
                    && args[1] instanceof WebView
                    && mAbstractAvidAdSession != null) {

                Activity activity = (Activity) args[0];
                mWebView = (WebView) args[1];
                mAbstractAvidAdSession.registerAdView(mWebView, activity);
                Logging.out(LOG_TAG, "register view " + mWebView);
            }
        }

        private void recordReady() {
            if (mDeferred && mAbstractAvidAdSession != null) {
                AvidDeferredAdSessionListener avidDeferredAdSessionListener = mAbstractAvidAdSession.getAvidDeferredAdSessionListener();
                avidDeferredAdSessionListener.recordReadyEvent();
                Logging.out(LOG_TAG, "recordReadyEvent()");
            }
        }

        private void endSession() {
            if (mAbstractAvidAdSession != null) {
                mAbstractAvidAdSession.unregisterAdView(mWebView);
                Logging.out(LOG_TAG, "unregisterAdView()");
                mAbstractAvidAdSession.endSession();
                mAbstractAvidAdSession = null;
                Logging.out(LOG_TAG, "endSession()");
            }
            mDeferred = false;
        }
    }

    private boolean isBanner() {
        return mAdFormat == Constants.AdFormat.BANNER;
    }

    private boolean isInterstitial() {
        return mAdFormat == Constants.AdFormat.INTERSTITIAL;
    }

    private boolean isFullscreenMode() {
        return mIsInFullScreenMode;
    }

    private boolean isBannerGoingToFullscreen() {
        return isBanner() && isFullscreenMode();
    }

    private boolean isBannerGoingFromFullscreen() {
        return isBanner() && !isFullscreenMode();
    }
}
