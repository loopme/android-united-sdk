package com.loopme;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.debugging.Params;
import com.loopme.tracker.partners.LoopMeTracker;

import java.text.DecimalFormat;

public abstract class AdWrapper extends AdConfig {
    private static final String LOG_TAG = AdWrapper.class.getSimpleName();
    private boolean mIsAutoLoadingPaused;
    private int mFailCounter;
    private final String mAppKey;
    private final Activity mActivity;
    private CountDownTimer mSleepLoadTimer;
    private long mStartLoadingTime;
    private int mLoadingCounter;
    private int mShowMissedCounter;
    private int mShowCounter;

    public AdWrapper(Activity activity, String appKey) {
        mActivity = activity;
        mAppKey = appKey;
    }

    public abstract Constants.AdFormat getAdFormat();
    public abstract Constants.PlacementType getPlacementType();

    public abstract void onAutoLoadPaused();

    public void load(IntegrationType integrationType) {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.setIntegrationType(integrationType);
        }
        load();
    }

    /**
     * Shows interstitial.
     * Interstitial should be loaded and ready to be shown.
     * <p>
     * As a result you'll receive onLoopMeInterstitialShow() callback
     */
    public void show() {
        if (isShowing()) {
            Logging.out(LOG_TAG, "Ad is already presented on the screen");
            return;
        }
        if (isReady(mFirstLoopMeAd)) {
            show(mFirstLoopMeAd);
        } else {
            postShowMissedEvent();
        }
    }

    public void pause() {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.pause();
        }
    }

    public void resume() {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.resume();
        }
    }

    /**
     * Indicates whether ad content was loaded successfully and ready to be displayed.
     * After you initialized a `LoopMeInterstitial`/`LoopMeBanner` object and triggered the `load` method,
     * this property will be set to TRUE on it's successful completion.
     * It is set to FALSE when loaded ad content has expired or already was presented,
     * in this case it requires next `load` method triggering
     */
    public boolean isReady() {
        return isReady(mFirstLoopMeAd);
    }

    /**
     * Indicates whether `LoopMeInterstitial`/`LoopMeBanner` currently presented on screen.
     * Ad status will be set to `AdState.SHOWING` after trigger `show` method
     *
     * @return true - if ad presented on screen
     * false - if ad absent on scrren
     */
    public boolean isShowing() {
        return isShowing(mFirstLoopMeAd);
    }

    /**
     * Indicates whether `LoopMeInterstitial`/`LoopMeBanner` in "loading ad content" process.
     * Ad status will be set to `AdState.LOADING` after trigger `load` method
     *
     * @return true - if ad is loading now
     * false - if ad is not loading now
     */
    public boolean isLoading() {
        return mFirstLoopMeAd != null && mFirstLoopMeAd.isLoading();
    }

    /**
     * Dismisses an interstitial ad
     * This method dismisses an interstitial ad and only if it is currently presented.
     * <p>
     * After it interstitial ad requires "loading process" to be ready for displaying
     * <p>
     * As a result you'll receive onLoopMeInterstitialHide() notification
     */
    public void dismiss() {
        dismiss(mFirstLoopMeAd);
    }

    /**
     * NOTE: should be in UI thread
     */
    public void destroy() {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.removeListener();
            mFirstLoopMeAd.destroy();
        }
        stopSleepLoadTimer();
    }

    public void removeListener() {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.removeListener();
        }
    }

    protected void reload(LoopMeAd loopMeAd) {
        if (isAutoLoadingEnabled()) {
            boolean isLoading = mFirstLoopMeAd != null && mFirstLoopMeAd.isLoading();
            if (!isReady(mFirstLoopMeAd) && !isLoading) {
                load(mFirstLoopMeAd);
            }
        }
    }

    protected void stopSleepLoadTimer() {
        if (mSleepLoadTimer != null) {
            Log.d(LOG_TAG, "Stop sleep timer");
            mSleepLoadTimer.cancel();
            mSleepLoadTimer = null;
        }
        mFailCounter = 0;
        mIsAutoLoadingPaused = false;
    }

    private void sleep() {
        if (mSleepLoadTimer != null) {
            return;
        }
        mSleepLoadTimer = new CountDownTimer(Constants.SLEEP_TIME, Constants.ONE_MINUTE_IN_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                Logging.out(LOG_TAG, "Till next attempt: " + millisUntilFinished / Constants.ONE_MINUTE_IN_MILLIS + " min.");
            }
            @Override
            public void onFinish() {
                stopSleepLoadTimer();
                load();
            }
        };
        float sleepTimeout = (float) Constants.SLEEP_TIME / Constants.ONE_MINUTE_IN_MILLIS;
        Logging.out(LOG_TAG, "Sleep timeout: " + sleepTimeout + " minutes");
        mSleepLoadTimer.start();
        mIsAutoLoadingPaused = true;
    }

    protected void increaseFailCounter(LoopMeAd loopMeAd) {
        if (!isAutoLoadingEnabled()) {
            return;
        }
        mFailCounter++;
        if (mFailCounter > Constants.MAX_FAIL_COUNT) {
            sleep();
        } else {
            Logging.out(LOG_TAG, "Attempt #" + mFailCounter);
            reload(loopMeAd);
        }
    }

    public void load() {
        if (isAutoLoadingEnabled() && mIsAutoLoadingPaused) {
            onAutoLoadPaused();
            return;
        }
        stopSleepLoadTimer();
        load(mFirstLoopMeAd);
    }

    protected void load(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            return;
        }
        loopMeAd.load();
        mStartLoadingTime = System.currentTimeMillis();
        mLoadingCounter++;
    }

    protected void show(LoopMeAd loopMeAd) {
        if (loopMeAd == null) {
            return;
        }
        loopMeAd.show();
        if (mLoadingCounter != mShowCounter) {
            LoopMeTracker.postDebugEvent(Params.SDK_SHOW, getPassedTime());
            mShowCounter++;
        }
    }

    protected void dismiss(LoopMeAd loopMeAd) {
        if (loopMeAd != null && loopMeAd.isShowing()) {
            loopMeAd.dismiss();
        }
    }

    protected boolean isReady(LoopMeAd loopMeAd) {
        return loopMeAd != null && loopMeAd.isReady();
    }

    protected boolean isShowing(LoopMeAd loopMeAd) {
        return loopMeAd != null && loopMeAd.isShowing();
    }

    /**
     * The appKey uniquely identifies your app to the LoopMe ad network.
     * To get an appKey visit the LoopMe Dashboard.
     */
    public String getAppKey() { return mAppKey; }

    public Context getContext() { return mActivity; }

    public void resetFailCounter() { mFailCounter = 0; }

    public LoopMeError getAutoLoadingPausedError() {
        return new LoopMeError("Paused by auto loading");
    }

    protected void postShowMissedEvent() {
        boolean isNeedSendMissedEvent = mLoadingCounter != mShowMissedCounter;
        if (isNeedSendMissedEvent) {
            LoopMeTracker.postDebugEvent(Params.SDK_MISSED, getPassedTime());
            mShowMissedCounter++;
        }
    }

    protected void onLoadedSuccess() { LoopMeTracker.postDebugEvent(Params.SDK_READY, getPassedTime()); }

    protected void onLoadFail() {
        mLoadingCounter = 0;
        mShowCounter = 0;
        mShowMissedCounter = 0;
    }

    protected String getPassedTime() {
        return new DecimalFormat("0.00")
            .format((double) (System.currentTimeMillis() - mStartLoadingTime) / 1000);
    }
}
