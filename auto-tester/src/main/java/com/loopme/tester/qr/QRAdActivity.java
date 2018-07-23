package com.loopme.tester.qr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.tester.Constants;
import com.loopme.tester.R;
import com.loopme.tester.qr.model.AdDescriptor;
import com.loopme.tester.qr.listener.InterstitialListenerAdapter;
import com.loopme.tester.utils.UiUtils;

public class QRAdActivity extends AppCompatActivity
        implements QReaderFragment.QReaderListener, View.OnClickListener {
    private LoopMeInterstitial mInterstitial;
    private boolean mIsBannerFragmentAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_ad);
        findViewById(R.id.qr_controls_close_button).setOnClickListener(this);

        addQReaderFragments();
        UiUtils.makeActivitySlidable(this);
    }

    @Override
    public void onAdDetected(@NonNull AdDescriptor descriptor) {
        initAd(descriptor);
    }

    private void initAd(@NonNull AdDescriptor descriptor) {
        if (descriptor.isInterstitial()) {
            handleInterstitialCase(descriptor);
        } else {
            addBannerFragment(descriptor);
        }
    }

    private void handleInterstitialCase(AdDescriptor descriptor) {
        initInterstitial();
        loadInterstitial(descriptor.getUrl());
    }

    @Override
    public void onTrashDetected(@NonNull String content) {
        showMessage(content + " is not Ad");
    }

    @Override
    public void onReplayClicked(String url) {
        loadInterstitial(url);
    }

    public void loadInterstitial(String url) {
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

    private void showProgress(boolean show) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_qr_ad_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).showProgress(show);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qr_controls_close_button: {
                onBackButtonPressed();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isBannerFragmentOnTop()) {
            onBackButtonPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void onBackButtonPressed() {
        if (isBannerFragmentOnTop()) {
            addQReaderFragments();
        } else {
            finish();
        }
    }

    private void addBannerFragment(AdDescriptor descriptor) {
        if (!mIsBannerFragmentAdded) {
            mIsBannerFragmentAdded = true;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_qr_ad_container, QrBannerFragment.newInstance(descriptor))
                    .commit();
        }
    }

    private void addQReaderFragments() {
        mIsBannerFragmentAdded = false;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_qr_ad_container, QReaderFragment.newInstance())
                .commit();
    }

    private boolean isBannerFragmentOnTop() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_qr_ad_container);
        return fragment instanceof QrBannerFragment;
    }

    public void initInterstitial() {
        if (mInterstitial == null) {
            mInterstitial = new LoopMeInterstitial(this, Constants.MOCK_APP_KEY);
            mInterstitial.setAutoLoading(false);
            mInterstitial.setListener(new InterstitialListenerAdapter() {
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
                public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
                    showProgress(false);

                }

                @Override
                public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
                    enableControlsView();
                }
            });
        }
    }

    private void enableControlsView() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_qr_ad_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).enableControlsView();
        }
    }
}
