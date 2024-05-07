package com.loopme.ad;

import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.AdIds;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.vast.WrapperParser;
import com.loopme.xml.Tracking;
import com.loopme.xml.vast4.Verification;
import com.loopme.xml.vast4.Wrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by katerina on 6/9/17.
 */

public class AdParams implements Serializable {

    private static final String LOG_TAG = AdParams.class.getSimpleName();

    private String mHtml;
    private String mFormat;
    private String mOrientation;
    private int mExpiredDate;

    private List<String> mPackageIds = new ArrayList<String>();
    private String mToken;

    private boolean mPartPreload;
    private boolean mVideo360;
    private boolean mIsMraid;
    private boolean mOwnCloseButton;
    private boolean mIsDebug;
    private AdIds mAdIds;

    private String mId;
    private int mDuration;

    private boolean mIsVpaid;
    private String mAdParams;
    private String mSkipTime;
    private String mVpaidJsUrl;
    private String mEndCardRedirectUrl;
    private String mVideoRedirectUrl;
    private AdType mAdType = AdType.HTML;

    private final List<String> mCompanionCreativeViewEventsList = new ArrayList<>();
    private List<String> mVideoFileUrlsList = new ArrayList<>();
    private List<String> mEndCardUrlList = new ArrayList<>();
    private List<String> mImpressionsList = new ArrayList<>();
    private List<String> mVideoClicksList = new ArrayList<>();
    private List<String> mEndCardClicksList = new ArrayList<>();

    private final List<String> mErrorUrlList = new ArrayList<>();
    private final List<Tracking> mTrackingEventsList = new ArrayList<>();
    private List<Verification> verificationList = new ArrayList<>();
    private Map<String, List<String>> mViewableImpressionMap = new HashMap<>();
    private List<String> mTrackersList = new ArrayList<>();
    private boolean mAutoLoading;

    AdSpotDimensions mAdSpotDimensions;

    public AdParams() {
    }

    public AdParams(AdParamsBuilder builder) {
        mFormat = builder.mBuilderFormat;
        mHtml = builder.mBuilderHtml;
        mOrientation = builder.mBuilderOrientation;
        mAdType = builder.mAdType;
        mExpiredDate = builder.mBuilderExpiredDate == 0 ?
                Constants.DEFAULT_EXPIRED_TIME :
                builder.mBuilderExpiredDate;
        mPackageIds = builder.mPackageIds;
        mTrackersList = builder.mTrackersList;
        mToken = builder.mToken;
        mPartPreload = builder.mPartPreload;
        mVideo360 = builder.mVideo360;
        mIsMraid = builder.mIsMraid;
        mIsDebug = builder.mIsDebug;
        mAdIds = builder.mAdIds;
        mAutoLoading = builder.mAutoLoading;
        mAdSpotDimensions = builder.mAdSpotDimensions;
        Logging.out(LOG_TAG, "Server response indicates  ad params: "
                + "format: " + mFormat + ", isAutoloading: " + mAutoLoading
                + ", mraid: " + mIsMraid + ", expire in: " + mExpiredDate);
    }

    public AdSpotDimensions getAdSpotDimensions() {
        return mAdSpotDimensions;
    }

    public boolean getPartPreload() {
        return mPartPreload;
    }

    public boolean isVideo360() {
        return mVideo360;
    }

    public String getHtml() {
        return mHtml;
    }

    public void setHtml(String html) {
        this.mHtml = html;
    }

    public String getAdFormat() {
        return mFormat;
    }

    public String getAdOrientation() {
        return mOrientation;
    }

    public int getExpiredTime() {
        return mExpiredDate;
    }

    public List<String> getPackageIds() {
        return mPackageIds;
    }

    public String getToken() {
        return mToken;
    }

    public boolean isOwnCloseButton() {
        return mOwnCloseButton;
    }

    public void setOwnCloseButton(boolean hasOwnCloseButton) {
        mOwnCloseButton = hasOwnCloseButton;
    }

    public boolean isVpaidAd() {
        return mIsVpaid;
    }

    public boolean isMraidAd() {
        return getAdType() == AdType.MRAID;
    }

    public boolean isLoopMeAd() {
        return getAdType() == AdType.HTML;
    }

    public void setAdType(AdType adType) {
        mAdType = adType;
    }

    public AdType getAdType() {
        return mAdType;
    }

