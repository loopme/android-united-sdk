package com.loopme.views;

import static com.loopme.debugging.Params.ERROR_MSG;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

public class LoopMeContainerView extends FrameLayout {

    private static final String LOG_TAG = LoopMeContainerView.class.getSimpleName();

    public LoopMeContainerView(Context context) {
        super(context);
    }

    public LoopMeContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopMeContainerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LoopMeContainerView(Context context, int width, int height) {
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
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Hardware acceleration is off");
            Logging.out(LOG_TAG, "Warning: hardware acceleration is off");
            LoopMeTracker.post(errorInfo);
        }
    }

}
