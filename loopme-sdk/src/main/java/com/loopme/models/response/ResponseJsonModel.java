
package com.loopme.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class ResponseJsonModel implements Serializable, Parcelable {
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

    public ResponseJsonModel withId(String id) {
        this.id = id;
        return this;
    }

    public List<Seatbid> getSeatbid() {
        return seatbid;
    }

    public void setSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
    }

    public ResponseJsonModel withSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(seatbid);
    }

    public int describeContents() {
        return 0;
    }
}
