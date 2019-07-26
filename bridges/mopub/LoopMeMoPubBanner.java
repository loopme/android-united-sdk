package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;

import com.loopme.IntegrationType;
import com.loopme.LoopMeBanner;
import com.loopme.common.LoopMeError;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

public class LoopMeMoPubBanner extends CustomEventBanner implements LoopMeBanner.Listener {

    private static final String LOG_TAG = LoopMeMoPubBanner.class.getSimpleName();

    private static LoopMeBanner mBanner;

    private CustomEventBanner.CustomEventBannerListener mBannerListener;
    private Activity mActivity;

    @Override
    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> map, Map<String, String> map1) {

        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, LOG_TAG, "Bridge loadBanner");

        mBannerListener = customEventBannerListener;
        mActivity = null;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            // You may also pass in an Activity Context in the localExtras map and retrieve it here.
        }

        String mLoopMeAppId = map1.get("app_key");
        FrameLayout mLoopMeBannerView = (FrameLayout) map.get("bannerView");

        MoPubLog.log(
                MoPubLog.AdapterLogEvent.CUSTOM,
                LOG_TAG,
                mLoopMeBannerView == null
                        ? "LoopMeBannerView is null"
                        : "LoopMeBannerView is correct");

        mBanner = LoopMeBanner.getInstance(mLoopMeAppId, mActivity);
        mBanner.bindView(mLoopMeBannerView);
        mBanner.setListener(this);
        mBanner.load(IntegrationType.MOPUB);
    }

    @Override
    public void onInvalidate() {
    }

    @Override
    public void onLoopMeBannerLoadSuccess(LoopMeBanner loopMeBanner) {
        MoPubView view = new MoPubView(mActivity);
        mBannerListener.onBannerLoaded(view);
        if (mBanner != null) {
            mBanner.show();
        }
    }

    public static void pause() {
        if (mBanner != null) {
            mBanner.pause();
        }
    }

    public static void resume() {
        if (mBanner != null) {
            mBanner.resume();
        }
    }

    public static void destroy() {
        if (mBanner != null) {
            mBanner.dismiss();
            mBanner.destroy();
        }
    }

    @Override
    public void onLoopMeBannerLoadFail(LoopMeBanner loopMeBanner, LoopMeError i) {
        mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner loopMeBanner) {

    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner loopMeBanner) {

    }

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner loopMeBanner) {
        mBannerListener.onBannerClicked();
    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner loopMeBanner) {
        mBannerListener.onLeaveApplication();
    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner loopMeBanner) {

    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner loopMeBanner) {

    }
}
