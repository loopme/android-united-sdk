package com.loopme.utils;

public class SessionManager {

    private static SessionManager instance;
    private long sessionStartTime;
    private long sessionDuration;
    private int adsShownCount = 0;
    private boolean isSessionActive = false;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession() {
        if (!isSessionActive) {
            sessionStartTime = System.currentTimeMillis();
            adsShownCount = 0;
            isSessionActive = true;
        }
    }

    public void endSession() {
        resetSessionData();
    }

    public long getSessionDuration() {
        if (isSessionActive) {
            long currentTime = System.currentTimeMillis();
            return sessionDuration + (currentTime - sessionStartTime) / 1000;
        } else {
            return sessionDuration;
        }
    }

    public void incrementAdsShownCount() {
        adsShownCount++;
    }

    public int getAdsShownCount() {
        return adsShownCount;
    }

    public void resetSessionData() {
        sessionDuration = 0;
        sessionStartTime = 0;
        adsShownCount = 0;
        isSessionActive = false;
    }
}

