package com.loopme.vast;

import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.xml.Impression;
import com.loopme.xml.Tracking;
import com.loopme.xml.vast4.Verification;
import com.loopme.xml.vast4.ViewableImpression;
import com.loopme.xml.vast4.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrapperParser {
    private final List<String> mErrorUrlList = new ArrayList<String>();
    private final List<Tracking> mTrackingList = new ArrayList<Tracking>();
    private final ArrayList<String> mSimpleImpressionList = new ArrayList<String>();
    private final Map<String, ArrayList<String>> mViewableImpressionMap = new HashMap<String, ArrayList<String>>();
    private final List<Verification> verificationList = new ArrayList<>();
    private final List<String> mVideoClicksList = new ArrayList<String>();
    private final List<String> mCompanionCreativeViewEventsList = new ArrayList<String>();
    private final List<String> mCompanionClickTrackingList = new ArrayList<String>();

    public WrapperParser(List<Wrapper> mWrapperList) {
        for (Wrapper wrapper : mWrapperList) {
            if (wrapper.getViewableImpression() != null) {
                ViewableImpression impressions = wrapper.getViewableImpression();
                addImpression(Constants.VIEWABLE, impressions.getViewableImpressionUrl());
                addImpression(Constants.NOT_VIEWABLE, impressions.getNotViewableImpressionUrl());
                addImpression(Constants.VIEW_UNDETERMINED, impressions.getViewUndeterminedUrl());
            }
            mTrackingList.addAll(wrapper.getCreativeTrackingList());
            if (wrapper.getError() != null) mErrorUrlList.add(wrapper.getError().getText());
            mVideoClicksList.addAll(wrapper.getVideoClicksList());
            mCompanionCreativeViewEventsList.addAll(wrapper.getCompanionTrackingEvents());
            mCompanionClickTrackingList.addAll(wrapper.getCompanionClickTrackingList());
            List<Impression> impressions = wrapper.getImpressions();
            if (impressions != null) {
                for (Impression impression : impressions) {
                    if (impression != null && !TextUtils.isEmpty(impression.getText())) {
                        mSimpleImpressionList.add(impression.getText());
                    }
                }
            }
            for (Verification verification : wrapper.getVerificationList())
                if (verification != null) verificationList.add(verification);
        }
    }

    public List<String> getVideoClicksList() { return mVideoClicksList; }
    public List<String> getErrorUrlList() { return mErrorUrlList; }
    public Map<String, ArrayList<String>> getViewableImpressions() { return mViewableImpressionMap; }
    public ArrayList<String> getSimpleImpressions() { return mSimpleImpressionList; }
    public List<Tracking> getTrackingEvents() { return mTrackingList; }
    public List<Verification> getVerificationList() { return verificationList; }
    public List<String> getCompanionCreativeViewList() { return mCompanionCreativeViewEventsList; }
    public List<String> getCompanionClickTrackingList() { return mCompanionClickTrackingList; }

    private void addImpression(String eventType, String eventUrl) {
        if (mViewableImpressionMap.get(eventType) == null) {
            mViewableImpressionMap.put(eventType, new ArrayList<>());
        }
        if (!TextUtils.isEmpty(eventUrl)) {
            mViewableImpressionMap.get(eventType).add(eventUrl);
        }
    }

}
