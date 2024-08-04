package com.loopme.xml;

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
import com.loopme.xml.vast4.ViewableImpression;

import java.util.ArrayList;
import java.util.List;

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
    public ViewableImpression getViewableImpression() { return viewableImpression; }
    public Error getError() { return error; }
    public Creatives getCreatives() { return creatives; }
    @NonNull
    public List<Impression> getImpressionList() { return impressionList != null ? impressionList : new ArrayList<>(); }
}
