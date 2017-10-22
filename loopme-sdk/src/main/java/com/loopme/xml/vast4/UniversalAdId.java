package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;

/**
 * Created by vynnykiakiv on 7/18/17.
 */

public class UniversalAdId {
    @Attribute
    private String idRegistry;

    @Attribute
    private String idValue;

    public String getIdValue() {
        return idValue;
    }

    public String getIdRegistry() {
        return idRegistry;
    }
}
