package com.loopme.banner_sample.app.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loopme.LoopMeBanner;
import com.loopme.banner_sample.R;
import com.loopme.common.LoopMeError;

public class SimpleBannerFragment extends Fragment {
    private LoopMeBanner mBanner;

    public static SimpleBannerFragment newInstance() { return new SimpleBannerFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_banner_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout containerView = view.findViewById(R.id.video_ad_spot);
        mBanner = LoopMeBanner.getInstance("3ae8c26803", getActivity());
        mBanner.bindView(containerView);
        mBanner.setListener(new LoopMeBanner.Listener() {
            @Override
            public void onLoopMeBannerLoadFail(LoopMeBanner loopMeBanner, LoopMeError loopMeError) {
                Toast.makeText(getActivity(), "LoadFail: " + loopMeError.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLoopMeBannerLoadSuccess(LoopMeBanner loopMeBanner) { mBanner.show(); }
            @Override
            public void onLoopMeBannerShow(LoopMeBanner loopMeBanner) { }
            @Override
            public void onLoopMeBannerClicked(LoopMeBanner loopMeBanner) { }
            @Override
            public void onLoopMeBannerExpired(LoopMeBanner loopMeBanner) { }
            @Override
            public void onLoopMeBannerHide(LoopMeBanner loopMeBanner) { }
            @Override
            public void onLoopMeBannerLeaveApp(LoopMeBanner loopMeBanner) { }
            @Override
            public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner loopMeBanner) { }
        });
        mBanner.setAutoLoading(false);
        mBanner.load();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBanner.resume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mBanner.pause();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBanner.dismiss();
        mBanner.destroy();
    }
}
