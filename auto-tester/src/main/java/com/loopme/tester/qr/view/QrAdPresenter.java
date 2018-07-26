package com.loopme.tester.qr.view;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.loopme.tester.qr.custom.InterstitialController;
import com.loopme.tester.qr.model.AdDescriptor;
import com.loopme.tester.tracker.AppEventTracker;

public class QrAdPresenter implements QrAdContract.Presenter, InterstitialController.Listener {
    private boolean mShowReplayView;
    private Activity mActivity;
    private AdDescriptor mAdDescriptor;
    private final QrAdContract.View mView;
    private boolean mIsInProcess;
    private final InterstitialController mInterstitialController;
    private boolean mIsViewCreated;

    public QrAdPresenter(Activity activity, QrAdContract.View view) {
        mActivity = activity;
        mView = view;
        mView.setPresenter(this);
        mInterstitialController = new InterstitialController(activity, this);
    }

    @Override
    public void onViewCreated() {
        if (!mIsViewCreated) {
            mIsViewCreated = true;
            addQReaderFragment();
            track(AppEventTracker.Event.QR_SCANNER_LAUNCHED);
        }
    }

    @Override
    public void onAdDetected(@NonNull AdDescriptor descriptor) {
        if (!mIsInProcess) {
            mIsInProcess = true;
            mAdDescriptor = descriptor;
            initAd(descriptor);
        }
    }

    @Override
    public void onNotAdDetected(@NonNull String content) {
        mView.showMessage(content + " is not Ad");
    }

    @Override
    public void onReplayClicked(@NonNull AdDescriptor descriptor) {
        if (descriptor.isInterstitial()) {
            loadInterstitial(descriptor.getUrl());
        } else {
            addBannerFragment(descriptor);
        }
    }

    @Override
    public void destroy() {
        mInterstitialController.destroy();
        mActivity = null;
    }

    @Override
    public void onBackPressed() {
        mIsInProcess = false;
        if (mView.isBannerFragmentOnTop()) {
            addQReaderFragment();
        } else {
            finish();
        }
    }

    @Override
    public void track(@NonNull AppEventTracker.Event event, Object... args) {
        AppEventTracker.getInstance().track(event, args);
    }

    private void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    private void initAd(@NonNull AdDescriptor descriptor) {
        if (descriptor.isInterstitial()) {
            handleInterstitialCase(descriptor.getUrl());
        } else {
            addBannerFragment(descriptor);
        }
    }

    private void addQReaderFragment() {
        mView.addQReaderFragment(mAdDescriptor, mShowReplayView);
    }

    private void addBannerFragment(AdDescriptor descriptor) {
        mShowReplayView = true;
        mView.addBannerFragment(descriptor);
    }


    private void handleInterstitialCase(String url) {
        mShowReplayView = false;
        loadInterstitial(url);
    }

    private void loadInterstitial(String url) {
        if (!mInterstitialController.isLoading()) {
            mInterstitialController.load(url);
        }
    }

    @Override
    public void onAdFail(String message) {
        mView.showMessage(message);
        mView.showProgress(false);
        mView.resumeQReader();
    }

    @Override
    public void onAdShow() {
        mIsInProcess = false;
        mView.showProgress(false);
    }

    @Override
    public void onAdHide() {
        mView.enableControlsView();
    }

    @Override
    public void onAdLoading() {
        mView.showProgress(true);
        mView.pauseQReader();
    }
}
