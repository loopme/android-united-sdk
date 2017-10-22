package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

/**
 * Created by vynnykiakiv on 7/18/17.
 */

public class IconClickTracking {
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
