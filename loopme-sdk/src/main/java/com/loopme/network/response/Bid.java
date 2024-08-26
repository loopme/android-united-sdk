
package com.loopme.network.response;

import static com.loopme.network.response.JSONParserUtils.parseStrings;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class Bid implements Serializable, Parcelable {
    private final static long serialVersionUID = 899597700635596792L;

    private static final class Param {
        private static final String ID = "id";
        private static final String ADM = "adm";
        private static final String EXT = "ext";
        private static final String CID = "cid";
        private static final String CRID = "crid";
        private static final String ADID = "adid";
        private static final String IURL = "iurl";
        private static final String IMPID = "impid";
        private static final String ADOMAIN = "adomain";
        private static final String WIDTH = "w";
        private static final String HEIGHT = "h";
    }

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

    public Ext getExt() { return ext; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    @NonNull
    public String getAdm() { return adm == null ? "" : adm; }
    public String getCrid() { return crid; }
    public String getCid() { return cid; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getAdid() { return adid; }
    public List<String> getAdomain() { return adomain; }

    // Start of Parcelable methods
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

        public Bid[] newArray(int size) { return (new Bid[size]); }
    };

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

    public int describeContents() { return 0; }
    // End of Parcelable methods

    // No args constructor for use in serialization
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

    public static Bid fromJSON(@NonNull JSONObject jsonObject) {
        String adm = jsonObject.optString(Param.ADM, "");
        boolean isVast = adm.trim().toUpperCase().contains("<VAST");
        JSONObject ext = jsonObject.optJSONObject(Param.EXT);
        return new Bid(
                Ext.fromJSON(ext == null ? new JSONObject() : ext, isVast),
                jsonObject.optString(Param.ID, ""),
                jsonObject.optString(Param.IMPID, ""),
                jsonObject.optString(Param.ADID, ""),
                adm,
                parseStrings(jsonObject, Param.ADOMAIN),
                jsonObject.optString(Param.IURL, ""),
                jsonObject.optString(Param.CID, ""),
                jsonObject.optString(Param.CRID, ""),
                jsonObject.optInt(Param.WIDTH, 0),
                jsonObject.optInt(Param.HEIGHT, 0)
        );
    }

    public String getOrientation() {
        return getExt() == null ? "" : getExt().getOrientation();
    }
}
