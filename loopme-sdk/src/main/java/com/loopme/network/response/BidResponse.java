package com.loopme.network.response;

import static com.loopme.network.response.JSONParserUtils.parseList;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.loopme.Logging;
import com.loopme.ad.AdType;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class BidResponse implements Serializable, Parcelable {
    private static final String LOG_TAG = BidResponse.class.getSimpleName();
    private final static long serialVersionUID = 738802787497556038L;

    private static final class Param {
        private static final String ID = "id";
        private static final String SEATBID = "seatbid";
    }

    private String id;
    private List<SeatBid> seatbid = null;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<SeatBid> getSeatbid() { return seatbid; }
    public void setSeatbid(List<SeatBid> seatbid) { this.seatbid = seatbid; }

    // Start of Parcelable methods
    public final static Parcelable.Creator<BidResponse> CREATOR = new Creator<BidResponse>() {
        public BidResponse createFromParcel(Parcel in) {
            BidResponse instance = new BidResponse();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.seatbid, (SeatBid.class.getClassLoader()));
            return instance;
        }
        public BidResponse[] newArray(int size) { return (new BidResponse[size]); }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(seatbid);
    }

    public int describeContents() { return 0; }
    // End of Parcelable methods

    public BidResponse() { }

    public BidResponse(String id, List<SeatBid> seatbid) {
        super();
        this.id = id;
        this.seatbid = seatbid;
    }

    public static BidResponse fromJSON(@NonNull JSONObject jsonObject) {
        return new BidResponse(
            jsonObject.optString(Param.ID, ""),
            parseList(jsonObject, Param.SEATBID, json -> SeatBid.fromJSON((JSONObject) json))
        );
    }

    @NonNull
    public static AdType getCreativeType(BidResponse responseModel) {
        try {
            String adm = responseModel.getSeatbid().get(0).getBid().get(0).getAdm();
            return adm.contains("<VAST") ? AdType.VAST : AdType.MRAID;
        } catch (IllegalArgumentException | NullPointerException ex) {
            Logging.out(LOG_TAG, ex.getMessage());
            return AdType.HTML;
        }
    }
}
