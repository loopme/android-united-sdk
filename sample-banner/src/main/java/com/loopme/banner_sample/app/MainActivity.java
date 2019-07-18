package com.loopme.banner_sample.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.loopme.LoopMeSdk;
import com.loopme.banner_sample.app.model.Constants;
import com.loopme.banner_sample.app.views.MainFeaturesFragment;
import com.loopme.banner_sample.app.views.RecyclerViewFragment;
import com.loopme.banner_sample.app.views.RecyclerViewShrinkFragment;
import com.loopme.banner_sample.app.views.SimpleBannerFragment;

public class MainActivity
        extends AppCompatActivity
        implements
        MainFeaturesFragment.OnItemClickedListener,
        LoopMeSdk.LoopMeSdkListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        setAsMainView(MainFeaturesFragment.newInstance(), false);

        tryInitLoopMeSdk();
    }

    @Override
    public void onItemClicked(String item) {
        switch (item) {
            case Constants.SIMPLE: {
                setAsMainView(SimpleBannerFragment.newInstance(), true);
                setTitle("Simple banner");
                break;
            }
            case Constants.RECYCLERVIEW: {
                setAsMainView(RecyclerViewFragment.newInstance(), true);
                setTitle("Recycler view");

                break;
            }
            case Constants.RECYCLERVIEW_SHRINK: {

                setAsMainView(RecyclerViewShrinkFragment.newInstance(), true);
                setTitle("Recycler view with shrink mode");
                break;
            }
        }
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
        LoopMeSdk.initialize(this, conf, this);
    }
}
