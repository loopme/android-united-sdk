package com.loopme.tester.ads;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.loopme.LoopMeBanner;
import com.loopme.common.LoopMeError;

public class AdLoopMeBanner implements LoopMeBanner.Listener, Ad {

    private LoopMeBanner mBanner;
    private FrameLayout mAdSpace;

    private AdListener mAdListener;

    public AdLoopMeBanner(Activity context, String appKey, FrameLayout view, AdListener listener, boolean autoLoadingEnabled) {
        mBanner = new LoopMeBanner(context, appKey);
        mBanner.bindView(view);
        mAdSpace = view;
        mAdListener = listener;
        mBanner.setAutoLoading(autoLoadingEnabled);
    }

    @Override
    public void loadAd() {
        if (mBanner != null) {
            if (mBanner.isReady()) {
                mAdListener.onLoadSuccess();
            } else {
                mBanner.setListener(this);
                mBanner.load();
            }
        } else {
            mAdListener.onLoadFail("");
        }
    }

    @Override
    public void showAd() {
        if (mBanner != null) {
            mAdSpace.setVisibility(View.VISIBLE);
            mBanner.show();
        }
    }

    @Override
    public void dismissAd() {
        if (mBanner != null) {
            mBanner.dismiss();
        }
    }

    @Override
    public void destroyAd() {
        if (mBanner != null) {
            mBanner.destroy();
        }
    }

    @Override
    public void onPause() {
        if (mBanner != null) {
            mBanner.pause();
        }
    }

    @Override
    public void onResume() {
        if (mBanner != null) {
            mBanner.resume();
        }
    }

    @Override
    public boolean isReady() {
        return mBanner != null && mBanner.isReady();
    }

    @Override
    public boolean isShowing() {
        return mBanner != null && mBanner.isShowing();
    }

    @Override
    public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
        if (mAdListener != null) {
            mAdListener.onLoadSuccess();
        }
    }

    @Override
    public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
        if (mAdListener != null) {
            mAdListener.onLoadFail(error.getMessage());
        }
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner banner) {
        if (mAdListener != null) {
            mAdListener.onShow();
        }
    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner banner) {
        if (mAdListener != null) {
            mAdListener.onHide();
        }
    }

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner banner) {

    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {

    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {

    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner banner) {
        if (mAdListener != null) {
            mAdListener.onExpired();
        }
    }
}
