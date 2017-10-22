package com.loopme.xml;

import android.text.TextUtils;

import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.CustomClick;

import java.util.ArrayList;
import java.util.List;

public class VideoClicks {

    @Tag()
    private ClickThrough clickThrough;

    @Tag()
    private CustomClick customClick;

    @Tag("ClickTracking")
    private List<ClickTracking> clickTrackingList;

    public List<ClickTracking> getClickTrackingList() {
        return clickTrackingList;
    }

    public ClickThrough getClickThrough() {
        return clickThrough;
    }

    public CustomClick getCustomClick() {
        return customClick;
    }

    public ArrayList<String> getClicksList() {
        ArrayList<String> clicksList = new ArrayList<>();
        if (clickTrackingList != null) {
            clicksList.addAll(getClickTrackingUrlList());
        }
        if (customClick != null && !TextUtils.isEmpty(customClick.getText())) {
            clicksList.add(customClick.getText());
        }
        return clicksList;
    }

    private ArrayList<String> getClickTrackingUrlList() {
        if (clickTrackingList != null) {
            ArrayList<String> clicksList = new ArrayList<>();
            for (ClickTracking clickTraсking : clickTrackingList) {
                if (!TextUtils.isEmpty(clickTraсking.getText())) {
                    clicksList.add(clickTraсking.getText());
                }
            }
            return clicksList;
        }
        return new ArrayList<>();
    }
}
