package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class Tracking {

    @Attribute
    private String event;

    @Attribute
    private String offset;

    @Text
    private String text;

    public Tracking(String event, String offset, String text) {
        this.event = event;
        this.offset = offset;
        this.text = text;
    }

    public String getEvent() {
        return event;
    }

    public String getOffset() {
        return offset;
    }

    public String getText() {
        return text;
    }
}
