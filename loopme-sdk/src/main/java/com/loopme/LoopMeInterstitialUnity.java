package com.loopme;

import android.app.Activity;

import com.loopme.common.LoopMeError;
import com.unity3d.player.UnityPlayer;


// TODO. Move to unity-android-sdk aar lib.
public class LoopMeInterstitialUnity implements LoopMeInterstitial.Listener {

    private static final String LOG_TAG = LoopMeInterstitialUnity.class.getSimpleName();

    private volatile LoopMeInterstitial mLoopMeInterstitial;
    private volatile Activity mActivity;

    /**
     * Create instance of interstitial for Unity
     *
     * @param activity
     * @throws IllegalArgumentException if activity is null
     */
    public LoopMeInterstitialUnity(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("activity is null");
        } else {
            mActivity = activity;
        }
    }

    public synchronized void setAppKey(final String appKey) {
        Logging.out(LOG_TAG, "setAppKey = " + appKey);
        if ((appKey == null) || appKey == "") {
            Logging.out(LOG_TAG, "wrong app key");
        } else {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mLoopMeInterstitial = LoopMeInterstitial.getInstance(appKey, mActivity);
                    mLoopMeInterstitial.setListener(LoopMeInterstitialUnity.this);
                }
            });
        }
    }

    public void load() {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mLoopMeInterstitial != null) {
                    mLoopMeInterstitial.load();
                }
            }
        });
    }

    public void show() {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mLoopMeInterstitial != null) {
                    mLoopMeInterstitial.show();
                }
            }
        });
    }

    public void destroy() {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mLoopMeInterstitial != null) {
                    mLoopMeInterstitial.destroy();
                }
            }
        });
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialLoadSuccess");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialDidLoadNotification", "");
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial,
                                             LoopMeError error) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialLoadFail");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialDidFailToLoadAdNotification", "");
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialShow");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialDidAppearNotification", "");
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialHide");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialDidDisappearNotification", "");
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialClicked");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialDidReceiveTapNotification", "");
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialLeaveApp");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialWillLeaveApplicationNotification", "");
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialExpired");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialDidExpireNotification", "");
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        Logging.out(LOG_TAG, "onLoopMeInterstitialVideoDidReachEnd");
        UnityPlayer.UnitySendMessage("LoopMeEventsManager", "interstitialVideoDidReachEnd", "");
    }
}