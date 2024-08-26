package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class AdSystem {

    @Attribute
    private String version;

    @Text
    private String text;

    public String getVersion() { return version; }
    public String getText() { return text; }
}
