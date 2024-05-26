package com.loopme.xml;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;

import java.util.List;

public class CompanionAds {
    @Attribute
    private String required;

    @Tag("Companion")
    private List<Companion> companionList;

    public List<Companion> getCompanionList() {
        return companionList;
    }

    public String getRequired() {
        return required;
    }

    public boolean hasCompanionList() {
        return companionList != null && !companionList.isEmpty();
    }
}
