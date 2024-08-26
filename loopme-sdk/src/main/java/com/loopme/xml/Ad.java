package com.loopme.xml;

import com.loopme.xml.vast4.Wrapper;
import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;

public class Ad {

    @Attribute
    private String id;
    @Attribute
    private int sequence;
    @Attribute
    private boolean conditionalAd;

    @Tag
    private InLine inLine;
    @Tag
    private Wrapper wrapper;

    public Wrapper getWrapper() { return wrapper; }
    public String getId() { return id; }
    public InLine getInLine() { return inLine; }
    public boolean isConditionalAd() { return conditionalAd; }
}
