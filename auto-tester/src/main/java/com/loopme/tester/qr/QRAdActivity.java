package com.loopme.tester.qr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.tester.R;
import com.loopme.tester.utils.UiUtils;

public class QRAdActivity extends AppCompatActivity
        implements QReaderFragment.QReaderListener,
        LoopMeInterstitial.Listener,
        QRControlsFragment.Listener {
    private LoopMeInterstitial mInterstitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_ad);

        loadFragments();
        UiUtils.makeActivitySlidable(this);
        mInterstitial = new LoopMeInterstitial(this, "mockAppKey");
        mInterstitial.setListener(this);
        mInterstitial.setAutoLoading(false);
    }

    private void loadFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_qr_ad_reader_container, QReaderFragment.newInstance())
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_qr_ad_controls_container, QRControlsFragment.newInstance())
                .commit();
    }

    @Override
    public void onUrlDetected(String url) {
        setReplayUrl(url);
        loadAd(url);
    }

    @Override
    public void onNoneUrlDetected(String content) {
        showMessage(content + " is not URL");
    }

    @Override
    public void onCloseClicked() {
        finish();
    }

    @Override
    public void onReplayClicked(String url) {
        loadAd(url);
    }

    public void loadAd(String url) {
        if (mInterstitial != null && !mInterstitial.isLoading()) {
            mInterstitial.load(url);
            showProgress(true);
            onPause();
        }
    }

    private void showAd() {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    private void setReplayUrl(String url) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_qr_ad_controls_container);
        if (fragment instanceof QRControlsFragment) {
            ((QRControlsFragment) fragment).setReplayUrl(url);
        }
    }

    private void showProgress(boolean show) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_qr_ad_reader_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).showProgress(show);
        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_qr_ad_root), message, Snackbar.LENGTH_LONG).show();
    }

    private void enableControlsView() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_qr_ad_controls_container);
        if (fragment instanceof QRControlsFragment) {
            ((QRControlsFragment) fragment).enableControlsView();
        }
    }

    @Override
    protected void onDestroy() {
        mInterstitial.destroy();
        super.onDestroy();
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        showAd();
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        showMessage(error.getMessage());
        showProgress(false);
        onResume();
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        enableControlsView();
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
        showProgress(false);
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
    }
}
