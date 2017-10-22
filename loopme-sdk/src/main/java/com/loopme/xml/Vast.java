package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;

public class Vast {

    @Attribute
    private String version;

    @Tag
    private Ad ad;

    @Tag
    private Status status;

    public String getVersion() {
        return version;
    }

    public Ad getAd() {
        return ad;
    }

    public Status getStatus() {
        return status;
    }
}
