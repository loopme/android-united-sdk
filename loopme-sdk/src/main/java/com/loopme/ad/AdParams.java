package com.loopme.ad;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.network.response.BidResponse;
import com.loopme.parser.xml.XmlParser;
import com.loopme.utils.Utils;
import com.loopme.vast.WrapperParser;
import com.loopme.xml.Ad;
import com.loopme.xml.Companion;
import com.loopme.xml.InLine;
import com.loopme.xml.Linear;
import com.loopme.xml.Tracking;
import com.loopme.xml.Vast;
import com.loopme.xml.vast4.Verification;
import com.loopme.xml.vast4.Wrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdParams implements Serializable {
    private static final String LOG_TAG = AdParams.class.getSimpleName();

    private String mHtml;
    private String mFormat;
    private String mOrientation;
    private int mExpiredDate;

    private List<String> mPackageIds = new ArrayList<>();
    private String mToken;

    private boolean mPartPreload;
    private boolean mOwnCloseButton;
    private boolean mIsDebug;

    private String mId;
    private int mDuration;

    private boolean mIsVpaid;
    private String mAdParams;
    private String mSkipTime;
    private String mVpaidJsUrl;
    private String mEndCardRedirectUrl;
    private String mVideoRedirectUrl;
    private AdType mAdType = AdType.HTML;

    private String mRequestId;
    private String mCid;
    private String mCrid;

    private final List<String> mCompanionCreativeViewEventsList = new ArrayList<>();
    private List<String> mVideoFileUrls = new ArrayList<>();
    private List<String> mEndCardUrls = new ArrayList<>();
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

    public AdParams() { }

    public AdParams(AdParamsBuilder builder) {
        mFormat = builder.mBuilderFormat;
        mHtml = builder.mBuilderHtml;
        mOrientation = builder.mBuilderOrientation;
        mAdType = builder.mAdType;
        mExpiredDate = builder.mBuilderExpiredDate == 0 ?
            Constants.DEFAULT_EXPIRED_TIME : builder.mBuilderExpiredDate;
        mPackageIds = builder.mPackageIds;
        mTrackersList = builder.mTrackersList;
        mToken = builder.mToken;
        mPartPreload = builder.mPartPreload;
        mIsDebug = builder.mIsDebug;
        mAutoLoading = builder.mAutoLoading;
        mAdSpotDimensions = builder.mAdSpotDimensions;
        mRequestId = builder.mRequestId;
        mCid = builder.mCid;
        mCrid = builder.mCrid;
        Logging.out(LOG_TAG, "Server response indicates  ad params: "
            + "format: " + mFormat + ", isAutoloading: " + mAutoLoading
            + ", mraid: " + builder.mIsMraid + ", expire in: " + mExpiredDate);
    }

    public AdSpotDimensions getAdSpotDimensions() { return mAdSpotDimensions; }

    public String getRequestId() { return mRequestId; }
    public String getCid() { return mCid; }
    public String getCrid() { return mCrid; }
    public String getAdFormat() { return mFormat; }
    public String getAdOrientation() { return mOrientation; }
    public List<String> getPackageIds() { return mPackageIds; }
    public String getToken() { return mToken; }
    public boolean getAutoLoading() { return mAutoLoading; }
    public void setOrientation(String orientation) { mOrientation = orientation; }
    public boolean getPartPreload() { return mPartPreload; }
    public List<String> getTrackers() { return mTrackersList; }

    public boolean isMraidAd() { return getAdType() == AdType.MRAID; }
    public boolean isLoopMeAd() { return getAdType() == AdType.HTML; }
    public boolean isVastAd() { return getAdType() == AdType.VAST && !mIsVpaid; }

    public void setExpiredDate(int expiredDate) { mExpiredDate = expiredDate; }
    public int getExpiredTime() { return mExpiredDate; }

    public boolean isVpaidAd() { return mIsVpaid; }
    public void setVpaid() { mIsVpaid = true; }

    public String getHtml() { return mHtml; }
    public void setHtml(String html) { mHtml = html; }

    public boolean isOwnCloseButton() { return mOwnCloseButton; }
    public void setOwnCloseButton(boolean hasOwnCloseButton) { mOwnCloseButton = hasOwnCloseButton; }

    public void setAdType(AdType adType) { mAdType = adType; }
    public AdType getAdType() { return mAdType; }

    public boolean isDebug() { return mIsDebug; }
    public void setDebug(boolean isDebug) { mIsDebug = isDebug; }

    public int getDuration() { return mDuration; }
    public void setDuration(int duration) { mDuration = duration; }

    public void setEndCardRedirectUrl(String endCardRedirectUrl) { mEndCardRedirectUrl = endCardRedirectUrl; }
    public String getEndCardRedirectUrl() { return mEndCardRedirectUrl; }

    public String getId() { return mId; }
    public void setId(String id) { mId = id; }

    public String getAdParams() { return mAdParams; }
    public void setAdParams(String adParams) { mAdParams = adParams; }

    public String getVpaidJsUrl() { return mVpaidJsUrl; }
    public void setVpaidJsUrl(String vpaidJsUrl) {
        mVpaidJsUrl = vpaidJsUrl;
        mIsVpaid = (
            mVpaidJsUrl != null && (mVpaidJsUrl.startsWith("https://") || mVpaidJsUrl.startsWith("http://"))
        );
    }

    public List<String> getImpressionsList() { return mImpressionsList; }
    public void setImpressionsList(List<String> impressions) { mImpressionsList = impressions; }

    public List<String> getCompanionCreativeViewEvents() { return mCompanionCreativeViewEventsList; }
    public void setCompanionCreativeViewEvents(List<String> companionCreativeViewEvents) {
        mCompanionCreativeViewEventsList.addAll(companionCreativeViewEvents);
    }

    public List<Tracking> getTrackingEventsList() { return new ArrayList<>(mTrackingEventsList); }
    public void setTrackingEventsList(@NonNull List<Tracking> events) {
        mTrackingEventsList.addAll(events);
    }

    public List<String> getVideoClicks() { return mVideoClicksList; }
    public void setVideoClicks(List<String> videoClicks) { mVideoClicksList = videoClicks; }

    public List<String> getEndCardClicks() { return mEndCardClicksList; }
    public void setEndCardClicks(List<String> endCardClicks) { mEndCardClicksList = endCardClicks; }

    public String getVideoRedirectUrl() { return mVideoRedirectUrl; }
    public void setVideoRedirectUrl(String videoRedirectUrl) { mVideoRedirectUrl = videoRedirectUrl; }

    @Nullable
    public String getVideoFileUrl() { return mVideoFileUrls.isEmpty() ? null : mVideoFileUrls.get(0); }
    public List<String> getVideoFiles() { return mVideoFileUrls; }
    public void setVideoFileUrlsList(List<String> urls) { mVideoFileUrls = urls; }

    @Nullable
    public String getEndCardUrl () { return mEndCardUrls.isEmpty() ? null : mEndCardUrls.get(0); }
    public List<String> getEndCards() { return mEndCardUrls; }
    public void setEndCardUrlList(List<String> urls) { mEndCardUrls = urls; }

    public String getSkipTime() { return mSkipTime; }
    public void setSkipTime(String skipTime) { mSkipTime = skipTime; }

    public List<Verification> getVerificationList() { return new ArrayList<>(verificationList); }
    public void setVerificationList(List<Verification> verificationList) {
        this.verificationList = new ArrayList<>(verificationList);
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

    public List<String> getErrorUrlList() { return mErrorUrlList; }
    public void addErrorUrl(String errorUrl) { mErrorUrlList.add(errorUrl); }

    public boolean hasVast4ViewableImpressions() {
        return mViewableImpressionMap != null && !mViewableImpressionMap.isEmpty();
    }

    public void setViewableImpressionMap(Map<String, List<String>> viewableImpressionMap) {
        mViewableImpressionMap = viewableImpressionMap;
    }

    public List<String> getVisibleImpressions() {
        return getViewableImpressionByType(Constants.VIEWABLE);
    }

    public List<String> getNotVisibleImpressions() {
        return getViewableImpressionByType(Constants.NOT_VIEWABLE);
    }

    private List<String> getViewableImpressionByType(String type) {
        if (mViewableImpressionMap == null) {
            return Collections.emptyList();
        }
        for (String key : mViewableImpressionMap.keySet()) {
            if (key.equalsIgnoreCase(type)) {
                return mViewableImpressionMap.get(key);
            }
        }
        return Collections.emptyList();
    }

    public static AdParams getAdParams(String vastString, List<Wrapper> mWrappersList) {
        AdParams adParams = parse(vastString, null);
        adParams.parseWrappers(mWrappersList);
        return adParams;
    }

    @NonNull
    public static AdParams parse(@NonNull String vastString, @Nullable AdParams adParams) {
        AdParams sAdParams = adParams == null ? new AdParams() : adParams;
        try {
            return parseResponse(vastString, sAdParams);
        } catch (IllegalArgumentException e) {
            return sAdParams;
        }
    }

    @NonNull
    private static AdParams parseResponse(String response, AdParams sAdParams) throws IllegalArgumentException {
        Vast vast = parseVast(response);
        if (vast == null || vast.getAd() == null) {
            throw new IllegalArgumentException("Vast or vast.getAd() shouldn't be null");
        }
        Ad ad = vast.getAd();
        InLine inLine = ad.getInLine();
        if (inLine == null) {
            throw new IllegalArgumentException("NonLinear AD is not supported");
        }
        Linear linear = inLine.getLinear();
        if (linear == null) {
            throw new IllegalArgumentException("NonLinear AD is not supported");
        }
        sAdParams.setExpiredDate(Constants.DEFAULT_EXPIRED_TIME);
        sAdParams.setId(ad.getId());

        sAdParams.setImpressionsList(inLine.getImpressionList());
        sAdParams.addErrorUrl(inLine.getError());
        sAdParams.setViewableImpressionMap(inLine.getViewableImpression());
        sAdParams.setVerificationList(inLine.getVerifications());

        sAdParams.setSkipTime(linear.getSkipoffset());
        sAdParams.setTrackingEventsList(linear.getTrackingEvents());
        sAdParams.setDuration(Utils.parseDuration(linear.getDuration().getText()));
        sAdParams.setAdParams(linear.getAdParameters());
        sAdParams.setOrientation(linear.getOrientation());
        sAdParams.setVideoClicks(linear.getVideoClickEvents());
        sAdParams.setVideoRedirectUrl(linear.getClickThroughUrl());
        sAdParams.setVpaidJsUrl(linear.getVpaidUrl());
        sAdParams.setVideoFileUrlsList(linear.getVideoFiles());

        Companion companion = Companion.getCompanion(inLine.getCreatives());
        if (companion == null) return sAdParams;
        sAdParams.setEndCardUrlList(Collections.singletonList(companion.getCompanionUrl()));
        sAdParams.setEndCardRedirectUrl(companion.getCompanionRedirectUrl());
        sAdParams.setEndCardClicks(companion.getCompanionClickEvents());
        sAdParams.setCompanionCreativeViewEvents(companion.getCompanionViewEvents());
        return sAdParams;
    }

    @Nullable
    private static Vast parseVast(String xml) {
        try {
            return XmlParser.parse(xml, Vast.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidXml(BidResponse response) {
        String xml = response.getAdm();
        if (TextUtils.isEmpty(xml)) return false;
        Vast vast = parseVast(xml);
        return vast != null && vast.getAd() != null;
    }

    public boolean isValid() {
        return !mVideoFileUrls.isEmpty() ||
            (mHtml != null && !mHtml.isEmpty()) ||
            (mVpaidJsUrl != null && !mVpaidJsUrl.isEmpty());
    }
}
