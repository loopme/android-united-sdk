package com.loopme.ironsorce_mediation_sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ironsource.adapters.custom.loopme.LoopMeCustomAdapter;
import com.ironsource.mediationsdk.IronSource;
import com.loopme.mopub_mediation_sample.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String appKey = "15b99565d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> loopMeConf = new HashMap<>();

        // Use this method in case if you Publisher is willing to ask GDPR consent with your own or
        // MoPub dialog AND don't want LoopMe consent dialog to be shown - pass GDPR consent to this method.
        //LoopMeAdapterConfiguration.setPublisherConsent(loopMeConf, true);

        // Unfortunately, MoPub SDK v5.5.x doesn't pass Activity context to initializeNetwork method:
        // pass it here, before initializing MoPub SDK.
        LoopMeCustomAdapter.setWeakActivity(this);
        IronSource.init(this, appKey, IronSource.AD_UNIT.INTERSTITIAL);

        Toast.makeText(this, "IronSource has been initialized", Toast.LENGTH_SHORT).show();
        enableButtons();
    }

    private void enableButtons() {
        findViewById(R.id.interstitial).setEnabled(true);
        findViewById(R.id.rewarded).setEnabled(true);
        findViewById(R.id.banner).setEnabled(true);
    }

    public void openActivity(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.interstitial:
                intent = new Intent(this, InterstitialActivity.class);
                break;
        }

        startActivity(intent);
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