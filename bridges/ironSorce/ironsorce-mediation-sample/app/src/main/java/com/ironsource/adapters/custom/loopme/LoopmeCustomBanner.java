package com.ironsource.adapters.custom.loopme;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner;
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.loopme.LoopMeBanner;
import com.loopme.common.LoopMeError;

@Keep
public class LoopmeCustomBanner extends BaseBanner<LoopmeCustomAdapter> {

    private static final String LOG_TAG = LoopmeCustomBanner.class.getSimpleName();

    private LoopMeBanner mBanner;

    public LoopmeCustomBanner(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull ISBannerSize isBannerSize, @NonNull BannerAdListener listener) {
        try {
            String appkey = ((LoopmeCustomAdapter) getNetworkAdapter()).getLoopmeAppkey();
            mBanner = LoopMeBanner.getInstance(appkey, activity);
            mBanner.setAutoLoading(false);
            mBanner.setListener(new LoopMeBanner.Listener() {
                @Override
                public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerLoadSuccess");
                    listener.onAdLoadSuccess();
                }

                @Override
                public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
                    Log.d(LOG_TAG, "onLoopMeBannerLoadFail");
                }

                @Override
                public void onLoopMeBannerShow(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerShow");
                }

                @Override
                public void onLoopMeBannerHide(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerHide");
                }

                @Override
                public void onLoopMeBannerClicked(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerClicked");
                }

                @Override
                public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerLeaveApp");
                }

                @Override
                public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerVideoDidReachEnd");
                }

                @Override
                public void onLoopMeBannerExpired(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerExpired");
                }
            });
            mBanner.load();
        } catch (Exception e) {
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, -1, e.getMessage());
        }
    }

    @Override
    public void destroyAd(@NonNull AdData adData) {
        mBanner.destroy();
    }
}