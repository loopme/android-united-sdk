package com.loopme.tracker.partners.moat;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.webkit.WebView;

import com.loopme.BuildConfig;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.tracker.AdIds;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.constants.Event;
import com.loopme.tracker.constants.Partner;
import com.loopme.tracker.Tracker;
import com.loopme.utils.ArrayUtils;
import com.loopme.utils.Utils;
import com.moat.analytics.mobile.loo.MoatAdEvent;
import com.moat.analytics.mobile.loo.MoatAdEventType;
import com.moat.analytics.mobile.loo.MoatAnalytics;
import com.moat.analytics.mobile.loo.MoatFactory;
import com.moat.analytics.mobile.loo.MoatOptions;
import com.moat.analytics.mobile.loo.NativeVideoTracker;
import com.moat.analytics.mobile.loo.WebAdTracker;

import java.util.HashMap;
import java.util.Map;

public class MoatTracker implements Tracker {

    private static final String LOG_TAG = MoatTracker.class.getSimpleName();
    private static final String MOAT_TRACKER_LOG_PREFIX = "Moat call event: ";
    private static final int ARGUMENTS_COUNT_2 = 2;
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;

    private WebTracker mMoatWebTracker;
    private NativeTracker mMoatNativeTracker;
    private double mPreviousVolume;

    public MoatTracker(LoopMeAd loopMeAd, AdType adType) {
        if (loopMeAd == null) {
            Logging.out(LOG_TAG, "LoopMeAd should not be null");
            return;
        }
        if (adType == AdType.NATIVE) {
            mMoatNativeTracker = new NativeTracker(loopMeAd);
        } else {
            mMoatWebTracker = new WebTracker(loopMeAd);
        }
    }

