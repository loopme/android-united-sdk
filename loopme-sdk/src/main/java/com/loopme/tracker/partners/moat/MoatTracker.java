package com.loopme.tracker.partners.moat;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebView;

import com.loopme.BuildConfig;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.constants.Partner;
import com.loopme.tracker.Tracker;
import com.loopme.utils.ArrayUtils;
import com.moat.analytics.mobile.loo.MoatAdEvent;
import com.moat.analytics.mobile.loo.MoatAdEventType;
import com.moat.analytics.mobile.loo.MoatAnalytics;
import com.moat.analytics.mobile.loo.MoatFactory;
import com.moat.analytics.mobile.loo.MoatOptions;
import com.moat.analytics.mobile.loo.NativeVideoTracker;
import com.moat.analytics.mobile.loo.WebAdTracker;

import java.util.Map;

public class MoatTracker implements Tracker {
    private static String sLOG_TAG = MoatTracker.class.getSimpleName();
    private MoatBaseTracker mMoatTracker;

    public MoatTracker(LoopMeAd loopMeAd, AdType adType) {
        switch (adType) {
            case NATIVE: {
                mMoatTracker = new MoatNativeTracker(loopMeAd);
                break;
            }
            case WEB: {
                mMoatTracker = new MoatWebTracker(loopMeAd);
                break;
            }
        }
    }

