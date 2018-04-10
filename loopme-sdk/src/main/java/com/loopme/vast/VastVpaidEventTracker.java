package com.loopme.vast;

import android.webkit.URLUtil;

import com.loopme.HttpUtil;
import com.loopme.Logging;
import com.loopme.utils.StringUtils;
import com.loopme.webservice.ExecutorHelper;
import com.loopme.xml.Tracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VastVpaidEventTracker {
    private static final String LOG_TAG = VastVpaidEventTracker.class.getSimpleName();

    private Set<String> mUsedEventsSet = new HashSet<>();
    private List<Tracking> mEventsList = new ArrayList<>();

    public VastVpaidEventTracker(List<Tracking> events) {
        mUsedEventsSet.clear();
        mEventsList.clear();
        mEventsList.addAll(events);
    }

    public void postEvent(String eventsUrlOrType, String addMessage) {
        if (URLUtil.isNetworkUrl(eventsUrlOrType)) {
            postEventByUrl(eventsUrlOrType, addMessage);
        } else {
            postEventByType(eventsUrlOrType, addMessage);
        }
    }

    private void postEventByType(String eventType, String addMessage) {
        for (Tracking event : mEventsList) {
            if (event.isTypeOf(eventType)) {
                postEventByUrl(event.getText(), addMessage);
            }
        }
    }

    private void postEventByUrl(String url, String addMessage) {
        if (!mUsedEventsSet.contains(url)) {
            mUsedEventsSet.add(url);
            trackVastEvent(url, addMessage);
        }
    }


    public static synchronized void trackVastEvent(final String eventUrl, final String addMessage) {

        ExecutorHelper.getExecutor().submit(new Runnable() {
            @Override
            public void run() {
                String completeUrl = StringUtils.setMessage(eventUrl, addMessage);
                HttpUtil.sendRequest(completeUrl, null, null);
                Logging.out(LOG_TAG, completeUrl);
            }
        });
    }
}
