package com.loopme.tester.qr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.loopme.LoopMeBanner;
import com.loopme.common.LoopMeError;
import com.loopme.tester.Constants;
import com.loopme.tester.R;
import com.loopme.tester.qr.listener.BannerListenerAdapter;
import com.loopme.tester.qr.model.AdDescriptor;

public class QrBannerFragment extends Fragment {
    private static final String ARG_AD_DESCRIPTOR = "ARG_AD_DESCRIPTOR";
    private AdDescriptor mAdDescriptor;
    private FrameLayout mBannerView;
    private LoopMeBanner mBanner;
    private Activity mActivity;

    public static QrBannerFragment newInstance(AdDescriptor descriptor) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_AD_DESCRIPTOR, descriptor);
        QrBannerFragment fragment = new QrBannerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        retrieveAdDescriptor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBannerView = view.findViewById(R.id.fragment_qr_banner_view_container);
        configBannerView();
        initBanner();
        if (mAdDescriptor != null) {
            loadBanner(mAdDescriptor.getUrl());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBanner != null) {
            mBanner.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBanner != null) {
            mBanner.pause();
        }
    }

    private void configBannerView() {
        if (mAdDescriptor != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mAdDescriptor.getWidth(), mAdDescriptor.getHeight());
//            mBannerView.setLayoutParams(params);
        }
    }

    private void loadBanner(String url) {
        if (mBanner != null) {
            mBanner.load(url);
        }
    }

    @Override
    public void onDestroyView() {
        mActivity = null;
        if (mBanner != null) {
            mBanner.destroy();
            mBanner = null;
        }
        super.onDestroyView();
    }

    private void retrieveAdDescriptor() {
        if (getArguments() != null) {
            mAdDescriptor = getArguments().getParcelable(ARG_AD_DESCRIPTOR);
        }
    }

    private void initBanner() {
        if (mBanner == null) {
            mBanner = new LoopMeBanner(mActivity, Constants.MOCK_APP_KEY);
            mBanner.setAutoLoading(false);
            mBanner.bindView(mBannerView);
            mBanner.setListener(new BannerListenerAdapter() {
                @Override
                public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                    if (mBanner != null) {
                        mBanner.show();
                    }
                }

                @Override
                public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
