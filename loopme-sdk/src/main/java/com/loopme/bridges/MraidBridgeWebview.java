package com.loopme.bridges;

import android.net.Uri;

import com.loopme.listener.AdReadyListener;
import com.loopme.models.Errors;

final class MraidBridgeWebview {
    private static final String LOG_TAG = MraidBridgeWebview.class.getSimpleName();
    static final String SUCCESS = "/success";
    static final String CLOSE = "/close";
    static final String FAIL = "/fail";

    static void handleCommands(MraidBridgeListener mMraidBridgeListener, AdReadyListener adReadyListener, Uri uri, String command) {
        if (MraidBridgeWebview.FAIL.equals(command)) {
            mMraidBridgeListener.onLoadFail(Errors.SPECIFIC_WEBVIEW_ERROR);
        }
        if (MraidBridgeWebview.SUCCESS.equals(command)) {
            mMraidBridgeListener.onLoadSuccess();
            adReadyListener.onCall();
        }
        if (MraidBridgeWebview.CLOSE.equals(command)) {
            mMraidBridgeListener.close();
        }
        mMraidBridgeListener.onLoopMeCallComplete(uri.toString());
    }
}
