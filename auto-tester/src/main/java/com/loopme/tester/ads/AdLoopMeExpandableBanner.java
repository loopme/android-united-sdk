package com.loopme.tester.ads;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.loopme.LoopMeExpandableBanner;
import com.loopme.common.LoopMeError;

/**
 * Created by katerina on 10/23/17.
 */

public class AdLoopMeExpandableBanner implements LoopMeExpandableBanner.Listener, Ad {

    private LoopMeExpandableBanner mBanner;
    private FrameLayout mAdSpace;

    private AdListener mAdListener;

    public AdLoopMeExpandableBanner(Activity context, String appKey, FrameLayout view, AdListener listener, boolean autoLoadingEnabled) {
        mBanner = new LoopMeExpandableBanner(context, appKey);
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
    public void onLoopMeExpandableBannerLoadSuccess(LoopMeExpandableBanner banner) {
        if (mAdListener != null) {
            mAdListener.onLoadSuccess();
        }
    }

    @Override
    public void onLoopMeExpandableBannerLoadFail(LoopMeExpandableBanner banner, LoopMeError error) {
        if (mAdListener != null) {
            mAdListener.onLoadFail(error.getMessage());
        }
    }

    @Override
    public void onLoopMeExpandableBannerShow(LoopMeExpandableBanner banner) {
        if (mAdListener != null) {
            mAdListener.onShow();
        }
    }

    @Override
    public void onLoopMeExpandableBannerWrapped(LoopMeExpandableBanner banner, boolean isWrapped) {

    }

    @Override
    public void onLoopMeExpandableBannerHide(LoopMeExpandableBanner banner) {
        if (mAdListener != null) {
            mAdListener.onHide();
        }
    }

    @Override
    public void onLoopMeExpandableBannerClicked(LoopMeExpandableBanner banner) {

    }

    @Override
    public void onLoopMeExpandableBannerLeaveApp(LoopMeExpandableBanner banner) {

    }

    @Override
    public void onLoopMeExpandableBannerVideoDidReachEnd(LoopMeExpandableBanner banner) {

    }

    @Override
    public void onLoopMeExpandableBannerExpired(LoopMeExpandableBanner banner) {

    }
}
