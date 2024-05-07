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
    private final List<Wrapper> mWrapperList = new ArrayList<Wrapper>();
    private final List<String> mErrorUrlList = new ArrayList<String>();
    private final List<Tracking> mTrackingList = new ArrayList<Tracking>();
    private final ArrayList<String> mSimpleImpressionList = new ArrayList<String>();
    private final Map<String, ArrayList<String>> mViewableImpressionMap = new HashMap<String, ArrayList<String>>();

    private final List<Verification> verificationList = new ArrayList<>();

    private final List<String> mVideoClicksList = new ArrayList<String>();

    private final List<String> mCompanionCreativeViewEventsList = new ArrayList<String>();
    private final List<String> mCompanionClickTrackingList = new ArrayList<String>();

    public WrapperParser(List<Wrapper> wrapperList) {
        mWrapperList.addAll(wrapperList);
        parse();
    }

    private void parse() {
        setViewableImpressions();
        setSimpleImpressionsList();
        setTrackingEvents();
        setErrorUrlList();
        setVerificationList();
        setVideoClicksList();
        setCompanionAdsTrackingDetails();
    }

    private void setCompanionAdsTrackingDetails() {
        for (Wrapper wrapper : mWrapperList) {
            mCompanionCreativeViewEventsList.addAll(wrapper.getCompanionTrackingEvents());
            mCompanionClickTrackingList.addAll(wrapper.getCompanionClickTrackingList());
        }
    }

    private void setVideoClicksList() {
        for (Wrapper wrapper : mWrapperList) {
            mVideoClicksList.addAll(wrapper.getVideoClicksList());
        }
    }

    public List<String> getVideoClicksList() {
        return mVideoClicksList;
    }

    private void setViewableImpressions() {
        for (Wrapper wrapper : mWrapperList) {
            addViewableImpressions(wrapper.getViewableImpression());
        }
    }

    private void setSimpleImpressionsList() {
        for (Wrapper wrapper : mWrapperList) {
            addSimpleImpressions(wrapper.getImpressions());
        }
    }

    private void addSimpleImpressions(List<Impression> impressions) {
        if (impressions != null) {
            for (Impression impression : impressions) {
                addImpression(impression);
            }
        }
    }

    private void addImpression(Impression impression) {
        if (impression != null && !TextUtils.isEmpty(impression.getText())) {
            mSimpleImpressionList.add(impression.getText());
        }
    }

    private void setTrackingEvents() {
        for (Wrapper wrapper : mWrapperList) {
            addTrackingEvents(wrapper);
        }
    }

    private void setErrorUrlList() {
        for (Wrapper wrapper : mWrapperList) {
            addErrorUrl(wrapper);
        }
    }

    private void setVerificationList() {
        for (Wrapper wrapper : mWrapperList)
            addToVerificationList(wrapper);
    }

    private void addToVerificationList(Wrapper wrapper) {
        if (wrapper == null)
            return;

        List<Verification> verificationList = wrapper.getVerificationList();
        if (verificationList == null)
            return;

        for (Verification verification : verificationList)
            if (verification != null)
                this.verificationList.add(verification);
    }

    public List<String> getErrorUrlList() {
        return mErrorUrlList;
    }

    public Map<String, ArrayList<String>> getViewableImpressions() {
        return mViewableImpressionMap;
    }

    public ArrayList<String> getSimpleImpressions() {
        return mSimpleImpressionList;
    }

    public List<Tracking> getTrackingEvents() {
        return mTrackingList;
    }

    public List<Verification> getVerificationList() {
        return verificationList;
    }

    private void addErrorUrl(Wrapper wrapper) {
        if (wrapper != null && wrapper.getError() != null) {
            mErrorUrlList.add(wrapper.getError().getText());
        }
    }

    private void addTrackingEvents(Wrapper wrapper) {
        if (wrapper != null) {
            List<Tracking> trackingList = wrapper.getCreativeTrackingList();
            if (trackingList != null) {
                mTrackingList.addAll(trackingList);
            }
        }
    }

    private void addViewableImpressions(ViewableImpression impressions) {
        if (impressions != null) {
            addImpression(Constants.VIEWABLE, impressions.getViewableImpressionUrl());
            addImpression(Constants.NOT_VIEWABLE, impressions.getNotViewableImpressionUrl());
            addImpression(Constants.VIEW_UNDETERMINED, impressions.getViewUndeterminedUrl());
        }
    }

    private void addImpression(String eventType, String eventUrl) {
        if (isEventListWithSuchTypeExist(eventType)) {
            putToExistEventList(eventType, eventUrl);
        } else {
            createNewEventListWithType(eventType, eventUrl);
        }
    }

    private void putToExistEventList(String eventType, String eventUrl) {
        if (areStringsValid(eventType, eventUrl)) {
            mViewableImpressionMap.get(eventType).add(eventUrl);
        }
    }

    private void createNewEventListWithType(String eventType, String eventUrl) {
        if (areStringsValid(eventType, eventUrl)) {
            ArrayList<String> eventsList = new ArrayList<>();
            eventsList.add(eventUrl);
            mViewableImpressionMap.put(eventType, eventsList);
        }
    }

    private boolean isEventListWithSuchTypeExist(String eventType) {
        ArrayList<String> eventsList = mViewableImpressionMap.get(eventType);
        return eventsList != null;
    }

    private boolean areStringsValid(String eventType, String eventUrl) {
        return !TextUtils.isEmpty(eventType) && !TextUtils.isEmpty(eventUrl);
    }

    public List<String> getCompanionCreativeViewList() {
        return mCompanionCreativeViewEventsList;
    }

    public List<String> getCompanionClickTrackingList() {
        return mCompanionClickTrackingList;
    }
}
