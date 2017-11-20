package com.loopme;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceInfo that = (ResourceInfo) o;

        if (!mUrl.equals(that.mUrl)) return false;
        return mResourceName.equals(that.mResourceName);

    }

    @Override
    public int hashCode() {
        int result = mUrl.hashCode();
        result = 31 * result + mResourceName.hashCode();
        return result;
    }
}
