
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.loopme.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "advertiser",
        "v360",
        "orientation",
        "debug",
        "lineitem",
        "appname",
        "crtype",
        "campaign",
        "measure_partners",
        "autoloading",
        "package_ids"
})
public class Ext implements Serializable, Parcelable {

    private final static long serialVersionUID = 2452708648583644210L;

    @JsonProperty("advertiser")
    private String advertiser;
    @JsonProperty("v360")
    private long v360;
    @JsonProperty("orientation")
    private String orientation;
    @JsonProperty("debug")
    private long debug;
    @JsonProperty("lineitem")
    private String lineitem;
    @JsonProperty("appname")
    private String appname;
    @JsonProperty("crtype")
    private String crtype;
    @JsonProperty("campaign")
    private String campaign;
    @JsonProperty("measure_partners")
    private List<String> measurePartners = null;
    @JsonProperty("autoLoading")
    private long autoLoading = Constants.AUTO_LOADING_ABSENCE;
    @JsonProperty("package_ids")
    private List<String> packageIds = new ArrayList<String>();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<Ext> CREATOR = new Creator<Ext>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Ext createFromParcel(Parcel in) {
            Ext instance = new Ext();
            instance.advertiser = ((String) in.readValue((String.class.getClassLoader())));
            instance.v360 = ((long) in.readValue((long.class.getClassLoader())));
            instance.orientation = ((String) in.readValue((String.class.getClassLoader())));
            instance.debug = ((long) in.readValue((long.class.getClassLoader())));
            instance.lineitem = ((String) in.readValue((String.class.getClassLoader())));
            instance.appname = ((String) in.readValue((String.class.getClassLoader())));
            instance.crtype = ((String) in.readValue((String.class.getClassLoader())));
            instance.campaign = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.measurePartners, (String.class.getClassLoader()));
            in.readList(instance.packageIds, (String.class.getClassLoader()));
            instance.additionalProperties = ((Map<String, Object>) in.readValue((Map.class.getClassLoader())));
            instance.autoLoading = ((long) in.readValue((long.class.getClassLoader())));
            return instance;
        }

        public Ext[] newArray(int size) {
            return (new Ext[size]);
        }

    };

    /**
     * No args constructor for use in serialization
     */
    public Ext() {
    }

    /**
     * @param orientation
     * @param crtype
     * @param measurePartners
     * @param appname
     * @param lineitem
     * @param debug
     * @param campaign
     * @param advertiser
     * @param v360
     * @param autoloading
     * @param packageIds
     */
    public Ext(String advertiser, long v360, String orientation, long debug, String lineitem, String appname, String crtype, String campaign, List<String> measurePartners, long autoloading, List<String> packageIds) {
        super();
        this.advertiser = advertiser;
        this.v360 = v360;
        this.orientation = orientation;
        this.debug = debug;
        this.lineitem = lineitem;
        this.appname = appname;
        this.crtype = crtype;
        this.campaign = campaign;
        this.measurePartners = measurePartners;
        this.autoLoading = autoloading;
        this.packageIds = packageIds;
    }

    @JsonProperty("advertiser")
    public String getAdvertiser() {
        return advertiser;
    }

    @JsonProperty("advertiser")
    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public Ext withAdvertiser(String advertiser) {
        this.advertiser = advertiser;
        return this;
    }

    @JsonProperty("v360")
    public long getV360() {
        return v360;
    }

    @JsonProperty("v360")
    public void setV360(long v360) {
        this.v360 = v360;
    }

    public Ext withV360(long v360) {
        this.v360 = v360;
        return this;
    }

    @JsonProperty("orientation")
    public String getOrientation() {
        return orientation;
    }

    @JsonProperty("orientation")
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Ext withOrientation(String orientation) {
        this.orientation = orientation;
        return this;
    }

    @JsonProperty("debug")
    public long getDebug() {
        return debug;
    }

    @JsonProperty("debug")
    public void setDebug(long debug) {
        this.debug = debug;
    }

    public Ext withDebug(long debug) {
        this.debug = debug;
        return this;
    }

    @JsonProperty("lineitem")
    public String getLineitem() {
        return lineitem;
    }

    @JsonProperty("lineitem")
    public void setLineitem(String lineitem) {
        this.lineitem = lineitem;
    }

    public Ext withLineitem(String lineitem) {
        this.lineitem = lineitem;
        return this;
    }

    @JsonProperty("appname")
    public String getAppname() {
        return appname;
    }

    @JsonProperty("appname")
    public void setAppname(String appname) {
        this.appname = appname;
    }

    public Ext withAppname(String appname) {
        this.appname = appname;
        return this;
    }

    @JsonProperty("crtype")
    public String getCrtype() {
        return crtype;
    }

    @JsonProperty("crtype")
    public void setCrtype(String crtype) {
        this.crtype = crtype;
    }

    public Ext withCrtype(String crtype) {
        this.crtype = crtype;
        return this;
    }

    @JsonProperty("campaign")
    public String getCampaign() {
        return campaign;
    }

    @JsonProperty("campaign")
    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public Ext withCampaign(String campaign) {
        this.campaign = campaign;
        return this;
    }

    @JsonProperty("measure_partners")
    public List<String> getMeasurePartners() {
        return measurePartners;
    }

    @JsonProperty("measure_partners")
    public void setMeasurePartners(List<String> measurePartners) {
        this.measurePartners = measurePartners;
    }

    public Ext withMeasurePartners(List<String> measurePartners) {
        this.measurePartners = measurePartners;
        return this;
    }

    @JsonProperty("autoloading")
    public void setAutoLoading(long autoloading) {
        this.autoLoading = autoloading;
    }

    @JsonProperty("autoloading")
    public long getAutoLoading() {
        return autoLoading;
    }

    public Ext withAutoLoading(long autoLoading) {
        this.autoLoading = autoLoading;
        return this;
    }

    @JsonProperty("package_ids")
    public List<String> getPackageIds() {
        return packageIds;
    }

    @JsonProperty("package_ids")
    public void setPackageIds(List<String> packageIds) {
        this.packageIds = packageIds;
    }

    public Ext withPackageIds(List<String> packageIds) {
        this.packageIds = packageIds;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Ext withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(advertiser);
        dest.writeValue(v360);
        dest.writeValue(orientation);
        dest.writeValue(debug);
        dest.writeValue(lineitem);
        dest.writeValue(appname);
        dest.writeValue(crtype);
        dest.writeValue(campaign);
        dest.writeList(measurePartners);
        dest.writeList(packageIds);
        dest.writeValue(additionalProperties);
        dest.writeValue(autoLoading);
    }

    public int describeContents() {
        return 0;
    }

}
