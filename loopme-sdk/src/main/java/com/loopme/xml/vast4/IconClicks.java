package com.loopme.xml.vast4;

import com.loopme.parser.xml.Tag;


public class IconClicks {
    @Tag
    private IconClickThrough iconClickThrough;

    @Tag
    private IconClickTracking iconClickTracking;

    public IconClickThrough getIconClickThrough() {
        return iconClickThrough;
    }

    public IconClickTracking getIconClickTracking() {
        return iconClickTracking;
    }
}
