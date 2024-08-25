package com.loopme.xml.vast4;

import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.parser.xml.XmlParser;
import com.loopme.xml.Vast;

public class VastInfo {
    private VastInfo() { }

    private Wrapper mWrapper;
    public Wrapper getWrapper() { return mWrapper; }
    public void setWrapper(Wrapper wrapper) { mWrapper = wrapper; }
    public boolean hasWrapper() { return mWrapper != null; }

    private LoopMeError mError;
    public void setError(LoopMeError error) { mError = error; }
    public boolean hasError() { return mError != null; }

    public static VastInfo getVastInfo(String vastString) {
        VastInfo info = new VastInfo();
        try {
            Vast vast = XmlParser.parse(vastString, Vast.class);
            info.setWrapper(vast.getAd() == null ? null : vast.getAd().getWrapper());
            return info;
        } catch (Exception e) {
            info.setError(Errors.SYNTAX_ERROR_IN_XML);
            return info;
        }
    }
}
