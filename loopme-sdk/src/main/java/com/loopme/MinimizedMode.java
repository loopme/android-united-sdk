package com.loopme;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.utils.Utils;

public class MinimizedMode {

    private static final String LOG_TAG = MinimizedMode.class.getSimpleName();
    private static final int DEFAULT_HEIGHT = 100;
    private static final int DEFAULT_WIDTH = 300;
    private final AdSpotDimensions mMinimizedViewDims = new AdSpotDimensions(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    private final ViewGroup mRoot;
    private final RecyclerView mRecyclerView;
    private int mPosition;

    public int getWidth() { return mMinimizedViewDims.getWidth(); }
    public int getHeight() { return mMinimizedViewDims.getHeight(); }

    public int getMarginRight() { return 10; }
    public int getMarginBottom() { return 10; }

    public ViewGroup getRootView() { return mRoot; }
    public AdSpotDimensions getDimensions() { return mMinimizedViewDims; }
    public void setPosition(int position) { mPosition = position; }

    public MinimizedMode(@NonNull ViewGroup root, @NonNull RecyclerView recyclerView) {
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

}
