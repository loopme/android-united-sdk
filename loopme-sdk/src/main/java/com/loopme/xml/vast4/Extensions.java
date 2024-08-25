package com.loopme.xml.vast4;

import com.loopme.parser.xml.Tag;

import java.util.List;


/**
 * Created by vynnykiakiv on 7/18/17.
 */

public class Extensions {
    @Tag("Extension")
    private List<Extension> extensionList;

    public List<Extension> getExtensionList() { return extensionList; }
}
