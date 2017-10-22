package com.loopme.views.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.loopme.views.CloseButton;

public class MraidVideoActivity extends Activity {

    private static final String LOG_TAG = MraidVideoActivity.class.getSimpleName();
    private static final String EXTRAS_VIDEO_URL = "videoUrl";
    private RelativeLayout mRelativeLayout;
    private VideoView mVideoView;
    private CloseButton mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRelativeLayout = new RelativeLayout(this);
        mVideoView = new VideoView(this);
        mVideoView.setVideoPath(getIntent().getStringExtra(EXTRAS_VIDEO_URL));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCloseButton.setVisibility(View.VISIBLE);
            }
        });
        setLayoutParams();
        setContentView(mRelativeLayout);
        initCloseButton();
    }

    private void setLayoutParams() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mRelativeLayout.addView(mVideoView, lp);
    }

    private void initCloseButton() {
        mCloseButton = new CloseButton(this);
//        mCloseButton.addInLayout(mRelativeLayout);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MraidVideoActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null && !mVideoView.isPlaying()) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }
}
