package com.loopme.tester.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.loopme.tester.db.BaseModel;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;

import java.io.Serializable;

import static com.loopme.tester.db.contracts.AdContract.AdEntry;

/**
 * Created by Katerina Knyrik
 * on 06.12.16.
 */
@JsonRootName(value = "adSpot")
public class AdSpot extends BaseModel implements Serializable, Parcelable {

    public static final String BASE_URL_DEFAULT_VALUE = "loopme.me/api/loopme/ads/v3";

    @JsonIgnore
    private long mAdSpotId;
    @JsonProperty(value = "name")
    private String mName;
    @JsonProperty(value = "appKey")
    private String mAppKey;
    @JsonIgnore
    private String mBaseUrl;
    @JsonProperty(value = "adType")
    private AdType mAdType;
    @JsonProperty(value = "sdkType")
    private AdSdk mSdk;
    @JsonIgnore
    private long mTime;

    public AdSpot(String name, AdSdk sdk, AdType type, String appKey, String baseUrl, long time) {
        mName = name;
        mSdk = sdk;
        mAdType = type;
        mAppKey = appKey;
        mBaseUrl = baseUrl;
        mTime = time;
    }

    public AdSpot() {
    }

    public AdSpot(AdSpot adSpot) {
        mAdSpotId = adSpot.mAdSpotId;
        mName = adSpot.mName;
        mAppKey = adSpot.mAppKey;
        mBaseUrl = adSpot.mBaseUrl;
        mAdType = adSpot.mAdType;
        mSdk = adSpot.mSdk;
        mTime = adSpot.mTime;
    }

    @JsonIgnore
    public long getAdSpotId() {
        return mAdSpotId;
    }

    @JsonIgnore
    public String getName() {
        return mName;
    }

    @JsonIgnore
    public void setName(String name) {
        this.mName = name;
    }

    @JsonIgnore
    public String getAppKey() {
        return mAppKey;
    }

    @JsonIgnore
    public void setAppKey(String appKey) {
        this.mAppKey = appKey;
    }

    @JsonIgnore
    public String getBaseUrl() {
        return mBaseUrl;
    }

    @JsonIgnore
    public void setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    @JsonIgnore
    public AdType getType() {
        return mAdType;
    }

    @JsonIgnore
    public void setType(AdType adType) {
        this.mAdType = adType;
    }

    @JsonIgnore
    public AdSdk getSdk() {
        return mSdk;
    }

    @JsonIgnore
    public void setSdk(AdSdk sdk) {
        this.mSdk = sdk;
    }

    @JsonIgnore
    public long getTime() {
        return mTime;
    }

    @JsonIgnore
    public void setTime(long time) {
        this.mTime = time;
    }

    public static final Creator<AdSpot> CREATOR = new Creator<AdSpot>() {
        @Override
        public AdSpot createFromParcel(Parcel in) {
            return new AdSpot(in);
        }

        @Override
        public AdSpot[] newArray(int size) {
            return new AdSpot[size];
        }
    };

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AdEntry.COLUMN_NAME, mName);
        contentValues.put(AdEntry.COLUMN_APPKEY, mAppKey);
        contentValues.put(AdEntry.COLUMN_BASE_URL, mBaseUrl);
        contentValues.put(AdEntry.COLUMN_SDK, mSdk.toString());
        contentValues.put(AdEntry.COLUMN_TYPE, mAdType.toString());
        contentValues.put(AdEntry.COLUMN_TIME, mTime);
        return contentValues;
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if (cursor == null || cursor.isClosed() || cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return;
        }
        mName = getString(cursor, AdEntry.COLUMN_NAME);
        mAppKey = getString(cursor, AdEntry.COLUMN_APPKEY);
        mBaseUrl = getString(cursor, AdEntry.COLUMN_BASE_URL);
        mSdk = getSdk(getString(cursor, AdEntry.COLUMN_SDK));
        mAdType = getType(getString(cursor, AdEntry.COLUMN_TYPE));
        mTime = getLong(cursor, AdEntry.COLUMN_TIME);
        setIDFromCursor(cursor);
    }

    public static AdSpot createFromCursor(Cursor cursor) {
        AdSpot adSpot = new AdSpot();
        adSpot.fromCursor(cursor);
        return adSpot;
    }

    public void setIDFromCursor(Cursor cursor) {
        int ind = cursor.getColumnIndex(AdEntry.COLUMN_ID);
        if (ind >= 0) {
            setAdSpotId(cursor.getLong(ind));
        }
    }

    public AdType getType(String type) {
        if (AdType.BANNER.toString().equalsIgnoreCase(type)) {
            mAdType = AdType.BANNER;
        } else if (AdType.INTERSTITIAL.toString().equalsIgnoreCase(type)) {
            mAdType = AdType.INTERSTITIAL;
        }
        return mAdType;
    }

    public AdSdk getSdk(String sdk) {
        if (AdSdk.LOOPME.toString().equalsIgnoreCase(sdk)) {
            mSdk = AdSdk.LOOPME;

        } else if (AdSdk.MOPUB.toString().equalsIgnoreCase(sdk)) {
            mSdk = AdSdk.MOPUB;

        } else if (AdSdk.LMVPAID.toString().equalsIgnoreCase(sdk)) {
            mSdk = AdSdk.LMVPAID;
        }
        return mSdk;
    }

    public void setAdSpotId(long adSpotId) {
        this.mAdSpotId = adSpotId;
    }

    protected AdSpot(Parcel in) {
        mAdSpotId = in.readLong();
        mName = in.readString();
        mAppKey = in.readString();
        getSdk(in.readString());
        getType(in.readString());
        mTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mAdSpotId);
        parcel.writeString(mName);
        parcel.writeString(mAppKey);
        parcel.writeString(mSdk.toString());
        parcel.writeString(mAdType.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdSpot adSpot = (AdSpot) o;

        return mName.equals(adSpot.getName()) &&
                mAppKey.equals(adSpot.getAppKey()) &&
                mAdType == adSpot.getType() &&
                mSdk == adSpot.getSdk();

    }

}
