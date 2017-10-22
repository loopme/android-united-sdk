package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;

public class Verification {

    @Attribute
    private String vendor;

    @Tag
    private JavaScriptResource javaScriptResource;

    @Tag
    private FlashResource flashResource;

    @Tag
    private ViewableImpression viewableImpression;

    public String getVendor() {
        return vendor;
    }

    public JavaScriptResource getJavaScriptResource() {
        return javaScriptResource;
    }

    public FlashResource getFlashResource() {
        return flashResource;
    }

    public ViewableImpression getViewableImpression() {
        return viewableImpression;
    }
}
