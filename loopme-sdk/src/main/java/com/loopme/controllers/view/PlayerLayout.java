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
import com.loopme.Constants.Layout;
import com.loopme.R;
import com.loopme.utils.Utils;
import com.loopme.utils.ViewUtils;

import java.util.Locale;

public class PlayerLayout extends FrameLayout
        implements GestureDetector.OnGestureListener, TextureView.SurfaceTextureListener {

    private static final int TIME_MARGIN = Utils.convertDpToPixel(6);
    private static final float TEXT_SIZE = 12;
    private final int SKIP_BUTTON_ID = View.generateViewId();
    private final int MUTE_BUTTON_ID = View.generateViewId();

    private int mViewPosition;
    private boolean mMuteState;
    private Surface mSurface;
    private final TextView mTextView;
    private final ProgressBar mProgressBar;
    private final ImageView mMuteButton;
    private final ImageView mSkipButton;
    private final TextureView mPlayerTextureView;
    private final View[] buttonViews;
    private final OnPlayerListener mListener;

    public PlayerLayout(@NonNull Context context, WebView webView, OnPlayerListener listener) {
        super(context);
        mListener = listener;

        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI);

        mSkipButton = new ImageView(getContext());
        mSkipButton.setId(SKIP_BUTTON_ID);
        mSkipButton.setScaleType(ImageView.ScaleType.CENTER);
        mSkipButton.setImageResource(R.drawable.l_skip);
        mSkipButton.setVisibility(View.INVISIBLE);
        mSkipButton.setLayoutParams(new FrameLayout.LayoutParams(btnSizePx, btnSizePx, Gravity.END));

        mMuteButton = new ImageView(getContext());
        mMuteButton.setId(MUTE_BUTTON_ID);
        mMuteButton.setScaleType(ImageView.ScaleType.CENTER);
        mMuteButton.setImageResource(R.drawable.l_unmute);
        mMuteButton.setVisibility(View.VISIBLE);
        mMuteButton.setLayoutParams(new FrameLayout.LayoutParams(btnSizePx, btnSizePx, Gravity.START));

        mProgressBar = new ProgressBar(getContext(), null, android.R.style.Widget_ProgressBar_Horizontal);
        mProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.l_progress_bar, null));
        int PROGRESS_HEIGHT = Utils.convertDpToPixel(3);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PROGRESS_HEIGHT, Gravity.BOTTOM));

        mPlayerTextureView = new TextureView(getContext());
        mPlayerTextureView.setId(View.generateViewId());
        mPlayerTextureView.setLayoutParams(Layout.MATCH_PARENT_CENTER);
        mPlayerTextureView.setSurfaceTextureListener(this);

        mTextView = new TextView(getContext());
        mTextView.setTextSize(TEXT_SIZE);
        mTextView.setTextColor(getContext().getResources().getColor(R.color.white_pale));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        params.setMargins(TIME_MARGIN, 0, 0, TIME_MARGIN);
        mTextView.setLayoutParams(params);
        if (webView != null) {
            webView.setId(View.generateViewId());
        }
        buttonViews = new View[]{mMuteButton, mSkipButton};
        setLayoutParams(Layout.MATCH_PARENT_CENTER);
        addView(mPlayerTextureView, generateViewPosition());
        if (webView != null) {
            addView(webView, generateViewPosition());
        }
        addView(mSkipButton, generateViewPosition());
        addView(mMuteButton, generateViewPosition());
        addView(mProgressBar, generateViewPosition());
        addView(mTextView, generateViewPosition());
    }

    private static FrameLayout.LayoutParams calculateNewLayoutParams(
        @NonNull FrameLayout.LayoutParams layoutParams,
        int videoWidth, int videoHeight,
        int resizeWidth, int resizeHeight,
        Constants.StretchOption stretchOption
    ) {
        layoutParams.gravity = Gravity.CENTER;
        if (stretchOption == Constants.StretchOption.STRETCH) {
            layoutParams.width = resizeWidth;
            layoutParams.height = resizeHeight;
            return layoutParams;
        }
        if (stretchOption == Constants.StretchOption.NONE) {
            float percent = 0;
            if (videoWidth > videoHeight) {
                layoutParams.width = resizeWidth;
                layoutParams.height = (int) ((float) videoHeight / videoWidth * resizeWidth);
                percent = layoutParams.height != 0 ?
                        (resizeHeight - layoutParams.height * 100f) / layoutParams.height : 0;
            } else {
                layoutParams.height = resizeHeight;
                layoutParams.width = (int) ((float) videoWidth / videoHeight * resizeHeight);
                percent = layoutParams.width != 0 ?
                        (resizeWidth - layoutParams.width * 100f) / layoutParams.width : 0;
            }
            int DEFAULT_THRESHOLD = 11;
            if (percent < DEFAULT_THRESHOLD) {
                layoutParams.width = resizeWidth;
                layoutParams.height = resizeHeight;
            }
            return layoutParams;
        }
        return layoutParams;
    }

    public void adjustLayoutParams(int width, int height, int containerWidth, int containerHeight) {
        FrameLayout.LayoutParams oldParams = (FrameLayout.LayoutParams) mPlayerTextureView.getLayoutParams();
        ViewGroup.LayoutParams newParams = calculateNewLayoutParams(
            oldParams, width, height, containerWidth, containerHeight, Constants.StretchOption.NONE
        );
        mPlayerTextureView.setLayoutParams(newParams);
    }

    public Surface getSurface() {
        return mSurface;
    }

    private final GestureDetector gestureDetector = new GestureDetector(getContext(), this);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        View v = ViewUtils.findVisibleView(buttonViews, e);
        if (mListener != null) {
            if (v == null) {
                mListener.onPlayerClick();
            } else if (v.getId() == MUTE_BUTTON_ID) {
                mMuteState = !mMuteState;
                mMuteButton.setImageResource(mMuteState ? R.drawable.l_mute : R.drawable.l_unmute);
                mListener.onMuteClick(mMuteState);
            } else if (v.getId() == SKIP_BUTTON_ID) {
                mListener.onSkipClick();
            }
        }
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

    public void setProgress(int progress) {
        int maxDuration = mProgressBar == null ? 0 : mProgressBar.getMax();
        if (mProgressBar != null) {
            mProgressBar.setProgress(maxDuration - progress);
        }
        if (progress < 200) {
            String TIME_FINISHED = "00:00";
            mTextView.setText(TIME_FINISHED);
            return;
        }
        int timeLeft = (progress + Constants.MILLIS_IN_SECOND) / Constants.MILLIS_IN_SECOND;
        int minutes = timeLeft / Constants.SECONDS_IN_MINUTE;
        int seconds = timeLeft % Constants.SECONDS_IN_MINUTE;
        mTextView.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    public void setMaxProgress(int duration) {
        if (mProgressBar != null) {
            mProgressBar.setMax(duration);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurface = new Surface(surfaceTexture);
        if (mListener != null) {
            mListener.onSurfaceTextureReady(mSurface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }

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
