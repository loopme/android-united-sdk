package com.loopme.views;

import android.content.Context;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.bridges.BridgeCommandBuilder;
import com.loopme.bridges.mraid.MraidBridge;
import com.loopme.controllers.MraidController;


public class MraidView extends LoopMeWebView {

    private static final String LOG_TAG = MraidView.class.getSimpleName();
    private String mCurrentMraidState;

    public MraidView(Context context, final MraidController mraidController) {
        super(context);
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        mraidController.setMraidView(this);
        setWebViewClient(new MraidBridge(mraidController));
        setDefaultWebChromeClient();
    }

    public void loadData(String html) {
        loadDataWithBaseURL(Constants.MRAID_ANDROID_ASSET, html, Constants.MIME_TYPE_TEXT_HTML, Constants.UTF_8, null);
    }

    public void setIsViewable(boolean isViewable) {
        String command = BridgeCommandBuilder.mraidSetIsViewable(isViewable);
        Logging.out(LOG_TAG, "setIsViewable " + isViewable);
        loadCommand(command);
    }

    public void notifyReady() {
        String command = BridgeCommandBuilder.mraidNotifyReady();
        Logging.out(LOG_TAG, "notifyReady");
        loadCommand(command);
    }

    public void notifyError() {
        String command = BridgeCommandBuilder.mraidNotifyError();
        Logging.out(LOG_TAG, "notifyError");
        loadCommand(command);
    }

    public void notifyStateChange() {
        String command = BridgeCommandBuilder.mraidNotifyStateChange();
        Logging.out(LOG_TAG, "state changed");
        loadCommand(command);
    }

    public void setState(String state) {
        if (!TextUtils.equals(mCurrentMraidState, state)) {
            String command = BridgeCommandBuilder.mraidSetState(state);
            mCurrentMraidState = state;
            Logging.out(LOG_TAG, "setState " + state);
            loadCommand(command);
        }
    }

    public void notifySizeChangeEvent(int width, int height) {
        String command = BridgeCommandBuilder.mraidNotifySizeChangeEvent(width, height);
        Logging.out(LOG_TAG, "notifySizeChangeEvent");
        loadCommand(command);
    }

    public void resize() {
        String command = BridgeCommandBuilder.mraidResize();
        Logging.out(LOG_TAG, "resize " + command);
        loadCommand(command);
    }

    public boolean isExpanded() {
        return TextUtils.equals(mCurrentMraidState, Constants.MraidState.EXPANDED);
    }

    public void onMraidCallComplete(String completedCommand) {
        String command = BridgeCommandBuilder.mraidNativeCallComplete();
        loadCommand(command);
        Logging.out(LOG_TAG, "onMraidCallComplete " + completedCommand);
    }

    public void onLoopMeCallComplete(String completedCommand) {
        String command = BridgeCommandBuilder.isNativeCallFinished(true);
        loadCommand(command);
        Logging.out(LOG_TAG, "onLoopMeCallComplete " + completedCommand);
    }

    public boolean isResized() {
        return TextUtils.equals(mCurrentMraidState, Constants.MraidState.RESIZED);
    }
}
