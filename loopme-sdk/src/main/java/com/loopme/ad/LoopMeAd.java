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
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.controllers.display.DisplayControllerVast;
import com.loopme.controllers.display.DisplayControllerVpaid;
import com.loopme.controllers.interfaces.DisplayController;
import com.loopme.debugging.LiveDebug;
import com.loopme.loaders.AdFetchTask;
import com.loopme.models.Errors;
import com.loopme.request.RequestParamsUtils;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;
import com.loopme.utils.ValidationHelper;

import java.util.Observable;
import java.util.Observer;

public abstract class LoopMeAd extends AutoLoadingConfig implements AdTargeting, Observer {

    private static final String LOG_TAG = LoopMeAd.class.getSimpleName();
    private static final String WRONG_PARAMETERS = "Context or AppKey is null!";

    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private IntegrationType mIntegrationType = IntegrationType.NORMAL;
    private AdTargetingData mAdTargetingData = new AdTargetingData();
    protected DisplayController mDisplayController;
    protected volatile FrameLayout mContainerView;

    private AdParams mAdParams;
    private Activity mContext;
    private Type mPreferredAdType = Type.ALL;
    private Timers mTimers;
    private AdFetchTask mAdFetchTask;

    protected long mStartLoadingTime;
    protected Constants.AdState mAdState = Constants.AdState.NONE;
    private String mAppKey;
    protected boolean mIsReady;
    private int mAdId;
    private volatile boolean mIsReverseOrientationRequest;

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

    public boolean isBanner() {
        return getAdFormat() == Constants.AdFormat.BANNER;
    }

    public boolean isInterstitial() {
        return getAdFormat() == Constants.AdFormat.INTERSTITIAL;
    }

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
     *
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

    public void destroy() {
        mIsReady = false;
        setAdState(Constants.AdState.NONE);
        stopFetchAdTask();
        clearTargetingData();
        destroyTimers();
        destroyDisplayController();
        Helpers.reset();
        LoopMeAdHolder.removeAd(this);
//        LiveDebug.reset();
        Logging.out(LOG_TAG, "Ad is destroyed");
    }

    private void stopFetchAdTask() {
        if (mAdFetchTask != null) {
            mAdFetchTask.stopFetch();
        }
    }

    private void clearTargetingData() {
        if (mAdTargetingData != null) {
            mAdTargetingData.clear();
        }
    }

