package com.loopme.controllers.view;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.R;
import com.loopme.utils.ImageUtils;
import com.loopme.utils.Utils;
import com.loopme.utils.ViewUtils;

public class EndCardLayout extends FrameLayout implements GestureDetector.OnGestureListener {
    private final int END_CARD_VIEW_ID = View.generateViewId();
    private final int CLOSE_BUTTON_ID = View.generateViewId();
    private final int REPLAY_BUTTON_ID = View.generateViewId();
    private ImageView mEndCardImageView;
    private ImageView mCloseImageView;
    private ImageView mReplayImageView;
    private View[] buttonViews;

    private final OnEndCardListener mListener;

    public EndCardLayout(@NonNull Context context, OnEndCardListener listener) {
        super(context);
        mListener = listener;
        configureViews();
        buildLayout();
    }

    private void buildLayout() {
        setLayoutParams(Utils.createMatchParentLayoutParams());
        addView(mEndCardImageView, 0);
        addView(mCloseImageView, 1);
        addView(mReplayImageView, 2);
        setVisibility(View.GONE);
    }

    private void configureViews() {
        configureEndCardImageView();
        configureCloseView();
        configureReplayView();
        buttonViews = new View[]{mReplayImageView, mCloseImageView};
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
        if (v == null)
            onEndCardClick();
        else if (v.getId() == REPLAY_BUTTON_ID)
            onReplayClick();
        else if (v.getId() == CLOSE_BUTTON_ID)
            onCloseClick();
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

    public void destroy() {
        if (mEndCardImageView != null) {
            mEndCardImageView.setImageDrawable(null);
        }
    }


    private void configureReplayView() {
        mReplayImageView = new ImageView(getContext());
        mReplayImageView.setId(REPLAY_BUTTON_ID);
        mReplayImageView.setScaleType(ImageView.ScaleType.CENTER);
        mReplayImageView.setImageResource(R.drawable.l_replay);
        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI, getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            btnSizePx, btnSizePx, Gravity.START
        );
        mReplayImageView.setLayoutParams(params);
    }

    private void configureCloseView() {
        mCloseImageView = new ImageView(getContext());
        mCloseImageView.setId(CLOSE_BUTTON_ID);
        mCloseImageView.setScaleType(ImageView.ScaleType.CENTER);
        mCloseImageView.setImageResource(R.drawable.l_close);
        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI, getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            btnSizePx, btnSizePx, Gravity.END
        );
        mCloseImageView.setLayoutParams(params);
    }

    private void configureEndCardImageView() {
        mEndCardImageView = new ImageView(getContext());
        mEndCardImageView.setId(END_CARD_VIEW_ID);
        mEndCardImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mEndCardImageView.setLayoutParams(Utils.createMatchParentLayoutParams());
    }

    public void setEndCard(String imageUri) {
        ImageUtils.setScaledImage(mEndCardImageView, imageUri);
    }

    private void onEndCardClick() {
        if (mListener != null) {
            mListener.onEndCardClick();
        }
    }

    private void onCloseClick() {
        if (mListener != null) {
            mListener.onCloseClick();
        }
    }

    private void onReplayClick() {
        if (mListener != null) {
            mListener.onReplayClick();
        }
    }

    public interface OnEndCardListener {
        void onEndCardClick();
        void onCloseClick();
        void onReplayClick();
    }
}
