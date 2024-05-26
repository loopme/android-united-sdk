
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.loopme.Logging;
import com.loopme.ad.AdType;

import java.io.Serializable;
import java.util.List;

public class ResponseJsonModel implements Serializable, Parcelable {
    private static final String LOG_TAG = ResponseJsonModel.class.getSimpleName();
    private final static long serialVersionUID = 738802787497556038L;
    private String id;
    private List<Seatbid> seatbid = null;
    public final static Parcelable.Creator<ResponseJsonModel> CREATOR = new Creator<ResponseJsonModel>() {
        public ResponseJsonModel createFromParcel(Parcel in) {
            ResponseJsonModel instance = new ResponseJsonModel();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.seatbid, (Seatbid.class.getClassLoader()));
            return instance;
        }
        public ResponseJsonModel[] newArray(int size) {
            return (new ResponseJsonModel[size]);
        }

    };

    public ResponseJsonModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Seatbid> getSeatbid() {
        return seatbid;
    }

    public void setSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(seatbid);
    }

    public int describeContents() {
        return 0;
    }

    public static String getCreativeType(ResponseJsonModel responseModel) {
        try {
            return responseModel.getSeatbid().get(0).getBid().get(0).getExt().getCrtype();
        } catch (IllegalArgumentException | NullPointerException ex) {
            Logging.out(LOG_TAG, ex.getMessage());
            return AdType.HTML.name();
        }
    }
}
