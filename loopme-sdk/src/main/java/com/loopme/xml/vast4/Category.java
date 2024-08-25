package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class Category {
    @Text
    private String text;

    @Attribute
    private String authority;

    public String getText() { return text; }
    public String getAuthority() { return authority; }
}
