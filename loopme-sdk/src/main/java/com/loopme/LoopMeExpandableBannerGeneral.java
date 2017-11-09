package com.loopme;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.time.TimersType;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.UiUtils;

/**
 * Created by katerina on 10/23/17.
 */

public class LoopMeExpandableBannerGeneral extends LoopMeAd {

    private static final String LOG_TAG = LoopMeExpandableBannerGeneral.class.getSimpleName();
    private LoopMeExpandableBannerGeneral.Listener mAdListener;
    private volatile FrameLayout mBannerView;
    private boolean mIsVideoFinished;

    public LoopMeExpandableBannerGeneral(Activity context, String appKey) {
        super(context, appKey);
        Logging.out(LOG_TAG, "Start creating banner with app key: " + appKey);
    }

    public static LoopMeExpandableBannerGeneral getInstance(String appKey, Activity activity) {
        return new LoopMeExpandableBannerGeneral(activity, appKey);
    }

    public void playbackFinishedWithError() {
        mIsVideoFinished = true;
    }

    @Override
    public int getAdFormat() {
        return Constants.AdFormat.EXPANDABLE_BANNER;
    }

    @Override
    public AdSpotDimensions getAdSpotDimensions() {
        if (mBannerView != null) {
            int width = 300;
            int height = 50;
            return new AdSpotDimensions(width, height);
        }
        return new AdSpotDimensions(0, 0);
    }

    @Override
    public void onAdExpired() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerExpired(this);
        }
        Logging.out(LOG_TAG, "Ads content expired");
    }

    @Override
    public void onAdLoadSuccess() {
        stopTimer(TimersType.FETCHER_TIMER);
        long currentTime = System.currentTimeMillis();
        long loadingTime = currentTime - mStartLoadingTime;

        setReady(true);
        setAdState(Constants.AdState.NONE);
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerLoadSuccess(this);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
        Logging.out(LOG_TAG, "Ad successfully loaded (" + loadingTime + "ms)");
    }

    @Override
    public void onAdAlreadyLoaded() {
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerLoadSuccess(this);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
    }

    @Override
    public void onAdLoadFail(final LoopMeError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onLoopMeExpandableBannerLoadFail(error);
            }
        });
    }

    public void onLoopMeExpandableBannerLoadFail(LoopMeError error) {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        stopTimer(TimersType.FETCHER_TIMER);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerLoadFail(this, error);
        } else {
            Logging.out(LOG_TAG, "Warning: empty listener");
        }
        Logging.out(LOG_TAG, "Ad fails to load: " + error.getMessage());
    }

    @Override
    public void onAdLeaveApp() {
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerLeaveApp(this);
        }
        Logging.out(LOG_TAG, "Leaving application");
    }

    @Override
    public void onAdClicked() {
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerClicked(this);
        }
    }

    @Override
    public void onAdVideoDidReachEnd() {
        mIsVideoFinished = true;
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerVideoDidReachEnd(this);
        }
        Logging.out(LOG_TAG, "Video reach end");
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            UiUtils.broadcastIntent(getContext(), Constants.DESTROY_INTENT, getAdId());
            onLoopMeExpandableBannerHide();
            Logging.out(LOG_TAG, "Dismiss ad");
        }
    }

    private void onLoopMeExpandableBannerHide() {
        setReady(false);
        setAdState(Constants.AdState.NONE);
        destroyDisplayController();
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerHide(this);
        }
        Logging.out(LOG_TAG, "Ad disappeared from screen");
    }

    @Override
    public void show() {
        if (isPrepared()) {
            showInternal();
            if (isLoopMeAd()) {
                resume();
            } else if (isVastAd() || isVpaidAd() && mDisplayController != null) {
                getDisplayController().onPlay(0);
            }
            Logging.out(LOG_TAG, "Banner did start showing ad");
        } else {
            Logging.out(LOG_TAG, "Banner is not ready");
        }
    }

    private void onLoopMeExpandableBannerShow() {
        mIsVideoFinished = true;
        if (mAdListener != null) {
            mAdListener.onLoopMeExpandableBannerShow(this);
        }
        Logging.out(LOG_TAG, "Ad appeared on screen");
    }

    public void setListener(Listener listener) {
        if (listener != null) {
            mAdListener = listener;
        } else {
            Logging.out(LOG_TAG, "Warning listener is null.");
        }
    }

    public Listener getListener() {
        return mAdListener;
    }

    @Override
    public void removeListener() {
        mAdListener = null;
    }

    @Override
    public void destroy() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
                LoopMeExpandableBannerGeneral.super.destroy();
            }
        });

    }

    public void switchToMinimizedMode() {
        if (getDisplayController() != null && getDisplayController() instanceof DisplayControllerLoopMe && isShowing() && !mIsVideoFinished) {
            DisplayControllerLoopMe displayControllerLoopMe = (DisplayControllerLoopMe) getDisplayController();
            if (displayControllerLoopMe.isBackFromExpand()) {
                return;
            }
            if (displayControllerLoopMe.isMinimizedModeEnable()) {
                displayControllerLoopMe.switchToMinimizedMode();
            } else {
                pause();
            }
        }
    }

    public void setMinimizedMode(MinimizedMode mode) {
        if (mDisplayController != null && mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).setMinimizedMode(mode);
        }
    }

    void showNativeVideo() {
        if (isPrepared()) {
            showInternal();
            Logging.out(LOG_TAG, "Banner did start showing ad (native)");
        } else {
            LoopMeTracker.post("Banner is not ready");
        }
    }

    private boolean isPrepared() {
        return isReady() && isViewBinded() && !isShowing();
    }

    /**
     * @param frameLayout - @link FrameLayout (container for ad) where ad will be displayed.
     */
    public void bindView(FrameLayout frameLayout) {
        if (frameLayout != null) {
            mBannerView = frameLayout;
            mContainerView = frameLayout;
        } else {
            LoopMeTracker.post("Bind view is null");
        }
    }

    public FrameLayout getBannerView() {
        return mBannerView;
    }

    public boolean isViewBinded() {
        return mBannerView != null;
    }

    private void showInternal() {
        setAdState(Constants.AdState.SHOWING);
        stopTimer(TimersType.EXPIRATION_TIMER);
        mContainerView = mBannerView;
        buildAdView();
        mBannerView.setVisibility(View.VISIBLE);
        onLoopMeExpandableBannerShow();
//        getDisplayController().onPlay(0);
    }

    public interface Listener {

        void onLoopMeExpandableBannerLoadSuccess(LoopMeExpandableBannerGeneral banner);

        void onLoopMeExpandableBannerLoadFail(LoopMeExpandableBannerGeneral banner, LoopMeError error);

        void onLoopMeExpandableBannerShow(LoopMeExpandableBannerGeneral banner);

        void onLoopMeExpandableBannerWrapped(LoopMeExpandableBannerGeneral banner, boolean isWrapped);

        void onLoopMeExpandableBannerHide(LoopMeExpandableBannerGeneral banner);

        void onLoopMeExpandableBannerClicked(LoopMeExpandableBannerGeneral banner);

        void onLoopMeExpandableBannerLeaveApp(LoopMeExpandableBannerGeneral banner);

        void onLoopMeExpandableBannerVideoDidReachEnd(LoopMeExpandableBannerGeneral banner);

        void onLoopMeExpandableBannerExpired(LoopMeExpandableBannerGeneral banner);
    }

}
