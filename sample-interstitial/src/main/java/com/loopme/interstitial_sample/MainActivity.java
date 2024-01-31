package com.loopme.interstitial_sample;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.loopme.LoopMeInterstitial;
import com.loopme.LoopMeSdk;
import com.loopme.common.LoopMeError;
import com.loopme.gdpr.GdprChecker;

public class MainActivity
        extends AppCompatActivity
        implements
        LoopMeInterstitial.Listener,
        LoopMeSdk.LoopMeSdkListener {

    private LoopMeInterstitial mInterstitial;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        tryInitLoopMeSdk();
    }

    private void initViews() {
        findViewById(R.id.show_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowClicked();
            }
        });
        findViewById(R.id.load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadClicked();
            }
        });
        initProgressDialog();
    }

    @Override
    protected void onPause() {
        cancelDialog();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cancelDialog();
        // Clean up resources
        destroyInterstitial();
        super.onDestroy();
    }

    private void onLoadClicked() {
        // Create new interstitial object
        if (mInterstitial == null) {
            mInterstitial = LoopMeInterstitial.getInstance(LoopMeInterstitial.TEST_PORT_INTERSTITIAL, this);
            mInterstitial.setAutoLoading(false);
        }

        if (mInterstitial != null) {
            showProgress();

            // Adding listener to receive SDK notifications during the
            // loading/displaying ad processes
            mInterstitial.setListener(this);

            // Start loading immediately
            mInterstitial.load();
        }
    }

    private void onShowClicked() {
        // Checks whether ad ready to be shown
        if (mInterstitial != null && mInterstitial.isReady()) {
            // Show ad
            mInterstitial.show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.interstitial_is_not_ready, Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);
    }

    private void cancelDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void destroyInterstitial() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    private void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        dismissProgress();
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        dismissProgress();
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
        dismissProgress();
    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onSdkInitializationSuccess() {
        Toast.makeText(this, "LoopMe SDK initialized. Good to go…", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSdkInitializationFail(int errorCode, String message) {
        Toast.makeText(this, "LoopMe SDK failed to initialize. Trying again…", Toast.LENGTH_SHORT).show();
        tryInitLoopMeSdk();
    }

    private void tryInitLoopMeSdk() {
        if (LoopMeSdk.isInitialized())
            return;

        Toast.makeText(this, "Wait for LoopMe SDK initialization…", Toast.LENGTH_SHORT).show();

        LoopMeSdk.Configuration conf = new LoopMeSdk.Configuration();
        conf.setPublisherConsent(new GdprChecker.PublisherConsent("BO9ZhJEO9ZhJEAAABBENDW-AAAAyPAAA"));
        LoopMeSdk.initialize(this, conf, this);
    }
}