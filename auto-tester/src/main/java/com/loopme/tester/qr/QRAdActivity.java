package com.loopme.tester.qr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.tester.R;
import com.loopme.tester.qr.fragment.QReaderFragment;
import com.loopme.tester.utils.UiUtils;

public class QRAdActivity extends AppCompatActivity implements QReaderFragment.QReaderListener, LoopMeInterstitial.Listener, View.OnClickListener {
    private LoopMeInterstitial mInterstitial;
    private String mUrl;
    private TextView mReplayUrlTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_ad);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_qr_ad_reader_container, QReaderFragment.newInstance())
                .commit();
        mReplayUrlTextView = (TextView) findViewById(R.id.activity_qr_ad_replay_url_text_view);
        findViewById(R.id.activity_qr_ad_replay_image_view).setOnClickListener(this);
        findViewById(R.id.activity_qr_ad_back_button).setOnClickListener(this);
        UiUtils.makeActivitySlidable(this);
        initAd();
    }

    private void initAd() {
        if (mInterstitial == null) {
            mInterstitial = new LoopMeInterstitial(this, "mockAppKey");
            mInterstitial.setListener(this);
            mInterstitial.setAutoLoading(false);
        }
    }

    @Override
    protected void onDestroy() {
        mInterstitial.destroy();
        super.onDestroy();
    }

    @Override
    public void onUrlDetected(String url) {
        mUrl = url;
        loadAd(url);

    }

    @Override
    public void onNoneUrlDetected(String content) {
        showMessage(content + " is not URL");
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

    public void reload() {
        if (mInterstitial != null) {
            loadAd(mUrl);
        }
    }

    public void loadAd(String url) {
        if (mInterstitial != null && !mInterstitial.isLoading()) {
            mInterstitial.load(url);
            showProgress(true);
            onPause();
        }
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_qr_ad_replay_image_view) {
            reload();
        } else if (v.getId() == R.id.activity_qr_ad_back_button) {
            finish();
        }
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
        showProgress(false);
        findViewById(R.id.activity_qr_ad_replay_view_layout).setVisibility(View.VISIBLE);
        mReplayUrlTextView.setText(mUrl);
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
