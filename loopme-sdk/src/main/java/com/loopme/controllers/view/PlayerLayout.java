package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.GestureDetector;
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

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.loopme.Constants;
import com.loopme.R;
import com.loopme.utils.Utils;
import com.loopme.utils.ViewUtils;

import java.util.Locale;

public class PlayerLayout extends FrameLayout
        implements GestureDetector.OnGestureListener, TextureView.SurfaceTextureListener {

    private static final int PROGRESS_HEIGHT = Utils.convertDpToPixel(3);
    private static final int TIME_MARGIN = Utils.convertDpToPixel(6);
    private static final float TEXT_SIZE = 12;

    private final int SKIP_BUTTON_ID = View.generateViewId();
    private final int PLAYER_VIEW_ID = View.generateViewId();
    private final int WEBVIEW_ID = View.generateViewId();
    private final int MUTE_BUTTON_ID = View.generateViewId();

    private int mViewPosition;
    private boolean mMuteState;
    private Surface mSurface;
    private final WebView mWebView;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    private ImageView mMuteButton;
    private ImageView mSkipButton;
    private TextureView mPlayerTextureView;

    private View[] buttonViews;

    private final OnPlayerListener mListener;
    private static final String TIME_FINISHED = "00:00";
    private static final int MINIMAL_TIME_STEP = 200;

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
        buttonViews = new View[]{mMuteButton, mSkipButton};
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

    public void adjustLayoutParams(int width, int height, int containerWidth, int containerHeight) {
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mPlayerTextureView.getLayoutParams();
        ViewGroup.LayoutParams newParams = Utils.calculateNewLayoutParams(
            oldParams, width, height, containerWidth, containerHeight, Constants.StretchOption.NONE
        );
        mPlayerTextureView.setLayoutParams(newParams);
    }

    public Surface getSurface() {
        return mSurface;
    }

    private void configureWebView() {
        if (mWebView != null)
            mWebView.setId(WEBVIEW_ID);
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
        mProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.l_progress_bar, null));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PROGRESS_HEIGHT, Gravity.BOTTOM);
        mProgressBar.setLayoutParams(params);
    }

    private void configureSkipButton() {
        mSkipButton = new ImageView(getContext());
        mSkipButton.setId(SKIP_BUTTON_ID);
        mSkipButton.setScaleType(ImageView.ScaleType.CENTER);
        mSkipButton.setImageResource(R.drawable.l_skip);
        mSkipButton.setVisibility(View.INVISIBLE);
        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI, getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            btnSizePx, btnSizePx, Gravity.END
        );
        mSkipButton.setLayoutParams(params);
    }

    private void configureMuteButton() {
        mMuteButton = new ImageView(getContext());
        mMuteButton.setId(MUTE_BUTTON_ID);
        mMuteButton.setScaleType(ImageView.ScaleType.CENTER);
        mMuteButton.setImageResource(R.drawable.l_unmute);
        mMuteButton.setVisibility(View.VISIBLE);
        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI, getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            btnSizePx, btnSizePx, Gravity.START
        );
        mMuteButton.setLayoutParams(params);
    }

    private void configureTextureView() {
        mPlayerTextureView = new TextureView(getContext());
        mPlayerTextureView.setId(PLAYER_VIEW_ID);
        mPlayerTextureView.setLayoutParams(Utils.createMatchParentLayoutParams());
        mPlayerTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final GestureDetector gestureDetector = new GestureDetector(getContext(), this);

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        View v = ViewUtils.findVisibleView(buttonViews, e);
        if (v == null)
            onPlayerClick();
        else if (v.getId() == MUTE_BUTTON_ID)
            muteVideo();
        else if (v.getId() == SKIP_BUTTON_ID)
            onSkipClick();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void showSkipButton() {
        if (mSkipButton != null)
            mSkipButton.setVisibility(View.VISIBLE);
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
        mMuteButton.setImageResource(mMuteState ? R.drawable.l_mute : R.drawable.l_unmute);
    }

    private String createTimeStamp(int progress) {
        int timeLeft = progress / Constants.MILLIS_IN_SECOND;
        int minutes = timeLeft / Constants.SECONDS_IN_MINUTE;
        int seconds = timeLeft % Constants.SECONDS_IN_MINUTE;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    public void setProgress(int progress) {
        setProgressInProgressBar(getMaxDuration() - progress);
        String timeStamp = progress < MINIMAL_TIME_STEP ?
            TIME_FINISHED : createTimeStamp(progress + Constants.MILLIS_IN_SECOND);
        mTextView.setText(timeStamp);
    }

    private int getMaxDuration() {
        return mProgressBar == null ? 0 : mProgressBar.getMax();
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
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }

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
