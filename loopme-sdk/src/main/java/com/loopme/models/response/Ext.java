
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.loopme.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ext implements Serializable, Parcelable {

    private final static long serialVersionUID = 2452708648583644210L;

    private int v360;
    private int debug;
    private String advertiser;
    private String orientation;
    private String lineitem;
    private String appname;
    private String crtype;
    private String campaign;
    private List<String> measurePartners = null;
    private int autoLoading = Constants.AUTO_LOADING_ABSENCE;
    private List<String> packageIds = new ArrayList<String>();
    private String developer = "";
    private String company = "";

    private Map<String, Object> additionalProperties = new HashMap<>();
    public final static Parcelable.Creator<Ext> CREATOR = new Creator<>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Ext createFromParcel(Parcel in) {
            Ext instance = new Ext();
            instance.advertiser = ((String) in.readValue((String.class.getClassLoader())));
            instance.v360 = ((int) in.readValue((int.class.getClassLoader())));
            instance.orientation = ((String) in.readValue((String.class.getClassLoader())));
            instance.debug = ((int) in.readValue((int.class.getClassLoader())));
            instance.lineitem = ((String) in.readValue((String.class.getClassLoader())));
            instance.appname = ((String) in.readValue((String.class.getClassLoader())));
            instance.crtype = ((String) in.readValue((String.class.getClassLoader())));
            instance.campaign = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.measurePartners, (String.class.getClassLoader()));
            in.readList(instance.packageIds, (String.class.getClassLoader()));
            instance.additionalProperties = ((Map<String, Object>) in.readValue((Map.class.getClassLoader())));
            instance.autoLoading = ((int) in.readValue((int.class.getClassLoader())));
            instance.developer = ((String) in.readValue((String.class.getClassLoader())));
            instance.company = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Ext[] newArray(int size) {
            return (new Ext[size]);
        }

    };

    public Ext() { }

    public Ext(String advertiser, int v360, String orientation, int debug, String lineitem,
               String appname, String crtype, String campaign, List<String> measurePartners,
               int autoloading, List<String> packageIds, String developer, String company) {
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
        this.developer = developer;
        this.company = company;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public Ext withAdvertiser(String advertiser) {
        this.advertiser = advertiser;
        return this;
    }

    public int getV360() {
        return v360;
    }

    public void setV360(int v360) {
        this.v360 = v360;
    }

    public Ext withV360(int v360) {
        this.v360 = v360;
        return this;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public Ext withOrientation(String orientation) {
        this.orientation = orientation;
        return this;
    }

    public int getDebug() {
        return debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public Ext withDebug(int debug) {
        this.debug = debug;
        return this;
    }

    public String getLineitem() {
        return lineitem;
    }

    public void setLineitem(String lineitem) {
        this.lineitem = lineitem;
    }

    public Ext withLineitem(String lineitem) {
        this.lineitem = lineitem;
        return this;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public Ext withAppname(String appname) {
        this.appname = appname;
        return this;
    }

    public String getCrtype() {
        return crtype;
    }

    public void setCrtype(String crtype) {
        this.crtype = crtype;
    }

    public Ext withCrtype(String crtype) {
        this.crtype = crtype;
        return this;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public Ext withCampaign(String campaign) {
        this.campaign = campaign;
        return this;
    }

    public List<String> getMeasurePartners() {
        return measurePartners;
    }

    public void setMeasurePartners(List<String> measurePartners) {
        this.measurePartners = measurePartners;
    }

    public Ext withMeasurePartners(List<String> measurePartners) {
        this.measurePartners = measurePartners;
        return this;
    }

    public void setAutoLoading(int autoloading) {
        this.autoLoading = autoloading;
    }

    public int getAutoLoading() {
        return autoLoading;
    }

    public Ext withAutoLoading(int autoLoading) {
        this.autoLoading = autoLoading;
        return this;
    }

    public List<String> getPackageIds() {
        return packageIds;
    }

    public void setPackageIds(List<String> packageIds) {
        this.packageIds = packageIds;
    }

    public Ext withPackageIds(List<String> packageIds) {
        this.packageIds = packageIds;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

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
        dest.writeValue(developer);
        dest.writeValue(company);
    }

    public int describeContents() {
        return 0;
    }

    public String getCompany() {
        return company;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public Ext withDeveloper(String developer) {
        this.developer = developer;
        return this;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Ext withCompany(String company) {
        this.company = company;
        return this;
    }
}