    protected void destroyDisplayController() {
        // we need delay destroy view to let little bit time for previous commands to complete
        runOnUiThreadDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDisplayController != null) {
                    Logging.out(LOG_TAG, "Release " + mDisplayController.toString());
                    mDisplayController.onDestroy();
                    mDisplayController = null;
                }
            }
        }, Constants.DESTROY_TIME_DELAY);
    }

    /**
     * Indicates whether ad content was loaded successfully and ready to be displayed.
     * After you initialized a `LoopMeInterstitialVV`/`LoopMeBannerVV` object and triggered the `resolve` method,
     * this property will be set to TRUE on it's successful completion.
     * It is set to FALSE when loaded ad content has expired or already was presented,
     * in this case it requires next `resolve` method triggering
     */
    public boolean isReady() {
        return mIsReady;
    }

    /**
     * Indicates whether `LoopMeInterstitialVV`/`LoopMeBannerVV` currently presented on screen.
     * Ad status will be set to `AdState.SHOWING` after trigger `show` method
     *
     * @return true - if ad presented on screen
     * false - if ad absent on scrren
     */
    public boolean isShowing() {
        return mAdState == Constants.AdState.SHOWING;
    }

    /**
     * Indicates whether `LoopMeInterstitial`/`LoopMeBanner` in "loading ad content" process.
     * Ad status will be set to `AdState.LOADING` after trigger `resolve` method
     *
     * @return true - if ad is loading now
     * false - if ad is not loading now
     */
    public boolean isLoading() {
        return mAdState == Constants.AdState.LOADING;
    }

    public boolean isNoneState() {
        return mAdState == Constants.AdState.LOADING;
    }


    private void onResponseReceived(AdParams adParam) {
        if (adParam != null) {
            LoopMeTracker.trackSdkFeedBack(adParam.getPackageIds(), adParam.getToken());
            proceedPrepareAd(adParam);
        } else {
            onInternalLoadFail(Errors.DOWNLOAD_ERROR);
        }
        mIsReverseOrientationRequest = false;
    }

    private void proceedPrepareAd(AdParams adParam) {
        setBackendAutoLoadingValue(adParam.getAutoLoading());
        startTimer(TimersType.EXPIRATION_TIMER, adParam);
        setAdParams(adParam);
        initDisplayController();
        setLiveDebug(adParam);
    }

    public void onInternalLoadFail(LoopMeError error) {
        onAdLoadFail(error);
        onSendPostWarning(error);
    }

    public void onSendPostWarning(LoopMeError error) {
        LoopMeTracker.post(error);
    }

    private void setLiveDebug(AdParams adParams) {
        LiveDebug.setLiveDebug(this.getContext(), adParams.isDebug(), this.getAppKey());
    }

    private void startTimer(TimersType fetcherTimer, AdParams adParam) {
        if (mTimers != null) {
            if (adParam != null) {
                mTimers.setExpirationValidTime(adParam.getExpiredTime());
            }
            mTimers.startTimer(fetcherTimer);
        }
    }

    protected void stopTimer(TimersType timersType) {
        if (mTimers != null) {
            mTimers.stopTimer(timersType);
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

    protected boolean isLoopMeAd() {
        return mAdParams != null && mAdParams.isLoopMeAd();
    }

    public boolean isVastAd() {
        return mAdParams != null && mAdParams.isVastAd();
    }

    protected boolean isVpaidAd() {
        return mAdParams != null && mAdParams.isVpaidAd();
    }

    public boolean isMraidAd() {
        return mAdParams != null && mAdParams.isMraidAd();
    }

    public boolean isVideo360() {
        return mAdParams != null && mAdParams.isVideo360();
    }

    protected void buildAdView() {
        Logging.out(LOG_TAG, " build ad view interstitial " + mContainerView);
        if (mDisplayController != null) {
            if (isLoopMeAd() || isMraidAd()) {
                DisplayControllerLoopMe displayControllerLoopMe = (DisplayControllerLoopMe) mDisplayController;
                displayControllerLoopMe.buildView(mContainerView);
            } else if (isVastAd() || isVpaidAd()) {
                mDisplayController.onBuildVideoAdView(mContainerView);
            }
        }
    }

    /**
     * Starts loading ad content process.
     * It is recommended triggering it in advance to have interstitial/banner ad ready and to be able to display instantly in your
     * application.
     * After its execution, the interstitial/banner notifies whether the loading of the ad content failed or succeeded.
     */
    public void load() {
        load(IntegrationType.NORMAL);
    }

    public void load(IntegrationType integrationType) {
        setIntegrationType(integrationType);
        mStartLoadingTime = System.currentTimeMillis();
        if (ValidationHelper.isCouldLoadAd(this)) {
            fetchAd();
        }
    }

    private void fetchAd() {
        if (!isReady()) {
            proceedFetchAd();
        } else {
            onAdAlreadyLoaded();
        }
    }

    private void proceedFetchAd() {
        setAdState(Constants.AdState.LOADING);
        startTimer(TimersType.FETCHER_TIMER, null);
        mAdFetchTask = new AdFetchTask(this, initAdFetcherListener());
        mAdFetchTask.fetch();
    }

    private AdFetchTask.AdFetcherListener initAdFetcherListener() {
        return new AdFetchTask.AdFetcherListener() {
            @Override
            public void onAdFetchCompleted(AdParams adParams) {
                onResponseReceived(adParams);
            }

            @Override
            public void onAdFetchFailed(LoopMeError error) {
                onResponseReceived(error);
                stopFetchAdTask();
            }
        };
    }

    private void onResponseReceived(LoopMeError error) {
        if (TextUtils.isEmpty(error.getMessage())) {
            error.setErrorMessage(String.valueOf(error.getErrorCode()));
        }
        onInternalLoadFail(error);
        mIsReverseOrientationRequest = false;
    }

    @Override
    public void setKeywords(String keywords) {
        mAdTargetingData.setKeywords(keywords);
    }

    @Override
    public void setGender(String gender) {
        mAdTargetingData.setGender(gender);
    }

    @Override
    public void setYearOfBirth(int year) {
        mAdTargetingData.setYob(year);
    }

    @Override
    public void addCustomParameter(String param, String paramValue) {
        mAdTargetingData.setCustomParameters(param, paramValue);
    }

    public IntegrationType getIntegrationType() {
        return mIntegrationType;
    }

    public void setIntegrationType(IntegrationType mIntegrationType) {
        this.mIntegrationType = mIntegrationType != null ? mIntegrationType : IntegrationType.NORMAL;
    }

    public void setAdController(DisplayController displayController) {
        this.mDisplayController = displayController;
    }

    public DisplayController getDisplayController() {
        return mDisplayController;
    }

    public AdParams getAdParams() {
        return mAdParams;
    }

    public void setAdParams(AdParams mAdParams) {
        this.mAdParams = mAdParams;
    }

    public Type getPreferredAdType() {
        return mPreferredAdType;
    }

    public void setPreferredAdType(Type preferredAdType) {
        if (preferredAdType != null) {
            mPreferredAdType = preferredAdType;
        }
    }

    public Activity getContext() {
        return mContext;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable != null && observable instanceof Timers
                && arg != null && arg instanceof TimersType) {
            switch ((TimersType) arg) {
                case FETCHER_TIMER: {
                    onInternalLoadFail(Errors.AD_PROCESSING_TIMEOUT);
                    break;
                }
                case EXPIRATION_TIMER: {
                    onAdExpired();
                    break;
                }
            }
        }
    }

    private void destroyTimers() {
        if (mTimers != null) {
            mTimers.destroy();
            mTimers = null;
        }
    }

    public String getAppKey() {
        return mAppKey;
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

    public Constants.AdState getAdState() {
        return mAdState;
    }

    public void setAdState(Constants.AdState adState) {
        this.mAdState = adState;
    }

    public void setReady(boolean ready) {
        this.mIsReady = ready;
    }

    public boolean isFullScreen() {
        return mDisplayController != null && mDisplayController.isFullScreen();
    }

    public void setFullScreen(boolean isFullScreen) {
        mDisplayController.setFullScreen(isFullScreen);
    }

    public FrameLayout getContainerView() {
        return mContainerView;
    }

    public void rebuildView(FrameLayout loopMeContainerView) {
        if (mDisplayController != null && mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).onRebuildView(loopMeContainerView);
        }
    }

    public int getAdId() {
        return mAdId;
    }

    public void setReversOrientationRequest() {
        mIsReverseOrientationRequest = true;
    }

    public boolean isReverseOrientationRequest() {
        return mIsReverseOrientationRequest;
    }

    public enum Type {
        HTML,
        VIDEO,
        ALL;

        public static Type fromString(String type) {
            if (TextUtils.isEmpty(type)) {
                return ALL;
            }
            if (HTML.name().equalsIgnoreCase(type)) {
                return HTML;
            } else if (VIDEO.name().equalsIgnoreCase(type)) {
                return VIDEO;
            } else {
                return ALL;
            }
        }
    }

    public boolean isCustomBannerHtml() {
        return isCustomBanner() && mPreferredAdType == Type.HTML;
    }

    public boolean isCustomBanner() {
        int[] adSize = RequestParamsUtils.getAdSize(mContext, this);
        int width = adSize[0];
        int height = adSize[1];
        return isBanner() && Utils.isCustomBannerSize(width, height);
    }

    public boolean isExpandBannerVideo() {
        return isExpandBanner() && mPreferredAdType == Type.VIDEO;
    }

    public boolean isExpandBanner() {
        int[] adSize = RequestParamsUtils.getAdSize(mContext, this);
        int width = adSize[0];
        int height = adSize[1];
        return isBanner() && Utils.isExpandBanner(width, height);
    }
}
