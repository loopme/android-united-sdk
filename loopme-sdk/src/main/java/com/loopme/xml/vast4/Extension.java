package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;

/**
 * Created by vynnykiakiv on 7/18/17.
 */

public class Extension {

    @Attribute
    private String type;

    @Tag("total_available")
    private TotalAvailable totalAvailable;

    public TotalAvailable getTotalAvailable() { return totalAvailable; }
    public String getType() { return type; }
}
