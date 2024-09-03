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
import com.loopme.Constants.Layout;
import com.loopme.R;
import com.loopme.utils.ImageUtils;
import com.loopme.utils.Utils;
import com.loopme.utils.ViewUtils;

public class EndCardLayout extends FrameLayout implements GestureDetector.OnGestureListener {
    private final int CLOSE_BUTTON_ID = View.generateViewId();
    private final int REPLAY_BUTTON_ID = View.generateViewId();
    private ImageView mEndCardImageView;
    private View[] buttonViews;

    private final OnEndCardListener mListener;

    public EndCardLayout(@NonNull Context context, OnEndCardListener listener) {
        super(context);
        mListener = listener;

        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI);
        mEndCardImageView = new ImageView(getContext());
        mEndCardImageView.setId(View.generateViewId());
        mEndCardImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mEndCardImageView.setLayoutParams(Layout.MATCH_PARENT_CENTER);

        ImageView mCloseImageView = new ImageView(getContext());
        mCloseImageView.setId(CLOSE_BUTTON_ID);
        mCloseImageView.setScaleType(ImageView.ScaleType.CENTER);
        mCloseImageView.setImageResource(R.drawable.l_close);
        mCloseImageView.setLayoutParams(new FrameLayout.LayoutParams(btnSizePx, btnSizePx, Gravity.END));

        ImageView mReplayImageView = new ImageView(getContext());
        mReplayImageView.setId(REPLAY_BUTTON_ID);
        mReplayImageView.setScaleType(ImageView.ScaleType.CENTER);
        mReplayImageView.setImageResource(R.drawable.l_replay);
        mReplayImageView.setLayoutParams(new FrameLayout.LayoutParams(btnSizePx, btnSizePx, Gravity.START));

        buttonViews = new View[]{mReplayImageView, mCloseImageView};

        setLayoutParams(Layout.MATCH_PARENT_CENTER);
        addView(mEndCardImageView, 0);
        addView(mCloseImageView, 1);
        addView(mReplayImageView, 2);
        setVisibility(View.GONE);
    }

    private final GestureDetector gestureDetector = new GestureDetector(getContext(), this);

    @Override
    public boolean onTouchEvent(MotionEvent event) { return gestureDetector.onTouchEvent(event); }

    @Override
    public boolean onDown(MotionEvent e) { return true; }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        View v = ViewUtils.findVisibleView(buttonViews, e);
        if (v == null) {
            if (mListener != null) {
                mListener.onEndCardClick();
            }
        } else if (v.getId() == REPLAY_BUTTON_ID) {
            if (mListener != null) {
                mListener.onReplayClick();
            }
        } else if (v.getId() == CLOSE_BUTTON_ID) {
            if (mListener != null) {
                mListener.onCloseClick();
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

    public void destroy() {
        if (mEndCardImageView != null) {
            mEndCardImageView.setImageDrawable(null);
        }
    }

    public void setEndCard(String imageUri) {
        ImageUtils.setScaledImage(mEndCardImageView, imageUri);
    }

    public interface OnEndCardListener {
        void onEndCardClick();
        void onCloseClick();
        void onReplayClick();
    }
}
