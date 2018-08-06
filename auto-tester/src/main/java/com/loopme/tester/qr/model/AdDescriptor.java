package com.loopme.tester.qr.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.loopme.tester.enums.AdType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ad_type",
        "width",
        "height",
        "url"
})
public class AdDescriptor implements Parcelable {

    @JsonProperty("ad_type")
    private String adType = "";
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;
    @JsonProperty("url")
    private String url = "";
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public AdDescriptor() {
    }

    protected AdDescriptor(Parcel in) {
        adType = in.readString();
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adType);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AdDescriptor> CREATOR = new Creator<AdDescriptor>() {
        @Override
        public AdDescriptor createFromParcel(Parcel in) {
            return new AdDescriptor(in);
        }

        @Override
        public AdDescriptor[] newArray(int size) {
            return new AdDescriptor[size];
        }
    };

    @JsonProperty("ad_type")
    public String getAdType() {
        return adType;
    }

    @JsonProperty("ad_type")
    public void setAdType(String adType) {
        this.adType = adType;
    }

    @JsonProperty("width")
    public int getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(int width) {
        this.width = width;
    }

    @JsonProperty("height")
    public int getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(int height) {
        this.height = height;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isInterstitial() {
        return AdType.INTERSTITIAL == AdType.fromString(adType);
    }
}
