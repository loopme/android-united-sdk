package com.loopme.utils;

public class VideoSessionManager {

    private static VideoSessionManager instance;

    private long bufferingStartTime;
    private long totalBufferingTime;
    private int bufferCount = 0;
    private String mediaUrl;
    private boolean isBuffering = false;

    private VideoSessionManager() {}

    public static synchronized VideoSessionManager getInstance(String mediaUrl) {
        if (instance == null) {
            instance = new VideoSessionManager();
            instance.mediaUrl = mediaUrl;
        }
        return instance;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void startBuffering() {
            bufferingStartTime = System.currentTimeMillis();
            isBuffering = true;
    }

    public void endBuffering() {
            totalBufferingTime += System.currentTimeMillis() - bufferingStartTime;
            bufferCount++;
            isBuffering = false;
    }

    public long getTotalBufferingTime() {
        return totalBufferingTime / 1000;
    }

    public boolean isBuffering() {
        return isBuffering;
    }

    public long getAverageBufferingTime() {
        return bufferCount > 0 ? (totalBufferingTime / bufferCount) / 1000 : 0;
    }

    public int getBufferCount() {
        return bufferCount;
    }

    public void resetBufferingStats() {
        bufferingStartTime = 0;
        totalBufferingTime = 0;
        bufferCount = 0;
        isBuffering = false;
    }

    public static void resetInstance() {
        instance.resetBufferingStats();
        instance = null;
    }
}
