package com.loopme.xml.vast4;

import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewableImpression {

    @Attribute
    private String id;

    @Tag
    private Viewable viewable;
    @Tag
    private NotViewable notViewable;
    @Tag
    private ViewUndetermined viewUndetermined;

    private final Map<String, List<String>> mViewableImpressionMap = new HashMap<>();

    public String getId() { return id; }
    public String getViewableImpressionUrl() { return viewable == null ? "" : viewable.getText(); }
    public String getNotViewableImpressionUrl() { return notViewable == null ? "" : notViewable.getText(); }
    public String getViewUndeterminedUrl() { return viewUndetermined == null ? "" : viewUndetermined.getText(); }
    public ViewUndetermined getViewUndetermined() { return viewUndetermined; }
    public NotViewable getNotViewable() { return notViewable; }
    public Viewable getViewable() { return viewable; }

    public Map<String, List<String>> getViewableImpressionMap() {
        putToViewableImpressionMap(Constants.VIEWABLE, getViewableImpressionUrl());
        putToViewableImpressionMap(Constants.NOT_VIEWABLE, getNotViewableImpressionUrl());
        putToViewableImpressionMap(Constants.VIEW_UNDETERMINED, getViewUndeterminedUrl());
        return mViewableImpressionMap;
    }

    private void putToViewableImpressionMap(String impressionType, String impressionUrl) {
        if (TextUtils.isEmpty(impressionType) || TextUtils.isEmpty(impressionUrl)) return;
        ArrayList<String> list = new ArrayList<>();
        list.add(impressionUrl);
        mViewableImpressionMap.put(impressionType, list);
    }
}