    public static void startSdk(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            Logging.out(LOG_TAG, "loopMeAd should not be null");
            return;
        }
        MoatOptions options = new MoatOptions();
        options.disableAdIdCollection = true;
        MoatAnalytics.getInstance().start(options, loopMeAd.getContext().getApplication());
        Logging.out(LOG_TAG, "Sdk started: " + Partner.MOAT.name());
    }


    @Override
    public void track(Event event, Object... args) {
        switch (event) {
            case START_MEASURING: {
                webTrackerStartTracking();
                break;
            }
            case STOP: {
                stopTracker();
                break;
            }

            case PREPARE: {
                nativeTrackerStartTracking(args);
                break;
            }
            case VIDEO_STARTED: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_START);
                break;
            }
            case FIRST_QUARTILE: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_FIRST_QUARTILE);
                break;
            }

            case MIDPOINT: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_MID_POINT);
                break;
            }
            case THIRD_QUARTILE: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_THIRD_QUARTILE);
                break;
            }
            case VIDEO_COMPLETE: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_COMPLETE);
                break;
            }
            case PAUSED: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_PAUSED);
                break;
            }
            case PLAYING: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_PLAYING);
                break;
            }
            case VIDEO_STOPPED: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_STOPPED);
                break;
            }
            case SKIPPED: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_SKIPPED);
                break;
            }
            case VOLUME_CHANGE: {
                nativeTrackerVolumeChange(args);
                break;
            }
            case ENTERED_FULLSCREEN: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_ENTER_FULLSCREEN);
                break;
            }
            case EXITED_FULLSCREEN: {
                nativeTrackerDispatchEvent(MoatAdEventType.AD_EVT_EXIT_FULLSCREEN);
                break;
            }
        }
    }

    private void nativeTrackerStartTracking(Object[] args) {
        if (ArrayUtils.isArgumentsValid(args) && args.length >= ARGUMENTS_COUNT_2) {

            MediaPlayer mediaPlayer = Utils.convertToMediaPlayer(args[FIRST_ARGUMENT]);
            View playerView = Utils.convertToView(args[SECOND_ARGUMENT]);

            if (ArrayUtils.isArgumentsValid(mediaPlayer, playerView, mMoatNativeTracker)) {
                mMoatNativeTracker.changeTargetView(playerView);
                mMoatNativeTracker.trackVideoAd(mediaPlayer, playerView);
            }
        }
    }

    private void nativeTrackerVolumeChange(Object[] args) {
        if (ArrayUtils.isArrayValid(args) && args.length >= ARGUMENTS_COUNT_2) {
            double volume = (double) args[FIRST_ARGUMENT];
            int currentPosition = (int) args[SECOND_ARGUMENT];
            if (!isVolumeChanged(volume)) {
                return;
            }
            MoatAdEvent event = new MoatAdEvent(MoatAdEventType.AD_EVT_VOLUME_CHANGE, currentPosition, volume);

            if (mMoatNativeTracker != null) {
                mMoatNativeTracker.dispatchEvent(event);
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + MoatAdEventType.AD_EVT_VOLUME_CHANGE.name());
            }
        }
    }

    private boolean isVolumeChanged(double currentVolume) {
        if (mPreviousVolume != currentVolume) {
            mPreviousVolume = currentVolume;
            return true;
        } else {
            return false;
        }
    }

    private void stopTracker() {
        if (mMoatNativeTracker != null) {
            mMoatNativeTracker.stopTracking();
            mMoatNativeTracker = null;
        }
        if (mMoatWebTracker != null) {
            mMoatWebTracker.stopTracking();
            mMoatWebTracker = null;
        }
    }

    private void webTrackerStartTracking() {
        if (mMoatWebTracker != null) {
            mMoatWebTracker.startTracking();
        }
    }

    private void nativeTrackerDispatchEvent(MoatAdEventType eventType) {
        if (mMoatNativeTracker != null) {
            mMoatNativeTracker.dispatchEvent(eventType);
        }
    }

    private class NativeTracker {
        private NativeVideoTracker mNativeTracker;
        private Map<String, String> mAdIds = new HashMap<>();
        private static final String LEVEL1 = "level1";
        private static final String LEVEL2 = "level2";
        private static final String LEVEL3 = "level3";
        private static final String LEVEL4 = "level4";
        private static final String SLICER1 = "slicer1";
        private static final String SLICER2 = "slicer2";


        private NativeTracker(LoopMeAd loopMeAd) {
            init(loopMeAd);
            setAdIds(loopMeAd.getAdParams().getAdIds());
        }

        private void init(LoopMeAd loopMeAd) {
            MoatFactory factory = MoatFactory.create();
            mNativeTracker = factory.createNativeVideoTracker(BuildConfig.MOAT_TOKEN);
            mNativeTracker.setActivity(loopMeAd.getContext());
            Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + "created native tracker");
        }

        private void stopTracking() {
            if (mNativeTracker != null) {
                mNativeTracker.stopTracking();
                mNativeTracker = null;
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + " onStopMoatTracking");
            }
        }

        private void dispatchEvent(MoatAdEventType eventType) {
            if (mNativeTracker != null) {
                mNativeTracker.dispatchEvent(new MoatAdEvent(eventType));
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + eventType.name());
            }
        }

        private void dispatchEvent(MoatAdEvent event) {
            if (mNativeTracker != null) {
                mNativeTracker.dispatchEvent(event);
            }
        }

        private void changeTargetView(View view) {
            if (mNativeTracker != null) {
                mNativeTracker.changeTargetView(view);
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + "changeTargetView");
            }
        }

        private void trackVideoAd(MediaPlayer mediaPlayer, View playerView) {
            if (mNativeTracker != null) {
                mNativeTracker.trackVideoAd(mAdIds, mediaPlayer, playerView);
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + "trackVideoAd");
            }
        }

        private void setAdIds(AdIds adIds) {
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put(LEVEL1, adIds.getAdvertiserId());
            dataMap.put(LEVEL2, adIds.getCampaignId());
            dataMap.put(LEVEL3, adIds.getLineItemId());
            dataMap.put(LEVEL4, adIds.getCreativeId());
            dataMap.put(SLICER1, adIds.getAppId());
            dataMap.put(SLICER2, adIds.getPlacementId());
            mAdIds.putAll(dataMap);
        }
    }

    private class WebTracker {
        private WebAdTracker mWebTracker;

        WebTracker(LoopMeAd loopMeAd) {
            init(loopMeAd.getDisplayController().getWebView(), loopMeAd.getContext());
        }

        private void init(WebView webView, Activity activity) {
            if (webView == null || activity == null) {
                Logging.out(LOG_TAG, "WebView or activity is null");
                return;
            }
            MoatFactory factory = MoatFactory.create();
            mWebTracker = factory.createWebAdTracker(webView);
            mWebTracker.setActivity(activity);
            Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + "created web tracker");
        }

        private void startTracking() {
            if (mWebTracker != null) {
                mWebTracker.startTracking();
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + "webTrackerStartTracking");
            }
        }

        private void stopTracking() {
            if (mWebTracker != null) {
                mWebTracker.stopTracking();
                mWebTracker = null;
                Logging.out(LOG_TAG, MOAT_TRACKER_LOG_PREFIX + "onStopMoatWebTracking");
            }
        }
    }
}
