package com.loopme.tester.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.tester.R;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.ui.activity.QReaderActivity;

public class QrFragment extends BaseFragment implements LoopMeInterstitial.Listener, View.OnClickListener {
    private LoopMeInterstitial mInterstitial;
    private String mUrl;
    private View mProgressBar;
    private View mReplayButton;
    private View mRootView;

    public static QrFragment newInstance(Bundle bundle) {
        QrFragment fragment = new QrFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_qr, container, false);
        setRootView(mRootView);
        mRootView.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUrl();
        mProgressBar = view.findViewById(R.id.fragment_qr_progress_bar);
        mReplayButton = view.findViewById(R.id.fragment_qr_replay);
        mReplayButton.setOnClickListener(this);
        initAd();
        loadAd();
    }

    private void setUrl() {
        if (getArguments() != null) {
            mUrl = getArguments().getString(QReaderActivity.ARG_AD_URL);
        }
    }

    private void loadAd() {
        enableReplayButton(false);
        enableProgressBar(true);
        if (mInterstitial != null) {
            mInterstitial.load(mUrl);
        }
    }

    private void initAd() {
        if (mInterstitial == null) {
            mInterstitial = new LoopMeInterstitial(getAppCompatActivity(), "mockAppKey");
            mInterstitial.setListener(this);
            mInterstitial.setAutoLoading(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_qr_replay: {
                loadAd();
                break;
            }
            case R.id.fragment_qr_container: {
                dismiss();
                break;
            }
        }
    }

    private void dismiss() {
        if (getAppCompatActivity() instanceof BaseActivity) {
            ((BaseActivity) getAppCompatActivity()).closeCurrentScreen();
        }
    }

    private void enableProgressBar(boolean enable) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    private void enableReplayButton(boolean enable) {
        if (mReplayButton != null) {
            mReplayButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        enableProgressBar(false);
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        Snackbar.make(mRootView, error.getMessage(), Snackbar.LENGTH_LONG).show();
        enableReplayButton(true);
        enableProgressBar(false);
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        enableReplayButton(true);
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
