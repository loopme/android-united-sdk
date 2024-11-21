package com.loopme.utils;

public class VideoSessionManager {

    private long bufferingStartTime;
    private long totalBufferingTimeMs;
    private int bufferCount = 0;
    private final String mediaUrl;
    private boolean isBuffering = false;

    public VideoSessionManager(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void startBuffering() {
        if (!isBuffering) {
            bufferingStartTime = System.currentTimeMillis();
            isBuffering = true;
        }
    }

    public boolean isBuffering() {
        return isBuffering;
    }

    public void endBuffering() {
        if (isBuffering) {
            totalBufferingTimeMs += System.currentTimeMillis() - bufferingStartTime;
            bufferCount++;
            isBuffering = false;
        }
    }

    public long getTotalBufferingTimeInSec() {
        return totalBufferingTimeMs / 1000;
    }

    public long getAverageBufferingTime() {
        return bufferCount > 0 ? getTotalBufferingTimeInSec() / bufferCount : 0;
    }

    public int getBufferCount() {
        return bufferCount;
    }
}
