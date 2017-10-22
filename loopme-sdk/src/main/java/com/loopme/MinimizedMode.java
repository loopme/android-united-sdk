package com.loopme;

import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.loopme.utils.Utils;

public class MinimizedMode {

    private static final String LOG_TAG = MinimizedMode.class.getSimpleName();

    private int mWidth = 100;
    private int mHeight = 100;
    private int mMarginRight = 10;
    private int mMarginBottom = 10;
    private ViewGroup mRoot;

    public MinimizedMode(ViewGroup root) {
        if (root == null) {
            Logging.out(LOG_TAG, "Error: Root view should be not null. Minimized mode will not work");
            return;
        }
        mRoot = root;

        DisplayMetrics dm = Utils.getDisplayMetrics();
        // portrait mode
        if (dm.heightPixels > dm.widthPixels) {
            mWidth = dm.widthPixels / 2;
        } else { //landscape mode
            mWidth = dm.widthPixels / 3;
        }
        mHeight = mWidth * 2 / 3;
        mWidth = mWidth - 6;
    }

    public void setViewSize(int width, int height) {
        mWidth = Utils.convertDpToPixel(width);
        mHeight = Utils.convertDpToPixel(height);
    }

    public void setMarginRight(int margin) {
        mMarginRight = Utils.convertDpToPixel(margin);
    }

    public void setMarginBottom(int margin) {
        mMarginBottom = Utils.convertDpToPixel(margin);
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
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
}
