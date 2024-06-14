package com.loopme.views;

import android.content.Context;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.bridges.BridgeCommandBuilder;
import com.loopme.bridges.mraid.MraidBridge;
import com.loopme.controllers.MraidController;
import com.loopme.listener.Listener;


public class MraidView extends LoopMeWebView {

    private String mCurrentMraidState;

    public MraidView(
        Context context, final MraidController mraidController, Listener adReadyListener
    ) {
        super(context);
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        mraidController.setMraidView(this);
        setWebViewClient(new MraidBridge(mraidController, adReadyListener));
        setDefaultWebChromeClient();
    }

    @Override
    public void loadHtml(String html) {
        loadDataWithBaseURL(
            Constants.MRAID_ANDROID_ASSET,
            html,
            Constants.MIME_TYPE_TEXT_HTML,
            Constants.UTF_8,
            null
        );
    }

    public void setIsViewable(boolean isViewable) {
        loadCommand(BridgeCommandBuilder.mraidSetIsViewable(isViewable));
    }

    public void notifyReady() {
        loadCommand(BridgeCommandBuilder.mraidNotifyReady());
    }

    public void notifyError() {
        loadCommand(BridgeCommandBuilder.mraidNotifyError());
    }

    public void notifyStateChange() {
        loadCommand(BridgeCommandBuilder.mraidNotifyStateChange());
    }

    public void notifySizeChangeEvent(int width, int height) {
        loadCommand(BridgeCommandBuilder.mraidNotifySizeChangeEvent(width, height));
    }

    public void onMraidCallComplete(String completedCommand) {
        loadCommand(BridgeCommandBuilder.mraidNativeCallComplete());
    }

    public void onLoopMeCallComplete(String completedCommand) {
        loadCommand(BridgeCommandBuilder.isNativeCallFinished(true));
    }

    public void setState(String state) {
        if (!TextUtils.equals(mCurrentMraidState, state)) {
            mCurrentMraidState = state;
            loadCommand(BridgeCommandBuilder.mraidSetState(state));
        }
    }

    public boolean isExpanded() {
        return TextUtils.equals(mCurrentMraidState, Constants.MraidState.EXPANDED);
    }

    public boolean isResized() {
        return TextUtils.equals(mCurrentMraidState, Constants.MraidState.RESIZED);
    }
}
