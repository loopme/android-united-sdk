package com.loopme;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class LoopMeGestureListener implements View.OnTouchListener {

    private final Context mContext;
    private final Listener mListener;
    private final GestureDetector mGestureDetector;
    private final float MAX_ACCEPTABLE_VERTICAL_OFFSET_FOR_HORIZONTAL_SWIPE = 50;

    public LoopMeGestureListener(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
        mGestureDetector = initGestureDetector();
    }

    private GestureDetector initGestureDetector() {
        return new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent firstTap, MotionEvent lastTap, float velocityX, float velocityY) {
                float verticalOffset = Math.abs(firstTap.getY() - lastTap.getY());
                if (verticalOffset > MAX_ACCEPTABLE_VERTICAL_OFFSET_FOR_HORIZONTAL_SWIPE) {
                    return false;
                }
                onSwipe(firstTap.getX() < lastTap.getX());
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onClick();
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onTouchEvent(event);
    }

    private void onSwipe(boolean toRight) {
        if (mListener != null) {
            mListener.onSwipe(toRight);
        }
    }

    private void onClick() {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    public interface Listener {
        void onSwipe(boolean toRight);

        void onClick();
    }
}
