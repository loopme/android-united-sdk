package com.loopme.controllers.display;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.DisplayController;
import com.loopme.models.Message;
import com.loopme.tracker.constants.AdType;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.tracker.AdEvents;
import com.loopme.tracker.EventManager;

public abstract class BaseTrackableController implements DisplayController, AdEvents {

    private static final long DELAY_UNTIL_EXECUTE = 100;
    protected String mLogTag;
    private EventManager mEventManager;
    private LoopMeAd mLoopMeAd;
    private boolean mIsImpressionTracked;
    private String mOrientation;

    public BaseTrackableController(LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
        setOrientation();
    }

    private void setOrientation() {
        if (mLoopMeAd != null) {
            mOrientation = mLoopMeAd.getAdParams().getAdOrientation();
        }
    }

    @Override
    public void onStartLoad() {
        if (isEventManagerNeeded())
            mEventManager = new EventManager(mLoopMeAd);
    }

    protected void initTrackers() {
        if (isNativeAd()) {
            onInitTracker(AdType.NATIVE);
        } else {
            onInitTracker(AdType.WEB);
        }
    }

    // TODO.
    private boolean isNativeAd() {
        return mLoopMeAd != null && !mLoopMeAd.isMraidAd() && !mLoopMeAd.isVpaidAd() && (mLoopMeAd.isVideo360() || mLoopMeAd.isVastAd());
    }

    private boolean isEventManagerNeeded() {
        if (mLoopMeAd == null) {
            return false;
        }
        AdParams adParams = mLoopMeAd.getAdParams();
        return adParams != null && !adParams.getTrackers().isEmpty();
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
                    mLoopMeAd.onSendPostWarning(error);
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
    }

    @Override
    public void onMessage(Message type, String message) {
        switch (type) {
            case ERROR: {
                LoopMeTracker.post(message);
                onAdErrorEvent(message);
                break;
            }
            case LOG: {
                Logging.out(mLogTag, message);
                break;
            }
        }
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        onAdClickedEvent();
        onMessage(Message.LOG, "Handle url");

        if (AdUtils.tryStartCustomTabs(loopMeAd.getContext(), url))
            loopMeAd.onAdLeaveApp();
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
    public void onAdVolumeChangedEvent(float volume, int currentPosition) {
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

    @Override
    public void onAdRecordReady() {
        if (mEventManager != null) {
            mEventManager.onAdRecordReady();
        }
    }

    @Override
    public void onAdRegisterView(Activity activity, View view) {
        if (mEventManager != null) {
            mEventManager.onAdRegisterView(activity, view);
        }
    }

    @Override
    public void onAdInjectJs(LoopMeAd loopMeAd) {
        if (mEventManager != null) {
            mEventManager.onAdInjectJs(loopMeAd);
        }
    }

    @Override
    public void onAdInjectJsVpaid(StringBuilder html) {
        if (mEventManager != null) {
            mEventManager.onAdInjectJsVpaid(html);
        }
    }

    @Override
    public void onAdRecordAdClose() {
        if (mEventManager != null) {
            mEventManager.onAdRecordAdClose();
        }
    }

    public void postImpression() {
        if (!mIsImpressionTracked) {
            onAdRecordReady();
            onAdLoadedEvent();
            onAdImpressionEvent();
            mIsImpressionTracked = true;
        }
    }

    @Override
    public int getOrientation() {
        return getOrientationFromAdParams();
    }

    protected int getOrientationFromAdParams() {
        if (TextUtils.equals(mOrientation, Constants.ORIENTATION_PORT)) {
            return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        } else if (TextUtils.equals(mOrientation, Constants.ORIENTATION_LAND)) {
            return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        } else {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }
    }

    protected boolean isTrackerAvailable() {
        return mEventManager != null;
    }

    @Override
    public void onNewActivity(Activity activity) {
        if (mEventManager != null) {
            mEventManager.onNewActivity(activity);
        }
    }
}
