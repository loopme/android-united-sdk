package com.loopme.banner_sample.app.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopme.LoopMeBanner;
import com.loopme.LoopMeBannerView;
import com.loopme.banner_sample.app.R;
import com.loopme.common.LoopMeError;

public class SimpleBannerFragment extends Fragment implements LoopMeBanner.Listener {
    private LoopMeBanner mBanner;

    public static SimpleBannerFragment newInstance() {
        return new SimpleBannerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_banner_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoopMeBannerView containerView = (LoopMeBannerView) view.findViewById(R.id.video_ad_spot);
        initBanner(containerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeBanner();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseBanner();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyBanner();
    }

    private void initBanner(LoopMeBannerView containerView) {
        mBanner = LoopMeBanner.getInstance(LoopMeBanner.TEST_MPU_BANNER, getActivity());
        mBanner.bindView(containerView);
        mBanner.setListener(this);
        mBanner.setAutoLoading(false);
        mBanner.load();
    }

    private void destroyBanner() {
        if (mBanner != null) {
            mBanner.dismiss();
            mBanner.destroy();
        }
    }

    private void pauseBanner() {
        if (mBanner != null) {
            mBanner.pause();
        }
    }

    private void resumeBanner() {
        if (mBanner != null) {
            mBanner.resume();
        }
    }

    private void showBanner() {
        if (mBanner != null) {
            mBanner.show();
        }
    }

    @Override
    public void onLoopMeBannerLoadFail(LoopMeBanner loopMeBanner, LoopMeError loopMeError) {
        Toast.makeText(getActivity(), "LoadFail: " + loopMeError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoopMeBannerLoadSuccess(LoopMeBanner loopMeBanner) {
        showBanner();
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner loopMeBanner) {
    }

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner loopMeBanner) {
    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner loopMeBanner) {
    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner loopMeBanner) {
    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner loopMeBanner) {
    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner loopMeBanner) {
    }
}
