package com.loopme.vast;

import android.text.TextUtils;
import com.loopme.HttpUtil;
import com.loopme.Logging;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.utils.StringUtils;
import com.loopme.utils.Utils;
import com.loopme.xml.Tracking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VastVpaidEventTracker {

    private static final String LOG_TAG = VastVpaidEventTracker.class.getSimpleName();

    private static ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static Set<String> sUsedEvents = new HashSet<>();

    private static List<Tracking> sEvents = new ArrayList<>();

    public static void addAllEvents(List<Tracking> events) {
        sUsedEvents.clear();
        sEvents.clear();
        sEvents.addAll(events);
    }

    private VastVpaidEventTracker() {
    }

    private static void postEventByType(String eventType, String addMessage) {
        for (Tracking event : sEvents) {
            if (event.getEvent().equalsIgnoreCase(eventType)) {
                postEventByUrl(event.getText(), addMessage);
            }
        }
    }

    public static void postEvent(String event, String addMessage) {
        if (Utils.isUrl(event)) {
            postEventByUrl(event, addMessage);
        } else {
            postEventByType(event, addMessage);
        }
    }

    public static void postEvent(String event) {
        postEvent(event, "");
    }

    private static synchronized void postEventByUrl(String url, String addMessage) {
        if (sUsedEvents.contains(url)) {
            return;
        } else {
            sUsedEvents.add(url);
        }

        final String completeUrl = StringUtils.setMessage(url, addMessage);
        sExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Logging.out(LOG_TAG, completeUrl);
                HttpUtil.sendRequest(completeUrl, null, null);
            }
        });
    }

    public static void clear() {
        sUsedEvents.clear();
        sEvents.clear();
    }
}
