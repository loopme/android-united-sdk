package com.loopme.xml.vast4;

import com.loopme.common.LoopMeError;

public class VastInfo {
    private boolean mHasWrapper;
    private Wrapper mWrapper;
    private String mVastTagUrl;
    private LoopMeError mError;

    public VastInfo() {
    }

    public String getVastTagUrl() {
        return mVastTagUrl;
    }

    public void setVastTagUrl(String mVastTagUrl) {
        this.mVastTagUrl = mVastTagUrl;
    }

    public boolean hasWrapper() {
        return mHasWrapper;
    }

    public void setHasWrapper(boolean mHasWrapper) {
        this.mHasWrapper = mHasWrapper;
    }

    public Wrapper getWrapper() {
        return mWrapper;
    }

    public void setWrapper(Wrapper mWrapper) {
        this.mWrapper = mWrapper;
    }

    public void setError(LoopMeError mError) {
        this.mError = mError;
    }

    public boolean hasError() {
        return mError != null;
    }
}
