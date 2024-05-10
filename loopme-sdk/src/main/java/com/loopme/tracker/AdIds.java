package com.loopme.tracker;

import android.text.TextUtils;

import java.util.HashMap;

public class AdIds {
    private String mAdvertiserId = "";
    private String mPlacementId = "";
    private String mCampaignId = "";
    private String mLineItemId = "";
    private String mCreativeId = "";
    private String mAppId = "";

    private static final String LEVEL1 = "level1";
    private static final String LEVEL2 = "level2";
    private static final String LEVEL3 = "level3";
    private static final String LEVEL4 = "level4";
    private static final String SLICER1 = "slicer1";
    private static final String SLICER2 = "slicer2";


    public void setAdvertiserId(String advertiserId) {
        this.mAdvertiserId = TextUtils.isEmpty(advertiserId) ? "" : advertiserId;
    }

    public void setCampaignId(String campaignId) {
        this.mCampaignId = TextUtils.isEmpty(campaignId) ? "" : campaignId;
    }

    public void setLineItemId(String lineItemId) {
        this.mLineItemId = TextUtils.isEmpty(lineItemId) ? "" : lineItemId;
    }

    public void setAppId(String appId) {
        this.mAppId = TextUtils.isEmpty(appId) ? "" : appId;
    }

    public void setPlacementId(String placementId) {
        this.mPlacementId = TextUtils.isEmpty(placementId) ? "" : placementId;
    }

    public void setCreativeId(String creativeId) {
        this.mCreativeId = TextUtils.isEmpty(creativeId) ? "" : creativeId;
    }

    public String getAdvertiserId() {
        return mAdvertiserId;
    }

    public String getCampaignId() {
        return mCampaignId;
    }

    public String getLineItemId() {
        return mLineItemId;
    }

    public String getAppId() {
        return mAppId;
    }

    public String getPlacementId() {
        return mPlacementId;
    }

    public String getCreativeId() {
        return mCreativeId;
    }

    public HashMap<String, String> toHashMap() {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put(LEVEL1, getAdvertiserId());
        dataMap.put(LEVEL2, getCampaignId());
        dataMap.put(LEVEL3, getLineItemId());
        dataMap.put(LEVEL4, getCreativeId());
        dataMap.put(SLICER1, getAppId());
        dataMap.put(SLICER2, getPlacementId());
        return dataMap;
    }

    public void setCompany(String company) {
        String mCompany = TextUtils.isEmpty(company) ? "" : company;
    }

    public void setAppName(String appName) {
        String mAppName = TextUtils.isEmpty(appName) ? "" : appName;
    }

    public void setAppBundle(String appBundle) {
        String mAppBundle = TextUtils.isEmpty(appBundle) ? "" : appBundle;
    }

    public void setDeveloper(String developer) {
        String mDeveloper = TextUtils.isEmpty(developer) ? "" : developer;
    }
}
