
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Seatbid implements Serializable, Parcelable {
    private final static long serialVersionUID = -4812741118671444258L;
    private List<Bid> bid = null;
    public final static Parcelable.Creator<Seatbid> CREATOR = new Creator<Seatbid>() {
        public Seatbid createFromParcel(Parcel in) {
            Seatbid instance = new Seatbid();
            in.readList(instance.bid, (Bid.class.getClassLoader()));
            return instance;
        }
        public Seatbid[] newArray(int size) {
            return (new Seatbid[size]);
        }
    };

    public Seatbid() { }

    public Seatbid(List<Bid> bid) {
        super();
        this.bid = bid;
    }

    public List<Bid> getBid() {
        return bid;
    }

    public void setBid(List<Bid> bid) {
        this.bid = bid;
    }

    public Seatbid withBid(List<Bid> bid) {
        this.bid = bid;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(bid);
    }

    public int describeContents() {
        return 0;
    }
}
