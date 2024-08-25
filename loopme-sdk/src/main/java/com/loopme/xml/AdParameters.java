package com.loopme.xml;

import androidx.annotation.NonNull;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class AdParameters {
    @Attribute
    private String xmlEncoded;

    @Text
    private String text;

    @NonNull
    public String getText() { return text == null ? "" : text; }

    public String getXmlEncoded() { return xmlEncoded; }
}
