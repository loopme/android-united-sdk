package com.loopme.tracker.partners.ias;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.integralads.avid.library.loopme.deferred.AvidDeferredAdSessionListener;
import com.integralads.avid.library.loopme.session.AbstractAvidAdSession;
import com.integralads.avid.library.loopme.session.AvidAdSessionManager;
import com.integralads.avid.library.loopme.session.AvidDisplayAdSession;
import com.integralads.avid.library.loopme.session.AvidManagedVideoAdSession;
import com.integralads.avid.library.loopme.session.ExternalAvidAdSessionContext;
import com.integralads.avid.library.loopme.video.AvidVideoPlaybackListener;
import com.loopme.BuildConfig;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.Tracker;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.utils.Utils;
import com.moat.analytics.mobile.loo.NativeVideoTracker;

public class IasTracker implements Tracker {
    private static String sLOG_TAG;
    private Tracker mTracker;
    private boolean mDeferred;
    private boolean mIsInFullScreenMode;
    private boolean mBackFromFullScreen;
    private ExternalAvidAdSessionContext mAdSessionContext;
    private Constants.AdFormat mAdFormat;
    private IasUrlProvider mUrlProvider;

    public IasTracker(LoopMeAd loopMeAd, AdType adType) {
        if (loopMeAd == null) {
            Logging.out(sLOG_TAG, "LoopMeAd should not be null!");
            return;
        }
        mUrlProvider = new IasUrlProvider(loopMeAd.getAdParams().getAdIds());
        mAdFormat = loopMeAd.getAdFormat();
        mDeferred = true;
        mAdSessionContext = createAvidAdSessionContext();
        init(adType, loopMeAd.getContext());
        printDesc(adType);
    }

    private void printDesc(AdType adType) {
        Logging.out(sLOG_TAG, "Tracker type: " + sLOG_TAG + "; Format: " + mAdFormat.name() + "; Ad type: " + adType);
    }

    private void init(AdType type, Context context) {
        if (type == AdType.NATIVE) {
            mTracker = new IasNativeTracker(context);
        } else {
            mTracker = new IasWebTracker(context);
        }
    }

    @Override
    public void track(Event event, Object... args) {
        try {
            if (mTracker != null) {
                mTracker.track(event, args);
            }
        } catch (Exception e) {
            Logging.out(sLOG_TAG, e.getMessage());
        }
    }

    private ExternalAvidAdSessionContext createAvidAdSessionContext() {
        String partnerVersion = getPartnerVersion();
        return new ExternalAvidAdSessionContext(partnerVersion, mDeferred);
    }

    private String getPartnerVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static void startSdk(LoopMeAd loopMeAd) {
        //in IAS sdk there is no such method
    }

    private class IasNativeTracker extends IasBaseTracker {
        private AvidVideoPlaybackListener mAvidVideoPlaybackListener;
        private AvidManagedVideoAdSession mVideoAdSession;
        private boolean mVideoStarted;
        private boolean mIsFirstQuartileTracked;
        private boolean mIsMidPointTracked;
        private boolean mIsThirdQuartileTracked;
        private int mPreviousVolume = -1;
        private boolean mIsInjected;
        private boolean mIsComplete;

        private IasNativeTracker(Context context) {
            sLOG_TAG = IasNativeTracker.class.getSimpleName();
            init(context);
        }

        @Override
        public void init(Context context) {
            if (context == null) {
                Logging.out(sLOG_TAG, "Context should not be null!");
                return;
            }

            mVideoAdSession = AvidAdSessionManager.startAvidManagedVideoAdSession(context, mAdSessionContext);
            setAbstractAvidAdSession(mVideoAdSession);
            mAvidVideoPlaybackListener = mVideoAdSession.getAvidVideoPlaybackListener();
            Logging.out(sLOG_TAG, "init " + mVideoAdSession.getClass().getSimpleName());
        }

        @Override
        public void track(Event event, Object... args) {
            super.track(event, args);
            switch (event) {
                case LOADED: {
                    recordAdLoadedEvent();
                    break;
                }
                case REGISTER: {
                    injectJsResForNative();
                    break;
                }
//                case IMPRESSION: {
//                    recordAdImpressionEvent();
//                    break;
//                }
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
                    handleVolumeChange(args);
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
                case CLOSE: {
                    recordAdUserCloseEvent();
                }
            }
        }

