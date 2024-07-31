package com.loopme;

import static com.loopme.debugging.Params.CID;
import static com.loopme.debugging.Params.CRID;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.REQUEST_ID;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

/**
 * Deprecated since sdk version 6.0. Use {@link FrameLayout} instead.
 */

// TODO: Remove deprecated class
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
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Hardware acceleration is off");
            errorInfo.put(REQUEST_ID, BidManager.getInstance().getRequestId());
            errorInfo.put(CID, BidManager.getInstance().getCurrentCid());
            errorInfo.put(CRID, BidManager.getInstance().getCurrentCrid());
            LoopMeTracker.post(errorInfo);
        }
    }
}

