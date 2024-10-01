package com.loopme.loaders;

public class AdRequestType {

    private final boolean isBanner;
    private final boolean isVideo;
    private final boolean isRewarded;

    public AdRequestType(boolean isBanner, boolean isVideo, boolean isRewarded) {
        this.isBanner = isBanner;
        this.isVideo = isVideo;
        this.isRewarded = isRewarded;
    }

    public boolean isBanner() {
        return isBanner;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public boolean isRewarded() {
        return isRewarded;
    }
}
