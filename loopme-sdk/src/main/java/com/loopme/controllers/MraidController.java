package com.loopme.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.MraidOrientation;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.mraid.MraidBridge;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.parser.ParseService;
import com.loopme.utils.InternetUtils;
import com.loopme.utils.Utils;
import com.loopme.views.MraidView;
import com.loopme.views.activity.AdBrowserActivity;
import com.loopme.views.activity.MraidVideoActivity;

public class MraidController implements MraidBridge.OnMraidBridgeListener {

    private static final String LOG_TAG = MraidController.class.getSimpleName();
    private static final String EXTRAS_VIDEO_URL = "videoUrl";

    private LoopMeAd mLoopMeAd;
    private MraidView mMraidView;

    private int mWidth;
    private int mHeight;

    private boolean mAllowOrientationChange = true;
    private MraidOrientation mForceOrientation = MraidOrientation.NONE;

    public MraidController(LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
    }

    public void setMraidView(MraidView mraidView) {
        mMraidView = mraidView;
    }

    @Override
    public void close() {
        if (mMraidView.isExpanded()) {
            Logging.out(LOG_TAG, "collapse banner");
            ((DisplayControllerLoopMe) mLoopMeAd.getDisplayController()).collapseMraidBanner();
        } else {
            Logging.out(LOG_TAG, "close");
            mLoopMeAd.dismiss();
        }
    }

    @Override
    public void onNativeCallComplete(String command) {
        mMraidView.onNativeCallComplete(command);
    }

    @Override
    public void setOrientationProperties(boolean allowOrientationChange, MraidOrientation forceOrientation) {
        mAllowOrientationChange = allowOrientationChange;
        mForceOrientation = forceOrientation;
    }

    @Override
    public void open(String url) {
        Logging.out(LOG_TAG, "open " + url);
        Context context = mMraidView.getContext();
        if (InternetUtils.isOnline(context)) {
            Intent intent = new Intent(context, AdBrowserActivity.class);
            intent.putExtra(Constants.EXTRA_URL, url);
            intent.putExtra(Constants.AD_ID_TAG, mLoopMeAd.getAdId());
            intent.putExtra(Constants.FORMAT_TAG, mLoopMeAd.getAdFormat());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Logging.out(LOG_TAG, "No internet connection");
        }
    }

    @Override
    public void resize(int width, int height) {
        Logging.out(LOG_TAG, "resize");
        if (mLoopMeAd.getAdFormat() == Constants.AdFormat.BANNER) {
            setBannerSize(width, height);
            mMraidView.resize();
            mMraidView.setState(Constants.MraidState.RESIZED);
            mMraidView.notifySizeChangeEvent(width, height);
            mMraidView.setIsViewable(true);
        }
    }

    private void setBannerSize(int width, int height) {
        if (mLoopMeAd.getAdFormat() == Constants.AdFormat.BANNER) {
            LoopMeBannerGeneral banner = (LoopMeBannerGeneral) mLoopMeAd;
            ViewGroup.LayoutParams params = banner.getBannerView().getLayoutParams();
            params.width = width;
            params.height = height;
            banner.getBannerView().setLayoutParams(params);
        }
    }

    @Override
    public void playVideo(String url) {
        Logging.out(LOG_TAG, "playVideo");
        Intent i = new Intent(mMraidView.getContext(), MraidVideoActivity.class);
        i.putExtra(EXTRAS_VIDEO_URL, url);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mMraidView.getContext().startActivity(i);
        mMraidView.setIsViewable(true);
    }

    @Override
    public void expand(boolean isExpand) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXTRAS_ALLOW_ORIENTATION_CHANGE, mAllowOrientationChange);
        bundle.putInt(Constants.EXTRAS_FORCE_ORIENTATION, mForceOrientation.getActivityInfoOrientation());
        AdUtils.startAdActivity(mLoopMeAd, isExpand, bundle);
        mMraidView.setIsViewable(true);
        Logging.out(LOG_TAG, "expand " + isExpand);
        mMraidView.setState(Constants.MraidState.EXPANDED);
    }

    @Override
    public void onLoadSuccess() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdLoadSuccess();
        }
    }

    @Override
    public void onChangeCloseButtonVisibility(boolean hasOwnCloseButton) {
        setOwnCloseButton(hasOwnCloseButton);
        broadcastCloseButtonIntent(hasOwnCloseButton);
    }

    private void broadcastCloseButtonIntent(boolean hasOwnCloseButton) {
        Intent intent = new Intent();
        intent.setAction(Constants.MRAID_NEED_CLOSE_BUTTON);
        intent.putExtra(Constants.EXTRAS_CUSTOM_CLOSE, hasOwnCloseButton);
        sendBroadcast(intent);
    }

    private void setOwnCloseButton(boolean hasOwnCloseButton) {
        if (mLoopMeAd != null) {
            mLoopMeAd.getAdParams().setOwnCloseButton(hasOwnCloseButton);
        }
    }

    private void sendBroadcast(Intent intent) {
        if (mLoopMeAd != null) {
            mLoopMeAd.getContext().sendBroadcast(intent);
        }
    }

    public void onCollapseBanner() {
        mMraidView.notifySizeChangeEvent(mWidth, mHeight);
        mMraidView.setState(Constants.MraidState.DEFAULT);
        mMraidView.setIsViewable(true);
        mMraidView.setWebViewState(Constants.WebviewState.VISIBLE);
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setBannerSize(String html) {
        ParseService parser = new ParseService();
        mWidth = Utils.convertDpToPixel(300);
        mHeight = Utils.convertDpToPixel(50);
    }
}