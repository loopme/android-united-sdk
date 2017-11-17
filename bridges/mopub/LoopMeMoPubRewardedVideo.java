package com.mopub.mobileads;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.loopme.IntegrationType;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.Logging;
import com.loopme.common.LoopMeError;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;

import java.util.Map;

public class LoopMeMoPubRewardedVideo extends CustomEventRewardedVideo implements LoopMeInterstitial.Listener {

    private static final String LOG_TAG = LoopMeMoPubRewardedVideo.class.getSimpleName();
    private static final String KEY_EXTRA_LOOPME_APPKEY = "app_key";
    private static final String KEY_EXTRA_REWARDED_CURRENCY = "currency_type";
    private static final String KEY_EXTRA_REWARDED_AMOUNT = "amount";
    private static final int NO_AMOUNT = 0;
    private String mLoopMeAppId;
    private String mRewardedCurrency;
    private int mRewardedAmount;
    private MoPubReward mMoPubReward;
    private LoopMeInterstitial mLoopMeRewardedVideo;

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity,
                                            @NonNull Map<String, Object> localExtras,
                                            @NonNull Map<String, String> serverExtras) throws Exception {
        Logging.out(LOG_TAG, "Bridge checkAndInitializeSdk");
        mLoopMeAppId = serverExtras.get(KEY_EXTRA_LOOPME_APPKEY);

        if (!isAppKeyValid()) {
            Logging.out(LOG_TAG, "App key should not be null");
            return false;
        }

        handleReward(serverExtras);
        initRewardedVideo(launcherActivity);
        return true;
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity,
                                          @NonNull Map<String, Object> localExtras,
                                          @NonNull Map<String, String> serverExtras) throws Exception {
        Logging.out(LOG_TAG, "Bridge loadWithSdkInitialized");

        mLoopMeAppId = serverExtras.get(KEY_EXTRA_LOOPME_APPKEY);
        if (!isAppKeyValid()) {
            return;
        }

        if (mLoopMeRewardedVideo == null) {
            initRewardedVideo(activity);
        }

        if (mLoopMeRewardedVideo.isReady()) {
            onLoopMeInterstitialLoadSuccess(mLoopMeRewardedVideo);
        } else {
            mLoopMeRewardedVideo.load(IntegrationType.MOPUB);
        }
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mLoopMeAppId;
    }

    @Override
    protected boolean hasVideoAvailable() {
        return mLoopMeRewardedVideo != null && mLoopMeRewardedVideo.isReady();
    }

    @Override
    protected void showVideo() {
        if (hasVideoAvailable()) {
            mLoopMeRewardedVideo.show();
        } else {
            Logging.out(LOG_TAG, "rewarded video is not ready");
        }
    }

    @Override
    public void onInvalidate() {
        if (mLoopMeRewardedVideo != null) {
            mLoopMeRewardedVideo.destroy();
        }
    }

    private void handleReward(Map<String, String> serverExtras) {
        retrieveRewardedData(serverExtras);
        setReward();
    }

    private void setReward() {
        if (isRewardedDataValid()) {
            mMoPubReward = MoPubReward.success(mRewardedCurrency, mRewardedAmount);
        } else {
            mMoPubReward = MoPubReward.success(MoPubReward.NO_REWARD_LABEL, MoPubReward.NO_REWARD_AMOUNT);
        }
    }

    private boolean isRewardedDataValid() {
        return !TextUtils.isEmpty(mRewardedCurrency) && mRewardedAmount != 0;
    }

    private boolean isAppKeyValid() {
        if (TextUtils.isEmpty(mLoopMeAppId)) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    LoopMeMoPubRewardedVideo.class,
                    LoopMeMoPubRewardedVideo.class.getSimpleName(),
                    MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return false;
        } else {
            return true;
        }
    }

    private void initRewardedVideo(Activity activity) {
        mLoopMeRewardedVideo = LoopMeInterstitial.getInstance(mLoopMeAppId, activity);
        mLoopMeRewardedVideo.setListener(LoopMeMoPubRewardedVideo.this);
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial loopMeRewardedVideo) {
        MoPubRewardedVideoManager.onRewardedVideoClicked(
                LoopMeMoPubRewardedVideo.class,
                mLoopMeAppId);
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial loopMeRewardedVideo) {
        MoPubRewardedVideoManager.onRewardedVideoClosed(
                LoopMeMoPubRewardedVideo.class,
                mLoopMeAppId);
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial loopMeRewardedVideo) {
        MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
                LoopMeMoPubRewardedVideo.class,
                mLoopMeAppId);
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial loopMeRewardedVideo, LoopMeError error) {
        MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                LoopMeMoPubRewardedVideo.class,
                mLoopMeAppId,
                MoPubErrorCode.INTERNAL_ERROR);
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial loopMeRewardedVideo) {
        MoPubRewardedVideoManager.onRewardedVideoStarted(
                LoopMeMoPubRewardedVideo.class,
                mLoopMeAppId);
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        MoPubRewardedVideoManager.onRewardedVideoCompleted(LoopMeMoPubRewardedVideo.class,
                mLoopMeAppId, mMoPubReward);
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial loopMeRewardedVideo) {
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial loopMeRewardedVideo) {
    }

    private int getInt(String amount) {
        try {
            mRewardedAmount = Integer.valueOf(amount);
            return mRewardedAmount;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Logging.out(LOG_TAG, "Rewarded amount is not of type int");
        }
        return NO_AMOUNT;
    }

    private void retrieveRewardedData(Map<String, String> serverExtras) {
        mRewardedCurrency = serverExtras.get(KEY_EXTRA_REWARDED_CURRENCY);
        String amount = serverExtras.get(KEY_EXTRA_REWARDED_AMOUNT);
        if (!TextUtils.isEmpty(amount)) {
            mRewardedAmount = getInt(amount);
        }
    }
}