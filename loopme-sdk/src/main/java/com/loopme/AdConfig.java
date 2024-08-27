package com.loopme;

import com.loopme.ad.LoopMeAd;

public abstract class AdConfig implements AdTargeting {
    protected LoopMeAd mFirstLoopMeAd;

    public boolean isAutoLoadingEnabled() {
        return mFirstLoopMeAd != null && mFirstLoopMeAd.isAutoLoadingEnabled();
    }

    public void setAutoLoading(boolean autoLoadingEnabled) {
        if (mFirstLoopMeAd != null) mFirstLoopMeAd.setAutoLoading(autoLoadingEnabled);
    }

    @Override
    public void setKeywords(String keywords) {
        if (mFirstLoopMeAd != null) mFirstLoopMeAd.setKeywords(keywords);
    }

    @Override
    public void setGender(String gender) {
        if (mFirstLoopMeAd != null) mFirstLoopMeAd.setGender(gender);
    }

    @Override
    public void setYearOfBirth(int year) {
        if (mFirstLoopMeAd != null) mFirstLoopMeAd.setYearOfBirth(year);
    }
}