    public static void startSdk(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            Logging.out(sLOG_TAG, "loopMeAd should not be null");
            return;
        }
        MoatOptions options = new MoatOptions();
        options.disableAdIdCollection = true;
        MoatAnalytics.getInstance().start(options, loopMeAd.getContext().getApplication());
        Logging.out(sLOG_TAG, "Sdk started: " + Partner.MOAT.name());
    }


    @Override
    public void track(Event event, Object... args) {
        if (mMoatTracker != null) {
            mMoatTracker.track(event, args);
        }
    }


    private class MoatNativeTracker extends MoatBaseTracker {
        private NativeVideoTracker mTracker;
        private static final int FIRST_ARGUMENT = 0;
        private static final int SECOND_ARGUMENT = 1;
        private float mPreviousVolume;
        private final Map<String, String> mAdIds;

        protected MoatNativeTracker(LoopMeAd loopMeAd) {
            super(loopMeAd);
            mAdIds = loopMeAd.getAdParams().getAdIds().toHashMap();
            sLOG_TAG = MoatNativeTracker.class.getSimpleName();
        }

        protected void init(LoopMeAd loopMeAd) {
            mTracker = createNativeVideoTracker();
            mTracker.setActivity(loopMeAd.getContext());
            Logging.out(sLOG_TAG, "created native tracker");
        }

        @Override
        public void track(Event event, Object... args) {
            switch (event) {
                case STOP: {
                    stopTracking();
                    break;
                }
                case PREPARE: {
                    startTracking(args);
                    break;
                }
                case VIDEO_STARTED: {
                    dispatchEvent(MoatAdEventType.AD_EVT_START);
                    break;
                }
                case FIRST_QUARTILE: {
                    dispatchEvent(MoatAdEventType.AD_EVT_FIRST_QUARTILE);
                    break;
                }
                case MIDPOINT: {
                    dispatchEvent(MoatAdEventType.AD_EVT_MID_POINT);
                    break;
                }
                case THIRD_QUARTILE: {
                    dispatchEvent(MoatAdEventType.AD_EVT_THIRD_QUARTILE);
                    break;
                }
                case VIDEO_COMPLETE: {
                    dispatchEvent(MoatAdEventType.AD_EVT_COMPLETE);
                    break;
                }
                case PAUSED: {
                    dispatchEvent(MoatAdEventType.AD_EVT_PAUSED);
                    break;
                }
                case PLAYING: {
                    dispatchEvent(MoatAdEventType.AD_EVT_PLAYING);
                    break;
                }
                case VIDEO_STOPPED: {
                    dispatchEvent(MoatAdEventType.AD_EVT_STOPPED);
                    break;
                }
                case SKIPPED: {
                    dispatchEvent(MoatAdEventType.AD_EVT_SKIPPED);
                    break;
                }
                case VOLUME_CHANGE: {
                    onVolumeChange(args);
                    break;
                }
                case ENTERED_FULLSCREEN: {
                    dispatchEvent(MoatAdEventType.AD_EVT_ENTER_FULLSCREEN);
                    break;
                }
                case EXITED_FULLSCREEN: {
                    dispatchEvent(MoatAdEventType.AD_EVT_EXIT_FULLSCREEN);
                    break;
                }
            }
        }


        private void startTracking(Object[] args) {
            if (ArrayUtils.isArgumentsValid(args)
                    && args.length >= 2
                    && args[FIRST_ARGUMENT] instanceof MediaPlayer mediaPlayer
                    && args[SECOND_ARGUMENT] instanceof View playerView) {

                changeTargetView(playerView);
                trackVideoAd(mediaPlayer, playerView);
            }
        }

        private void stopTracking() {
            if (mTracker != null) {
                mTracker.stopTracking();
                mTracker = null;
                Logging.out(sLOG_TAG, "stopTracking");
            }
        }

        private void dispatchEvent(MoatAdEventType eventType) {
            if (mTracker != null) {
                mTracker.dispatchEvent(new MoatAdEvent(eventType));
                Logging.out(sLOG_TAG, eventType.name());
            }
        }

        private void changeTargetView(View view) {
            if (mTracker != null) {
                mTracker.changeTargetView(view);
                Logging.out(sLOG_TAG, "changeTargetView");
            }
        }

        private void trackVideoAd(MediaPlayer mediaPlayer, View playerView) {
            if (mTracker != null) {
                mTracker.trackVideoAd(mAdIds, mediaPlayer, playerView);
                Logging.out(sLOG_TAG, "trackVideoAd");
            }
        }

        private void onVolumeChange(Object[] args) {
            if (ArrayUtils.isArrayValid(args)
                    && args.length >= 2
                    && args[FIRST_ARGUMENT] instanceof Float
                    && args[SECOND_ARGUMENT] instanceof Integer) {

                float volume = (float) args[FIRST_ARGUMENT];
                int currentPosition = (int) args[SECOND_ARGUMENT];

                if (isVolumeChanged(volume)) {
                    MoatAdEvent event = new MoatAdEvent(MoatAdEventType.AD_EVT_VOLUME_CHANGE, currentPosition, (double) volume);
                    if (mTracker != null) {
                        mTracker.dispatchEvent(event);
                    }
                    Logging.out(sLOG_TAG, "onVolumeChange " + volume);
                }
            }
        }

        private boolean isVolumeChanged(float currentVolume) {
            if (mPreviousVolume != currentVolume) {
                mPreviousVolume = currentVolume;
                return true;
            } else {
                return false;
            }
        }
    }

    private class MoatWebTracker extends MoatBaseTracker {
        private WebAdTracker mTracker;
        private  boolean mIsTrackingStarted;

        protected MoatWebTracker(LoopMeAd loopMeAd) {
            super(loopMeAd);
            sLOG_TAG = MoatWebTracker.class.getSimpleName();
        }

        @Override
        protected void init(LoopMeAd loopMeAd) {
            mTracker = createWebAdTracker();
            mTracker.setActivity(loopMeAd.getContext());
            Logging.out(sLOG_TAG, "created web tracker");
        }

        @Override
        public void track(Event event, Object... args) {
            switch (event) {
                case NEW_ACTIVITY: {
                    onNewActivity(args);
                    break;
                }
                case START_MEASURING: {
                    startTracking();
                    break;
                }
                case STOP: {
                    stopTracking();
                    break;
                }
            }
        }

        private void startTracking() {
            if (mTracker != null && !mIsTrackingStarted) {
                mIsTrackingStarted = true;
                mTracker.startTracking();
                Logging.out(sLOG_TAG, "startTracking");
            }
        }

        private void stopTracking() {
            if (mTracker != null) {
                mTracker.stopTracking();
                mTracker = null;
                Logging.out(sLOG_TAG, "stopTracking");
            }
        }

        private void onNewActivity(Object[] args) {
            if (ArrayUtils.isArgumentsValid(args) && args[0] instanceof Activity) {
                onNewActivity((Activity) args[0]);
            }
        }

        public void onNewActivity(Activity activity) {
            if (mTracker != null) {
                mTracker.setActivity(activity);
                Logging.out(sLOG_TAG, "onNewActivity");
            }
        }
    }

    private abstract class MoatBaseTracker implements Tracker {
        private final WebView mWebView;
        private final MoatFactory mFactory;

        protected MoatBaseTracker(LoopMeAd loopMeAd) {
            mFactory = MoatFactory.create();
            mWebView = loopMeAd.getDisplayController().getWebView();
            init(loopMeAd);
        }

        protected abstract void init(LoopMeAd loopMeAd);

        protected WebAdTracker createWebAdTracker() {
            return mFactory.createWebAdTracker(mWebView);
        }

        protected NativeVideoTracker createNativeVideoTracker() {
            return mFactory.createNativeVideoTracker(BuildConfig.MOAT_TOKEN);
        }
    }
}
