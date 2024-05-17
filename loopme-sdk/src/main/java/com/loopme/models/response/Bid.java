
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Bid implements Serializable, Parcelable {
    private Ext ext;
    private String impid;
    private String id;
    private String adid;
    private String adm;
    private List<String> adomain = null;
    private String iurl;
    private String cid;
    private String crid;
    public final static Parcelable.Creator<Bid> CREATOR = new Creator<>() {
        public Bid createFromParcel(Parcel in) {
            Bid instance = new Bid();
            instance.ext = ((Ext) in.readValue((Ext.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.impid = ((String) in.readValue((String.class.getClassLoader())));
            instance.adid = ((String) in.readValue((String.class.getClassLoader())));
            instance.adm = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.adomain, (java.lang.String.class.getClassLoader()));
            instance.iurl = ((String) in.readValue((String.class.getClassLoader())));
            instance.cid = ((String) in.readValue((String.class.getClassLoader())));
            instance.crid = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Bid[] newArray(int size) {
            return (new Bid[size]);
        }
    };
    private final static long serialVersionUID = 899597700635596792L;

    /**
     * No args constructor for use in serialization
     */
    public Bid() { }

    public Bid(Ext ext, String id, String impid, String adid, String adm, List<String> adomain, String iurl, String cid, String crid) {
        super();
        this.ext = ext;
        this.id = id;
        this.impid = impid;
        this.adid = adid;
        this.adm = adm;
        this.adomain = adomain;
        this.iurl = iurl;
        this.cid = cid;
        this.crid = crid;
    }

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
    }

    public Bid withExt(Ext ext) {
        this.ext = ext;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bid withId(String id) {
        this.id = id;
        return this;
    }

    public String getImpid() {
        return impid;
    }

    public void setImpid(String impid) {
        this.impid = impid;
    }

    public Bid withImpid(String impid) {
        this.impid = impid;
        return this;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public Bid withAdid(String adid) {
        this.adid = adid;
        return this;
    }

    public String getAdm() {
        return adm;
    }

    public void setAdm(String adm) {
        this.adm = adm;
    }

    public Bid withAdm(String adm) {
        this.adm = adm;
        return this;
    }

    public List<String> getAdomain() {
        return adomain;
    }

    public void setAdomain(List<String> adomain) {
        this.adomain = adomain;
    }

    public Bid withAdomain(List<String> adomain) {
        this.adomain = adomain;
        return this;
    }

    public String getIurl() {
        return iurl;
    }

    public void setIurl(String iurl) {
        this.iurl = iurl;
    }

    public Bid withIurl(String iurl) {
        this.iurl = iurl;
        return this;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Bid withCid(String cid) {
        this.cid = cid;
        return this;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public Bid withCrid(String crid) {
        this.crid = crid;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(ext);
        dest.writeValue(id);
        dest.writeValue(impid);
        dest.writeValue(adid);
        dest.writeValue(adm);
        dest.writeList(adomain);
        dest.writeValue(iurl);
        dest.writeValue(cid);
        dest.writeValue(crid);
    }

    public int describeContents() {
        return 0;
    }
}