    public boolean isVastAd() {
        return getAdType() == AdType.VAST && !mIsVpaid;
    }

    public boolean getAutoLoading() {
        return mAutoLoading;
    }

    public static class AdParamsBuilder {

        private final String mBuilderFormat;

        private String mBuilderHtml;
        private String mBuilderOrientation;
        private int mBuilderExpiredDate;

        private List<String> mPackageIds = new ArrayList<>();
        private List<String> mTrackersList = new ArrayList<>();
        private String mToken;
        private AdType mAdType;

        private boolean mPartPreload;
        private boolean mVideo360;
        private boolean mIsMraid;
        private boolean mIsDebug;
        private AdIds mAdIds;
        private boolean mAutoLoading;
        private AdSpotDimensions mAdSpotDimensions;


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

        public AdParamsBuilder partPreload(boolean preload) {
            mPartPreload = preload;
            return this;
        }

        public AdParamsBuilder video360(boolean b) {
            mVideo360 = b;
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

        public AdParamsBuilder adType(AdType adType) {
            mAdType = adType;
            return this;
        }

        public AdParamsBuilder token(String token) {
            mToken = token;
            return this;
        }

        public AdParamsBuilder html(String html) {
            if (TextUtils.isEmpty(html)) {
                LoopMeTracker.post("Broken response [empty html]", Constants.ErrorType.SERVER);
            }
            mBuilderHtml = html;
            return this;
        }

        public AdParamsBuilder orientation(String orientation) {
            if (isValidOrientationValue(orientation)) {
                mBuilderOrientation = orientation;
            } else {
                if (!TextUtils.isEmpty(mBuilderFormat) && mBuilderFormat.equalsIgnoreCase(Constants.INTERSTITIAL_TAG))
                    LoopMeTracker.post("Broken response [invalid orientation: " + orientation + "]", Constants.ErrorType.SERVER);
            }
            return this;
        }

        public AdParamsBuilder expiredTime(int time) {
            int timeSec = time * 1000;
            mBuilderExpiredDate = Math.max(Constants.DEFAULT_EXPIRED_TIME, timeSec);
            return this;
        }

        public AdParams build() {
            if (isValidFormatValue()) {
                return new AdParams(this);
            } else {
                Logging.out(LOG_TAG, "Wrong ad format value");
                return null;
            }
        }

        private boolean isValidFormatValue() {
            return mBuilderFormat != null
                    && (mBuilderFormat.equalsIgnoreCase(Constants.BANNER_TAG)
                    || mBuilderFormat.equalsIgnoreCase(Constants.INTERSTITIAL_TAG));
        }

        private boolean isValidOrientationValue(String orientation) {
            return orientation != null && (orientation.equalsIgnoreCase(Constants.ORIENTATION_PORT)
                    || orientation.equalsIgnoreCase(Constants.ORIENTATION_LAND));
        }

        public AdParamsBuilder debug(boolean debug) {
            mIsDebug = debug;
            return this;
        }

        public AdParamsBuilder adIds(AdIds adIds) {
            mAdIds = adIds;
            return this;
        }

        public AdParamsBuilder adSpotDimensions(AdSpotDimensions adSpotDimensions) {
            mAdSpotDimensions = adSpotDimensions;
            return this;
        }
    }

    public boolean isDebug() {
        return mIsDebug;
    }

