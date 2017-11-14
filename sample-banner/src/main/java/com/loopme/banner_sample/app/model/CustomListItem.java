package com.loopme.banner_sample.app.model;

public class CustomListItem {

    private final String mTitle;
    private final String mSubtitle;
    private final int mIconId;

    public CustomListItem(String title, String subtitle, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIconId = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public int getIconId() {
        return mIconId;
    }
}
