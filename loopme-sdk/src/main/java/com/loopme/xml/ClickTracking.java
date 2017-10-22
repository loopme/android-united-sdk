package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class ClickTracking {
    @Attribute
    private String id;

    @Text
    private String text;

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }
}
