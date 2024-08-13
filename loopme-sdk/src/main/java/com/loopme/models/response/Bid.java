
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
    private int width;
    private int height;
    public final static Parcelable.Creator<Bid> CREATOR = new Creator<Bid>() {
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
            instance.width = in.readInt();
            instance.height = in.readInt();
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

    public Bid(Ext ext, String id, String impid, String adid, String adm, List<String> adomain, String iurl, String cid, String crid, int width, int height) {
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
        this.width = width;
        this.height = height;
    }

    public Ext getExt() {
        return ext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdid() {
        return adid;
    }

    public String getAdm() {
        return adm;
    }

    public String getCrid() { return crid; }

    public String getCid() { return cid; }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public int describeContents() {
        return 0;
    }
}
