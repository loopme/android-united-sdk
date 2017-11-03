package com.loopme.banner_sample.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.loopme.banner_sample.app.views.MainFeaturesFragment;
import com.loopme.banner_sample.app.views.RecyclerViewShrinkFragment;
import com.loopme.banner_sample.app.views.SimpleBannerFragment;
import com.loopme.banner_sample.app.views.RecyclerViewFragment;
import com.loopme.banner_sample.app.model.Constants;

public class MainActivity extends AppCompatActivity implements MainFeaturesFragment.OnItemClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        setAsMainView(MainFeaturesFragment.newInstance(), false);
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
        if (fragment != null && fragment instanceof MainFeaturesFragment) {
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
