package com.ironsource.adapters.custom.loopme;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

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
            String appkey = adData.getConfiguration().get("instancekey").toString();
            mBanner = LoopMeBanner.getInstance(appkey, activity);
            FrameLayout container = new FrameLayout(activity);
            mBanner.bindView(container);
            mBanner.setAutoLoading(false);
            mBanner.setListener(new LoopMeBanner.Listener() {
                @Override
                public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                    Toast.makeText(activity, "Successfully loaded LoopMe banner", Toast.LENGTH_LONG).show();
                    listener.onAdLoadSuccess(mBanner.getBannerView(), new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Gravity.CENTER
                    ));
                    Log.d(LOG_TAG, "onLoopMeBannerLoadSuccess");
                    mBanner.show();
                }

                @Override
                public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
                    Log.d(LOG_TAG, "onLoopMeBannerLoadFail");
                    Toast.makeText(activity, "Failed to load LoopMe banner: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                            -2,
                            error.getErrorType() + ": " + error.getMessage());
                }

                @Override
                public void onLoopMeBannerShow(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerShow");
                    listener.onAdOpened();
                }

                @Override
                public void onLoopMeBannerHide(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerHide");
                }

                @Override
                public void onLoopMeBannerClicked(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerClicked");
                    listener.onAdClicked();
                }

                @Override
                public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
                    Log.d(LOG_TAG, "onLoopMeBannerLeaveApp");
                    listener.onAdLeftApplication();
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
            Log.e(LOG_TAG, e.getMessage());
            listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, -1, e.getMessage());
        }
    }

    @Override
    public void destroyAd(@NonNull AdData adData) {
        mBanner.destroy();
    }
}