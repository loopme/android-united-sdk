
package com.loopme.network.response;

import static com.loopme.network.response.JSONParserUtils.parseList;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class SeatBid implements Serializable, Parcelable {
    private final static long serialVersionUID = -4812741118671444258L;

    private static final class Param {
        private static final String BID = "bid";
        private static final String SEAT = "seat";
    }

    private List<Bid> bid = null;
    private String seat = "";

    public List<Bid> getBid() { return bid; }
    public String getSeat() { return seat; }

    // Start of Parcelable methods
    public final static Parcelable.Creator<SeatBid> CREATOR = new Creator<SeatBid>() {
        public SeatBid createFromParcel(Parcel in) {
            SeatBid instance = new SeatBid();
            in.readList(instance.bid, (Bid.class.getClassLoader()));
            instance.seat = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }
        public SeatBid[] newArray(int size) {
            return (new SeatBid[size]);
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(bid);
        dest.writeValue(seat);
    }

    public int describeContents() { return 0; }
    // End of Parcelable methods

    public SeatBid() { }

    public SeatBid(List<Bid> bid, String seat) {
        super();
        this.bid = bid;
        this.seat = seat;
    }

    public static SeatBid fromJSON(@NonNull JSONObject jsonObject) {
        return new SeatBid(
            parseList(jsonObject, Param.BID, json -> Bid.fromJSON((JSONObject) json)),
            jsonObject.optString(Param.SEAT, "")
        );
    }
}
