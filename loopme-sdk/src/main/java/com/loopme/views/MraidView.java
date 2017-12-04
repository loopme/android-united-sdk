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
    private String mCurrentAdState;

    public MraidView(Context context, MraidController mraidController) {
        super(context);
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        mraidController.setMraidView(this);
        setWebViewClient(new MraidBridge(mraidController, this));
        Logging.out(LOG_TAG, "Encoding: " + getSettings().getDefaultTextEncodingName());
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
        if (!TextUtils.equals(mCurrentAdState, state)) {
            String command = BridgeCommandBuilder.mraidSetState(state);
            mCurrentAdState = state;
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

    public void setWebViewState(Constants.WebviewState state) {
        super.setWebViewState(BridgeCommandBuilder.LOOPME_PREFIX, state);
    }

    public boolean isExpanded() {
        return TextUtils.equals(mCurrentAdState, Constants.MraidState.EXPANDED);
    }

    public void onNativeCallComplete(String command) {
        loadCommand(command);
        Logging.out(LOG_TAG, "onNativeCallComplete " + command);
    }

}
