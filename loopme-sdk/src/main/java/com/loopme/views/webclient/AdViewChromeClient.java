package com.loopme.views.webclient;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;

public class AdViewChromeClient extends WebChromeClient {
    private OnErrorFromJsCallback mCallback;
    private static final String UNCAUGHT_ERROR = "Uncaught";

    public AdViewChromeClient() {
    }

    public AdViewChromeClient(OnErrorFromJsCallback callback) {
        this.mCallback = callback;
    }

    private static final String LOG_TAG = AdViewChromeClient.class.getSimpleName();

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR ||
                consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.WARNING) {
            Logging.out(LOG_TAG, "Console Message: " + consoleMessage.message() + " " + consoleMessage.sourceId());
        }
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            LoopMeTracker.post("Error from js console: " + consoleMessage.message() + " " + consoleMessage.sourceId(), Constants.ErrorType.JS);
            onErrorFromJs(consoleMessage.message() + " " + consoleMessage.sourceId());
        }
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    private void onErrorFromJs(String message) {
        if (mCallback != null && message != null && message.contains(UNCAUGHT_ERROR)) {
            mCallback.onErrorFromJs(message);
        }
    }

    public interface OnErrorFromJsCallback {
        void onErrorFromJs(String message);
    }
}
