
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ext",
    "id",
    "impid",
    "adid",
    "adm",
    "adomain",
    "iurl",
    "cid",
    "crid"
})
public class Bid implements Serializable, Parcelable{

    @JsonProperty("ext")
    private Ext ext;
    @JsonProperty("id")
    private String id;
    @JsonProperty("impid")
    private String impid;
    @JsonProperty("adid")
    private String adid;
    @JsonProperty("adm")
    private String adm;
    @JsonProperty("adomain")
    private List<String> adomain = null;
    @JsonProperty("iurl")
    private String iurl;
    @JsonProperty("cid")
    private String cid;
    @JsonProperty("crid")
    private String crid;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<Bid> CREATOR = new Creator<Bid>() {


        @SuppressWarnings({
            "unchecked"
        })
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
            instance.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
            return instance;
        }

        public Bid[] newArray(int size) {
            return (new Bid[size]);
        }

    }
    ;
    private final static long serialVersionUID = 899597700635596792L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Bid() {
    }

    /**
     * 
     * @param id
     * @param adid
     * @param crid
     * @param adm
     * @param adomain
     * @param impid
     * @param iurl
     * @param cid
     * @param ext
     */
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

    @JsonProperty("ext")
    public Ext getExt() {
        return ext;
    }

    @JsonProperty("ext")
    public void setExt(Ext ext) {
        this.ext = ext;
    }

    public Bid withExt(Ext ext) {
        this.ext = ext;
        return this;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Bid withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("impid")
    public String getImpid() {
        return impid;
    }

    @JsonProperty("impid")
    public void setImpid(String impid) {
        this.impid = impid;
    }

    public Bid withImpid(String impid) {
        this.impid = impid;
        return this;
    }

    @JsonProperty("adid")
    public String getAdid() {
        return adid;
    }

    @JsonProperty("adid")
    public void setAdid(String adid) {
        this.adid = adid;
    }

    public Bid withAdid(String adid) {
        this.adid = adid;
        return this;
    }

    @JsonProperty("adm")
    public String getAdm() {
        return adm;
    }

    @JsonProperty("adm")
    public void setAdm(String adm) {
        this.adm = adm;
    }

    public Bid withAdm(String adm) {
        this.adm = adm;
        return this;
    }

    @JsonProperty("adomain")
    public List<String> getAdomain() {
        return adomain;
    }

    @JsonProperty("adomain")
    public void setAdomain(List<String> adomain) {
        this.adomain = adomain;
    }

    public Bid withAdomain(List<String> adomain) {
        this.adomain = adomain;
        return this;
    }

    @JsonProperty("iurl")
    public String getIurl() {
        return iurl;
    }

    @JsonProperty("iurl")
    public void setIurl(String iurl) {
        this.iurl = iurl;
    }

    public Bid withIurl(String iurl) {
        this.iurl = iurl;
        return this;
    }

    @JsonProperty("cid")
    public String getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(String cid) {
        this.cid = cid;
    }

    public Bid withCid(String cid) {
        this.cid = cid;
        return this;
    }

    @JsonProperty("crid")
    public String getCrid() {
        return crid;
    }

    @JsonProperty("crid")
    public void setCrid(String crid) {
        this.crid = crid;
    }

    public Bid withCrid(String crid) {
        this.crid = crid;
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

    public Bid withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
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
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
