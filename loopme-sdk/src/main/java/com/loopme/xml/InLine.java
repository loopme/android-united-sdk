package com.loopme.xml;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.AdTitle;
import com.loopme.xml.vast4.AdVerifications;
import com.loopme.xml.vast4.Advertiser;
import com.loopme.xml.vast4.Category;
import com.loopme.xml.vast4.Description;
import com.loopme.xml.vast4.Extensions;
import com.loopme.xml.vast4.Pricing;
import com.loopme.xml.vast4.Survey;
import com.loopme.xml.vast4.Verification;
import com.loopme.xml.vast4.ViewableImpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InLine {
    @Tag
    private AdSystem adSystem;
    @Tag
    private Survey survey;
    @Tag
    private Pricing pricing;
    @Tag
    private Advertiser advertiser;
    @Tag
    private AdTitle adTitle;
    @Tag("Category")
    private List<Category> categoryList;
    @Tag
    private Description description;
    @Tag
    private Error error;
    @Tag
    private ViewableImpression viewableImpression;
    @Tag
    private AdVerifications adVerifications;
    @Tag
    private Extensions extensions;
    @Tag("Impression")
    private List<Impression> impressionList;
    @Tag
    private Creatives creatives;

    public Extensions getExtensions() { return extensions; }
    public Survey getSurvey() { return survey; }
    public Pricing getPricing() { return pricing; }
    public Advertiser getAdvertiser() { return advertiser; }
    public Description getDescription() { return description; }
    public List<Category> getCategories() { return categoryList; }
    public AdTitle getAdTitle() { return adTitle; }
    public AdSystem getAdSystem() { return adSystem; }
    public AdVerifications getAdVerifications() { return adVerifications; }

    @NonNull
    public List<Verification> getVerifications() {
        List<Verification> verifications = adVerifications == null ?
            new ArrayList<>() : adVerifications.getVerificationList();
        return verifications == null ? new ArrayList<>() : verifications;
    }

    @NonNull
    public Map<String, List<String>> getViewableImpression() {
        return viewableImpression == null ? new HashMap<>() :viewableImpression.getViewableImpressionMap();
    }

    @NonNull
    public String getError() { return error == null ? "" : error.getText(); }

    @NonNull
    public List<Creative> getCreatives() {
         return creatives == null ? new ArrayList<>() : creatives.getCreativeList();
    }

    public Linear getLinear () {
        for (Creative creative : getCreatives()) {
            if (creative.getLinear() != null) return creative.getLinear();
        }
        return null;
    }

    @NonNull
    public List<String> getImpressionList() {
        List<Impression> imps = impressionList == null ? new ArrayList<>() : impressionList;
        List<String> impressions = new ArrayList<>();
        for (Impression impression : imps) {
            if (!TextUtils.isEmpty(impression.getText())) {
                impressions.add(impression.getText());
            }
        }
        return impressions;
    }
}
