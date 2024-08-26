package com.loopme.xml.vast4;

import com.loopme.parser.xml.Tag;

import java.util.List;

public class CreativeExtensions {
    @Tag("CreativeExtension")
    private List<CreativeExtension> creativeExtensionList;

    public List<CreativeExtension> getCreativeExtensionList() { return creativeExtensionList; }
}
