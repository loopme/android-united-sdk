package com.loopme;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.FrameLayout;

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
public class LoopMeBanner extends AdWrapper {
    public static final String TEST_MPU_BANNER = "test_mpu";
    private static final String FIRST_BANNER = "FIRST_BANNER";
    private static final String SECOND_BANNER = "SECOND_BANNER";
    private static final String LOG_TAG = LoopMeBanner.class.getSimpleName();
    private Listener mMainAdListener;
    private volatile FrameLayout mBannerView;
    private String mCurrentAd = FIRST_BANNER;

    /**
     * Creates new `LoopMeBanner` object with the given appKey
     *
     * @param activity - application context
     * @param appKey   - your app key
     * @throws IllegalArgumentException if any of parameters is null
     */
    public LoopMeBanner(Activity activity, String appKey) {
        super(activity, appKey);
        mFirstLoopMeAd = LoopMeBannerGeneral.getInstance(appKey, activity);
        if (isAutoLoadingEnabled()) {
            mSecondLoopMeAd = LoopMeBannerGeneral.getInstance(appKey, activity);
        }
    }

    /**
     * Getting already initialized ad object or create new one with specified appKey
     * Note: Returns null if Android version under 4.0
     *
     * @param appKey   - your app key
     * @param activity - Activity context
     * @return instance of LoopMeBanner
     */
    public static LoopMeBanner getInstance(String appKey, Activity activity) {
        return new LoopMeBanner(activity, appKey);
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
            }
        } else {
            LoopMeTracker.post("Bind view is null");
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

    /**
     * Checks whether any view already binded to ad or not.
     *
     * @return true - if binded,
     * false - otherwise.
     */
    public boolean isViewBinded() {
        return mBannerView != null;
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
    public int getAdFormat() {
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
        if (banner != null && banner instanceof LoopMeBannerGeneral) {
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
        if (banner != null && banner instanceof LoopMeBannerGeneral) {
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
        if (banner != null && banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).setMinimizedMode(mode);
        }
    }

    private void showNativeVideo(LoopMeAd banner) {
        if (banner != null && banner instanceof LoopMeBannerGeneral) {
            ((LoopMeBannerGeneral) banner).showNativeVideo();
        }
    }

    public void switchToMinimizedMode(LoopMeAd banner) {
        if (banner != null && banner instanceof LoopMeBannerGeneral) {
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
            }

            @Override
            public void onLoopMeBannerLoadFail(LoopMeBannerGeneral banner, LoopMeError error) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeBannerLoadFail(LoopMeBanner.this, error);
                }
                increaseFailCounter(banner);
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