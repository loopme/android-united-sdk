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
    private View mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CloseButton mCloseButton = new CloseButton(this);
        mCloseButton.setOnClickListener(v -> finish());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        );
        RelativeLayout mRelativeLayout = new RelativeLayout(this);
        String url = getIntent().getStringExtra(EXTRAS_VIDEO_URL);
        mAdView = TextUtils.isEmpty(url) ? new ImageView(this) : new VideoView(this);
        if (mAdView instanceof VideoView) {
            ((VideoView) mAdView).setVideoPath(url);
            ((VideoView) mAdView).setOnPreparedListener(MediaPlayer::start);
            ((VideoView) mAdView).setOnCompletionListener(mp -> mCloseButton.setVisibility(View.VISIBLE));
        }
        mRelativeLayout.addView(mAdView, lp);
        setContentView(mRelativeLayout);
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
