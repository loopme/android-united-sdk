package com.loopme.ad;

import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.Constants.AdFormat;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by katerina on 6/9/17.
 */

public class AdParamsBuilder {
    private static final String LOG_TAG = AdParamsBuilder.class.getSimpleName();
    String mBuilderFormat;

    String mBuilderHtml;
    String mBuilderOrientation;
    int mBuilderExpiredDate;

    List<String> mPackageIds = new ArrayList<>();
    List<String> mTrackersList = new ArrayList<>();
    String mToken;
    AdType mAdType;

    boolean mPartPreload;
    boolean mIsMraid;
    boolean mIsDebug;
    boolean mAutoLoading;
    AdSpotDimensions mAdSpotDimensions;

    String mRequestId;
    String mCid;
    String mCrid;

    public AdParamsBuilder() {}

    public AdParamsBuilder requestId(String value) {
        mRequestId = value;
        return this;
    }

    public AdParamsBuilder cid(String value) {
        mCid = value;
        return this;
    }

    public AdParamsBuilder crid(String value) {
        mCrid = value;
        return this;
    }


    public AdParamsBuilder format(String format) {
        mBuilderFormat = format;
        return this;
    }

    public AdParamsBuilder packageIds(List<String> installPacakage) {
        mPackageIds = installPacakage;
        return this;
    }

    public AdParamsBuilder trackersList(List<String> trackersList) {
        mTrackersList = trackersList;
        return this;
    }

    public AdParamsBuilder mraid(boolean b) {
        mIsMraid = b;
        return this;
    }

    public AdParamsBuilder autoLoading(boolean autoloading) {
        mAutoLoading = autoloading;
        return this;
    }

    public AdParamsBuilder token(String token) {
        mToken = token;
        return this;
    }

    public AdParamsBuilder html(String html) {
        if (TextUtils.isEmpty(html)) {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Broken response [empty html]");
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.SERVER);
            LoopMeTracker.post(errorInfo);
        }
        mBuilderHtml = html;
        return this;
    }

    public AdParamsBuilder orientation(String orientation) {
        if (
            Constants.ORIENTATION_PORT.equalsIgnoreCase(orientation) ||
            Constants.ORIENTATION_LAND.equalsIgnoreCase(orientation)
        ) {
            mBuilderOrientation = orientation;
            return this;
        }
        if (Constants.INTERSTITIAL_TAG.equalsIgnoreCase(mBuilderFormat)) {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Broken response [invalid orientation: " + orientation + "]");
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.SERVER);
            LoopMeTracker.post(errorInfo);
        }
        return this;
    }

    @NonNull
    public static String getAdFormat(@NonNull AdFormat adFormat) {
        if (adFormat == AdFormat.INTERSTITIAL) return Constants.INTERSTITIAL_TAG;
        return adFormat == AdFormat.BANNER ? Constants.BANNER_TAG : Constants.INTERSTITIAL_TAG;
    }

    public AdParamsBuilder debug(boolean debug) {
        mIsDebug = debug;
        return this;
    }

    public AdParamsBuilder adSpotDimensions(AdSpotDimensions adSpotDimensions) {
        mAdSpotDimensions = adSpotDimensions;
        return this;
    }
}
