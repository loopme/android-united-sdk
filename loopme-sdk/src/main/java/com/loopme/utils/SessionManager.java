package com.loopme.utils;

import com.loopme.LoopMeSdk;

import java.util.UUID;

public class SessionManager {

    private static SessionManager instance;
    private long sessionStartTime;
    private long sessionDuration;
    private int adsShownCount = 0;
    private boolean isSessionActive = false;
    private String sessionId;

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
            sessionId = generateSessionId();
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

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public String getSessionId() {
        return LoopMeSdk.isInitialized() ? sessionId : null;
    }

  private void resetSessionData() {
        sessionDuration = 0;
        sessionStartTime = 0;
        adsShownCount = 0;
        isSessionActive = false;
        sessionId = null;
    }
}

