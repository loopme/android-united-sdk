package com.loopme;

/**
 * Created by vynnykiakiv on 8/4/17.
 */

public class ResourceInfo {
    private String mUrl;
    private String mResourceName;

    public ResourceInfo() {
    }

    public ResourceInfo(String url, String resourceName) {
        mUrl = url;
        mResourceName = resourceName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getResourceName() {
        return mResourceName;
    }

    public void setResourceName(String mResourceName) {
        this.mResourceName = mResourceName;
    }
}
