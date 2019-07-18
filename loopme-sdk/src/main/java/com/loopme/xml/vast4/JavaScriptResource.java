package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;


public class JavaScriptResource {
    @Attribute
    private String apiFramework;

    @Attribute
    private boolean browserOptional;

    @Text
    private String text;

    public String getApiFramework() {
        return apiFramework;
    }

    public boolean getBrowserOptional() {
        return browserOptional;
    }

    public String getText() {
        return text;
    }

}
