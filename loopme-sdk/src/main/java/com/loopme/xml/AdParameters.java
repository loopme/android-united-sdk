package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class AdParameters {
    @Attribute
    private String xmlEncoded;

    @Text
    private String text;

    public String getText() {
        return text;
    }

    public String getXmlEncoded() {
        return xmlEncoded;
    }

}
