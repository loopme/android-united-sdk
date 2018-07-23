package com.loopme.tester.qr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopme.tester.R;

public class QRControlsFragment extends Fragment implements View.OnClickListener {
    private Listener mListener;
    private String mReplayUrl;
    private View mReplayView;

    public static QRControlsFragment newInstance() {
        return new QRControlsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_controls, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mReplayView = view.findViewById(R.id.fragment_qr_controls_replay_layout);
        view.findViewById(R.id.fragment_qr_controls_replay_image_view).setOnClickListener(this);
        view.findViewById(R.id.fragment_qr_controls_close_button).setOnClickListener(this);
        TextView replayUrlTextView = (TextView) view.findViewById(R.id.fragment_qr_controls_replay_url_text_view);
        replayUrlTextView.setText(R.string.qr_watch_again_text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_qr_controls_replay_image_view) {
            onReplay(mReplayUrl);
        } else if (v.getId() == R.id.fragment_qr_controls_close_button) {
            onClose();
        }
    }

    private void onReplay(String url) {
        if (mListener != null) {
            mListener.onReplayClicked(url);
        }
    }

    private void onClose() {
        if (mListener != null) {
            mListener.onCloseClicked();
        }
    }

    public void setReplayUrl(String url) {
        mReplayUrl = url;
    }

    public void enableControlsView() {
        if (mReplayView != null && mReplayView.getVisibility() == View.GONE) {
            mReplayView.setVisibility(View.VISIBLE);
        }
    }

    public interface Listener {
        void onCloseClicked();

        void onReplayClicked(String url);
    }
}
