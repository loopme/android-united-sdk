package com.loopme.tester.qr.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.loopme.tester.R;
import com.loopme.tester.qr.custom.QReader;
import com.loopme.tester.qr.model.AdDescriptor;
import com.loopme.tester.qr.model.AdDescriptorUtils;
import com.loopme.tester.tracker.AppEventTracker;

import github.nisrulz.qreader.QRDataListener;

public class QReaderFragment extends QrBaseFragment implements QRDataListener, View.OnClickListener {
    private static final String ARG_SHOW_REPLAY_VIEW = "ARG_SHOW_REPLAY_VIEW";
    private boolean mShowReplayView;
    private View mProgressBar;
    private View mReplayView;
    private QReader mLoopMeQReader;
    private QReaderListener mListener;
    private final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static QReaderFragment newInstance(AdDescriptor descriptor, boolean showReplayView) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_AD_DESCRIPTOR, descriptor);
        bundle.putBoolean(ARG_SHOW_REPLAY_VIEW, showReplayView);
        QReaderFragment fragment = new QReaderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            mShowReplayView = getArguments().getBoolean(ARG_SHOW_REPLAY_VIEW);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.fragment_qr_controls_replay_layout).setVisibility(mShowReplayView ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.fragment_qr_controls_replay_image_view).setOnClickListener(this);
        mReplayView = view.findViewById(R.id.fragment_qr_controls_replay_layout);
        mProgressBar = view.findViewById(R.id.fragment_qr_reader_progress_bar);
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.fragment_qr_reader_surface_view);
        mLoopMeQReader = new QReader(surfaceView, this);
    }

    public void showProgress(boolean show) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resume();
    }

    public void resume() {
        if (mLoopMeQReader != null) {
            mLoopMeQReader.resume();
        }
    }

    public void pause() {
        if (mLoopMeQReader != null) {
            mLoopMeQReader.pause();
        }
    }

    @Override
    public void onPause() {
        pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mLoopMeQReader != null) {
            mLoopMeQReader.destroy();
        }
        mLoopMeQReader = null;
        mListener = null;
        super.onDestroyView();
    }

    @Override
    public void onDetected(String content) {
        handleContent(content);
    }

    private void handleContent(String content) {
        if (AdDescriptorUtils.isValid(content)) {
            mAdDescriptor = AdDescriptorUtils.parseAdDescriptor(content);
            onAdDetected(mAdDescriptor);
        } else {
            onTrashDetected(content);
        }
    }

    public void onAdDetected(final AdDescriptor descriptor) {
        HANDLER.post(() -> {
            if (mListener != null) {
                mListener.onAdDetected(descriptor);
            }
        });
    }

    public void onTrashDetected(final String content) {
        HANDLER.post(() -> {
            if (mListener != null) {
                mListener.onNotAdDetected(content);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_qr_controls_replay_image_view: {
                onReplayClicked();
                break;
            }
        }
    }

    private void onReplayClicked() {
        track(AppEventTracker.Event.QR_AD_WATCH_AGAIN);
        if (mListener != null) {
            mListener.onReplayClicked(mAdDescriptor);
        }
    }

    private void track(@NonNull AppEventTracker.Event event) {
        AppEventTracker.getInstance().track(event);
    }

    public void enableControlsView() {
        if (mReplayView != null && mReplayView.getVisibility() == View.GONE) {
            mReplayView.setVisibility(View.VISIBLE);
        }
    }

    public void setListener(QReaderListener listener) {
        mListener = listener;
    }

    public interface QReaderListener {
        void onAdDetected(@NonNull AdDescriptor descriptor);

        void onNotAdDetected(@NonNull String content);

        void onReplayClicked(AdDescriptor descriptor);
    }
}
