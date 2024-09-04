package com.loopme.bridges;

import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_URL;

import android.net.Uri;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.loopme.Logging;
import com.loopme.listener.AdReadyListener;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.views.MraidView;
import com.loopme.views.webclient.WebViewClientCompat;

import java.util.HashMap;

final class PROTOCOL {
    static final String DATA = "data";
    static final String LOOPME = "loopme";
    static final String MRAID = "mraid";
}

public class MraidBridge extends WebViewClientCompat {

    private static final String LOG_TAG = MraidBridge.class.getSimpleName();
    private final MraidBridgeListener mMraidBridgeListener;
    private final AdReadyListener adReadyListener;

    public boolean userClicked = false;

    public MraidBridge(@NonNull MraidBridgeListener mraidBridgeListener, @NonNull AdReadyListener readyListener) {
        mMraidBridgeListener = mraidBridgeListener;
        adReadyListener = readyListener;
    }

    @Override
    public boolean shouldOverrideUrlLoadingCompat(WebView webView, String url) {
        Uri uri;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            String errorMessage = "Broken redirect in bridge: ";
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, errorMessage);
            errorInfo.put(ERROR_URL, url);
            LoopMeTracker.post(errorInfo);
            Logging.out(LOG_TAG, errorMessage);
            ((MraidView) webView).notifyError();
            return true;
        }
        String scheme = uri.getScheme();
        // Do not handle data scheme or if listener is null
        if (PROTOCOL.DATA.equalsIgnoreCase(scheme)) {
            return false;
        }
        if (PROTOCOL.MRAID.equalsIgnoreCase(scheme)) {
            String command = uri.getHost();
            MraidBridgeCommand.handleCommand(mMraidBridgeListener, uri, command, userClicked);
            return true;
        }
        // loopme://webview/close
        // new URI("loopme://webview/close").getScheme() -> loopme
        // new URI("loopme://webview/close").getHost() -> webview
        // new URI("loopme://webview/close").getPath() -> /close
        if (PROTOCOL.LOOPME.equalsIgnoreCase(scheme)) {
            String command = uri.getPath();
            MraidBridgeWebview.handleCommands(mMraidBridgeListener, adReadyListener, uri, command);
            return true;
        }

        mMraidBridgeListener.open(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mMraidBridgeListener.onLoadSuccess();
        adReadyListener.onCall();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        ((MraidView) view).notifyError();
    }
}