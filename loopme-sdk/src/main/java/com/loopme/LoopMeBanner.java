package com.loopme;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.tracker.partners.LoopMeTracker;

/**
 * The `LoopMeBanner` class provides facilities to display a custom size ads
 * during natural transition points in your application.
 * <p>
 * It is recommended to implement `LoopMeBanner.Listener` to stay informed about ad state changes,
 * such as when an ad has been loaded or has failed to load its content, when video ad has been watched completely,
 * when an ad has been presented or dismissed from the screen, and when an ad has expired or received a tap.
 */
public final class LoopMeBanner extends AdWrapper {
    // TODO: Remove. Why we need hardcoded test_mpu? We don't have such app keys in dashboard.
    public static final String TEST_MPU_BANNER = "test_mpu";
    // TODO: What the reason of FIRST_BANNER and SECOND_BANNER? It's does not describe any logic.
    private static final String FIRST_BANNER = "FIRST_BANNER";
    private static final String SECOND_BANNER = "SECOND_BANNER";
    private static final String LOG_TAG = LoopMeBanner.class.getSimpleName();
    private Listener mMainAdListener;
    private volatile FrameLayout mBannerView;
    private String mCurrentAd = FIRST_BANNER;

    private LoopMeBanner() {
        super(null, null);
    }

    /**
     * Creates new `LoopMeBanner` object with the given appKey
     *
     * @param activity - application context
     * @param appKey   - your app key
     * @throws IllegalArgumentException if any of parameters is null
     */
    private LoopMeBanner(Activity activity, String appKey) {
        super(activity, appKey);
        mFirstLoopMeAd = LoopMeBannerGeneral.getInstance(appKey, activity);
        if (isAutoLoadingEnabled()) {
            mSecondLoopMeAd = LoopMeBannerGeneral.getInstance(appKey, activity);
        }
    }

