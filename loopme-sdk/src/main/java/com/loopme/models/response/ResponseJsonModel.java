
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
    "id",
    "seatbid"
})
public class ResponseJsonModel implements Serializable, Parcelable {

    private final static long serialVersionUID = 738802787497556038L;

    @JsonProperty("id")
    private String id;
    @JsonProperty("seatbid")
    private List<Seatbid> seatbid = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<ResponseJsonModel> CREATOR = new Creator<ResponseJsonModel>() {
        @SuppressWarnings({
            "unchecked"
        })
        public ResponseJsonModel createFromParcel(Parcel in) {
            ResponseJsonModel instance = new ResponseJsonModel();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.seatbid, (Seatbid.class.getClassLoader()));
            instance.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
            return instance;
        }

        public ResponseJsonModel[] newArray(int size) {
            return (new ResponseJsonModel[size]);
        }

    };

    /**
     * No args constructor for use in serialization
     * 
     */
    public ResponseJsonModel() {
    }

    /**
     * 
     * @param id
     * @param seatbid
     */
    public ResponseJsonModel(String id, List<Seatbid> seatbid) {
        super();
        this.id = id;
        this.seatbid = seatbid;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public ResponseJsonModel withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("seatbid")
    public List<Seatbid> getSeatbid() {
        return seatbid;
    }

    @JsonProperty("seatbid")
    public void setSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
    }

    public ResponseJsonModel withSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
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

    public ResponseJsonModel withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(seatbid);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
