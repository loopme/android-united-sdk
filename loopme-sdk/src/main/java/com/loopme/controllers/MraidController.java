package com.loopme.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.mraid.MraidBridge;
import com.loopme.utils.InternetUtils;
import com.loopme.views.MraidView;
import com.loopme.views.activity.AdBrowserActivity;
import com.loopme.views.activity.MraidVideoActivity;

public class MraidController implements MraidBridge.OnMraidBridgeListener {

    private static final String LOG_TAG = MraidController.class.getSimpleName();
    private static final String EXTRAS_VIDEO_URL = "videoUrl";

    private LoopMeAd mLoopMeAd;
    private MraidView mMraidView;

    public MraidController(LoopMeAd loopMeAd) {
        mLoopMeAd = loopMeAd;
    }

    public void setMraidView(MraidView mraidView) {
        mMraidView = mraidView;
    }

    @Override
    public void close() {
        Logging.out(LOG_TAG, "close");
        // TODO: 10/2/17 change to   mLoopMeAd.dismiss() after merge with auto loading
        mLoopMeAd.destroy();
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
        if (mLoopMeAd.getAdFormat() == Constants.AdFormat.BANNER || mLoopMeAd.getAdFormat() == Constants.AdFormat.EXPANDABLE_BANNER) {
            setBannerSize(width, height);
            mMraidView.resize();
            mMraidView.setState(Constants.MraidState.RESIZED);
            mMraidView.notifySizeChangeEvent(width, height);
            mMraidView.setIsViewable(true);
        }
    }

    private void setBannerSize(int width, int height) {
        if (mLoopMeAd.getAdFormat() == Constants.AdFormat.BANNER || mLoopMeAd.getAdFormat() == Constants.AdFormat.EXPANDABLE_BANNER) {
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
        mMraidView.setIsViewable(true);
        Logging.out(LOG_TAG, "expand " + isExpand);
        mMraidView.setState(Constants.MraidState.EXPANDED);

//        View view = mMraidView;
        mMraidView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

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
}