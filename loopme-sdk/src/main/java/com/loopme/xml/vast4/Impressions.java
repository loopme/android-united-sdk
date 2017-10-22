package com.loopme.xml.vast4;


import com.loopme.parser.xml.Tag;
import com.loopme.xml.Impression;

import java.util.List;

public class Impressions {
    @Tag("Impression")
    private List<Impression> impressionList;

    public List<Impression> getImpressionList() {
        return impressionList;
    }
}
