package com.loopme;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.utils.Utils;

public class MinimizedMode {

    private static final String LOG_TAG = MinimizedMode.class.getSimpleName();
    private final AdSpotDimensions mMinimizedViewDims = new AdSpotDimensions(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private static final int DEFAULT_HEIGHT = 100;
    private static final int DEFAULT_WIDTH = 300;
    private int mMarginRight = 10;
    private int mMarginBottom = 10;
    private ViewGroup mRoot;
    private RecyclerView mRecyclerView;
    private int mPosition;

    public MinimizedMode(ViewGroup root, RecyclerView recyclerView) {
        if (root == null || recyclerView == null) {
            Logging.out(LOG_TAG, "Error: Root view or recyclerView should be not null. Minimized mode will not work");
            return;
        }
        mRoot = root;
        mRecyclerView = recyclerView;
        if (mMinimizedViewDims != null) {
            Utils.setDimensions(mMinimizedViewDims);
        }
    }

    public void setViewSize(int width, int height) {
        int widthInPx = Utils.convertDpToPixel(width);
        int heightInPx = Utils.convertDpToPixel(height);
        mMinimizedViewDims.setWidth(widthInPx);
        mMinimizedViewDims.setHeight(heightInPx);
    }

    public int getWidth() {
        return mMinimizedViewDims.getWidth();
    }

    public int getHeight() {
        return mMinimizedViewDims.getHeight();
    }

    public ViewGroup getRootView() {
        return mRoot;
    }

    public int getMarginRight() {
        return mMarginRight;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    public AdSpotDimensions getMinimizedViewDims() {
        return mMinimizedViewDims;
    }

    public void addView(FrameLayout view) {
        if (mRoot != null) {
            mRoot.addView(view);
        }

    }

    public void onViewClicked() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}
