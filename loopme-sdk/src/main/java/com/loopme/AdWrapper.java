package com.loopme;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;

import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.interfaces.DisplayController;
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
        this.mActivity = activity;
        this.mAppKey = appKey;
    }

    public abstract Constants.AdFormat getAdFormat();

    public abstract void onAutoLoadPaused();

    public void load(IntegrationType integrationType) {
        if (mFirstLoopMeAd != null && mSecondLoopMeAd != null) {
            mFirstLoopMeAd.setIntegrationType(integrationType);
            mSecondLoopMeAd.setIntegrationType(integrationType);
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
        if (!isShowing()) {
            if (isReady(mFirstLoopMeAd)) {
                show(mFirstLoopMeAd);
            } else if (isReady(mSecondLoopMeAd)) {
                show(mSecondLoopMeAd);
            } else {
                postShowMissedEvent();
            }
        } else {
            Logging.out(LOG_TAG, "Ad is already presented on the screen");
        }
    }

    public void pause() {
        pause(mFirstLoopMeAd);
        pause(mSecondLoopMeAd);
    }

    public void resume() {
        resume(mFirstLoopMeAd);
        resume(mSecondLoopMeAd);
    }

    /**
     * Indicates whether ad content was loaded successfully and ready to be displayed.
     * After you initialized a `LoopMeInterstitial`/`LoopMeBanner` object and triggered the `load` method,
     * this property will be set to TRUE on it's successful completion.
     * It is set to FALSE when loaded ad content has expired or already was presented,
     * in this case it requires next `load` method triggering
     */
    public boolean isReady() {
        return isReady(mFirstLoopMeAd) || isReady(mSecondLoopMeAd);
    }

    /**
     * Indicates whether `LoopMeInterstitial`/`LoopMeBanner` currently presented on screen.
     * Ad status will be set to `AdState.SHOWING` after trigger `show` method
     *
     * @return true - if ad presented on screen
     * false - if ad absent on scrren
     */
    public boolean isShowing() {
        return isShowing(mFirstLoopMeAd) || isShowing(mSecondLoopMeAd);
    }

    /**
     * Indicates whether `LoopMeInterstitial`/`LoopMeBanner` in "loading ad content" process.
     * Ad status will be set to `AdState.LOADING` after trigger `load` method
     *
     * @return true - if ad is loading now
     * false - if ad is not loading now
     */
    public boolean isLoading() {
        return isLoading(mFirstLoopMeAd) || isLoading(mSecondLoopMeAd);
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
        dismiss(mSecondLoopMeAd);
    }

    /**
     * NOTE: should be in UI thread
     */
    public void destroy() {
        destroy(mFirstLoopMeAd);
        destroy(mSecondLoopMeAd);
        stopSleepLoadTimer();
    }

    public DisplayController getAdController() {
        if (isReady(mFirstLoopMeAd)) {
            return mFirstLoopMeAd.getDisplayController();
        } else if (isReady(mSecondLoopMeAd)) {
            return mSecondLoopMeAd.getDisplayController();
        } else {
            return null;
        }
    }

    public void removeListener() {
        removeListener(mFirstLoopMeAd);
        removeListener(mSecondLoopMeAd);
    }


    private void removeListener(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.removeListener();
        }
    }

    protected void reload(LoopMeAd loopMeAd) {
        if (isAutoLoadingEnabled()) {
            if (!isReady(mFirstLoopMeAd) && !isLoading(mFirstLoopMeAd)) {
                load(mFirstLoopMeAd);
            }
            if (!isReady(mSecondLoopMeAd) && !isLoading(mSecondLoopMeAd)) {
                load(mSecondLoopMeAd);
            }
        }
    }

    protected void stopSleepLoadTimer() {
        if (mSleepLoadTimer != null) {
            Logging.out(LOG_TAG, "Stop sleep timer");
            mSleepLoadTimer.cancel();
            mSleepLoadTimer = null;
        }
        mFailCounter = 0;
        mIsAutoLoadingPaused = false;
    }

    private void sleep() {
        if (mSleepLoadTimer == null) {
            mSleepLoadTimer = initSleepLoadTimer();
            float sleepTimeout = (float) Constants.SLEEP_TIME / Constants.ONE_MINUTE_IN_MILLIS;
            Logging.out(LOG_TAG, "Sleep timeout: " + sleepTimeout + " minutes");
            mSleepLoadTimer.start();
            mIsAutoLoadingPaused = true;
        }
    }

    protected void increaseFailCounter(LoopMeAd loopMeAd) {
        if (isAutoLoadingEnabled()) {
            mFailCounter++;
            if (mFailCounter > Constants.MAX_FAIL_COUNT) {
                sleep();
            } else {
                Logging.out(LOG_TAG, "Attempt #" + mFailCounter);
                reload(loopMeAd);
            }
        }
    }

    private CountDownTimer initSleepLoadTimer() {
        return new CountDownTimer(Constants.SLEEP_TIME, Constants.ONE_MINUTE_IN_MILLIS) {
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
    }

    private void pause(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.pause();
        }
    }

    private void resume(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.resume();
        }
    }

    private void destroy(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.removeListener();
            loopMeAd.destroy();
        }
    }

    public void load() {
        if (isAutoLoadingPaused()) {
            onAutoLoadPaused();
            return;
        }
        stopSleepLoadTimer();
        load(mFirstLoopMeAd);
        if (isAutoLoadingEnabled()) {
            load(mSecondLoopMeAd);
        }
    }

    protected void load(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.load();
            onLoad();
        }
    }

    protected void show(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.show();
            onShow();
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

    private boolean isLoading(LoopMeAd loopMeAd) {
        return loopMeAd != null && loopMeAd.isLoading();
    }

    /**
     * The appKey uniquely identifies your app to the LoopMe ad network.
     * To get an appKey visit the LoopMe Dashboard.
     */
    public String getAppKey() {
        return mAppKey;
    }

    public Context getContext() {
        return mActivity;
    }

    private boolean isAutoLoadingPaused() {
        return isAutoLoadingEnabled() && mIsAutoLoadingPaused;
    }

    public void resetFailCounter() {
        mFailCounter = 0;
    }

    public LoopMeError getAutoLoadingPausedError() {
        return new LoopMeError("Paused by auto loading");
    }

    protected void postShowMissedEvent() {
        if (isNeedSendMissedEvent()) {
            LoopMeTracker.postDebugEvent(Params.SDK_MISSED, getPassedTime());
            mShowMissedCounter++;
        }
    }

    private void onShow() {
        if (isNeedSendShowEvent()) {
            LoopMeTracker.postDebugEvent(Params.SDK_SHOW, getPassedTime());
            mShowCounter++;
        }
    }

    protected void onLoadedSuccess() {
        LoopMeTracker.postDebugEvent(Params.SDK_READY, getPassedTime());
    }

    protected void onLoadFail() {
        resetCounters();
    }

    private void resetCounters() {
        mLoadingCounter = 0;
        mShowCounter = 0;
        mShowMissedCounter = 0;
    }


    private void onLoad() {
        mStartLoadingTime = System.currentTimeMillis();
        mLoadingCounter++;
    }

    protected String getPassedTime() {
        double time = (double) (System.currentTimeMillis() - mStartLoadingTime) / 1000;
        return new DecimalFormat("0.00").format(time);
    }

    private boolean isNeedSendMissedEvent() {
        return mLoadingCounter != mShowMissedCounter;
    }

    private boolean isNeedSendShowEvent() {
        return mLoadingCounter != mShowCounter;
    }
}
