package com.loopme.ironsorce_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.ironsource.adapters.custom.loopme.LoopmeCustomAdapter;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.loopme.LoopMeSdk;
import com.loopme.mopub_mediation_sample.R;

public class InterstitialActivity extends Activity {
    private static final String appKey = "your IS`s appkey";
    private static final String loopmeAppKey = "your Loopme`s appkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        View loadButton = findViewById(R.id.load_button);
        loadButton.setEnabled(false);
        // Use this method in case if you Publisher is willing to ask GDPR consent with your own or
        // MoPub dialog AND don't want LoopMe consent dialog to be shown - pass GDPR consent to this method.
        //LoopMeAdapterConfiguration.setPublisherConsent(loopMeConf, true);

        LoopMeSdk.initialize(this, new LoopMeSdk.Configuration(), new LoopMeSdk.LoopMeSdkListener() {
            @Override
            public void onSdkInitializationSuccess() {
                Toast.makeText(InterstitialActivity.this, "go", Toast.LENGTH_LONG).show();
                loadButton.setEnabled(true);
            }

            @Override
            public void onSdkInitializationFail(int error, String message) {
                Toast.makeText(InterstitialActivity.this, "g", Toast.LENGTH_LONG).show();
            }
        });
        LoopmeCustomAdapter.setWeakActivity(this);
        LoopmeCustomAdapter.setLoopmeAppkey("hello");
        IronSource.init(this, appKey, IronSource.AD_UNIT.INTERSTITIAL);
        IronSource.setAdaptersDebug(true);

        Toast.makeText(this, "IronSource has been initialized", Toast.LENGTH_SHORT).show();

        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             * Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
                Toast.makeText(getApplicationContext(), "onInterstitialAdReady", Toast.LENGTH_SHORT).show();
            }

            /**
             * invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
                Toast.makeText(getApplicationContext(), "onInterstitialAdLoadFailed", Toast.LENGTH_SHORT).show();
            }

            /**
             * Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
                Toast.makeText(getApplicationContext(), "onInterstitialAdOpened", Toast.LENGTH_SHORT).show();
            }

            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
                Toast.makeText(getApplicationContext(), "onInterstitialAdClosed", Toast.LENGTH_SHORT).show();
            }

            /**
             * Invoked when Interstitial ad failed to show.
             * @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
                Toast.makeText(getApplicationContext(), "onInterstitialAdShowFailed", Toast.LENGTH_SHORT).show();
            }

            /*
             * Invoked when the end user clicked on the interstitial ad, for supported networks only.
             */
            @Override
            public void onInterstitialAdClicked() {
                Toast.makeText(getApplicationContext(), "onInterstitialAdClicked", Toast.LENGTH_SHORT).show();
            }

            /** Invoked right before the Interstitial screen is about to open.
             *  NOTE - This event is available only for some of the networks.
             *  You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
                Toast.makeText(getApplicationContext(), "onInterstitialAdShowSucceeded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onShowClicked(View view) {
        if (IronSource.isInterstitialReady())
            IronSource.showInterstitial();
    }

    public void onLoadClicked(View view) {
        IronSource.loadInterstitial();
    }


    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
}