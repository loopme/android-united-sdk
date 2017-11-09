package com.loopme.views.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.loopme.views.CloseButton;

public class MraidVideoActivity extends Activity {

    private static final String LOG_TAG = MraidVideoActivity.class.getSimpleName();
    private static final String EXTRAS_VIDEO_URL = "videoUrl";
    private RelativeLayout mRelativeLayout;
    private View mAdView;
    private CloseButton mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRelativeLayout = new RelativeLayout(this);
        String url = getIntent().getStringExtra(EXTRAS_VIDEO_URL);
        if (TextUtils.isEmpty(url)) {
            mAdView = new ImageView(this);
        } else {
            mAdView = new VideoView(this);

            ((VideoView) mAdView).setVideoPath(getIntent().getStringExtra(EXTRAS_VIDEO_URL));
            ((VideoView) mAdView).setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            ((VideoView) mAdView).setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mCloseButton.setVisibility(View.VISIBLE);
                }
            });
        }
        setLayoutParams();
        setContentView(mRelativeLayout);
        initCloseButton();
    }

    private void setLayoutParams() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        mRelativeLayout.addView(mAdView, lp);
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
        if (mAdView != null && mAdView instanceof VideoView && !((VideoView) mAdView).isPlaying()) {
            ((VideoView) mAdView).resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdView != null && mAdView instanceof VideoView && ((VideoView) mAdView).isPlaying()) {
            ((VideoView) mAdView).pause();
        }
    }
}