    public void setDebug(boolean isDebug) {
        this.mIsDebug = isDebug;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public void setEndCardRedirectUrl(String endCardRedirectUrl) {
        this.mEndCardRedirectUrl = endCardRedirectUrl;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getEndCardRedirectUrl() {
        return mEndCardRedirectUrl;
    }

    public String getAdParams() {
        return mAdParams;
    }

    public String getVpaidJsUrl() {
        return mVpaidJsUrl;
    }

    public void setVpaid() {
        this.mIsVpaid = true;
    }

    public void setAdParams(String adParams) {
        this.mAdParams = adParams;
    }

    public void setVpaidJsUrl(String vpaidJsUrl) {
        this.mVpaidJsUrl = vpaidJsUrl;
    }

    public List<String> getImpressionsList() {
        return mImpressionsList;
    }

    public void setImpressionsList(List<String> impressions) {
        this.mImpressionsList = impressions;
    }

    public List<String> getCompanionCreativeViewEvents() {
        return mCompanionCreativeViewEventsList;
    }

    public void setCompanionCreativeViewEvents(List<String> companionCreativeViewEvents) {
        this.mCompanionCreativeViewEventsList.addAll(companionCreativeViewEvents);
    }

    public List<Tracking> getTrackingEventsList() {
        return new ArrayList<>(mTrackingEventsList);
    }

    public void setTrackingEventsList(List<Tracking> events) {
        if (events != null) {
            this.mTrackingEventsList.addAll(events);
        }
    }

    public List<String> getTrackers() {
        return mTrackersList;
    }

    public void setTrackers(List<String> trackersList) {
        this.mTrackersList = trackersList;
    }

    public List<String> getVideoClicks() {
        return mVideoClicksList;
    }

    public void setVideoClicks(List<String> videoClicks) {
        this.mVideoClicksList = videoClicks;
    }

    public List<String> getEndCardClicks() {
        return mEndCardClicksList;
    }

    public void setEndCardClicks(List<String> endCardClicks) {
        this.mEndCardClicksList = endCardClicks;
    }

    public String getVideoRedirectUrl() {
        return mVideoRedirectUrl;
    }

    public void setVideoRedirectUrl(String videoRedirectUrl) {
        this.mVideoRedirectUrl = videoRedirectUrl;
    }

    public List<String> getVideoFileUrlsList() {
        return mVideoFileUrlsList;
    }

    public void setVideoFileUrlsList(List<String> videoFileUrlsList) {
        this.mVideoFileUrlsList = videoFileUrlsList;
    }

    public List<String> getEndCardUrlList() {
        return mEndCardUrlList;
    }

    public void setEndCardUrlList(List<String> endCardUrlList) {
        this.mEndCardUrlList = endCardUrlList;
    }

    public String getSkipTime() {
        return mSkipTime;
    }

    public void setSkipTime(String skipTime) {
        this.mSkipTime = skipTime;
    }

    public void setOrientation(String mOrientation) {
        this.mOrientation = mOrientation;
    }

    public void parseWrappers(List<Wrapper> wrapperList) {
        WrapperParser parser = new WrapperParser(wrapperList);

        mViewableImpressionMap.putAll(parser.getViewableImpressions());
        mImpressionsList.addAll(parser.getSimpleImpressions());

        mTrackingEventsList.addAll(parser.getTrackingEvents());
        mErrorUrlList.addAll(parser.getErrorUrlList());

        verificationList.addAll(parser.getVerificationList());

        mVideoClicksList.addAll(parser.getVideoClicksList());
        mCompanionCreativeViewEventsList.addAll(parser.getCompanionCreativeViewList());
        mEndCardClicksList.addAll(parser.getCompanionClickTrackingList());
    }

    public List<String> getErrorUrlList() {
        return mErrorUrlList;
    }

    public boolean hasVast4ViewableImpressions() {
        return mViewableImpressionMap != null && !mViewableImpressionMap.isEmpty();
    }

    public Map<String, List<String>> getViewableImpressionMap() {
        return mViewableImpressionMap;
    }

    public void addErrorUrl(String errorUrl) {
        mErrorUrlList.add(errorUrl);
    }

    public void setViewableImpressionMap(Map<String, List<String>> viewableImpressionMap) {
        this.mViewableImpressionMap = viewableImpressionMap;
    }

    public void setVerificationList(List<Verification> verificationList) {
        this.verificationList = new ArrayList<>(verificationList);
    }

    public List<Verification> getVerificationList() {
        return new ArrayList<>(verificationList);
    }

    public List<String> getVisibleImpressions() {
        return getViewableImpressionByType(Constants.VIEWABLE);
    }

    public List<String> getNotVisibleImpressions() {
        return getViewableImpressionByType(Constants.NOT_VIEWABLE);
    }

    private List<String> getViewableImpressionByType(String type) {
        if (mViewableImpressionMap != null) {
            for (String key : mViewableImpressionMap.keySet()) {
                if (key.equalsIgnoreCase(type)) {
                    return mViewableImpressionMap.get(key);
                }
            }
        }
        return Collections.emptyList();
    }

    public void setExpiredDate(int mExpiredDate) {
        this.mExpiredDate = mExpiredDate;
    }

    public AdIds getAdIds() {
        return mAdIds;
    }
}
