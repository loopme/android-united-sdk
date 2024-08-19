package com.loopme.sdk_sample.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.loopme.sdk_sample.R;
import com.loopme.sdk_sample.app.model.Constants;
import com.loopme.sdk_sample.app.views.MainFeaturesFragment;
import com.loopme.sdk_sample.app.views.RecyclerViewFragment;
import com.loopme.sdk_sample.app.views.SimpleBannerFragment;

public class BannerActivity
    extends AppCompatActivity
    implements MainFeaturesFragment.OnItemClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        setAsMainView(MainFeaturesFragment.newInstance(), false);
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
}
