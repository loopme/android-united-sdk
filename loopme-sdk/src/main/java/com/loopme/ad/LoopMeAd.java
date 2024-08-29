package com.loopme.ad;

import static com.loopme.Constants.UNKNOWN_MSG;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.PLACEMENT_TYPE;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.loopme.AdTargeting;
import com.loopme.AdTargetingData;
import com.loopme.Constants;
import com.loopme.IdGenerator;
import com.loopme.IntegrationType;
import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.BaseTrackableController;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.controllers.display.DisplayControllerVast;
import com.loopme.controllers.display.DisplayControllerVpaid;
import com.loopme.debugging.LiveDebug;
import com.loopme.debugging.Params;
import com.loopme.loaders.AdFetchTask;
import com.loopme.loaders.AdFetchTaskByUrl;
import com.loopme.loaders.AdFetcherListener;
import com.loopme.models.Errors;
import com.loopme.network.HttpUtils;
import com.loopme.request.RequestParamsUtils;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public abstract class LoopMeAd extends AutoLoadingConfig implements AdTargeting, Observer {

    private static final String LOG_TAG = LoopMeAd.class.getSimpleName();
    private static final String WRONG_PARAMETERS = "Context or AppKey is null!";

    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private IntegrationType mIntegrationType = IntegrationType.NORMAL;
    private final AdTargetingData mAdTargetingData = new AdTargetingData();
    protected BaseTrackableController mDisplayController;
    protected volatile FrameLayout mContainerView;

    private AdParams mAdParams;
    private final Activity mContext;
    private final Type mPreferredAdType = Type.ALL;
    private Timers mTimers;
    private AdFetchTask mAdFetchTask;

    protected long mStartLoadingTime;
    protected Constants.AdState mAdState = Constants.AdState.NONE;
    private final String mAppKey;
    protected boolean mIsReady;
    private final int mAdId;

    private final AdFetcherListener adFetchListener = new AdFetcherListener() {
        @Override
        public void onAdFetchCompleted(AdParams adParams) {
            if (adParams != null) {
                LoopMeTracker.trackSdkFeedBack(adParams.getPackageIds(), adParams.getToken());
                proceedPrepareAd(adParams);
            } else {
                onInternalLoadFail(Errors.DOWNLOAD_ERROR);
            }
        }
        @Override
        public void onAdFetchFailed(LoopMeError error) {
            if (TextUtils.isEmpty(error.getMessage())) {
                error.setErrorMessage(String.valueOf(error.getErrorCode()));
            }
            onInternalLoadFail(error);
            if (mAdFetchTask != null) mAdFetchTask.stopFetch();
        }
    };

    public LoopMeAd(@NonNull Activity context, @NonNull String appKey) {
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException(WRONG_PARAMETERS);
        }
        mContext = context;
        mAppKey = appKey;
        context.runOnUiThread(() -> mTimers = new Timers(this));
        mAdId = IdGenerator.generateId();
        LiveDebug.init(context);
        LoopMeTracker.init(this);
    }

    /**
     * Indicates whether ad content was loaded successfully and ready to be displayed.
     * After you initialized a `LoopMeInterstitialVV`/`LoopMeBannerVV` object and triggered the `resolve` method,
     * this property will be set to TRUE on it's successful completion.
     * It is set to FALSE when loaded ad content has expired or already was presented,
     * in this case it requires next `resolve` method triggering
     */
    public boolean isReady() { return mIsReady; }
    /**
     * Indicates whether `LoopMeInterstitialVV`/`LoopMeBannerVV` currently presented on screen.
     * Ad status will be set to `AdState.SHOWING` after trigger `show` method
     *
     * @return true - if ad presented on screen
     * false - if ad absent on scrren
     */
    public boolean isShowing() { return mAdState == Constants.AdState.SHOWING; }
    /**
     * Indicates whether `LoopMeInterstitial`/`LoopMeBanner` in "loading ad content" process.
     * Ad status will be set to `AdState.LOADING` after trigger `resolve` method
     *
     * @return true - if ad is loading now
     * false - if ad is not loading now
     */
    public boolean isLoading() { return mAdState == Constants.AdState.LOADING; }
    public boolean isNoneState() { return mAdState == Constants.AdState.LOADING; }
    public boolean isBanner() { return getAdFormat() == Constants.AdFormat.BANNER; }
    public boolean isInterstitial() { return getAdFormat() == Constants.AdFormat.INTERSTITIAL; }
    protected boolean isLoopMeAd() { return mAdParams != null && mAdParams.isLoopMeAd(); }
    public boolean isVastAd() { return mAdParams != null && mAdParams.isVastAd(); }
    public boolean isVpaidAd() { return mAdParams != null && mAdParams.isVpaidAd(); }
    public boolean isMraidAd() { return mAdParams != null && mAdParams.isMraidAd(); }
    public boolean isFullScreen() { return mDisplayController != null && mDisplayController.isFullScreen(); }

    @NonNull
    public abstract Constants.AdFormat getAdFormat();
    public abstract Constants.PlacementType getPlacementType();
    public abstract AdSpotDimensions getAdSpotDimensions();
    public abstract void onAdExpired();
    public abstract void onAdLoadSuccess();
    public abstract void onAdAlreadyLoaded();
    public abstract void onAdLoadFail(LoopMeError errorCode);
    public abstract void onAdLeaveApp();
    public abstract void onAdClicked();
    public abstract void onAdVideoDidReachEnd();
    public abstract void dismiss();
    public abstract void show();
    public abstract void removeListener();

    /**
     * Packs error information into a `HashMap`.
     *
     * @param errorMessage the error message to include
     * @return a `HashMap` containing error information
     */
    public HashMap<String, String> packErrorInfo(@NonNull String errorMessage) {
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(ERROR_MSG, errorMessage);
        errorInfo.put(PLACEMENT_TYPE, getPlacementType().name().toLowerCase());
        errorInfo.put(Params.CID, getCurrentCid());
        errorInfo.put(Params.CRID, getCurrentCrid());
        errorInfo.put(Params.REQUEST_ID, getRequestId());
        return errorInfo;
    }

    /**
     * Links (@link LoopMeContainerView) view to banner.
     * If ad doesn't linked to @link LoopMeContainerView, it can't be display.
     * @param containerView - @link FrameLayout (container for ad) where ad will be displayed.
     */
    public void bindView(FrameLayout containerView) {
        Logging.out(LOG_TAG, " bind view " + containerView);
        if (containerView != null) {
            mContainerView = containerView;
        } else {
            LoopMeTracker.post(packErrorInfo("Bind view is null"));
        }
    }

    public void resume() {
        if (mDisplayController != null && isReady()) {
            mDisplayController.onResume();
            Logging.out(LOG_TAG, "Ad resumed");
        }
    }

    public void pause() {
        if (mDisplayController != null && isReady()) {
            mDisplayController.onPause();
        }
        Logging.out(LOG_TAG, "Ad paused");
    }

    public void destroy() {
        mIsReady = false;
        setAdState(Constants.AdState.NONE);
        if (mAdFetchTask != null) {
            mAdFetchTask.stopFetch();
        }
        mAdTargetingData.clear();
        if (mTimers != null) {
            mTimers.destroy();
            mTimers = null;
        }
        destroyDisplayController();
        LoopMeAdHolder.removeAd(this);
        Logging.out(LOG_TAG, "Ad is destroyed");
    }

    protected void destroyDisplayController() {
        // we need delay destroy view to let little bit time for previous commands to complete
        runOnUiThreadDelayed(() -> {
            if (mDisplayController == null) {
                return;
            }
            Logging.out(LOG_TAG, "Release " + mDisplayController);
            mDisplayController.onDestroy();
            mDisplayController = null;
        }, Constants.DESTROY_TIME_DELAY);
    }

    public void onInternalLoadFail(LoopMeError error) {
        runOnUiThread(() -> {
            onAdLoadFail(error);
            onSendPostWarning(error);
        });
    }

    public void onSendPostWarning(LoopMeError error) {
        runOnUiThread(() -> LoopMeTracker.post(packErrorInfo(error.getMessage())));
    }

    private void startTimer(TimersType fetcherTimer, AdParams adParam) {
        if (mTimers == null) {
            return;
        }
        if (adParam != null) {
            mTimers.setExpirationValidTime(adParam.getExpiredTime());
        }
        mTimers.startTimer(fetcherTimer);
    }

    protected void stopTimer(TimersType timersType) {
        if (mTimers != null) {
            mTimers.stopTimer(timersType);
        }
    }

    protected void buildAdView() {
        Logging.out(LOG_TAG, " build ad view interstitial " + mContainerView);
        if (mDisplayController == null) {
            return;
        }
        if (isLoopMeAd() || isMraidAd()) {
            DisplayControllerLoopMe displayControllerLoopMe = (DisplayControllerLoopMe) mDisplayController;
            displayControllerLoopMe.buildView(mContainerView);
        } else if (isVastAd() || isVpaidAd()) {
            mDisplayController.onBuildVideoAdView(mContainerView);
        }
    }

    /**
     * Starts loading ad content process.
     * It is recommended triggering it in advance to have interstitial/banner ad ready and to be able to display instantly in your
     * application.
     * After its execution, the interstitial/banner notifies whether the loading of the ad content failed or succeeded.
     */
    public void load() { load(mIntegrationType); }

    public AdFetchTask load(String url) {
        setAdState(Constants.AdState.LOADING);
        AdFetchTask adFetchTask = new AdFetchTaskByUrl(this, adFetchListener, url);
        adFetchTask.fetch();
        return adFetchTask;
    }

    private boolean isCouldLoadAd() {
        String error;
        if (getContext() == null) {
            Logging.out(LOG_TAG, "Context should not be null and should be instance of Activity");
            return false;
        }
        if (isLoading() || isShowing()) {
            Logging.out(LOG_TAG, "Ad is already loading or showing");
            return false;
        }
        boolean isCorrectIntegrationType = Arrays
            .asList(IntegrationType.values()).contains(getIntegrationType());
        if (!isCorrectIntegrationType) {
            error = "Incorrect integration type. Please use one from list";
            Logging.out(LOG_TAG, error);
            onAdLoadFail(new LoopMeError(error));
            return false;
        }
        if (!HttpUtils.isOnline(getContext())) {
            error = "No connection";
            Logging.out(LOG_TAG, error);
            onAdLoadFail(new LoopMeError(error));
            return false;
        }
        int[] adSize = RequestParamsUtils.getAdSize(mContext, this);
        int width = adSize[0];
        int height = adSize[1];
        boolean isMpu = AdSpotDimensions.getMpu().equals(new AdSpotDimensions(width, height));
        boolean isExpandBanner = AdSpotDimensions.getExpandBanner().equals(new AdSpotDimensions(width, height));
        boolean isCustomBannerSize = isBanner() && !(isExpandBanner || isMpu);
        boolean isExpandBannerSize = isBanner() && isExpandBanner;
        boolean isCustomBannerHtml = isCustomBannerSize && mPreferredAdType == Type.HTML;
        boolean isExpandBannerVideo = isExpandBannerSize && mPreferredAdType == Type.VIDEO;
        if (isCustomBannerHtml || isExpandBannerVideo) {
            error = "Container size is not valid for chosen ad type";
            Logging.out(LOG_TAG, error);
            onAdLoadFail(new LoopMeError(error));
            return false;
        }
        return true;
    }

    public void load(IntegrationType integrationType) {
        setIntegrationType(integrationType);
        mStartLoadingTime = System.currentTimeMillis();
        if (!isCouldLoadAd()) {
            return;
        }
        if (isReady()) {
            onAdAlreadyLoaded();
        } else {
            setAdState(Constants.AdState.LOADING);
            mAdFetchTask = new AdFetchTask(this, adFetchListener);
            mAdFetchTask.fetch();
        }
    }

    private void proceedPrepareAd(AdParams adParams) {
        setBackendAutoLoadingValue(adParams.getAutoLoading());
        startTimer(TimersType.EXPIRATION_TIMER, adParams);
        setAdParams(adParams);
        if (isVpaidAd()) {
            mDisplayController = new DisplayControllerVpaid(this);
        } else if (isVastAd()) {
            mDisplayController = new DisplayControllerVast(this);
        } else if (isLoopMeAd() || isMraidAd()) {
            mDisplayController = new DisplayControllerLoopMe(this);
        }
        if (mDisplayController != null) {
            mDisplayController.onStartLoad();
        }
        LiveDebug.setLiveDebug(this.getContext().getPackageName(), adParams.isDebug(), this.getAppKey());
    }

    public String getRequestId() {
        return mAdParams != null ? mAdParams.getRequestId() : UNKNOWN_MSG;
    }

    public String getCurrentCid() {
        return mAdParams != null ? mAdParams.getCid() : UNKNOWN_MSG;
    }

    public String getCurrentCrid() {
        return mAdParams != null ? mAdParams.getCrid() : UNKNOWN_MSG;
    }

    public AdTargetingData getAdTargetingData() { return mAdTargetingData; }
    public IntegrationType getIntegrationType() { return mIntegrationType; }
    public BaseTrackableController getDisplayController() { return mDisplayController; }
    public AdParams getAdParams() { return mAdParams; }
    public Type getPreferredAdType() { return mPreferredAdType; }
    public Activity getContext() { return mContext; }
    public String getAppKey() { return mAppKey; }
    public FrameLayout getContainerView() { return mContainerView; }
    public int getAdId() { return mAdId; }
    public void setAdParams(@NonNull AdParams adParams) { mAdParams = adParams; }
    public void setAdState(Constants.AdState adState) { mAdState = adState; }
    public void setReady(boolean ready) { mIsReady = ready; }
    @Override
    public void setKeywords(String keywords) { mAdTargetingData.setKeywords(keywords); }
    @Override
    public void setGender(String gender) { mAdTargetingData.setGender(gender); }
    @Override
    public void setYearOfBirth(int year) { mAdTargetingData.setYob(year); }

    public void setIntegrationType(IntegrationType mIntegrationType) {
        this.mIntegrationType = mIntegrationType != null ? mIntegrationType : IntegrationType.NORMAL;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (!(observable instanceof Timers) || !(arg instanceof TimersType)) return;
        if (arg == TimersType.EXPIRATION_TIMER) onAdExpired();
    }

    public void runOnUiThread(Runnable runnable) { if (mHandler != null) mHandler.post(runnable); }

    public void runOnUiThreadDelayed(Runnable runnable, long time) {
        if (mHandler != null) mHandler.postDelayed(runnable, time);
    }

    public void onNewContainer(@NonNull FrameLayout containerView) {
        if (isInterstitial()) {
            bindView(containerView);
            return ;
        }
        if (mDisplayController != null && mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).onRebuildView(containerView);
        }
    }

    public enum Type { HTML, VIDEO, ALL }
}
