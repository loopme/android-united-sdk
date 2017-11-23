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
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('webview', {isNativeCallFinished: ")
                .append("'")
                .append(b)
                .append("'});");
        return builder.toString();
    }

    public static String shake(boolean b) {
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('webview', {shake: ")
                .append("'")
                .append(b)
                .append("'});");
        return builder.toString();
    }

    public static String videoMute(boolean mute) {
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('video', {mute: ")
                .append("'")
                .append(mute)
                .append("'});");
        return builder.toString();
    }

    public static String videoCurrentTime(int time) {
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('video', {currentTime: ")
                .append("'")
                .append(time)
                .append("'});");
        return builder.toString();
    }

    public static String videoDuration(int time) {
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('video', {duration: ")
                .append("'")
                .append(time)
                .append("'});");
        return builder.toString();
    }

    public static String videoState(Constants.VideoState state) {
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('video', {state: ")
                .append("'")
                .append(state.name())
                .append("'});");
        return builder.toString();
    }

    public static String webviewState(String sdkPrefix,Constants.WebviewState state) {
        StringBuilder builder = new StringBuilder();
        builder.append(sdkPrefix)
                .append("('webview', {state: ")
                .append("'")
                .append(state.name())
                .append("'});");
        return builder.toString();
    }

    public static String fullscreenMode(boolean b) {
        StringBuilder builder = new StringBuilder();
        builder.append(LOOPME_PREFIX)
                .append("('webview', {fullscreenMode: ")
                .append(b)
                .append("});");
        return builder.toString();
    }

    public static String event360(String event) {
        StringBuilder builder = new StringBuilder();
        builder.append(PREFIX_360)
                .append("({eventType: 'INTERACTION', customEventName: 'video360&mode=")
                .append(event)
                .append("'});");
        return builder.toString();
    }

    public static String mraidSetIsViewable(boolean isViewable) {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("setIsViewable(")
                .append(isViewable)
                .append(");");
        return builder.toString();
    }

    public static String mraidNotifyReady() {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("notifyReadyEvent();");
        return builder.toString();
    }

    public static String mraidNotifyError() {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("notifyErrorEvent();");
        return builder.toString();
    }

    public static String mraidNotifyStateChange() {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("notifyStateChangeEvent();");
        return builder.toString();
    }

    public static String mraidSetState(String state) {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("setState('")
                .append(state)
                .append("');");
        return builder.toString();
    }

    public static String mraidNotifySizeChangeEvent(int width, int height) {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("notifySizeChangeEvent(")
                .append(width)
                .append(",")
                .append(height)
                .append(");");
        return builder.toString();
    }

    public static String mraidResize() {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("resize();");
        return builder.toString();
    }

    public static String nativeCallComplete() {
        StringBuilder builder = new StringBuilder();
        builder.append(MRAID_PREFIX)
                .append("mraidbridge.nativeCallComplete()");
        return builder.toString();
    }
}

