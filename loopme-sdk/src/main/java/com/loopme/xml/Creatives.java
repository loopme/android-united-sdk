package com.loopme.xml;

import com.loopme.parser.xml.Tag;

import java.util.ArrayList;
import java.util.List;

public class Creatives {

    @Tag("Creative")
    private List<Creative> creativeList;

    public List<Creative> getCreativeList() {
        return creativeList;
    }

    public List<Tracking> getTrackingList() {
        if (creativeList != null) {
            for (Creative creative : creativeList) {
                if (creative.getLinear() != null) {
                    return creative.getLinear().getTrackingList();
                }
            }
        }
        return null;
    }

    public List<String> getVideoClicksList() {
        if (creativeList != null) {
            List<String> videoClicksList = new ArrayList<>();
            for (Creative creative : creativeList) {
                videoClicksList.addAll(creative.getVideoClicksList());
            }
            return videoClicksList;
        }
        return new ArrayList<>();
    }
}
