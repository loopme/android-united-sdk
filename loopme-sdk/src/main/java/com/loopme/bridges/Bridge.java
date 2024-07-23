package com.loopme.bridges;

import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.ERROR_URL;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.views.AdView;
import com.loopme.views.webclient.WebViewClientCompat;

import java.util.HashMap;

public class Bridge extends WebViewClientCompat {

    private static final String LOG_TAG = Bridge.class.getSimpleName();

    private static final String LOOPME = "loopme";
    private static final String WEBVIEW = "webview";
    private static final String VIDEO = "video";

    private static final String WEBVIEW_CLOSE = "/close";
    private static final String WEBVIEW_FAIL = "/fail";
    private static final String WEBVIEW_SUCCESS = "/success";
    private static final String WEBVIEW_FULLSCREEN = "/fullscreenMode";

    private static final String VIDEO_LOAD = "/load";
    private static final String VIDEO_MUTE = "/mute";
    private static final String VIDEO_PLAY = "/play";
    private static final String VIDEO_PAUSE = "/pause";
    private static final String VIDEO_ENABLE_STRETCH = "/enableStretching";
    private static final String VIDEO_DISABLE_STRETCH = "/disableStretching";

    private static final String QUERY_PARAM_SRC = "src";
    private static final String QUERY_PARAM_CURRENT_TIME = "currentTime";
    private static final String QUERY_PARAM_MUTE = "mute";
    private static final String QUERY_PARAM_FULLSCREEN_MODE = "mode";

    private Listener mListener;
    private final com.loopme.listener.Listener adReadyListener;

    // TODO. Refactor.
    public Bridge(Listener listener, com.loopme.listener.Listener adReadyListener) {
        if (listener == null)
            Logging.out(LOG_TAG, "VideoBridgeListener should not be null");
        else
            mListener = listener;
        this.adReadyListener = adReadyListener;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        onJsLoadFail(description);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Logging.out(LOG_TAG, "onPageStarted");
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Logging.out(LOG_TAG, "onPageFinished");
        super.onPageFinished(view, url);
    }

