package com.loopme;

import com.loopme.ad.LoopMeAd;
import com.loopme.utils.Utils;

public abstract class AdConfig implements AdTargeting {
    protected LoopMeAd mFirstLoopMeAd;
    protected LoopMeAd mSecondLoopMeAd;


    /**
     * Changes default value of time interval during which video file will be cached.
     * Default time interval is 32 hours.
     */
    public void setVideoCacheTimeInterval(long milliseconds) {
        if (milliseconds > 0) {
            Constants.CACHED_VIDEO_LIFE_TIME = milliseconds;
        }
    }

    /**
     * Defines, should use mobile network for caching video or not.
     * By default, video will be cached on mobile network
     *
     * @param b - true if need to cache video on mobile network,
     *          false if need to cache video only on wi-fi network.
     */
    public void useMobileNetworkForCaching(boolean b) {
        Constants.USE_MOBILE_NETWORK_FOR_CACHING = b;
    }

    /**
     * Use it for figure out any problems during integration process.
     * We recommend to set it "false" after full integration and testing.
     * <p>
     * If true - all debug logs will be in Logcat.
     * If false - only main info logs will be in Logcat.
     */
    public void setDebugMode(boolean mode) {
        Constants.sDebugMode = mode;
    }

    public boolean isAutoLoadingEnabled() {
        return mFirstLoopMeAd != null && mFirstLoopMeAd.isAutoLoadingEnabled();
    }

    public void setAutoLoading(boolean autoLoadingEnabled) {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.setAutoLoading(autoLoadingEnabled);
        }
    }

    @Override
    public void setKeywords(String keywords) {
        setKeywords(keywords, mFirstLoopMeAd);
        setKeywords(keywords, mSecondLoopMeAd);
    }

    @Override
    public void setGender(String gender) {
        setGender(gender, mFirstLoopMeAd);
        setGender(gender, mSecondLoopMeAd);
    }

    @Override
    public void setYearOfBirth(int year) {
        setYearOfBirth(year, mFirstLoopMeAd);
        setYearOfBirth(year, mSecondLoopMeAd);
    }

    @Override
    public void addCustomParameter(String param, String paramValue) {
        addCustomParameter(param, paramValue, mFirstLoopMeAd);
        addCustomParameter(param, paramValue, mSecondLoopMeAd);
    }

    private void addCustomParameter(String param, String paramValue, LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            loopMeAd.addCustomParameter(param, paramValue);
        }
    }

    private void setYearOfBirth(int year, LoopMeAd baseAd) {
        if (baseAd != null) {
            baseAd.setYearOfBirth(year);
        }
    }

    private void setKeywords(String keywords, LoopMeAd baseAd) {
        if (baseAd != null) {
            baseAd.setKeywords(keywords);
        }
    }

    private void setGender(String gender, LoopMeAd baseAd) {
        if (baseAd != null) {
            baseAd.setGender(gender);
        }
    }

    /**
     * Removes all video files from cache.
     */
    public void clearCache() {
        if (mFirstLoopMeAd != null) {
            Utils.clearCache(mFirstLoopMeAd.getContext());
        }
    }

    public void setPreferredAd(LoopMeAd.Type type) {
        if (mFirstLoopMeAd != null) {
            mFirstLoopMeAd.setPreferredAdType(type);
        }
        if (mSecondLoopMeAd != null) {
            mSecondLoopMeAd.setPreferredAdType(type);
        }
    }
}