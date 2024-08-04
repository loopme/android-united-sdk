package com.loopme.ad;

import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;

import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by katerina on 6/9/17.
 */

public class AdParamsBuilder {
    private static final String LOG_TAG = AdParamsBuilder.class.getSimpleName();
    final String mBuilderFormat;

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

    public AdParamsBuilder(String format) {
        mBuilderFormat = format;
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
        boolean isValidOrientationValue = orientation != null &&
            (orientation.equalsIgnoreCase(Constants.ORIENTATION_PORT) ||
            orientation.equalsIgnoreCase(Constants.ORIENTATION_LAND));
        if (isValidOrientationValue) {
            mBuilderOrientation = orientation;
            return this;
        }
        if (!TextUtils.isEmpty(mBuilderFormat) && mBuilderFormat.equalsIgnoreCase(Constants.INTERSTITIAL_TAG)) {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Broken response [invalid orientation: " + orientation + "]");
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.SERVER);
            LoopMeTracker.post(errorInfo);
        }
        return this;
    }

    public boolean isValidFormatValue() {
        return mBuilderFormat != null &&
            (mBuilderFormat.equalsIgnoreCase(Constants.BANNER_TAG) ||
            mBuilderFormat.equalsIgnoreCase(Constants.INTERSTITIAL_TAG));
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
