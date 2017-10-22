package com.loopme.xml.vast4;


import com.loopme.parser.xml.Tag;

import java.util.List;

public class Icons {
    @Tag("Icon")
    private List<Icon> iconList;

    public List<Icon> getIconList() {
        return iconList;
    }

}