    @Override
    public boolean shouldOverrideUrlLoadingCompat(WebView webView, String url) {
        Logging.out(LOG_TAG, "shouldOverrideUrlLoadingCompat " + url);
        Uri uri;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Broken redirect in bridge: ");
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.JS);
            errorInfo.put(ERROR_URL, url);
            LoopMeTracker.post(errorInfo);
            return true;
        }
        if (!LOOPME.equalsIgnoreCase(uri.getScheme())) {
            handleNonLoopMe(url);
            return true;
        }
        // TODO. Something old. Remove?
        ((AdView) webView).sendNativeCallFinished();
        String host = uri.getHost();
        if (WEBVIEW.equalsIgnoreCase(host)) {
            handleWebViewCommands(uri);
            return true;
        }
        if (VIDEO.equalsIgnoreCase(host)) {
            handleVideoCommands(uri);
            return true;
        }
        return true;
    }

    private void handleWebViewCommands(Uri uri) {
        String command = uri.getPath();
        if (TextUtils.isEmpty(command))
            return;
        switch (command) {
            case WEBVIEW_CLOSE:
                onJsClose();
                break;
            case WEBVIEW_FAIL:
                onJsLoadFail("Ad received specific URL loopme://webview/fail");
                break;
            case WEBVIEW_FULLSCREEN:
                handleFullscreenMode(uri);
                break;
            case WEBVIEW_SUCCESS:
                onJsLoadSuccess();
                break;
        }
    }

    private void handleVideoCommands(Uri uri) {
        String command = uri.getPath();
        if (TextUtils.isEmpty(command))
            return;
        switch (command) {
            case VIDEO_LOAD:
                handleVideoLoad(uri);
                break;
            case VIDEO_MUTE:
                handleVideoMute(uri);
                break;
            case VIDEO_PLAY:
                handleVideoPlay(uri);
                break;
            case VIDEO_PAUSE:
                handleVideoPause(uri);
                break;
            case VIDEO_ENABLE_STRETCH:
                onJsVideoStretch(true);
                break;
            case VIDEO_DISABLE_STRETCH:
                onJsVideoStretch(false);
                break;
        }
    }

    private void handleFullscreenMode(Uri uri) {
        try {
            String modeString = detectQueryParameter(uri, QUERY_PARAM_FULLSCREEN_MODE);
            if (!isValidBooleanParameter(modeString)) {
                HashMap<String, String> errorInfo = new HashMap<>();
                errorInfo.put(ERROR_MSG, "Empty parameter in js command: fullscreen mode");
                errorInfo.put(ERROR_URL, uri.toString());
                errorInfo.put(ERROR_TYPE, Constants.ErrorType.JS);
                LoopMeTracker.post(errorInfo);
            } else {
                onJsFullscreenMode(Boolean.parseBoolean(modeString));
            }
        } catch (NullPointerException | UnsupportedOperationException e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    private boolean isValidBooleanParameter(String param) {
        return !TextUtils.isEmpty(param) &&
            (param.equalsIgnoreCase(Boolean.TRUE.toString()) ||
            param.equalsIgnoreCase(Boolean.FALSE.toString()));
    }

    private void handleVideoPause(Uri uri) {
        String pauseString = detectQueryParameter(uri, QUERY_PARAM_CURRENT_TIME);
        onJsVideoPause(pauseString == null ? 0 : Integer.parseInt(pauseString));
    }

    private void handleVideoPlay(Uri uri) {
        String playString = detectQueryParameter(uri, QUERY_PARAM_CURRENT_TIME);
        onJsVideoPlay(playString == null ? 0 : Integer.parseInt(playString));
    }

    private void handleVideoMute(Uri uri) {
        String muteString = detectQueryParameter(uri, QUERY_PARAM_MUTE);
        if (isValidBooleanParameter(muteString)) {
            onJsVideoMute(Boolean.parseBoolean(muteString));
        } else {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Empty parameter in js command: mute");
            errorInfo.put(ERROR_URL, uri.toString());
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.JS);
            LoopMeTracker.post(errorInfo);
        }
    }

    private void handleVideoLoad(Uri uri) {
        String videoUrl = detectQueryParameter(uri, QUERY_PARAM_SRC);
        if (!TextUtils.isEmpty(videoUrl)) {
            onJsVideoLoad(videoUrl);
        } else {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Empty parameter in js command: src");
            errorInfo.put(ERROR_URL, uri.toString());
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.JS);
            LoopMeTracker.post(errorInfo);
        }
    }

    private String detectQueryParameter(Uri uri, String parameter) {
        String result = null;
        try {
            result = uri.getQueryParameter(parameter);
        } catch (NullPointerException | UnsupportedOperationException e) {
            Logging.out(LOG_TAG, e.toString());
        }
        return result;
    }

    private void onJsFullscreenMode(boolean mode) {
        if (mListener != null)
            mListener.onJsFullscreenMode(mode);
    }

    private void onJsVideoMute(boolean mute) {
        if (mListener != null)
            mListener.onJsVideoMute(mute);
    }

    private void onJsVideoPlay(int time) {
        if (mListener != null)
            mListener.onJsVideoPlay(time);
    }

    private void onJsVideoPause(int pauseTime) {
        if (mListener != null)
            mListener.onJsVideoPause(pauseTime);
    }

    private void onJsVideoLoad(String videoUrl) {
        if (mListener != null)
            mListener.onJsVideoLoad(videoUrl);
    }

    private void onJsLoadFail(String description) {
        if (mListener != null)
            mListener.onJsLoadFail("onReceivedError " + description);
    }

    private void onJsVideoStretch(boolean stretch) {
        if (mListener != null)
            mListener.onJsVideoStretch(stretch);
    }

    private void onJsLoadSuccess() {
        if (mListener != null)
            mListener.onJsLoadSuccess();

        if (adReadyListener != null)
            adReadyListener.onCall();
    }

    private void onJsClose() {
        if (mListener != null)
            mListener.onJsClose();
    }

    private void handleNonLoopMe(String url) {
        if (mListener != null)
            mListener.onNonLoopMe(url);
    }

    public interface Listener {
        void onJsClose();
        void onJsLoadSuccess();
        void onJsLoadFail(String mess);
        void onJsFullscreenMode(boolean isFullScreen);
        void onJsVideoLoad(String videoUrl);
        void onJsVideoMute(boolean mute);
        void onJsVideoPlay(int time);
        void onJsVideoPause(int time);
        void onJsVideoStretch(boolean b);
        void onNonLoopMe(String url);
    }
}