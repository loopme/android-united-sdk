package com.loopme.tracker;

import android.text.TextUtils;

public class AdIds {
    private String mAdvertiserId = "";
    private String mPlacementId = "";
    private String mCampaignId = "";
    private String mLineItemId = "";
    private String mCreativeId = "";
    private String mAppId = "";

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
}
