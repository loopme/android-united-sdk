package com.loopme.xml.vast4;

import com.loopme.common.LoopMeError;

public class VastInfo {
    public VastInfo() { }

    private Wrapper mWrapper;
    public Wrapper getWrapper() { return mWrapper; }
    public void setWrapper(Wrapper wrapper) { mWrapper = wrapper; }
    public boolean hasWrapper() { return mWrapper != null; }

    private LoopMeError mError;
    public void setError(LoopMeError error) { mError = error; }
    public boolean hasError() { return mError != null; }
}
