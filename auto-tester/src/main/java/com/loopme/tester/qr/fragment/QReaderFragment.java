package com.loopme.tester.qr.fragment;

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
import android.webkit.URLUtil;

import com.loopme.tester.R;
import com.loopme.tester.qr.QReader;

import github.nisrulz.qreader.QRDataListener;

public class QReaderFragment extends Fragment implements QRDataListener {
    private QReader mLoopMeQReader;
    private QReaderListener mListener;
    private View mProgressBar;
    private Handler HANDLER = new Handler(Looper.getMainLooper());

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
        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.fragment_qr_reader_surface_view);
        mProgressBar = view.findViewById(R.id.fragment_qr_reader_progress_bar);
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
        mLoopMeQReader.resume();
    }

    @Override
    public void onPause() {
        mLoopMeQReader.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mLoopMeQReader.destroy();
        super.onDestroyView();
    }

    @Override
    public void onDetected(final String content) {
        if (URLUtil.isNetworkUrl(content)) {
            onUrlDetected(content);
        } else {
            onNoneUrlDetected(content);
        }

    }

    public void onUrlDetected(final String url) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onUrlDetected(url);
                }
            }
        });
    }

    public void onNoneUrlDetected(final String content) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onNoneUrlDetected(content);
                }
            }
        });
    }

    public interface QReaderListener {
        void onUrlDetected(String url);

        void onNoneUrlDetected(String content);
    }
}
