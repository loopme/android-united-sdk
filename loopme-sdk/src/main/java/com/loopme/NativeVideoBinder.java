package com.loopme;

public class NativeVideoBinder {

    private int mLayoutId;
    private int mBannerViewId;

    private NativeVideoBinder(Builder builder) {
        mLayoutId = builder.mLayoutId;
        mBannerViewId = builder.mBannerViewId;
    }

    public int getLayout() {
        return mLayoutId;
    }

    public int getBannerViewId() {
        return mBannerViewId;
    }


    public static class Builder {

        private int mLayoutId;
        private int mBannerViewId;

        public Builder(int layoutId) {
            mLayoutId = layoutId;
        }

        public Builder setLoopMeBannerViewId(int id) {
            mBannerViewId = id;
            return this;
        }

        public NativeVideoBinder build() {
            return new NativeVideoBinder(this);
        }
    }
}

