package com.loopme.tester.ui.view;

import android.Manifest;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopme.Logging;
import com.loopme.tester.Constants;
import com.loopme.tester.R;
import com.loopme.tester.ads.Ad;
import com.loopme.tester.ads.AdListener;
import com.loopme.tester.ads.AdLoopMeBanner;
import com.loopme.tester.ads.AdLoopMeInterstitial;
import com.loopme.tester.ads.AdMopubBanner;
import com.loopme.tester.ads.AdMopubInterstitial;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.ui.activity.MainActivity;
import com.loopme.tester.utils.Utils;
import com.mopub.mobileads.MoPubView;

public class AutoView implements View.OnClickListener, AdListener,
        ScreenOffBroadcastReceiver.OnScreenOffListener {
    private static final String LOG_TAG = Constants.PREFIX + AutoView.class.getSimpleName();

    private int mRequestsAmount;
    private int mTimeBetweenRequests;
    private int mAutoCloseTime;
    private int mFailedCounter;
    private int mShownCounter;
    private int mRequestsCounter;
    private boolean mIsBulkTestStopped;
    private boolean mFirstAutoLaunch = true;
    private boolean mIsAutoLoadingEnabled;

    private Ad mAd;
    private AdSpot mAdSpot;
    private View mRootView;
    private View mAutoTestParamsLayout;
    private EditText mRequestsAmountEdit;
    private EditText mTimeBetweenRequestsEdit;
    private EditText mAutoCloseTimeEdit;
    private TextView mLoadingLabel;
    private TextView mRunButton;
    private TextView mFailedAdsTextView;
    private TextView mShowedAdsTextView;
    private TextView mRequestsAdsTextView;
    private BaseActivity mActivity;
    private Runnable mAutoCloseRunnable;
    private ScreenOffBroadcastReceiver mScreenOffReceiver;
    private Runnable mShowAdRunnable;
    private Runnable mStopAdRunnable;

    public AutoView(View layout, AdSpot adSpot, BaseActivity activity) {
        mRootView = layout;
        mAdSpot = adSpot;
        mActivity = activity;
        mScreenOffReceiver = new ScreenOffBroadcastReceiver(mActivity, this);
        mIsAutoLoadingEnabled = getAutoLoadingState();
        mAutoCloseRunnable = initAutoCloseRunnable();
        mShowAdRunnable = initShowAdRunnable();
        mStopAdRunnable = initStopAdRunnable();
    }

    public void initView() {
        if (mRootView == null) {
            Logging.out(LOG_TAG, "View is null");
            return;
        }
        mAutoTestParamsLayout = mRootView.findViewById(R.id.auto_test_params_relative_layout);
        mLoadingLabel = (TextView) mRootView.findViewById(R.id.loading_label_auto);
        mRequestsAmountEdit = (EditText) mRootView.findViewById(R.id.ad_requests);
        mTimeBetweenRequestsEdit = (EditText) mRootView.findViewById(R.id.ad_request_time);
        mAutoCloseTimeEdit = (EditText) mRootView.findViewById(R.id.auto_close_time);
        mFailedAdsTextView = (TextView) mRootView.findViewById(R.id.failed);
        mShowedAdsTextView = (TextView) mRootView.findViewById(R.id.showed);
        mRequestsAdsTextView = (TextView) mRootView.findViewById(R.id.requests);
        mRunButton = (TextView) mRootView.findViewById(R.id.run_ad_auto);

        mRootView.findViewById(R.id.stop_ad_auto).setOnClickListener(this);
        mRunButton.setOnClickListener(this);
        updateResults();
    }

    @Override
    public void onLoadSuccess() {
        showAdRunnable();
    }

    @Override
    public void onLoadFail(String error) {
        Logging.out(LOG_TAG, "onLoadFail: " + error);
        if (mIsAutoLoadingEnabled) {
            increaseRequestCounter();
        }
        mFailedCounter++;
        updateResults();
        if (!mIsAutoLoadingEnabled) {
            loadAd();
        }
        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
    }

    private void setLoadedLabel() {
        if (mAd != null && !mAd.isShowing()) {
            mLoadingLabel.setText(R.string.ad_loaded);
        }
    }

    @Override
    public void onShow() {
        Logging.out(LOG_TAG, String.valueOf(mShownCounter));
        increaseShownCounter();
        updateResults();
        autoCloseAd();
    }

    private void increaseRequestCounter() {
        mRequestsCounter++;
    }

    private void increaseShownCounter() {
        mShownCounter++;
    }

    @Override
    public void onExpired() {
        // do nothing
    }

    @Override
    public void onHide() {
        Logging.out(LOG_TAG, "ad is hidden");
        showAutoTestingParams(true);
        if (!mIsAutoLoadingEnabled && !mIsBulkTestStopped) {
            dismissAd();
            loadAd();
        }
    }

    private void showAutoTestingParams(boolean visible) {
        if (!isBanner()) {
            return;
        }
        if (mAutoTestParamsLayout != null) {
            if (visible) {
                mAutoTestParamsLayout.setVisibility(View.VISIBLE);
            } else {
                mAutoTestParamsLayout.setVisibility(View.GONE);
            }
        }
    }

    private boolean isBanner() {
        return mAd != null && (mAd instanceof AdMopubBanner || mAd instanceof AdLoopMeBanner);
    }

    private void showAdRunnable() {
        if (!mIsBulkTestStopped) {
            if (mFirstAutoLaunch) {
                postRunnable(mShowAdRunnable);
                mFirstAutoLaunch = false;
            } else {
                postRunnableDelayed(mShowAdRunnable, mTimeBetweenRequests * Constants.ONE_SECOND_IN_MILLIS);
                setLoadedLabel();
            }
        }
    }

    private void postRunnableDelayed(Runnable runnable, int delayInLillis) {
        if (mActivity != null) {
            mActivity.postRunnableDelayed(runnable, delayInLillis);
        }
    }

    private void postRunnable(Runnable runnable) {
        if (mActivity != null) {
            mActivity.postRunnable(runnable);
        }
    }

    private void autoCloseAd() {
        postRunnableDelayed(mAutoCloseRunnable, mAutoCloseTime * Constants.ONE_SECOND_IN_MILLIS);
    }

    private Runnable initAutoCloseRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    Logging.out(LOG_TAG, "time to auto close");
                    dismissAd();
                    proceedToLoad();
                    showAutoTestingParams(true);
                } catch (Exception e) {
                    Logging.out(LOG_TAG, e.getMessage());
                }
            }
        };
    }

    private void proceedToLoad() {
        if (mIsAutoLoadingEnabled) {
            autoLoadingLogic();
        }
    }

    private void autoLoadingLogic() {
        if (isAutoTestingInProcess()) {
            if (isAdReady()) {
                autoLoadedSuccess();
            } else {
                autoLoadedSuccessDelayed();
            }
        } else {
            autoTestingFinished();
        }
    }

    private void autoLoadedSuccessDelayed() {
        postRunnableDelayed(new Runnable() {
            @Override
            public void run() {
                autoLoadedSuccess();
            }
        }, Constants.ONE_SECOND_IN_MILLIS / 2);
    }

    private void autoLoadedSuccess() {
        increaseRequestCounter();
        updateResults();
        onLoadSuccess();
    }

    private void autoTestingFinished() {
        mFirstAutoLaunch = true;
        onStopRun();
        mLoadingLabel.setText(R.string.auto_testing_finished);
        setLoadingLabelVisibility(true);
    }

    private boolean isAdReady() {
        return mAd != null && mAd.isReady();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_ad_auto:
                onStartRun();
                break;

            case R.id.stop_ad_auto:
                onStopRun();
                resetCountersViews();
                break;

            default:
                break;
        }
    }

    private void resetCountersViews() {
        resetCounters();
        updateResults();
    }

    private void onStopRun() {
        postRunnable(mStopAdRunnable);
        mFirstAutoLaunch = true;
        showAutoTestingParams(true);
        enableInputViews(true);
        setLoadingLabelVisibility(false);
    }

    private void setLoadingLabelVisibility(boolean visible) {
        if (mLoadingLabel != null) {
            if (visible) {
                mLoadingLabel.setVisibility(View.VISIBLE);
            } else {
                mLoadingLabel.setVisibility(View.GONE);
            }
        }
    }

    private void onStartRun() {
        if (!isValidInput()) {
            return;
        }
        askAllowLogging();
        startAd();
    }

    private void askAllowLogging() {
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    BaseActivity.PERMISSION_REQUEST_CODE_SAVE_LOG);
        }
    }

    private void startAd() {
        mIsBulkTestStopped = false;
        setLoadingLabelVisibility(true);
        resetCounters();
        readEditTextValues();
        loadAd();
    }

    private void loadAd() {
        if (isAutoTestingInProcess()) {
            loadManual();
        } else {
            autoTestingFinished();
        }
    }

    private boolean isAutoTestingInProcess() {
        return mRequestsCounter < mRequestsAmount;
    }

    private void loadManual() {
        if (mAd == null) {
            initAd();
        }
        if (mAd.isReady()) {
            onLoadSuccess();
        }
        if (mAd != null) {
            increaseRequestCounter();
            mAd.loadAd();
            updateResults();
            updateLoadingLabel();
        }
    }

    private void updateLoadingLabel() {
        mLoadingLabel.setText(mActivity.getString(R.string.loading));
    }

    private void updateResults() {
        mFailedAdsTextView.setText(mActivity.getString(R.string.failed_1, mFailedCounter));
        mShowedAdsTextView.setText(mActivity.getString(R.string.showed_1, mShownCounter));
        mRequestsAdsTextView.setText(mActivity.getString(R.string.requests_1, mRequestsCounter));
    }

    private void readEditTextValues() {
        String reqAmount = mRequestsAmountEdit.getText().toString();
        if (!TextUtils.isEmpty(reqAmount)) {
            mRequestsAmount = Utils.parseToInt(reqAmount);
        }

        String timeReq = mTimeBetweenRequestsEdit.getText().toString();
        if (!TextUtils.isEmpty(timeReq)) {
            mTimeBetweenRequests = Utils.parseToInt(timeReq);
        }

        String autoCloseTime = mAutoCloseTimeEdit.getText().toString();
        if (!TextUtils.isEmpty(autoCloseTime)) {
            mAutoCloseTime = Utils.parseToInt(autoCloseTime);
        }
    }

    private void updateRequestCounter() {
        mLoadingLabel.setText(mActivity.getString(R.string.request_counter_2, mRequestsCounter, mRequestsAmount));
    }

    private void enableInputViews(boolean isEnable) {
        mRequestsAmountEdit.setEnabled(isEnable);
        mTimeBetweenRequestsEdit.setEnabled(isEnable);
        mAutoCloseTimeEdit.setEnabled(isEnable);

        mRunButton.setEnabled(isEnable);
    }


    private void resetCounters() {
        mRequestsCounter = 0;
        mFailedCounter = 0;
        mShownCounter = 0;
    }

    private void showAd() {
        if (mAd != null && mAd.isReady()) {
            mAd.showAd();
        }
    }

    public void onDestroy() {
        unregisterScreenOffReceiver();
        setLoadingLabelVisibility(false);
        dismissAd();
        destroyAd();
        resetCounters();
    }

    public void onPause() {
        pauseAd();
    }

    public void onResume() {
        resumeAd();
    }

    private void pauseAd() {
        if (mAd != null) {
            mAd.onPause();
        }
    }

    private void resumeAd() {
        if (mAd != null) {
            mAd.onResume();
        }
    }

    private void dismissAd() {
        if (mAd != null) {
            mAd.dismissAd();
        }
    }

    private void destroyAd() {
        if (mAd != null) {
            mAd.destroyAd();
            mAd = null;
        }
    }

    private void initAd() {
        if (mAdSpot == null) {
            return;
        }
        AdSdk adSdk = mAdSpot.getSdk();
        AdType adType = mAdSpot.getType();

        if (adType == AdType.INTERSTITIAL && (adSdk == AdSdk.LOOPME || adSdk == AdSdk.LMVPAID)) {
            mAd = initializeLoopmeInterstitial();
        } else if (adType == AdType.INTERSTITIAL && adSdk == AdSdk.MOPUB) {
            mAd = initializeMopubInterstitial();
        } else if (adType == AdType.BANNER && (adSdk == AdSdk.LOOPME || adSdk == AdSdk.LMVPAID)) {
            mAd = initializeLoopmeBanner();
        } else if (adType == AdType.BANNER && adSdk == AdSdk.MOPUB) {
            mAd = initializeMopubBanner();
        }
    }

    private Ad initializeLoopmeInterstitial() {
        return new AdLoopMeInterstitial(mActivity, mAdSpot.getAppKey(), this, mActivity.getAutoLoadingState());
    }

    private Ad initializeLoopmeBanner() {
        FrameLayout banner = (FrameLayout) mRootView.findViewById(R.id.loopme_banner);
        return new AdLoopMeBanner(mActivity, mAdSpot.getAppKey(), banner, this, mActivity.getAutoLoadingState());
    }

    private Ad initializeMopubInterstitial() {
        return new AdMopubInterstitial(mActivity, mAdSpot.getAppKey(), this);
    }

    private Ad initializeMopubBanner() {
        MoPubView moPubView = new MoPubView(mActivity);
        ((ViewGroup) mRootView).addView(moPubView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) moPubView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        moPubView.setLayoutParams(params);
        return new AdMopubBanner(moPubView, mAdSpot.getAppKey(), this);
    }

    private boolean isValidInput() {
        readEditTextValues();
        return areParamsParcelable() && isCorrectParams();
    }

    private boolean areParamsParcelable() {
        if (!Utils.isInt(mRequestsAmountEdit.getText().toString())) {
            Toast.makeText(mActivity, R.string.ad_request_amount_is_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isInt(mTimeBetweenRequestsEdit.getText().toString())) {
            Toast.makeText(mActivity, R.string.time_between_request_is_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Utils.isInt(mAutoCloseTimeEdit.getText().toString())) {
            Toast.makeText(mActivity, R.string.auto_close_time_is_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isCorrectParams() {
        return isCorrectRequestAmounth() && isCorrectAutoCloseTime() && isCorrectTimeBetweenRequests();
    }

    private boolean isCorrectTimeBetweenRequests() {
        if (mTimeBetweenRequests >= 0) {
            return true;
        } else {
            Toast.makeText(mActivity, R.string.time_between_request_is_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isCorrectAutoCloseTime() {
        if (mAutoCloseTime != 0) {
            return true;
        } else {
            Toast.makeText(mActivity, R.string.auto_close_time_is_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isCorrectRequestAmounth() {
        if (mRequestsAmount != 0) {
            return true;
        } else {
            Toast.makeText(mActivity, R.string.ad_request_amount_is_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void setAdSpot(AdSpot mAdSpot) {
        this.mAdSpot = mAdSpot;
    }

    public void cancelTask() {
        postRunnable(mStopAdRunnable);
        mFirstAutoLaunch = true;
        mIsBulkTestStopped = true;
        dismissAd();
        resetCounters();
    }

    private void unregisterScreenOffReceiver() {
        if (mScreenOffReceiver != null) {
            mScreenOffReceiver.destroy();
        }
    }

    private boolean getAutoLoadingState() {
        return mActivity != null && mActivity.getAutoLoadingState();
    }

    @Override
    public void onScreenOff() {
        onStopRun();
        Logging.out(LOG_TAG, "Automated testing stopped");
    }

    private Runnable initShowAdRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    showAutoTestingParams(false);
                    updateRequestCounter();
                    showAd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable initStopAdRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                Logging.out(LOG_TAG, "ad stopped manually");
                removeRunnable(mAutoCloseRunnable);
                mIsBulkTestStopped = true;
                dismissAd();
                destroyAd();
                enableInputViews(true);
                updateResults();
                showAutoTestingParams(true);
            }
        };
    }

    private void removeRunnable(Runnable runnable) {
        if (mActivity != null) {
            mActivity.removeRunnable(runnable);
        }
    }
}
