package com.loopme.vast;

import android.text.TextUtils;


import com.loopme.Constants;
import com.loopme.xml.Impression;
import com.loopme.xml.Tracking;
import com.loopme.xml.vast4.ViewableImpression;
import com.loopme.xml.vast4.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrapperParser {
    private List<Wrapper> mWrapperList = new ArrayList<Wrapper>();
    private List<String> mErrorUrlList = new ArrayList<String>();
    private List<Tracking> mTrackingList = new ArrayList<Tracking>();
    private ArrayList<String> mSimpleImpressionList = new ArrayList<String>();
    private Map<String, ArrayList<String>> mViewableImpressionMap = new HashMap<String, ArrayList<String>>();
    private ArrayList<String> mAdVerificationJavaScriptUrlList = new ArrayList<String>();
    private List<String> mVideoClicksList = new ArrayList<String>();

    private List<String> mCompanionCreativeViewEventsList = new ArrayList<String>();
    private List<String> mCompanionClickTrackingList = new ArrayList<String>();

    public WrapperParser(List<Wrapper> wrapperList) {
        mWrapperList.addAll(wrapperList);
        parse();
    }

    private void parse() {
        setViewableImpressions();
        setSimpleImpressionsList();
        setTrackingEvents();
        setErrorUrlList();
        setAdVerificationJavaScriptUrlList();
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

    private void setAdVerificationJavaScriptUrlList() {
        for (Wrapper wrapper : mWrapperList) {
            addAdVerificationJavaScriptUrl(wrapper);
        }
    }

    private void addAdVerificationJavaScriptUrl(Wrapper wrapper) {
        if (wrapper != null) {
            List<String> javaScriptUrlList = wrapper.getAdVerificationJavaScriptUrlList();
            for (String jsUrl : javaScriptUrlList) {
                addToAdVerificationJavaScriptUrlList(jsUrl);
            }
        }
    }

    private void addToAdVerificationJavaScriptUrlList(String jsUrl) {
        if (!TextUtils.isEmpty(jsUrl)) {
            mAdVerificationJavaScriptUrlList.add(jsUrl);
        }
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

    public ArrayList<String> getAdVerificationJavaScriptUrlList() {
        return mAdVerificationJavaScriptUrlList;
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
