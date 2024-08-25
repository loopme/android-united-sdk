package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.TrackingEvents;

import java.util.List;

public class Verification {

    @Attribute
    private String vendor;

    @Tag("JavaScriptResource")
    private List<JavaScriptResource> javaScriptResourceList;
    @Tag
    private TrackingEvents trackingEvents;
    @Tag
    private VerificationParameters verificationParameters;

    public String getVendor() { return vendor; }
    public List<JavaScriptResource> getJavaScriptResourceList() { return javaScriptResourceList; }
    public TrackingEvents getTrackingEvents() { return trackingEvents; }
    public VerificationParameters getVerificationParameters() { return verificationParameters; }
}
