package com.loopme;


import android.app.Activity;
import androidx.annotation.Nullable;

import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;

/**
 * The `LoopMeInterstitial` class provides the facilities to display a full-screen ad
 * during natural transition points in your application.
 * <p>
 * It is recommended to implement `LoopMeInterstitial.Listener`
 * to stay informed about ad state changes,
 * such as when an ad has been loaded or has failed to load its content, when video ad has been watched completely,
 * when an ad has been presented or dismissed from the screen, and when an ad has expired or received a tap.
 */
public final class LoopMeInterstitial extends AdWrapper {

    private static final String LOG_TAG = LoopMeInterstitial.class.getSimpleName();
    private Listener mMainAdListener;

    private LoopMeInterstitial() {
        super(null, null);
    }

    /**
     * Creates new `LoopMeInterstitial` object with the given appKey
     *
     * @param activity - application context
     * @param appKey   - your app key
     * @throws IllegalArgumentException if any of parameters is null
     */
    private LoopMeInterstitial(Activity activity, String appKey) {
        super(activity, appKey);
        mFirstLoopMeAd = LoopMeInterstitialGeneral.getInstance(appKey, activity);
        if (isAutoLoadingEnabled()) {
            mSecondLoopMeAd = LoopMeInterstitialGeneral.getInstance(appKey, activity);
        }
    }

    /**
     * Creates new `LoopMeInterstitial` object with the given appKey
     *
     * @param activity - application context
     * @param appKey   - your app key
     * @throws IllegalArgumentException if any of parameters is null
     */
    private LoopMeInterstitial(Activity activity, String appKey, boolean isRewarded) {
        super(activity, appKey);
        mFirstLoopMeAd = LoopMeInterstitialGeneral.getInstance(appKey, activity);
        ((LoopMeInterstitialGeneral) mFirstLoopMeAd).setRewarded(isRewarded);
        if (isAutoLoadingEnabled()) {
            mSecondLoopMeAd = LoopMeInterstitialGeneral.getInstance(appKey, activity);
            ((LoopMeInterstitialGeneral) mSecondLoopMeAd).setRewarded(isRewarded);
        }
    }

    /**
     * Getting already initialized ad object or create new one with specified appKey
     *
     * @param appKey   your app key
     * @param activity activity context
     * @return instance of LoopMeInterstitial;
     * null - android version is under API 21 (5.0 LOLLIPOP) or SDK isn't initialized
     */
    @Nullable
    public static LoopMeInterstitial getInstance(String appKey, Activity activity) {
        return LoopMeSdk.isInitialized()
                ? new LoopMeInterstitial(activity, appKey)
                : null;
    }

    /**
     * Getting already initialized ad object or create new one with specified appKey
     *
     * @param appKey   your app key
     * @param activity activity context
     * @return instance of LoopMeInterstitial;
     * null - android version is under API 21 (5.0 LOLLIPOP) or SDK isn't initialized
     */
    @Nullable
    public static LoopMeInterstitial getInstance(String appKey, Activity activity, boolean isRewarded) {
        return LoopMeSdk.isInitialized()
                ? new LoopMeInterstitial(activity, appKey, isRewarded)
                : null;
    }


    @Override
    public Constants.AdFormat getAdFormat() {
        return Constants.AdFormat.INTERSTITIAL;
    }

    @Override
    public void onAutoLoadPaused() {
        if (mMainAdListener != null) {
            mMainAdListener.onLoopMeInterstitialLoadFail(this, getAutoLoadingPausedError());
        }
    }

    /**
     * Sets listener in order to receive notifications during the loading/displaying ad processes
     *
     * @param listener - LoopMeInterstitial.Listener
     */
    public void setListener(Listener listener) {
        mMainAdListener = listener;
        setListener(initInternalListener(), mFirstLoopMeAd);
        setListener(initInternalListener(), mSecondLoopMeAd);
    }

    private void setListener(LoopMeInterstitialGeneral.Listener listener, LoopMeAd interstitial) {
        if (interstitial != null) {
            ((LoopMeInterstitialGeneral) interstitial).setListener(listener);
        }
    }

    public Listener getListener() {
        return mMainAdListener;
    }

    public void removeListener() {
        super.removeListener();
        mMainAdListener = null;
    }

    private LoopMeInterstitialGeneral.Listener initInternalListener() {
        return new LoopMeInterstitialGeneral.Listener() {
            @Override
            public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialLoadSuccess(LoopMeInterstitial.this);
                }
                resetFailCounter();
                onLoadedSuccess();
            }

            @Override
            public void onLoopMeInterstitialLoadFail(LoopMeInterstitialGeneral interstitial, LoopMeError error) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialLoadFail(LoopMeInterstitial.this, error);
                }
                increaseFailCounter(interstitial);
                onLoadFail();
            }

            @Override
            public void onLoopMeInterstitialHide(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialHide(LoopMeInterstitial.this);
                }
                reload(interstitial);
            }

            @Override
            public void onLoopMeInterstitialShow(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialShow(LoopMeInterstitial.this);
                }
            }

            @Override
            public void onLoopMeInterstitialClicked(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialClicked(LoopMeInterstitial.this);
                }
            }

            @Override
            public void onLoopMeInterstitialLeaveApp(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialLeaveApp(LoopMeInterstitial.this);
                }
            }

            @Override
            public void onLoopMeInterstitialExpired(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialExpired(LoopMeInterstitial.this);
                }
            }

            @Override
            public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitialGeneral interstitial) {
                if (mMainAdListener != null) {
                    mMainAdListener.onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial.this);
                }
            }
        };
    }

    public void load(String url) {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.load(url);
        }
    }

    public interface Listener {

        void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial);

        void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error);

        void onLoopMeInterstitialShow(LoopMeInterstitial interstitial);

        void onLoopMeInterstitialHide(LoopMeInterstitial interstitial);

        void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial);

        void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial);

        void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial);

        void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial);
    }
}