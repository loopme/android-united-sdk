
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
    private int debug;
    private String advertiser;
    private String orientation;
    private String lineitem;
    private String appname;
    private String crtype;
    private String campaign;
    private List<String> measurePartners = null;
    private int autoLoading = Constants.AUTO_LOADING_ABSENCE;
    private List<String> packageIds = new ArrayList<>();
    private String developer = "";
    private String company = "";

    private Map<String, Object> additionalProperties = new HashMap<>();
    public final static Parcelable.Creator<Ext> CREATOR = new Creator<Ext>() {
        @SuppressWarnings({ "unchecked" })
        public Ext createFromParcel(Parcel in) {
            Ext instance = new Ext();
            instance.advertiser = ((String) in.readValue((String.class.getClassLoader())));
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

        public Ext[] newArray(int size) { return (new Ext[size]); }
    };

    public Ext() { }

    public Ext(String advertiser, String orientation, int debug, String lineitem,
               String appname, String crtype, String campaign, List<String> measurePartners,
               int autoloading, List<String> packageIds, String developer, String company) {
        super();
        this.advertiser = advertiser;
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

    public String getOrientation() {
        return orientation;
    }

    public int getDebug() {
        return debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    public String getLineitem() {
        return lineitem;
    }

    public String getAppname() {
        return appname;
    }

    public String getCrtype() {
        return crtype;
    }

    public String getCampaign() {
        return campaign;
    }

    public List<String> getMeasurePartners() {
        return measurePartners;
    }

    public int getAutoLoading() {
        return autoLoading;
    }

    public List<String> getPackageIds() {
        return packageIds;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(advertiser);
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
}
