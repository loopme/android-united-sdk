
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
    "bid"
})
public class Seatbid implements Serializable, Parcelable {

    private final static long serialVersionUID = -4812741118671444258L;

    @JsonProperty("bid")
    private List<Bid> bid = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public final static Parcelable.Creator<Seatbid> CREATOR = new Creator<Seatbid>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Seatbid createFromParcel(Parcel in) {
            Seatbid instance = new Seatbid();
            in.readList(instance.bid, (Bid.class.getClassLoader()));
            instance.additionalProperties = ((Map<String, Object> ) in.readValue((Map.class.getClassLoader())));
            return instance;
        }

        public Seatbid[] newArray(int size) {
            return (new Seatbid[size]);
        }

    };

    /**
     * No args constructor for use in serialization
     * 
     */
    public Seatbid() {
    }

    /**
     * 
     * @param bid
     */
    public Seatbid(List<Bid> bid) {
        super();
        this.bid = bid;
    }

    @JsonProperty("bid")
    public List<Bid> getBid() {
        return bid;
    }

    @JsonProperty("bid")
    public void setBid(List<Bid> bid) {
        this.bid = bid;
    }

    public Seatbid withBid(List<Bid> bid) {
        this.bid = bid;
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

    public Seatbid withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(bid);
        dest.writeValue(additionalProperties);
    }

    public int describeContents() {
        return  0;
    }

}
