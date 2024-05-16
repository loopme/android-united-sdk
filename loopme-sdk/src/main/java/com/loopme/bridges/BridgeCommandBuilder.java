package com.loopme.bridges;

import com.loopme.Constants;

/**
 * Class helper to build LoopMe javascript bridge commands
 */
public class BridgeCommandBuilder {
    private BridgeCommandBuilder() { }
    public static final String MRAID_PREFIX = "javascript:mraidbridge.";
    public static final String LOOPME_PREFIX = "javascript:window.L.bridge.set";
    private static final String PREFIX_360 = "javascript:window.L.track";

    public static String isNativeCallFinished(boolean b) {
        return LOOPME_PREFIX + "('webview', {isNativeCallFinished: " + "'" + b + "'});";
    }
    public static String shake(boolean b) {
        return LOOPME_PREFIX + "('webview', {shake: " + "'" + b + "'});";
    }
    public static String videoMute(boolean mute) {
        return LOOPME_PREFIX + "('video', {mute: " + "'" + mute + "'});";
    }

    public static String videoCurrentTime(int time) {
        return LOOPME_PREFIX + "('video', {currentTime: " + "'" + time + "'});";
    }

    public static String videoDuration(int time) {
        return LOOPME_PREFIX + "('video', {duration: " + "'" + time + "'});";
    }

    public static String videoState(Constants.VideoState state) {
        return LOOPME_PREFIX + "('video', {state: " + "'" + state.name() + "'});";
    }

    public static String webviewState(String sdkPrefix,Constants.WebviewState state) {
        return sdkPrefix + "('webview', {state: " + "'" + state.name() + "'});";
    }

    public static String fullscreenMode(boolean b) {
        return LOOPME_PREFIX + "('webview', {fullscreenMode: " + b + "});";
    }

    public static String event360(String event) {
        return PREFIX_360 + "({eventType: 'INTERACTION', customEventName: 'video360&mode=" + event + "'});";
    }

    public static String mraidSetIsViewable(boolean isViewable) {
        return MRAID_PREFIX + "setIsViewable(" + isViewable + ");";
    }

    public static String mraidNotifyReady() {
        return MRAID_PREFIX + "notifyReadyEvent();";
    }

    public static String mraidNotifyError() {
        return MRAID_PREFIX + "notifyErrorEvent();";
    }

    public static String mraidNotifyStateChange() {
        return MRAID_PREFIX + "notifyStateChangeEvent();";
    }

    public static String mraidSetState(String state) {
        return MRAID_PREFIX + "setState('" + state + "');";
    }

    public static String mraidNotifySizeChangeEvent(int width, int height) {
        return MRAID_PREFIX + "notifySizeChangeEvent(" + width + ", " + height + ");";
    }

    public static String mraidResize() {
        return MRAID_PREFIX + "resize();";
    }

    public static String mraidNativeCallComplete() {
        return MRAID_PREFIX + "nativeCallComplete();";
    }
}

