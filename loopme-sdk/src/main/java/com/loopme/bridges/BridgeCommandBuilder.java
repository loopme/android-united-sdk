package com.loopme.bridges;

import com.loopme.Constants;
import com.loopme.Logging;

/**
 * Class helper to build LoopMe javascript bridge commands
 */
public class BridgeCommandBuilder {
    private static final String LOG_TAG = BridgeCommandBuilder.class.getSimpleName();
    private BridgeCommandBuilder() { }
    public static final String MRAID_PREFIX = "javascript:window.mraidbridge && mraidbridge.";
    public static final String LOOPME_PREFIX = "javascript:window.L && window.L.bridge && window.L.bridge.set";

    private static String withLog(String command) {
        Logging.out(LOG_TAG, command);
        return command;
    }

    public static String isNativeCallFinished(boolean b) {
        return withLog(LOOPME_PREFIX + "('webview', {isNativeCallFinished: '" + b + "'});");
    }
    public static String shake(boolean b) {
        return withLog(LOOPME_PREFIX + "('webview', {shake: '" + b + "'});");
    }
    public static String videoMute(boolean mute) {
        return withLog(LOOPME_PREFIX + "('video', {mute: '" + mute + "'});");
    }

    public static String videoCurrentTime(int time) {
        return withLog(LOOPME_PREFIX + "('video', {currentTime: '" + time + "'});");
    }

    public static String videoDuration(int time) {
        return withLog(LOOPME_PREFIX + "('video', {duration: '" + time + "'});");
    }

    public static String videoState(Constants.VideoState state) {
        return withLog(LOOPME_PREFIX + "('video', {state: '" + state.name() + "'});");
    }

    public static String webviewState(String sdkPrefix, Constants.WebviewState state) {
        return withLog(sdkPrefix + "('webview', {state: '" + state.name() + "'});");
    }

    public static String fullscreenMode(boolean b) {
        return withLog(LOOPME_PREFIX + "('webview', {fullscreenMode: " + b + "});");
    }

    public static String mraidSetIsViewable(boolean isViewable) {
        return withLog(MRAID_PREFIX + "setIsViewable(" + isViewable + ");");
    }

    public static String mraidNotifyReady() {
        return withLog(MRAID_PREFIX + "notifyReadyEvent();");
    }

    public static String mraidNotifyError() {
        return withLog(MRAID_PREFIX + "notifyErrorEvent();");
    }

    public static String mraidNotifyStateChange() {
        return withLog(MRAID_PREFIX + "notifyStateChangeEvent();");
    }

    public static String mraidSetState(String state) {
        return withLog(MRAID_PREFIX + "setState('" + state + "');");
    }

    public static String mraidNotifySizeChangeEvent(int width, int height) {
        return withLog(MRAID_PREFIX + "notifySizeChangeEvent(" + width + ", " + height + ");");
    }

    public static String mraidResize() {
        return withLog(MRAID_PREFIX + "resize();");
    }

    public static String mraidNativeCallComplete() {
        return withLog(MRAID_PREFIX + "nativeCallComplete();");
    }
}

