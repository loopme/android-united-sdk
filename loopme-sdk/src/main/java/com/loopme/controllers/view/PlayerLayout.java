package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopme.Constants;
import com.loopme.R;
import com.loopme.utils.Utils;

public class PlayerLayout extends FrameLayout implements View.OnTouchListener, View.OnClickListener, TextureView.SurfaceTextureListener {
    private static final int SKIP_BUTTON_ID = View.generateViewId();
    private static final int PLAYER_VIEW_ID = View.generateViewId();
    private static final int WEBVIEW_ID = View.generateViewId();
    private static final int MUTE_BUTTON_ID = View.generateViewId();
    private static final int PROGRESS_HEIGHT = Utils.convertDpToPixel(3);
    private static final int TIME_MARGIN = Utils.convertDpToPixel(6);
    private static final float TEXT_SIZE = 12;
    private int mViewPosition;
    private boolean mMuteState;
    private Surface mSurface;
    private WebView mWebView;
    private TextureView mPlayerTextureView;
    private TextView mTextView;
    private ImageView mMuteButton;
    private ProgressBar mProgressBar;
    private ImageView mSkipButton;
    private OnPlayerListener mListener;

    public PlayerLayout(@NonNull Context context, WebView webView, OnPlayerListener listener) {
        super(context);
        mWebView = webView;
        mListener = listener;
        configureViews();
        buildLayout();
    }

    private void configureViews() {
        configureSkipButton();
        configureMuteButton();
        configureProgressBar();
        configureTextureView();
        configureTimeTextView();
        configureWebView();
        setListeners();
    }

    private void buildLayout() {
        setLayoutParams(Utils.createMatchParentLayoutParams());
        addView(mPlayerTextureView, generateViewPosition());
        if (mWebView != null) {
            addView(mWebView, generateViewPosition());
        }
        addView(mSkipButton, generateViewPosition());
        addView(mMuteButton, generateViewPosition());
        addView(mProgressBar, generateViewPosition());
        addView(mTextView, generateViewPosition());
    }

    private void setListeners() {
        mMuteButton.setOnClickListener(this);
        mSkipButton.setOnClickListener(this);
        mPlayerTextureView.setSurfaceTextureListener(this);
        if (mWebView == null) {
            mPlayerTextureView.setOnClickListener(this);
        }
    }

    public void adjustLayoutParams(int width, int height, int containerWidth, int containerHeight) {
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mPlayerTextureView.getLayoutParams();
        ViewGroup.LayoutParams newParams = Utils.calculateNewLayoutParams(oldParams, width, height,
                containerWidth, containerHeight, Constants.StretchOption.NONE);
        mPlayerTextureView.setLayoutParams(newParams);
    }

    public Surface getSurface() {
        return mSurface;
    }

    private void configureWebView() {
        if (mWebView != null) {
            mWebView.setId(WEBVIEW_ID);
            mWebView.setOnTouchListener(this);
        }
    }

    private void configureTimeTextView() {
        mTextView = new TextView(getContext());
        mTextView.setTextSize(TEXT_SIZE);
        mTextView.setTextColor(getContext().getResources().getColor(R.color.white_pale));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        params.gravity = Gravity.BOTTOM;
        params.setMargins(TIME_MARGIN, 0, 0, TIME_MARGIN);
        mTextView.setLayoutParams(params);
    }

    private void configureProgressBar() {
        mProgressBar = new ProgressBar(getContext(), null, android.R.style.Widget_ProgressBar_Horizontal);
        mProgressBar.setProgressDrawable(getContext().getResources().getDrawable(R.drawable.l_progress_bar));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PROGRESS_HEIGHT, Gravity.BOTTOM);
        mProgressBar.setLayoutParams(params);
    }

    private void configureSkipButton() {
        mSkipButton = new ImageView(getContext());
        mSkipButton.setId(SKIP_BUTTON_ID);
        mSkipButton.setScaleType(ImageView.ScaleType.CENTER);
        mSkipButton.setImageResource(R.drawable.l_skip);
        mSkipButton.setVisibility(View.INVISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Constants.BUTTON_SIZE, Constants.BUTTON_SIZE, Gravity.END);
        mSkipButton.setLayoutParams(params);
    }

    private void configureMuteButton() {
        mMuteButton = new ImageView(getContext());
        mMuteButton.setId(MUTE_BUTTON_ID);
        mMuteButton.setScaleType(ImageView.ScaleType.CENTER);
        mMuteButton.setImageResource(R.drawable.l_unmute);
        mMuteButton.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Constants.BUTTON_SIZE, Constants.BUTTON_SIZE, Gravity.START);
        mMuteButton.setLayoutParams(params);
    }

    private void configureTextureView() {
        mPlayerTextureView = new TextureView(getContext());
        mPlayerTextureView.setId(PLAYER_VIEW_ID);
        mPlayerTextureView.setLayoutParams(Utils.createMatchParentLayoutParams());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == SKIP_BUTTON_ID) {
            onSkipClick();
        } else if (id == WEBVIEW_ID || id == PLAYER_VIEW_ID) {
            onPlayerClick();
        } else {
            if (id == MUTE_BUTTON_ID) {
                muteVideo();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && v.getId() == WEBVIEW_ID) {
            onPlayerClick();
            return false;
        }
        return true;
    }

    public void showSkipButton() {
        if (mSkipButton != null) {
            mSkipButton.setVisibility(View.VISIBLE);
            mSkipButton.setClickable(true);
        }
    }


    public boolean isMute() {
        return mMuteState;
    }

    private void muteVideo() {
        mMuteState = !mMuteState;
        switchButton();
        onMuteClick(mMuteState);
    }


    private void switchButton() {
        if (mMuteState) {
            mMuteButton.setImageResource(R.drawable.l_mute);
        } else {
            mMuteButton.setImageResource(R.drawable.l_unmute);
        }
    }

    public void setProgress(int progress) {
        setProgressInProgressBar(getMaxDuration() - progress);
        String timeStamp = Utils.createTimeStamp(progress);
        mTextView.setText(timeStamp);
    }

    private int getMaxDuration() {
        if (mProgressBar != null) {
            return mProgressBar.getMax();
        }
        return 0;
    }

    private void setProgressInProgressBar(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }


    public void setMaxProgress(int duration) {
        if (mProgressBar != null) {
            mProgressBar.setMax(duration);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurface = new Surface(surfaceTexture);
        onSurfaceTextureReady(mSurface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void onSurfaceTextureReady(Surface surface) {
        if (mListener != null) {
            mListener.onSurfaceTextureReady(surface);
        }
    }

    private void onPlayerClick() {
        if (mListener != null) {
            mListener.onPlayerClick();
        }
    }

    private void onMuteClick(boolean mute) {
        if (mListener != null) {
            mListener.onMuteClick(mute);
        }
    }

    private void onSkipClick() {
        if (mListener != null) {
            mListener.onSkipClick();
        }
    }

    public TextureView getPlayerView() {
        return mPlayerTextureView;
    }

    public interface OnPlayerListener {
        void onSurfaceTextureReady(Surface surface);

        void onPlayerClick();

        void onMuteClick(boolean mute);

        void onSkipClick();
    }

    private int generateViewPosition() {
        return mViewPosition++;
    }
}
