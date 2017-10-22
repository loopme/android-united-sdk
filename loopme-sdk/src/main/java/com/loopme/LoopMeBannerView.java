package com.loopme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.loopme.tracker.partners.LoopMeTracker;

/**
 * Deprecated since sdk version 6.0. Use {@link FrameLayout} instead.
 */

@Deprecated
public class LoopMeBannerView extends FrameLayout {

    private static final String LOG_TAG = LoopMeBannerView.class.getSimpleName();

    public LoopMeBannerView(Context context) {
        super(context);
    }

    public LoopMeBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopMeBannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LoopMeBannerView(Context context, int width, int height) {
        super(context);
        LayoutParams params = new LayoutParams(width, height);
        setLayoutParams(params);
    }

    /**
     * Sets banner size
     *
     * @param width  - width
     * @param height - height
     */
    public void setViewSize(int width, int height) {
        android.view.ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (RuntimeException e) {
            Logging.out(LOG_TAG, "Error during onDetachedFromWindow: " + e.getMessage());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isHardwareAccelerated()) {
            Logging.out(LOG_TAG, "Warning: hardware acceleration is off");
            LoopMeTracker.post("Hardware acceleration is off");
        }
    }
}

