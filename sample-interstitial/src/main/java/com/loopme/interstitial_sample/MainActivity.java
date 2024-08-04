package com.loopme.interstitial_sample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopme.LoopMeInterstitial;
import com.loopme.LoopMeSdk;
import com.loopme.common.LoopMeError;
import com.loopme.gdpr.GdprChecker;

public class MainActivity extends AppCompatActivity {

    private LoopMeInterstitial mInterstitial;

    private Button showBtn;
    private Button loadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showBtn = findViewById(R.id.show_button);
        showBtn.setEnabled(false);
        showBtn.setOnClickListener(view -> {
            if (mInterstitial.isReady()) {
                mInterstitial.show();
            } else {
                alert("Interstitial is not ready");
            }
        });

        loadBtn = findViewById(R.id.load_button);
        loadBtn.setEnabled(false);
        loadBtn.setOnClickListener(view -> onLoadClicked());

        tryInitLoopMeSdk();
    }

    @Override
    protected void onDestroy() {
        if (mInterstitial != null) mInterstitial.destroy();
        super.onDestroy();
    }

    private void onLoadClicked() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
            mInterstitial = null;
        }
        mInterstitial = LoopMeInterstitial.getInstance("b5df00223e", this);
        mInterstitial.setAutoLoading(false);

        // Adding listener to receive SDK notifications during the
        // loading/displaying ad processes
        mInterstitial.setListener(new LoopMeInterstitial.Listener() {
            @Override
            public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
                showBtn.setEnabled(true);
            }
            @Override
            public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
                alert(error.getMessage());
            }
            @Override
            public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) { }
            @Override
            public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) { showBtn.setEnabled(false); }
            @Override
            public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) { }
            @Override
            public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) { }
            @Override
            public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) { }
            @Override
            public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) { }
        });

        // Start loading immediately
        mInterstitial.load();
        showBtn.setEnabled(false);
    }

    private void alert(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void tryInitLoopMeSdk() {
        if (LoopMeSdk.isInitialized()) return;
        alert("LoopMe SDK: initialization…");
        LoopMeSdk.Configuration conf = new LoopMeSdk.Configuration();
        conf.setPublisherConsent(new GdprChecker.PublisherConsent("BO9ZhJEO9ZhJEAAABBENDW-AAAAyPAAA"));
        LoopMeSdk.initialize(this, conf, new LoopMeSdk.LoopMeSdkListener() {
            @Override
            public void onSdkInitializationSuccess() {
                loadBtn.setEnabled(true);
                alert("LoopMe SDK: initialized");
            }
            @Override
            public void onSdkInitializationFail(int errorCode, String message) {
                tryInitLoopMeSdk();
                alert("LoopMe SDK: failed to initialize. Trying again…");
            }
        });
    }
}