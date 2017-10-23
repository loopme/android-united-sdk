package com.loopme.tester.enums;

import com.loopme.tester.R;

/**
 * Created by katerina on 2/17/17.
 */

public enum ViewMode {

    VIEW(0, R.string.empty_string),
    CREATE(1, R.string.new_ad_spot),
    EDIT(2, R.string.empty_string),
    INFO(3, R.string.ad_spots);

    private int mId;
    private int mTitle;

    ViewMode(int id, int title) {
        this.mId = id;
        this.mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public int getTitle() {
        return mTitle;
    }
}
