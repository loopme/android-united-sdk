package com.loopme.sdk_sample.app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopme.LoopMeInterstitial;
import com.loopme.sdk_sample.R;
import com.loopme.sdk_sample.databinding.ActivityInterstitialBinding;
import com.loopme.common.LoopMeError;

public class InterstitialActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private LoopMeInterstitial interstitial;
    private ActivityInterstitialBinding binding;
    private String selectedAppKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInterstitialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpSpinner();
        setUpButtons();
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.interstiatial_app_keys, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.appKeySpinner.setAdapter(adapter);
        binding.appKeySpinner.setOnItemSelectedListener(this);
    }

    private void setUpButtons() {
        binding.showButton.setEnabled(false);
        binding.showButton.setOnClickListener(view -> {
            if (interstitial.isReady()) {
                interstitial.show();
                binding.loadButton.setEnabled(true);
                binding.loadCustomButton.setEnabled(true);
            } else {
                alert("Interstitial is not ready");
            }
        });

        binding.loadButton.setOnClickListener(view -> loadInterstitial(selectedAppKey));
        binding.loadCustomButton.setOnClickListener(view -> {
            String appkey = binding.appKeyEt.getText().toString();
            loadInterstitial(appkey.isEmpty() ? getString(R.string.default_interstitial_app_key) : appkey);
        });
    }

    @Override
    protected void onDestroy() {
        if (interstitial != null) interstitial.destroy();
        super.onDestroy();
    }

    private void loadInterstitial(String appKey) {
        binding.loadButton.setEnabled(false);
        binding.loadCustomButton.setEnabled(false);
        if (interstitial != null) {
            interstitial.destroy();
            interstitial = null;
        }
        interstitial = LoopMeInterstitial.getInstance(appKey, this);
        interstitial.setAutoLoading(false);
        interstitial.setListener(new InterstitialListener());
        interstitial.load();
        binding.showButton.setEnabled(false);
    }

    private void alert(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedAppKey = (String) parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class InterstitialListener implements LoopMeInterstitial.Listener {
        @Override
        public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
            binding.showButton.setEnabled(true);
        }

        @Override
        public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
            alert(error.getMessage());
            binding.loadButton.setEnabled(true);
            binding.loadCustomButton.setEnabled(true);
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
    }
}