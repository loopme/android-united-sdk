package com.loopme.ad;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.loopme.AdTargeting;
import com.loopme.AdTargetingData;
import com.loopme.Constants;
import com.loopme.Helpers;
import com.loopme.IdGenerator;
import com.loopme.IntegrationType;
import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.display.BaseTrackableController;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.controllers.display.DisplayControllerVast;
import com.loopme.controllers.display.DisplayControllerVpaid;
import com.loopme.debugging.LiveDebug;
import com.loopme.loaders.AdFetchTask;
import com.loopme.loaders.AdFetchTaskByUrl;
import com.loopme.loaders.AdFetcherListener;
import com.loopme.models.Errors;
import com.loopme.request.RequestParamsUtils;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.InternetUtils;

import java.util.Arrays;
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
    private volatile boolean mIsReverseOrientationRequest;

    public final AdFetcherListener adFetchListener = new AdFetcherListener() {
        @Override
        public void onAdFetchCompleted(AdParams adParams) { LoopMeAd.this.onAdFetchCompleted(adParams); }
        @Override
        public void onAdFetchFailed(LoopMeError error) {
            LoopMeAd.this.onAdFetchFailed(error);
            stopFetchAdTask();
        }
    };

    public LoopMeAd(Activity context, String appKey) {
        if (context == null || TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException(WRONG_PARAMETERS);
        }
        mContext = context;
        mAppKey = appKey;
        mTimers = new Timers(this);
        mAdId = IdGenerator.generateId();
        LiveDebug.init(context);
        Helpers.init(this);
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

    public abstract Constants.AdFormat getAdFormat();
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
     * Links (@link LoopMeContainerView) view to banner.
     * If ad doesn't linked to @link LoopMeContainerView, it can't be display.
     * @param containerView - @link FrameLayout (container for ad) where ad will be displayed.
     */
    public void bindView(FrameLayout containerView) {
        Logging.out(LOG_TAG, " bind view " + containerView);
        if (containerView != null) {
            mContainerView = containerView;
        } else {
            LoopMeTracker.post("Bind view is null");
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

    private void destroyTimers() {
        if (mTimers == null) {
            return;
        }
        mTimers.destroy();
        mTimers = null;
    }

    public void destroy() {
        mIsReady = false;
        setAdState(Constants.AdState.NONE);
        stopFetchAdTask();
        mAdTargetingData.clear();
        destroyTimers();
        destroyDisplayController();
        Helpers.reset();
        LoopMeAdHolder.removeAd(this);
        Logging.out(LOG_TAG, "Ad is destroyed");
    }

    private void stopFetchAdTask() {
        if (mAdFetchTask != null) {
            mAdFetchTask.stopFetch();
        }
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
        onAdLoadFail(error);
        onSendPostWarning(error);
    }

    public void onSendPostWarning(LoopMeError error) { LoopMeTracker.post(error); }

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
        startTimer(TimersType.FETCHER_TIMER, null);
        AdFetchTask adFetchTask = new AdFetchTaskByUrl(this, adFetchListener, url);
        adFetchTask.fetch();
        return adFetchTask;
    }

    private AdFetchTask proceedFetchAd() {
        setAdState(Constants.AdState.LOADING);
        startTimer(TimersType.FETCHER_TIMER, null);
        AdFetchTask adFetchTask = new AdFetchTask(this, adFetchListener);
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
        if (!InternetUtils.isOnline(getContext())) {
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
            mAdFetchTask = proceedFetchAd();
        }
    }

    private void initDisplayController() {
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
    }

    private void proceedPrepareAd(AdParams adParams) {
        setBackendAutoLoadingValue(adParams.getAutoLoading());
        startTimer(TimersType.EXPIRATION_TIMER, adParams);
        setAdParams(adParams);
        initDisplayController();
        LiveDebug.setLiveDebug(this.getContext(), adParams.isDebug(), this.getAppKey());
    }

    private void onAdFetchCompleted(AdParams adParams) {
        if (adParams != null) {
            LoopMeTracker.trackSdkFeedBack(adParams.getPackageIds(), adParams.getToken());
            proceedPrepareAd(adParams);
        } else {
            onInternalLoadFail(Errors.DOWNLOAD_ERROR);
        }
        mIsReverseOrientationRequest = false;
    }
    private void onAdFetchFailed(LoopMeError error) {
        if (TextUtils.isEmpty(error.getMessage())) {
            error.setErrorMessage(String.valueOf(error.getErrorCode()));
        }
        onInternalLoadFail(error);
        mIsReverseOrientationRequest = false;
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
    public boolean isReverseOrientationRequest() { return mIsReverseOrientationRequest; }
    public void setAdParams(AdParams mAdParams) { this.mAdParams = mAdParams; }
    public void setReversOrientationRequest() { mIsReverseOrientationRequest = true; }
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
    public void addCustomParameter(String param, String paramValue) {
        mAdTargetingData.setCustomParameters(param, paramValue);
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (!(observable instanceof Timers) || !(arg instanceof TimersType)) {
            return;
        }
        if (arg == TimersType.FETCHER_TIMER) {
            onInternalLoadFail(Errors.AD_PROCESSING_TIMEOUT);
        }
        if (arg == TimersType.EXPIRATION_TIMER) {
            onAdExpired();
        }
    }

    public void runOnUiThread(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    public void runOnUiThreadDelayed(Runnable runnable, long time) {
        if (mHandler != null) {
            mHandler.postDelayed(runnable, time);
        }
    }

    public void onNewContainer(FrameLayout containerView) {
        if (isInterstitial()) {
            bindView(containerView);
        } else {
            if (mDisplayController != null && mDisplayController instanceof DisplayControllerLoopMe) {
                ((DisplayControllerLoopMe) mDisplayController).onRebuildView(containerView);
            }
        }
    }

    public enum Type { HTML, VIDEO, ALL }
}
