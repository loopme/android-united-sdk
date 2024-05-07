package com.loopme.bridges;

import com.loopme.Constants;

/**
 * Class helper to build LoopMe javascript bridge commands
 */
public class BridgeCommandBuilder {

    private BridgeCommandBuilder() {
    }

    public static final String MRAID_PREFIX = "javascript:mraidbridge.";
    public static final String LOOPME_PREFIX = "javascript:window.L.bridge.set";
    private static final String PREFIX_360 = "javascript:window.L.track";

    public static String isNativeCallFinished(boolean b) {
        String builder = LOOPME_PREFIX +
                "('webview', {isNativeCallFinished: " +
                "'" +
                b +
                "'});";
        return builder;
    }

    public static String shake(boolean b) {
        String builder = LOOPME_PREFIX +
                "('webview', {shake: " +
                "'" +
                b +
                "'});";
        return builder;
    }

    public static String videoMute(boolean mute) {
        String builder = LOOPME_PREFIX +
                "('video', {mute: " +
                "'" +
                mute +
                "'});";
        return builder;
    }

    public static String videoCurrentTime(int time) {
        String builder = LOOPME_PREFIX +
                "('video', {currentTime: " +
                "'" +
                time +
                "'});";
        return builder;
    }

    public static String videoDuration(int time) {
        String builder = LOOPME_PREFIX +
                "('video', {duration: " +
                "'" +
                time +
                "'});";
        return builder;
    }

    public static String videoState(Constants.VideoState state) {
        String builder = LOOPME_PREFIX +
                "('video', {state: " +
                "'" +
                state.name() +
                "'});";
        return builder;
    }

    public static String webviewState(String sdkPrefix,Constants.WebviewState state) {
        String builder = sdkPrefix +
                "('webview', {state: " +
                "'" +
                state.name() +
                "'});";
        return builder;
    }

    public static String fullscreenMode(boolean b) {
        String builder = LOOPME_PREFIX +
                "('webview', {fullscreenMode: " +
                b +
                "});";
        return builder;
    }

    public static String event360(String event) {
        String builder = PREFIX_360 +
                "({eventType: 'INTERACTION', customEventName: 'video360&mode=" +
                event +
                "'});";
        return builder;
    }

    public static String mraidSetIsViewable(boolean isViewable) {
        String builder = MRAID_PREFIX +
                "setIsViewable(" +
                isViewable +
                ");";
        return builder;
    }

    public static String mraidNotifyReady() {
        String builder = MRAID_PREFIX +
                "notifyReadyEvent();";
        return builder;
    }

    public static String mraidNotifyError() {
        String builder = MRAID_PREFIX +
                "notifyErrorEvent();";
        return builder;
    }

    public static String mraidNotifyStateChange() {
        String builder = MRAID_PREFIX +
                "notifyStateChangeEvent();";
        return builder;
    }

    public static String mraidSetState(String state) {
        String builder = MRAID_PREFIX +
                "setState('" +
                state +
                "');";
        return builder;
    }

    public static String mraidNotifySizeChangeEvent(int width, int height) {
        String builder = MRAID_PREFIX +
                "notifySizeChangeEvent(" +
                width +
                ", " +
                height +
                ");";
        return builder;
    }

    public static String mraidResize() {
        String builder = MRAID_PREFIX +
                "resize();";
        return builder;
    }

    public static String mraidNativeCallComplete() {
        String builder = MRAID_PREFIX +
                "nativeCallComplete();";
        return builder;
    }
}

