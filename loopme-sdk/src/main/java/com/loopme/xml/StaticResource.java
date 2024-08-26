package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class StaticResource {

    @Attribute
    private String creativeType;

    @Text
    private String text;

    public String getText() { return text; }

    public String getCreativeType() { return creativeType; }
}
