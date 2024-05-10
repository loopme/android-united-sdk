package com.loopme.time;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdTimer;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by vynnykiakiv on 6/15/17.
 */

public class Timers extends Observable {
    private static final String LOG_TAG = Timers.class.getSimpleName();
    private AdTimer mExpirationTimer;
    private AdTimer mFetcherTimer;
    private AdTimer mRequestTimer;
    private AdTimer mPrepareAssetsTimer;
    private AdTimer mPrepareVpaidJsTimer;
    private AdTimer mGdprPageReadyTimer;
    private AdTimer.Listener mPrepareAssetsTimerListener;
    private AdTimer.Listener mPrepareVpaidJsTimerListener;
    private AdTimer.Listener mFetcherTimerListener;
    private AdTimer.Listener mExpirationListener;
    private AdTimer.Listener mRequestTimerListener;
    private AdTimer.Listener mGdprPageReadyTimerListener;
    private int mValidExpirationTime;

    public Timers(Observer observer) {
        addObserver(observer);
    }

    public void setExpirationValidTime(int validExpirationTime) {
        mValidExpirationTime = validExpirationTime;
    }

    private void initRequestTimer() {
        mRequestTimerListener = new AdTimer.Listener() {

            @Override
            public void onTimeout() {
                notifyTimeout(TimersType.REQUEST_TIMER);
            }
        };
        mRequestTimer = new AdTimer(Constants.REQUEST_TIMEOUT, mRequestTimerListener);
        Logging.out(LOG_TAG, "Request timeout: " + Constants.REQUEST_TIMEOUT / 1000 + " seconds");
    }

    private void initExpirationTimer() {
        mExpirationListener = () -> notifyTimeout(TimersType.EXPIRATION_TIMER);
        mExpirationTimer = new AdTimer(mValidExpirationTime, mExpirationListener);
        Logging.out(LOG_TAG, "Expiration timeout: " + mValidExpirationTime / (1000 * 60) + " minutes");
    }

    private void initFetcherTimer() {
        mFetcherTimerListener = () -> notifyTimeout(TimersType.FETCHER_TIMER);
        mFetcherTimer = new AdTimer(Constants.FETCH_TIMEOUT, mFetcherTimerListener);
        float fetchTimeout = (float) Constants.FETCH_TIMEOUT / Constants.ONE_MINUTE_IN_MILLIS;
        Logging.out(LOG_TAG, "Fetch timeout: " + fetchTimeout + " minutes");
    }

    private void notifyTimeout(TimersType timer) {
        setChanged();
        notifyObservers(timer);
    }

    public void startTimer(TimersType timersType) {
        if (timersType != null) {
            switch (timersType) {
                case FETCHER_TIMER: {
                    startFetcherTime();
                    break;
                }
                case EXPIRATION_TIMER: {
                    startExpirationTimer();
                    break;
                }
                case REQUEST_TIMER: {
                    startRequestTimer();
                    break;
                }
                case PREPARE_ASSETS_TIMER: {
                    startPrepareAssetsTimer();
                    break;
                }
                case PREPARE_VPAID_JS_TIMER: {
                    startPrepareVpaidJsTimer();
                    break;
                }
                case GDPR_PAGE_LOADED_TIMER: {
                    startGdprPageLoadedTimer();
                    break;
                }
            }
        }
    }

    private void startGdprPageLoadedTimer() {
        if (mGdprPageReadyTimer == null) {
            initGdprPageReadyTimer();
            mGdprPageReadyTimer.start();
            Logging.out(LOG_TAG, "Gdpr page ready timer starts");

        }
    }

    private void initGdprPageReadyTimer() {
        mGdprPageReadyTimerListener = () -> notifyTimeout(TimersType.GDPR_PAGE_LOADED_TIMER);
        mGdprPageReadyTimer = new AdTimer(Constants.GDPR_PAGE_READY_TIMEOUT, mGdprPageReadyTimerListener);
        Logging.out(LOG_TAG, "Gdpr page loaded timeout: " + Constants.GDPR_PAGE_READY_TIMEOUT / 1000 + " seconds");

    }

    private void startPrepareVpaidJsTimer() {
        if (mPrepareVpaidJsTimer == null) {
            initPrepareVpaidJsTimer();
            mPrepareVpaidJsTimer.start();
            Logging.out(LOG_TAG, "Prepare vpaid js timer starts");
        }
    }

