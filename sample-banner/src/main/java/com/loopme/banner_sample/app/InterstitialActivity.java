package com.loopme.banner_sample.app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopme.LoopMeInterstitial;
import com.loopme.banner_sample.databinding.ActivityInterstitialBinding;
import com.loopme.common.LoopMeError;

public class InterstitialActivity extends AppCompatActivity {

    private LoopMeInterstitial mInterstitial;

    private ActivityInterstitialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInterstitialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpButtons();
    }

    private void setUpButtons() {

        binding.showButton.setEnabled(false);
        binding.showButton.setOnClickListener(view -> {
            if (mInterstitial.isReady()) {
                mInterstitial.show();
            } else {
                alert("Interstitial is not ready");
            }
        });

        binding.loadButton.setOnClickListener(view -> onLoadClicked());
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
                binding.showButton.setEnabled(true);
            }

            @Override
            public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
                alert(error.getMessage());
            }

            @Override
            public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
            }

            @Override
            public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
                binding.showButton.setEnabled(false);
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
        });

        // Start loading immediately
        mInterstitial.load();
        binding.showButton.setEnabled(false);
    }

    private void alert(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}