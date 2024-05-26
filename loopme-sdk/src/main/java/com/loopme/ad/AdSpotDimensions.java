package com.loopme.ad;

import com.loopme.Constants;

public class AdSpotDimensions {

    public AdSpotDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private int width;

    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdSpotDimensions that = (AdSpotDimensions) o;

        if (width != that.width) return false;
        return height == that.height;
    }

    @Override
    public int hashCode() {
        return 31 * width + height;
    }

    public static AdSpotDimensions getMpu() {
        return new AdSpotDimensions(
            Constants.Banner.MPU_BANNER_WIDTH,
            Constants.Banner.MPU_BANNER_HEIGHT
        );
    }
    public static AdSpotDimensions getExpandBanner() {
        return new AdSpotDimensions(
            Constants.Banner.EXPAND_BANNER_WIDTH,
            Constants.Banner.EXPAND_BANNER_HEIGHT
        );

    }
    public static AdSpotDimensions getDefaultBanner() {
        return new AdSpotDimensions(
            Constants.DEFAULT_BANNER_WIDTH,
            Constants.DEFAULT_BANNER_HEIGHT
        );
    }

}
