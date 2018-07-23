package com.loopme.tester.qr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.qr.model.AdDescriptor;
import com.loopme.tester.qr.model.AdDescriptorUtils;

import github.nisrulz.qreader.QRDataListener;

public class QReaderFragment extends Fragment implements QRDataListener, View.OnClickListener {
    private QReader mLoopMeQReader;
    private QReaderListener mListener;
    private View mProgressBar;
    private final Handler HANDLER = new Handler(Looper.getMainLooper());
    private AdDescriptor mDescriptor;
    private View mReplayView;

    public static QReaderFragment newInstance() {
        return new QReaderFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QReaderListener) {
            mListener = (QReaderListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.fragment_qr_controls_replay_layout).setVisibility(View.GONE);
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
        if (mLoopMeQReader != null) {
            mLoopMeQReader.resume();
        }
    }

    @Override
    public void onPause() {
        if (mLoopMeQReader != null) {
            mLoopMeQReader.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mLoopMeQReader != null) {
            mLoopMeQReader.destroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onDetected(String content) {
        handleContent(content);
    }

    private void handleContent(String content) {
        if (AdDescriptorUtils.isValid(content)) {
            mDescriptor = AdDescriptorUtils.parseAdDescriptor(content);
            onAdDetected(mDescriptor);
        } else {
            onTrashDetected(content);
        }
    }

    public void onAdDetected(final AdDescriptor descriptor) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onAdDetected(descriptor);
                }
            }
        });
    }

    public void onTrashDetected(final String content) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onTrashDetected(content);
                }
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
        if (mListener != null) {
            mListener.onReplayClicked(mDescriptor.getUrl());
        }
    }

    public void enableControlsView() {
        if (mReplayView != null && mReplayView.getVisibility() == View.GONE) {
            mReplayView.setVisibility(View.VISIBLE);
        }
    }

    public interface QReaderListener {
        void onAdDetected(@NonNull AdDescriptor descriptor);

        void onTrashDetected(@NonNull String content);

        void onReplayClicked(String url);
    }
}