    /**
     * Getting already initialized ad object or create new one with specified appKey
     *
     * @param appKey   your app key
     * @param activity activity context
     * @return instance of LoopMeBanner;
     * null - android version is under API 21 (5.0 LOLLIPOP) or SDK isn't initialized
     */
    @Nullable
    public static LoopMeBanner getInstance(String appKey, Activity activity) {
        return LoopMeSdk.isInitialized()
                ? new LoopMeBanner(activity, appKey)
                : null;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            if (isReady(mFirstLoopMeAd)) {
                bindView(mBannerView, mFirstLoopMeAd);
                show(mFirstLoopMeAd);
                mCurrentAd = FIRST_BANNER;
            } else if (isReady(mSecondLoopMeAd)) {
                bindView(mBannerView, mSecondLoopMeAd);
                show(mSecondLoopMeAd);
                mCurrentAd = SECOND_BANNER;
            } else {
                postShowMissedEvent();
            }
        } else {
            LoopMeTracker.post("Banner is already showing");
        }
    }

    /**
     * Links (@link LoopMeBannerView) view to banner.
     * If ad doesn't linked to @link LoopMeBannerView, it can't be display.
     *
     * @param viewGroup - @link LoopMeBannerView (container for ad) where ad will be displayed.
     */
    public void bindView(FrameLayout viewGroup) {
        mBannerView = viewGroup;
        bindView(mBannerView, mFirstLoopMeAd);
        bindView(mBannerView, mSecondLoopMeAd);
    }

    public void setMinimizedMode(MinimizedMode mode) {
        setMinimizedMode(mode, mFirstLoopMeAd);
        setMinimizedMode(mode, mSecondLoopMeAd);
    }

    public FrameLayout getBannerView() {
        return mBannerView;
    }

    /**
     * Sets listener in order to receive notifications during the loading/displaying ad processes
     *
     * @param listener - LoopMeBanner.Listener
     */
    public void setListener(Listener listener) {
        mMainAdListener = listener;
        setListener(initInternalListener(), mFirstLoopMeAd);
        setListener(initInternalListener(), mSecondLoopMeAd);
    }

    public void showNativeVideo() {
        if (!isShowing()) {
            if (isReady(mFirstLoopMeAd)) {
                showNativeVideo(mFirstLoopMeAd);
            } else if (isReady(mSecondLoopMeAd)) {
                showNativeVideo(mSecondLoopMeAd);
            }
        }
    }

    public void switchToMinimizedMode() {
        switchToMinimizedMode(mFirstLoopMeAd);
        switchToMinimizedMode(mSecondLoopMeAd);
    }

    public void switchToNormalMode() {
        switchToNormalMode(mFirstLoopMeAd);
        switchToNormalMode(mSecondLoopMeAd);
    }

    @Override
    public Constants.AdFormat getAdFormat() {
        return Constants.AdFormat.BANNER;
    }

    @Override
    public void onAutoLoadPaused() {
        if (mMainAdListener != null) {
            mMainAdListener.onLoopMeBannerLoadFail(this, getAutoLoadingPausedError());
        }
    }

    public Listener getListener() {
        return mMainAdListener;
    }

    @Override
    public void dismiss() {
        dismissCurrent();
        loadCurrentBanner();
    }

    private void dismissCurrent() {
        if (TextUtils.equals(mCurrentAd, FIRST_BANNER)) {
            dismiss(mFirstLoopMeAd);
        } else {
            dismiss(mSecondLoopMeAd);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mMainAdListener = null;
    }

    private void switchToNormalMode(LoopMeAd banner) {
        if (banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).switchToNormalMode();
        }
    }

    private void loadCurrentBanner() {
        if (TextUtils.equals(mCurrentAd, FIRST_BANNER)) {
            reload(mFirstLoopMeAd);
        } else {
            reload(mSecondLoopMeAd);
        }
    }

    private void setListener(LoopMeBannerGeneral.Listener listener, LoopMeAd banner) {
        if (banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).setListener(listener);
        }
    }

    public void removeListener() {
        super.removeListener();
        mMainAdListener = null;
    }

    private void bindView(FrameLayout viewGroup, LoopMeAd banner) {
        if (banner != null) {
            banner.bindView(viewGroup);
        }
    }

    private void setMinimizedMode(MinimizedMode mode, LoopMeAd banner) {
        if (banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).setMinimizedMode(mode);
        }
    }

    private void showNativeVideo(LoopMeAd banner) {
        if (banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).showNativeVideo();
        }
    }

    public void switchToMinimizedMode(LoopMeAd banner) {
        if (banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).switchToMinimizedMode();
        }
    }

    private LoopMeBannerGeneral.Listener initInternalListener() {
        return new LoopMeBannerGeneral.Listener() {

            @Override
            public void onLoopMeBannerLoadSuccess(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerLoadSuccess(LoopMeBanner.this);
                }
                resetFailCounter();
                onLoadedSuccess();
            }

            @Override
            public void onLoopMeBannerLoadFail(LoopMeBannerGeneral banner, LoopMeError error) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerLoadFail(LoopMeBanner.this, error);
                }
                increaseFailCounter(banner);
                onLoadFail();
            }

            @Override
            public void onLoopMeBannerHide(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerHide(LoopMeBanner.this);
                }
            }

            @Override
            public void onLoopMeBannerShow(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerShow(LoopMeBanner.this);
                }
            }

            @Override
            public void onLoopMeBannerClicked(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerClicked(LoopMeBanner.this);
                }

            }

            @Override
            public void onLoopMeBannerLeaveApp(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerLeaveApp(LoopMeBanner.this);
                }
            }

            @Override
            public void onLoopMeBannerVideoDidReachEnd(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerVideoDidReachEnd(LoopMeBanner.this);
                }
            }

            @Override
            public void onLoopMeBannerExpired(LoopMeBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerExpired(LoopMeBanner.this);
                }
            }
        };
    }

    public boolean isFullScreenMode() {
        return mFirstLoopMeAd != null && mFirstLoopMeAd.isFullScreen();
    }

    public void load(String url) {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.load(url);
        }
    }

    public interface Listener {

        void onLoopMeBannerLoadSuccess(LoopMeBanner banner);

        void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error);

        void onLoopMeBannerShow(LoopMeBanner banner);

        void onLoopMeBannerHide(LoopMeBanner banner);

        void onLoopMeBannerClicked(LoopMeBanner banner);

        void onLoopMeBannerLeaveApp(LoopMeBanner banner);

        void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner);

        void onLoopMeBannerExpired(LoopMeBanner banner);
    }
}