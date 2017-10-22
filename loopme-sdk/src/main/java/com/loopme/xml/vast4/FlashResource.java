package com.loopme.xml.vast4;


import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

/**
 * Created by vynnykiakiv on 7/18/17.
 */

public class FlashResource {
    @Attribute
    private String apiFramework;

    @Text
    private String text;

    public String getApiFramework() {
        return apiFramework;
    }

    public String getText() {
        return text;
    }
}
