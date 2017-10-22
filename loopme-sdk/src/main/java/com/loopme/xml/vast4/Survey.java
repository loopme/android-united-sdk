package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;


public class Survey {
    @Text
    private String text;

    @Attribute
    private String type;

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
