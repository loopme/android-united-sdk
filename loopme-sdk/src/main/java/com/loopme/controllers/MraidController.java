package com.loopme.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Constants.Layout;
import com.loopme.Logging;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.MraidOrientation;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.MraidBridgeListener;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.utils.UiUtils;
import com.loopme.utils.Utils;
import com.loopme.views.MraidView;
import com.loopme.views.activity.MraidVideoActivity;

public class MraidController implements MraidBridgeListener {

    private static final String LOG_TAG = MraidController.class.getSimpleName();
    private static final String EXTRAS_VIDEO_URL = "videoUrl";

    private final LoopMeAd mLoopMeAd;
    private final MraidView mMraidView;

    private int mWidth;
    private int mHeight;

    private MraidOrientation mForceOrientation = MraidOrientation.NONE;

    public MraidController(@NonNull LoopMeAd loopMeAd, @NonNull MraidView mraidView) {
        mMraidView = mraidView;
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

    @Override
    public void close() {
        if (mMraidView.isExpanded()) {
            ((DisplayControllerLoopMe) mLoopMeAd.getDisplayController()).collapseMraidBanner();
            return;
        }
        if (mMraidView.isResized()) {
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
    public void onMraidCallComplete(String command) { mMraidView.onMraidCallComplete(); }

    @Override
    public void onLoopMeCallComplete(String command) { mMraidView.onLoopMeCallComplete(); }

    @Override
    public void setOrientationProperties(boolean allowOrientationChange, MraidOrientation forceOrientation) {
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
        AdUtils.startAdActivity(mLoopMeAd);
        mMraidView.setIsViewable(true);
        mMraidView.setState(Constants.MraidState.EXPANDED);
        Logging.out(LOG_TAG, "expand " + useCustomClose);
    }

    @Override
    public void onLoadSuccess() {
        mMraidView.setState(Constants.MraidState.DEFAULT);
        mMraidView.notifyReady();
        mLoopMeAd.onAdLoadSuccess();
    }

    @Override
    public void onLoadFail(LoopMeError error) { mLoopMeAd.onInternalLoadFail(error); }

    @Override
    public void onChangeCloseButtonVisibility(boolean hasOwnCloseButton) {
        mLoopMeAd.getAdParams().setOwnCloseButton(hasOwnCloseButton);
        Intent intent = new Intent(Constants.MRAID_NEED_CLOSE_BUTTON);
        intent.setPackage(mLoopMeAd.getContext().getPackageName());
        intent.putExtra(Constants.EXTRAS_CUSTOM_CLOSE, hasOwnCloseButton);
        mLoopMeAd.getContext().sendBroadcast(intent);
    }

    private void setAdContainerSize(int width, int height) {
        FrameLayout banner = ((LoopMeBannerGeneral) mLoopMeAd).getBannerView();
        ViewGroup.LayoutParams params = banner.getLayoutParams();
        params.width = width;
        params.height = height;
        banner.setLayoutParams(params);
    }

    public void onCollapseBanner() {
        destroyExpandedView();
        mMraidView.notifySizeChangeEvent(mWidth, mHeight);
        mMraidView.setState(Constants.MraidState.DEFAULT);
        mMraidView.setIsViewable(true);
    }

    public int getForceOrientation() { return mForceOrientation.getActivityInfoOrientation(); }
    public boolean isExpanded() { return mMraidView.isExpanded(); }

    public void destroyExpandedView() {
        if (mMraidView.isExpanded()) {
            UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
        }
    }

    public void onRebuildView(@NonNull FrameLayout containerView) { buildView(containerView); }
    public void buildMraidContainer(@NonNull FrameLayout containerView) { buildView(containerView); }

    private void buildView(@NonNull FrameLayout containerView) {
        if (mMraidView.getParent() != null) {
            ((ViewGroup) mMraidView.getParent()).removeView(mMraidView);
        }
        containerView.addView(mMraidView, Layout.MATCH_PARENT);
    }
}