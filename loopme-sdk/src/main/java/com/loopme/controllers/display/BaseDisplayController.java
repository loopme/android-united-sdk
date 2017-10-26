package com.loopme.controllers.display;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.DisplayController;
import com.loopme.models.Message;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.tracker.interfaces.AdEvents;
import com.loopme.tracker.viewability.EventManager;
import com.loopme.utils.InternetUtils;
import com.loopme.utils.UiUtils;
import com.loopme.vast.VastVpaidEventTracker;

public abstract class BaseDisplayController implements DisplayController, AdEvents {

    private static final long DELAY_UNTIL_EXECUTE = 100;
    protected String mLogTag;
    private EventManager mEventManager;
    private LoopMeAd mLoopMeAd;


    public BaseDisplayController(LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
    }

    @Override
    public void onStartLoad() {
        initEventManager();
    }

    protected void initTrackers() {
        if (mEventManager == null) {
            return;
        }
        if (isNativeTrackerNeeded()) {
            onInitTracker(AdType.NATIVE);
        } else {
            onInitTracker(AdType.WEB);
        }
    }

    private boolean isNativeTrackerNeeded() {
        return mLoopMeAd.isVideo360() || mLoopMeAd.getDisplayController() instanceof DisplayControllerVast;
    }

    protected void initEventManager() {
        if (isTrackerNeeded()) {
            mEventManager = new EventManager(mLoopMeAd);
        }
    }

    private boolean isTrackerNeeded() {
        return mLoopMeAd != null
                && mLoopMeAd.getAdParams() != null
                && !mLoopMeAd.getAdParams().getTrackers().isEmpty();
    }

    protected void onInternalLoadFail(final LoopMeError error) {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoopMeAd != null) {
                    mLoopMeAd.onInternalLoadFail(error);
                }
            }
        });
    }

    protected void onPostWarning(final LoopMeError error) {
        onUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoopMeAd != null) {
                    mLoopMeAd.onPostWarning(error);
                }
            }
        });
    }

    protected void onUiThread(Runnable runnable) {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThread(runnable);
        }
    }

    protected void postDelayed(Runnable action, long delayMillis) {
        if (mLoopMeAd != null) {
            mLoopMeAd.runOnUiThreadDelayed(action, delayMillis);
        }
    }

    @Override
    public void onResume() {
        onAdResumedEvent();
    }

    @Override
    public void onPause() {
        onAdPausedEvent();
    }

    @Override
    public void onDestroy() {
        onAdDestroyedEvent();
        VastVpaidEventTracker.clear();
    }

    @Override
    public void onMessage(Message type, String message) {
        switch (type) {
            case ERROR: {
                LoopMeTracker.post(message);
                onAdErrorEvent(message);
                break;
            }
            case EVENT: {
                VastVpaidEventTracker.postEvent(message);
                break;
            }
            case LOG: {
                Logging.out(mLogTag, message);
                break;
            }
        }
    }

    @Override
    public boolean onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        onAdClickedEvent();
        onMessage(Message.LOG, "Handle url");
        LoopMeAdHolder.putAd(loopMeAd);
        Intent intent = UiUtils.createRedirectIntent(url, loopMeAd);
        loopMeAd.getContext().startActivity(intent);
        return true;
    }

    // events region
    @Override
    public void onAdDurationEvents(int currentPosition, int videoDuration) {
        if (mEventManager != null) {
            mEventManager.onAdDurationEvents(currentPosition, videoDuration);
        }
    }

    @Override
    public void onAdDestroyedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdDestroyedEvent();
        }
    }

    @Override
    public void onAdResumedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdResumedEvent();
        }
    }

    @Override
    public void onAdStartedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdStartedEvent();
        }
    }

    @Override
    public void onAdStartedEvent(WebView webView, MediaPlayer mediaPlayer) {
        if (mEventManager != null) {
            mEventManager.onAdStartedEvent(webView, mediaPlayer);
        }
    }

    @Override
    public void onAdPausedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdPausedEvent();
        }
    }

    @Override
    public void onAdErrorEvent(String message) {
        if (mEventManager != null) {
            mEventManager.onAdErrorEvent(message);
        }
    }

    @Override
    public void onAdLoadedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdLoadedEvent();
        }
    }

    @Override
    public void onAdStoppedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdStoppedEvent();
        }
    }

    @Override
    public void onAdClickedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdClickedEvent();
        }
    }

    @Override
    public void onAdCompleteEvent() {
        if (mEventManager != null) {
            mEventManager.onAdCompleteEvent();
        }
    }

    @Override
    public void onAdVolumeChangedEvent(double volume, int currentPosition) {
        if (mEventManager != null) {
            mEventManager.onAdVolumeChangedEvent(volume, currentPosition);
        }
    }

    @Override
    public void onAdUserMinimizeEvent() {
        if (mEventManager != null) {
            mEventManager.onAdUserMinimizeEvent();
        }
    }

    @Override
    public void onAdUserCloseEvent() {
        if (mEventManager != null) {
            mEventManager.onAdUserCloseEvent();
        }
    }

    @Override
    public void onAdEnteredFullScreenEvent() {
        if (mEventManager != null) {
            mEventManager.onAdEnteredFullScreenEvent();
        }
    }

    @Override
    public void onAdExitedFullScreenEvent() {
        if (mEventManager != null) {
            mEventManager.onAdExitedFullScreenEvent();
        }
    }

    @Override
    public void onAdSkippedEvent() {
        if (mEventManager != null) {
            mEventManager.onAdSkippedEvent();
        }
    }

    @Override
    public void onAdUserAcceptInvitationEvent() {
        if (mEventManager != null) {
            mEventManager.onAdUserAcceptInvitationEvent();
        }
    }

    @Override
    public void onAdImpressionEvent() {
        if (mEventManager != null) {
            mEventManager.onAdImpressionEvent();
        }
    }

    @Override
    public void onAdPreparedEvent(MediaPlayer mediaPlayer, View playerView) {
        if (mEventManager != null) {
            mEventManager.onAdPreparedEvent(mediaPlayer, playerView);
        }
    }

    @Override
    public void onInitTracker(AdType type) {
        if (mEventManager != null) {
            mEventManager.onInitTracker(type);
        }
    }

    @Override
    public void onStartWebMeasuringDelayed() {
        mLoopMeAd.getContainerView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mEventManager != null) {
                    mEventManager.onStartWebMeasuringDelayed();
                }
            }
        }, DELAY_UNTIL_EXECUTE);
    }
}