    private void initPrepareVpaidJsTimer() {
        mPrepareVpaidJsTimerListener = () -> notifyTimeout(TimersType.PREPARE_VPAID_JS_TIMER);
        mPrepareVpaidJsTimer = new AdTimer(Constants.PREPARE_VPAID_JS_TIMEOUT, mPrepareVpaidJsTimerListener);
        Logging.out(LOG_TAG, "Prepare vpaid js timeout: " + Constants.PREPARE_VPAID_JS_TIMEOUT / 1000 + " seconds");
    }

    private void startPrepareAssetsTimer() {
        if (mPrepareAssetsTimer == null) {
            initPrepareAssetsTimer();
            mPrepareAssetsTimer.start();
            Logging.out(LOG_TAG, "Prepare assets timer starts");
        }
    }

    private void initPrepareAssetsTimer() {
        mPrepareAssetsTimerListener = () -> notifyTimeout(TimersType.PREPARE_ASSETS_TIMER);
        mPrepareAssetsTimer = new AdTimer(Constants.PREPARE_VAST_ASSET_TIMEOUT, mPrepareAssetsTimerListener);
        Logging.out(LOG_TAG, "Prepare assets timeout: " + Constants.PREPARE_VAST_ASSET_TIMEOUT / 1000 + " seconds");
    }

    private void startRequestTimer() {
        if (mRequestTimer == null) {
            initRequestTimer();
            mRequestTimer.start();
            Logging.out(LOG_TAG, "Request timer starts");
        }
    }

    private void startExpirationTimer() {
        if (mExpirationTimer == null) {
            initExpirationTimer();
            mExpirationTimer.start();
            Logging.out(LOG_TAG, "Expiration timer starts");
        }
    }

    private void startFetcherTime() {
        if (mFetcherTimer == null) {
            initFetcherTimer();
            mFetcherTimer.start();
            Logging.out(LOG_TAG, "Fetcher timer starts");
        }
    }

    private void stopExpirationTimer() {
        if (mExpirationTimer != null) {
            stopTimer(mExpirationTimer);
            Logging.out(LOG_TAG, "Stop schedule expiration");
        }
        mExpirationListener = null;
    }

    private void stopFetcherTimer() {
        if (mFetcherTimer != null) {
            stopTimer(mFetcherTimer);
            Logging.out(LOG_TAG, "Stop fetcher timer");
        }
        mFetcherTimerListener = null;
    }

    private void stopRequestTimer() {
        if (mRequestTimer != null) {
            stopTimer(mRequestTimer);
            Logging.out(LOG_TAG, "Stop request timer");
        }
        mRequestTimerListener = null;
    }

    private void stopTimer(AdTimer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void destroy() {
        stopRequestTimer();
        stopFetcherTimer();
        stopExpirationTimer();
        stopPrepareAssetsTimer();
        stopPrepareVpaidJsTimer();
        stopGdprPageReadyTimer();

        mRequestTimer = null;
        mFetcherTimer = null;
        mExpirationTimer = null;
        mPrepareVpaidJsTimer = null;
        mPrepareAssetsTimer = null;
        mGdprPageReadyTimer = null;
    }

    public void stopTimer(TimersType timersType) {
        if (timersType != null) {
            switch (timersType) {
                case FETCHER_TIMER: {
                    stopFetcherTimer();
                    break;
                }
                case EXPIRATION_TIMER: {
                    stopExpirationTimer();
                    break;
                }
                case REQUEST_TIMER: {
                    stopRequestTimer();
                    break;
                }
                case PREPARE_ASSETS_TIMER: {
                    stopPrepareAssetsTimer();
                    break;
                }
                case PREPARE_VPAID_JS_TIMER: {
                    stopPrepareVpaidJsTimer();
                    break;
                }
                case GDPR_PAGE_LOADED_TIMER: {
                    stopGdprPageReadyTimer();
                    break;
                }
            }
        }
    }

    private void stopGdprPageReadyTimer() {
        if (mGdprPageReadyTimer != null) {
            stopTimer(mGdprPageReadyTimer);
            Logging.out(LOG_TAG, "Stop gdpr page loaded timer");
        }
        mPrepareVpaidJsTimerListener = null;
    }

    private void stopPrepareVpaidJsTimer() {
        if (mPrepareVpaidJsTimer != null) {
            stopTimer(mPrepareVpaidJsTimer);
            Logging.out(LOG_TAG, "Stop prepare vpaid js timer");
        }
        mPrepareVpaidJsTimerListener = null;
    }

    private void stopPrepareAssetsTimer() {
        if (mPrepareAssetsTimer != null) {
            stopTimer(mPrepareAssetsTimer);
            Logging.out(LOG_TAG, "Stop prepare assets timer");
        }
        mPrepareAssetsTimerListener = null;

    }
}
