package com.loopme.banner_sample.app;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.loopme.LoopMeSdk;
import com.loopme.banner_sample.R;
import com.loopme.banner_sample.app.model.Constants;
import com.loopme.banner_sample.app.views.MainFeaturesFragment;
import com.loopme.banner_sample.app.views.RecyclerViewFragment;
import com.loopme.banner_sample.app.views.SimpleBannerFragment;

public class MainActivity
    extends AppCompatActivity
    implements MainFeaturesFragment.OnItemClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        setAsMainView(MainFeaturesFragment.newInstance(), false);
        tryInitLoopMeSdk();
    }

    @Override
    public void onItemClicked(String item) {
        if (item.equalsIgnoreCase(Constants.SIMPLE)) {
            setAsMainView(SimpleBannerFragment.newInstance(), true);
        }
        if (item.equalsIgnoreCase(Constants.RECYCLERVIEW)) {
            setAsMainView(RecyclerViewFragment.newInstance(), true);
        }
        setTitle("View: " + item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_root_view);
        if (fragment instanceof MainFeaturesFragment) {
            super.onBackPressed();
        } else {
            setAsMainView(MainFeaturesFragment.newInstance(), true);
            setTitle("LoopMe Banner Samples");
        }
    }

    private void setAsMainView(Fragment fragment, boolean replace) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (replace) {
            fragmentTransaction.replace(R.id.main_root_view, fragment);
        } else {
            fragmentTransaction.add(R.id.main_root_view, fragment);
        }
        fragmentTransaction.commit();
    }

    private void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void tryInitLoopMeSdk() {
        if (LoopMeSdk.isInitialized()) return;
        alert("LoopMe SDK: initialization…");
        LoopMeSdk.initialize(this, new LoopMeSdk.Configuration(), new LoopMeSdk.LoopMeSdkListener() {
            @Override
            public void onSdkInitializationSuccess() {
                alert("LoopMe SDK: initialized");
            }
            @Override
            public void onSdkInitializationFail(int errorCode, String message) {
                alert("LoopMe SDK: failed to initialize. Trying again…");
                tryInitLoopMeSdk();
            }
        });
    }
}
