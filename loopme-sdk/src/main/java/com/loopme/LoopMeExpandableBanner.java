package com.loopme;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.tracker.partners.LoopMeTracker;

/**
 * Created by katerina on 10/23/17.
 */

public class LoopMeExpandableBanner extends AdWrapper {

    public static final String TEST_MPU_BANNER = "test_mpu";
    private static final String FIRST_EXPANDABLE_BANNER = "FIRST_EXPANDABLE_BANNER";
    private static final String SECOND_EXPANDABLE_BANNER = "SECOND_EXPANDABLE_BANNER";
    private static final String LOG_TAG = LoopMeExpandableBanner.class.getSimpleName();
    private Listener mMainAdListener;
    private volatile FrameLayout mExpandableBannerView;
    private String mCurrentAd = LoopMeExpandableBanner.FIRST_EXPANDABLE_BANNER;

    /**
     * Creates new `LoopMeBanner` object with the given appKey
     *
     * @param activity - application context
     * @param appKey   - your app key
     * @throws IllegalArgumentException if any of parameters is null
     */
    public LoopMeExpandableBanner(Activity activity, String appKey) {
        super(activity, appKey);
        mFirstLoopMeAd = LoopMeExpandableBannerGeneral.getInstance(appKey, activity);
        if (isAutoLoadingEnabled()) {
            mSecondLoopMeAd = LoopMeExpandableBannerGeneral.getInstance(appKey, activity);
        }
    }

    /**
     * Getting already initialized ad object or create new one with specified appKey
     * Note: Returns null if Android version under 4.0
     *
     * @param appKey   - your app key
     * @param activity - Activity context
     * @return instance of LoopMeExpandableBanner
     */
    public static LoopMeExpandableBanner getInstance(String appKey, Activity activity) {
        return new LoopMeExpandableBanner(activity, appKey);
    }

    @Override
    public void show() {
        if (!isShowing()) {
            if (isReady(mFirstLoopMeAd)) {
                bindView(mExpandableBannerView, mFirstLoopMeAd);
                show(mFirstLoopMeAd);
                mCurrentAd = LoopMeExpandableBanner.FIRST_EXPANDABLE_BANNER;
            } else if (isReady(mSecondLoopMeAd)) {
                bindView(mExpandableBannerView, mSecondLoopMeAd);
                show(mSecondLoopMeAd);
                mCurrentAd = SECOND_EXPANDABLE_BANNER;
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
        mExpandableBannerView = viewGroup;
        bindView(mExpandableBannerView, mFirstLoopMeAd);
        bindView(mExpandableBannerView, mSecondLoopMeAd);
    }

    public void setMinimizedMode(MinimizedMode mode) {
        setMinimizedMode(mode, mFirstLoopMeAd);
        setMinimizedMode(mode, mSecondLoopMeAd);
    }

    public FrameLayout getBannerView() {
        return mExpandableBannerView;
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
        return mExpandableBannerView != null;
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
        return Constants.AdFormat.EXPANDABLE_BANNER;
    }

    @Override
    public void onAutoLoadPaused() {
        if (mMainAdListener != null) {
            mMainAdListener.onLoopMeExpandableBannerLoadFail(LoopMeExpandableBanner.this, getAutoLoadingPausedError());
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
        if (TextUtils.equals(mCurrentAd, LoopMeExpandableBanner.FIRST_EXPANDABLE_BANNER)) {
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
        if (TextUtils.equals(mCurrentAd, LoopMeExpandableBanner.FIRST_EXPANDABLE_BANNER)) {
            reload(mFirstLoopMeAd);
        } else {
            reload(mSecondLoopMeAd);
        }
    }

    private void setListener(LoopMeExpandableBannerGeneral.Listener listener, LoopMeAd banner) {
        if (banner != null && banner instanceof LoopMeExpandableBannerGeneral) {
            ((LoopMeExpandableBannerGeneral) banner).setListener(listener);
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
        if (banner != null && banner instanceof LoopMeExpandableBannerGeneral) {
            ((LoopMeExpandableBannerGeneral) banner).setMinimizedMode(mode);
        }
    }

    private void showNativeVideo(LoopMeAd banner) {
        if (banner != null && banner instanceof LoopMeExpandableBannerGeneral) {
            ((LoopMeExpandableBannerGeneral) banner).showNativeVideo();
        }
    }

    public void switchToMinimizedMode(LoopMeAd banner) {
        if (banner != null && banner instanceof LoopMeExpandableBannerGeneral) {
            ((LoopMeExpandableBannerGeneral) banner).switchToMinimizedMode();
        }
    }

    private LoopMeExpandableBannerGeneral.Listener initInternalListener() {
        return new LoopMeExpandableBannerGeneral.Listener() {

            @Override
            public void onLoopMeExpandableBannerLoadSuccess(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerLoadSuccess(LoopMeExpandableBanner.this);
                }
                resetFailCounter();
            }

            @Override
            public void onLoopMeExpandableBannerLoadFail(LoopMeExpandableBannerGeneral banner, LoopMeError error) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerLoadFail(LoopMeExpandableBanner.this, error);
                }
                increaseFailCounter(banner);
            }

            @Override
            public void onLoopMeExpandableBannerHide(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerHide(LoopMeExpandableBanner.this);
                }
            }

            @Override
            public void onLoopMeExpandableBannerShow(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerShow(LoopMeExpandableBanner.this);
                }
            }

            @Override
            public void onLoopMeExpandableBannerWrapped(LoopMeExpandableBannerGeneral banner, boolean isWrapped) {

            }

            @Override
            public void onLoopMeExpandableBannerClicked(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerClicked(LoopMeExpandableBanner.this);
                }

            }

            @Override
            public void onLoopMeExpandableBannerLeaveApp(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerLeaveApp(LoopMeExpandableBanner.this);
                }
            }

            @Override
            public void onLoopMeExpandableBannerVideoDidReachEnd(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerVideoDidReachEnd(LoopMeExpandableBanner.this);
                }
            }

            @Override
            public void onLoopMeExpandableBannerExpired(LoopMeExpandableBannerGeneral banner) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeExpandableBannerExpired(LoopMeExpandableBanner.this);
                }
            }
        };
    }

    public interface Listener {

        void onLoopMeExpandableBannerLoadSuccess(LoopMeExpandableBanner banner);

        void onLoopMeExpandableBannerLoadFail(LoopMeExpandableBanner banner, LoopMeError error);

        void onLoopMeExpandableBannerShow(LoopMeExpandableBanner banner);

        void onLoopMeExpandableBannerWrapped(LoopMeExpandableBanner banner, boolean isWrapped);

        void onLoopMeExpandableBannerHide(LoopMeExpandableBanner banner);

        void onLoopMeExpandableBannerClicked(LoopMeExpandableBanner banner);

        void onLoopMeExpandableBannerLeaveApp(LoopMeExpandableBanner banner);

        void onLoopMeExpandableBannerVideoDidReachEnd(LoopMeExpandableBanner banner);

        void onLoopMeExpandableBannerExpired(LoopMeExpandableBanner banner);
    }
}
