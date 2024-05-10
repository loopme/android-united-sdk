package com.loopme;

import com.loopme.ad.LoopMeAd;

public abstract class AdConfig implements AdTargeting {
    protected LoopMeAd mFirstLoopMeAd;
    protected LoopMeAd mSecondLoopMeAd;


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
}