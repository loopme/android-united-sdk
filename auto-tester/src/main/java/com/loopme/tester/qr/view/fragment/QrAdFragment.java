package com.loopme.tester.qr.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopme.tester.R;
import com.loopme.tester.qr.model.AdDescriptor;
import com.loopme.tester.qr.view.QrAdContract;
import com.loopme.tester.tracker.AppEventTracker;
import com.loopme.tester.utils.UiUtils;

public class QrAdFragment extends Fragment implements QrAdContract.View, View.OnClickListener {

    private QrAdContract.Presenter mPresenter;
    private Context mContext;

    public static QrAdFragment newInstance() {
        return new QrAdFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_ad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.qr_controls_back_button).setOnClickListener(this);
        if (mPresenter != null) {
            mPresenter.onViewCreated();
        }
    }

    @Override
    public void setPresenter(QrAdContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void addBannerFragment(AdDescriptor adDescriptor) {
        UiUtils.replaceFragmentDelayed(getChildFragmentManager(), R.id.fragment_qr_ad_container, QrBannerFragment.newInstance(adDescriptor));
    }

    @Override
    public void addQReaderFragment(AdDescriptor adDescriptor, boolean showReplayView) {
        QReaderFragment fragment = QReaderFragment.newInstance(adDescriptor, showReplayView);
        fragment.setListener(mPresenter);
        UiUtils.replaceFragmentDelayed(getChildFragmentManager(), R.id.fragment_qr_ad_container, fragment);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress(boolean show) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_qr_ad_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).showProgress(show);
        }
    }

    @Override
    public void enableControlsView() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_qr_ad_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).enableControlsView();
        }
    }

    @Override
    public void resumeQReader() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_qr_ad_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).resume();
        }
    }

    @Override
    public void pauseQReader() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_qr_ad_container);
        if (fragment instanceof QReaderFragment) {
            ((QReaderFragment) fragment).pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext = null;
    }

    @Override
    public boolean isBannerFragmentOnTop() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_qr_ad_container);
        return fragment instanceof QrBannerFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qr_controls_back_button: {
                onBackPressed();
                break;
            }
        }
    }

    private void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onBackPressed();
        }
    }

    public void track(AppEventTracker.Event event) {
        if (mPresenter != null) {
            mPresenter.track(event);
        }
    }
}