        private void recordAdLoadedEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdLoadedEvent();
                Logging.out(sLOG_TAG, "recordAdLoadedEvent");
            }
        }

        private void injectJsResForNative() {
            if (mVideoAdSession != null && !mIsInjected) {
                mVideoAdSession.injectJavaScriptResource(mUrlProvider.getCmTagUrl());
                mVideoAdSession.injectJavaScriptResource(mUrlProvider.getLoggingTagUrl());
                mIsInjected = true;
                Logging.out(sLOG_TAG, "Ias js injected: " + mUrlProvider.getCmTagUrl());
                Logging.out(sLOG_TAG, "Ias js injected: " + mUrlProvider.getLoggingTagUrl());
            }
        }

        private void recordAdImpressionEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdImpressionEvent();
                Logging.out(sLOG_TAG, "recordAdImpressionEvent");
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
                mIsComplete = false;
                mAvidVideoPlaybackListener.recordAdVideoStartEvent();
                Logging.out(sLOG_TAG, "recordAdVideoStartEvent");
            }
        }

        private void recordAdStoppedEvent() {
            if (mAvidVideoPlaybackListener != null && mVideoStarted) {
                mAvidVideoPlaybackListener.recordAdStoppedEvent();
                Logging.out(sLOG_TAG, "recordAdStoppedEvent");
            }
        }

        private void recordAdCompleteEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mIsComplete = true;
                mAvidVideoPlaybackListener.recordAdCompleteEvent();
                Logging.out(sLOG_TAG, "recordAdCompleteEvent");
            }
        }

        private void recordAdVideoFirstQuartileEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoFirstQuartileEvent();
                Logging.out(sLOG_TAG, "recordAdVideoFirstQuartileEvent");
            }
        }

        private void recordAdVideoMidpointEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoMidpointEvent();
                Logging.out(sLOG_TAG, "recordAdVideoMidpointEvent");
            }
        }

        private void recordAdVideoThirdQuartileEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVideoThirdQuartileEvent();
                Logging.out(sLOG_TAG, "recordAdVideoThirdQuartileEvent");
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
            if (mAvidVideoPlaybackListener != null && !isAdComplete()) {
                mAvidVideoPlaybackListener.recordAdPausedEvent();
                Logging.out(sLOG_TAG, "recordAdPausedEvent");
            }
        }

        private boolean isAdComplete() {
            return mIsComplete;
        }

        private void recordAdPlayingEvent() {
            if (isInterstitial()) {
                resume();
            } else if (!mBackFromFullScreen) {
                resume();
            }
        }

        private void resume() {
            if (mAvidVideoPlaybackListener != null && !isAdComplete()) {
                mAvidVideoPlaybackListener.recordAdPlayingEvent();
                Logging.out(sLOG_TAG, "recordAdPlayingEvent");
            }
        }

        private void recordAdUserCloseEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdUserCloseEvent();
                Logging.out(sLOG_TAG, "recordAdUserCloseEvent");
            }
        }

        private void recordAdClickThruEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdClickThruEvent();
                Logging.out(sLOG_TAG, "recordAdClickThruEvent");
            }
        }

        private void recordAdSkippedEvent() {
            if (mAvidVideoPlaybackListener != null) {
                mIsComplete = true;
                mAvidVideoPlaybackListener.recordAdSkippedEvent();
                Logging.out(sLOG_TAG, "recordAdSkippedEvent");
            }
        }

        private void handleVolumeChange(Object[] args) {
            if (Utils.isNotNull(args) && Utils.isFloat(args[0])) {
                int volume = (int) ((Float) args[0] * 100);
                if (isVolumeChange(volume)) {
                    recordAdVolumeChangeEvent(volume);
                }
            }
        }

        private boolean isVolumeChange(int volume) {
            if (mPreviousVolume != volume) {
                mPreviousVolume = volume;
                return true;
            } else {
                return false;
            }
        }

        private void recordAdVolumeChangeEvent(int volume) {
            if (mAvidVideoPlaybackListener != null) {
                mAvidVideoPlaybackListener.recordAdVolumeChangeEvent(volume);
                Logging.out(sLOG_TAG, "recordAdVolumeChangeEvent " + volume);
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
            if (mAvidVideoPlaybackListener != null && isBanner()) {
                mAvidVideoPlaybackListener.recordAdEnteredFullscreenEvent();
                Logging.out(sLOG_TAG, "recordAdEnteredFullscreenEvent");
            }
        }

        private void recordAdExitedFullscreenEvent() {
            if (mAvidVideoPlaybackListener != null && isBanner()) {
                mAvidVideoPlaybackListener.recordAdExitedFullscreenEvent();
                Logging.out(sLOG_TAG, "recordAdExitedFullscreenEvent");
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
                    Logging.out(sLOG_TAG, "recordAdError");
                }
            }
        }
    }

    private class IasWebTracker extends IasBaseTracker {
        private static final int VPAID_IAS_SCRIPTS_PLACEHOLDER = 62;

        private IasWebTracker(Context context) {
            sLOG_TAG = IasWebTracker.class.getSimpleName();
            init(context);
        }

        @Override
        public void init(Context context) {
            if (context == null) {
                Logging.out(sLOG_TAG, "Context should not be null!");
                return;
            }
            AvidDisplayAdSession avidDisplayAdSession = AvidAdSessionManager.startAvidDisplayAdSession(context, mAdSessionContext);
            setAbstractAvidAdSession(avidDisplayAdSession);
            Logging.out(sLOG_TAG, "init " + avidDisplayAdSession.getClass().getSimpleName());
        }

        @Override
        public void track(Event event, Object... args) {
            super.track(event, args);
            switch (event) {
                case INJECT_JS_VPAID: {
                    injectJsToVpaid(args);
                    break;
                }
            }
        }

        private void injectJsToVpaid(Object[] args) {
            if (Utils.isNotNull(args) && args[0] instanceof StringBuilder) {
                StringBuilder html = (StringBuilder) args[0];
                String scripts = mUrlProvider.getCmTagScript() + mUrlProvider.getLoggingTagScript();
                html.insert(VPAID_IAS_SCRIPTS_PLACEHOLDER, scripts);
            }
        }
    }

    private abstract class IasBaseTracker implements Tracker {
        private AbstractAvidAdSession mAbstractAvidAdSession;
        private View mView;

        protected abstract void init(Context context);

        @Override
        public void track(Event event, Object... args) {
            switch (event) {
                case REGISTER: {
                    registerView(args);
                    break;
                }

                case INJECT_JS_WEB: {
                    injectJsForWeb(args);
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
                    unregisterAndEndSession();
                    break;
                }
                case CLOSE: {
                    unregisterAndEndSession();
                    break;
                }
            }
        }

        private void injectJsForWeb(Object[] args) {
            if (isNativeTracker()) {
                return;
            }
            if (Utils.isNotNull(args) && args[0] instanceof LoopMeAd) {
                LoopMeAd loopMeAd = (LoopMeAd) args[0];
                String html = putJsToCreative(loopMeAd.getAdParams().getHtml());
                loopMeAd.getAdParams().setHtml(html);
            }
        }

        private String putJsToCreative(String html) {
            String firstPart = html.substring(0, 173);
            String secondPart = html.substring(173);
            String finalHtml = firstPart + mUrlProvider.getCmTagScript() + mUrlProvider.getLoggingTagScript();
            return finalHtml + secondPart;
        }

        private boolean isNativeTracker() {
            return this instanceof IasNativeTracker;
        }

        protected void setAbstractAvidAdSession(AbstractAvidAdSession mAbstractAvidAdSession) {
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
                    && args[1] instanceof View
                    && mAbstractAvidAdSession != null) {

                Activity activity = (Activity) args[0];
                mView = (View) args[1];
                mAbstractAvidAdSession.registerAdView(mView, activity);
                Logging.out(sLOG_TAG, "register view " + mView.getClass().getSimpleName());
            }
        }

        private void recordReady() {
            if (mDeferred && mAbstractAvidAdSession != null) {
                AvidDeferredAdSessionListener avidDeferredAdSessionListener = mAbstractAvidAdSession.getAvidDeferredAdSessionListener();
                avidDeferredAdSessionListener.recordReadyEvent();
                mDeferred = false;
                Logging.out(sLOG_TAG, "recordReadyEvent()");
            }
        }

        private void unregisterAndEndSession() {
            unregisterAdView();
            endSession();
            mAbstractAvidAdSession = null;
            mDeferred = false;
        }

        private void endSession() {
            if (mAbstractAvidAdSession != null) {
                mAbstractAvidAdSession.endSession();
                Logging.out(sLOG_TAG, "endSession()");
                Logging.out(sLOG_TAG, "==================================");
            }
        }

        private void unregisterAdView() {
            if (mAbstractAvidAdSession != null) {
                mAbstractAvidAdSession.unregisterAdView(mView);
                Logging.out(sLOG_TAG, "unregisterAdView()");
            }
        }

        protected WebView getView() {
            if (!(this instanceof NativeVideoTracker)) {
                return (WebView) mView;
            } else {
                return null;
            }
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
