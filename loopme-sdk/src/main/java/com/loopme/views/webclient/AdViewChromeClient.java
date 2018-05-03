package com.loopme.views.webclient;

import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

public class AdViewChromeClient extends WebChromeClient {
    private OnErrorFromJsCallback mCallback;
    private String mPrevErrorMessage = "";
    private static final String UNCAUGHT_ERROR = "Uncaught";
    private static final String VIDEO_SOURCE = "VIDEO_SOURCE";

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
            onErrorFromJs(consoleMessage.message() + ". Source: " + consoleMessage.sourceId());
        }
        if (isVideoSourceEvent(consoleMessage.message())) {
            onVideoSource(Utils.getSourceUrl(consoleMessage.message()));
        }
        return super.onConsoleMessage(consoleMessage);
    }

    private boolean isVideoSourceEvent(String message) {
        String[] tokens = message.split(":");
        return TextUtils.equals(tokens[0], VIDEO_SOURCE);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    private void onErrorFromJs(String message) {
        if (mCallback != null && message != null && message.contains(UNCAUGHT_ERROR) && isNewError(message)) {
            mCallback.onErrorFromJs(message);
        } else if (mCallback == null) {
            LoopMeTracker.post("Error from js console: " + message, Constants.ErrorType.JS);
        }
    }

    private boolean isNewError(String newErrorMessage) {
        if ((!TextUtils.equals(newErrorMessage, mPrevErrorMessage))) {
            mPrevErrorMessage = newErrorMessage;
            return true;
        } else {
            return false;
        }
    }

    private void onVideoSource(String source) {
        if (mCallback != null && mCallback instanceof OnErrorFromJsCallbackVpaid) {
            ((OnErrorFromJsCallbackVpaid) mCallback).onVideoSource(source);
        }
    }

    public interface OnErrorFromJsCallback {
        void onErrorFromJs(String message);

    }

    public interface OnErrorFromJsCallbackVpaid extends OnErrorFromJsCallback {
        void onVideoSource(String source);
    }
}
