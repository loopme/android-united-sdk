package com.loopme;

/**
 * Created by vynnykiakiv on 4/23/17.
 */

public class ShiftedValues {

    private final int mLeft;
    private final int mRight;
    private final int mTop;
    private final int mBottom;
    private boolean mLargeAd;
    private boolean mSmallAd;

    public boolean isLargeAd() {
        return mLargeAd;
    }

    public boolean isSmallAd() {
        return mSmallAd;
    }

    public int getLeft() {
        return mLeft;
    }

    public int getRight() {
        return mRight;
    }

    public int getTop() {
        return mTop;
    }

    public int getBottom() {
        return mBottom;
    }

    public ShiftedValues(int left, int right, int top, int bottom, boolean largeAd, boolean smallAd) {

        this.mLeft = left;
        this.mRight = right;
        this.mTop = top;
        this.mBottom = bottom;
        this.mLargeAd = largeAd;
        this.mSmallAd = smallAd;
    }
}
