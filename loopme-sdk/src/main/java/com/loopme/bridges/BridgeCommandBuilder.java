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

    public static String webviewState(Constants.WebviewState state) {
        return withLog(LOOPME_PREFIX + "('webview', {state: '" + state.name() + "'});");
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

