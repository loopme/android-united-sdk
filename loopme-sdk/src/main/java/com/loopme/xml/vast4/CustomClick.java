package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;


public class CustomClick {
    @Attribute
    private String id;

    @Text
    private String text;

    public String getText() { return text; }
    public String getId() { return id; }
}
