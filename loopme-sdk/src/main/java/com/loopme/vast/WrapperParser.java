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
    private List<Wrapper> mWrapperList;
    private List<String> mErrorUrlList = new ArrayList<>();
    private List<Tracking> mTrackingList = new ArrayList<>();
    private ArrayList<String> mSimpleImpressionList = new ArrayList<>();
    private Map<String, ArrayList<String>> mViewableImpressionMap = new HashMap<>();
    private ArrayList<String> mAdVerificationJavaScriptUrlList = new ArrayList<>();
    private List<String> mVideoClicksList = new ArrayList<>();

    public WrapperParser(List<Wrapper> wrapperList) {
        mWrapperList = wrapperList;
        parse();
    }

    private void parse() {
        setViewableImpressions();
        setSimpleImpressionsList();
        setTrackingEvents();
        setErrorUrlList();
        setAdVerificationJavaScriptUrlList();
        setVideoClicksList();
    }

    private void setVideoClicksList() {
        if (mWrapperList != null) {
            for (Wrapper wrapper : mWrapperList) {
                addVideoClicksList(wrapper.getVideoClicksList());
            }
        }
    }

    public List<String> getVideoClicksList() {
        return mVideoClicksList;
    }

    private void addVideoClicksList(List<String> videoClicks) {
        for (String videoClickUrl : videoClicks) {
            mVideoClicksList.add(videoClickUrl);
        }
    }

    private void setViewableImpressions() {
        if (mWrapperList != null) {
            for (Wrapper wrapper : mWrapperList) {
                addViewableImpressions(wrapper.getViewableImpression());
            }
        }
    }

    private void setSimpleImpressionsList() {
        if (mWrapperList != null) {
            for (Wrapper wrapper : mWrapperList) {
                addSimpleImpressions(wrapper.getImpressions());
            }
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
        if (mWrapperList != null) {
            for (Wrapper wrapper : mWrapperList) {
                addTrackingEvents(wrapper);
            }
        }
    }

    private void setErrorUrlList() {
        if (mWrapperList != null) {
            for (Wrapper wrapper : mWrapperList) {
                addErrorUrl(wrapper);
            }
        }
    }

    private void setAdVerificationJavaScriptUrlList() {
        if (mWrapperList != null) {
            for (Wrapper wrapper : mWrapperList) {
                addAdVerificationJavaScriptUrl(wrapper);
            }
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


}
