package com.loopme.tester.qr.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.loopme.tester.tracker.AppEventTracker;
import com.loopme.tester.utils.Utils;

public class QrBannerFragment extends QrBaseFragment {
    private FrameLayout mBannerView;
    private LoopMeBanner mBanner;
    private Activity mActivity;
    private View mProgressView;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_banner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mBannerView = view.findViewById(R.id.fragment_qr_banner_view_container);
        mProgressView = view.findViewById(R.id.fragment_qr_banner_progress_bar);
        configBannerView();
        initBanner();
        loadBanner();
    }

    private void showProgress(boolean show) {
        if (mProgressView != null) {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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
        if (mAdDescriptor != null && mBannerView != null) {
            int width = Utils.convertDpToPixel(mAdDescriptor.getWidth(), mActivity);
            int height = Utils.convertDpToPixel(mAdDescriptor.getHeight(), mActivity);
            ViewGroup.LayoutParams layoutParams = mBannerView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
        }
    }

    private void loadBanner() {
        if (mBanner != null && mAdDescriptor != null) {
            mBanner.load(mAdDescriptor.getUrl());
        }
    }

    @Override
    public void onDestroyView() {
        mActivity = null;
        destroyBanner();
        super.onDestroyView();
    }

    private void destroyBanner() {
        if (mBanner != null) {
            mBanner.destroy();
            mBanner = null;
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
                    show();
                    showProgress(false);
                    track(AppEventTracker.Event.QR_SUCCESS);
                }

                @Override
                public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
                    showProgress(false);
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    track(AppEventTracker.Event.QR_FAIL);
                }
            });
        }
    }

    private void track(AppEventTracker.Event event) {
        if (getParentFragment() instanceof QrAdFragment) {
            QrAdFragment qrAdFragment = (QrAdFragment) getParentFragment();
            qrAdFragment.track(event);
        }
    }

    private void show() {
        if (mBanner != null) {
            mBanner.show();
        }
    }
}
