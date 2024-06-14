package com.loopme.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.MraidOrientation;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.mraid.MraidBridge;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.utils.UiUtils;
import com.loopme.utils.Utils;
import com.loopme.views.MraidView;
import com.loopme.views.activity.MraidVideoActivity;

public class MraidController implements MraidBridge.OnMraidBridgeListener {

    private static final String LOG_TAG = MraidController.class.getSimpleName();
    private static final String EXTRAS_VIDEO_URL = "videoUrl";

    private final LoopMeAd mLoopMeAd;
    private MraidView mMraidView;

    private int mWidth;
    private int mHeight;

    private boolean mAllowOrientationChange = true;
    private MraidOrientation mForceOrientation = MraidOrientation.NONE;

    public MraidController(@NonNull LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
        if (!mLoopMeAd.isBanner()) {
            return;
        }
        AdSpotDimensions dimensions = loopMeAd.getAdParams().getAdSpotDimensions();
        if (dimensions == null) {
            return;
        }
        mWidth = Utils.convertDpToPixel(dimensions.getWidth());
        mHeight = Utils.convertDpToPixel(dimensions.getHeight());
    }

    public void setMraidView(MraidView mraidView) { mMraidView = mraidView; }

    @Override
    public void close() {
        if (isExpanded()) {
            Logging.out(LOG_TAG, "collapse banner");
            ((DisplayControllerLoopMe) mLoopMeAd.getDisplayController()).collapseMraidBanner();
            return;
        }
        boolean isResized = mMraidView != null && mMraidView.isResized();
        if (isResized) {
            Logging.out(LOG_TAG, "banner goes back to default ");
            if (!mLoopMeAd.isBanner()) {
                return;
            }
            setAdContainerSize(mWidth, mHeight);
            mMraidView.setState(Constants.MraidState.DEFAULT);
            mMraidView.notifySizeChangeEvent(mWidth, mHeight);
            mMraidView.setIsViewable(true);
            return;
        }
        Logging.out(LOG_TAG, "close");
        mLoopMeAd.dismiss();
    }

    @Override
    public void onMraidCallComplete(String command) {
        if (mMraidView != null) {
            mMraidView.onMraidCallComplete(command);
        }
    }

    @Override
    public void onLoopMeCallComplete(String command) {
        if (mMraidView != null) {
            mMraidView.onLoopMeCallComplete(command);
        }
    }

    @Override
    public void setOrientationProperties(boolean allowOrientationChange, MraidOrientation forceOrientation) {
        mAllowOrientationChange = allowOrientationChange;
        mForceOrientation = forceOrientation;
    }

    @Override
    public void open(String url) {
        Logging.out(LOG_TAG, "open " + url);
        mLoopMeAd.onAdClicked();
        if (AdUtils.tryStartCustomTabs(mMraidView.getContext(), url))
            mLoopMeAd.onAdLeaveApp();
    }

    @Override
    public void resize(int width, int height) {
        Logging.out(LOG_TAG, "resize");
        if (!mLoopMeAd.isBanner()) {
            return;
        }
        ViewGroup.LayoutParams params = ((LoopMeBannerGeneral) mLoopMeAd).getBannerView().getLayoutParams();
        if (params != null) {
            mWidth = params.width;
            mHeight = params.height;
        }
        setAdContainerSize(width, height);
        mMraidView.setState(Constants.MraidState.RESIZED);
        mMraidView.notifySizeChangeEvent(width, height);
        mMraidView.setIsViewable(true);
    }

    private void setAdContainerSize(int width, int height) {
        if (!mLoopMeAd.isBanner()) {
            return;
        }
        FrameLayout banner = ((LoopMeBannerGeneral) mLoopMeAd).getBannerView();
        ViewGroup.LayoutParams params = banner.getLayoutParams();
        params.width = width;
        params.height = height;
        banner.setLayoutParams(params);
    }

    @Override
    public void playVideo(String url) {
        Logging.out(LOG_TAG, "playVideo");
        Context context = mMraidView.getContext();
        Intent intent = new Intent(context, MraidVideoActivity.class);
        intent.setPackage(context.getPackageName());
        intent.putExtra(EXTRAS_VIDEO_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        mMraidView.setIsViewable(true);
    }

    @Override
    public void expand(boolean useCustomClose) {
        mLoopMeAd.getAdParams().setOwnCloseButton(useCustomClose);
        AdUtils.startAdActivity(mLoopMeAd, useCustomClose);
        mMraidView.setIsViewable(true);
        mMraidView.setState(Constants.MraidState.EXPANDED);
        Logging.out(LOG_TAG, "expand " + useCustomClose);
    }

    @Override
    public void onLoadSuccess() {
        if (mMraidView != null) {
            mMraidView.setState(Constants.MraidState.DEFAULT);
            mMraidView.notifyReady();
        }
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdLoadSuccess();
        }
    }

    @Override
    public void onLoadFail(LoopMeError error) {
        if (mLoopMeAd != null) {
            mLoopMeAd.onInternalLoadFail(error);
        }
    }

    @Override
    public void onChangeCloseButtonVisibility(boolean hasOwnCloseButton) {
        if (mLoopMeAd == null) {
            return;
        }
        mLoopMeAd.getAdParams().setOwnCloseButton(hasOwnCloseButton);
        Intent intent = new Intent(Constants.MRAID_NEED_CLOSE_BUTTON);
        intent.setPackage(mLoopMeAd.getContext().getPackageName());
        intent.putExtra(Constants.EXTRAS_CUSTOM_CLOSE, hasOwnCloseButton);
        mLoopMeAd.getContext().sendBroadcast(intent);
    }

    public void onCollapseBanner() {
        destroyExpandedView();
        mMraidView.notifySizeChangeEvent(mWidth, mHeight);
        mMraidView.setState(Constants.MraidState.DEFAULT);
        mMraidView.setIsViewable(true);
    }

    public int getWidth() { return mWidth; }
    public int getHeight() { return mHeight; }

    public int getForceOrientation() {
        return mForceOrientation.getActivityInfoOrientation();
    }

    public boolean isExpanded() {
        return mMraidView != null && mMraidView.isExpanded();
    }

    public void destroyExpandedView() {
        if (isExpanded()) {
            UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
        }
    }

    public void onRebuildView(FrameLayout containerView) {
        if (containerView == null || mMraidView == null) {
            return;
        }
        if (mMraidView.getParent() != null) {
            ((ViewGroup) mMraidView.getParent()).removeView(mMraidView);
        }
        containerView.addView(mMraidView, new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        ));
    }

    public void buildMraidContainer(FrameLayout containerView) {
        if (containerView == null || mMraidView == null) {
            return;
        }
        if (mMraidView.getParent() != null) {
            ((ViewGroup) mMraidView.getParent()).removeView(mMraidView);
        }
        containerView.addView(mMraidView, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));
    }
}