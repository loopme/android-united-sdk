package com.loopme.xml;

import androidx.annotation.NonNull;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class ClickTracking {
    @Attribute
    private String id;

    @Text
    private String text;

    @NonNull
    public String getText() { return text == null ? "" : text; }
    public String getId() { return id; }
}
