package com.loopme.bridges;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;
import com.loopme.views.AdView;

import java.net.URI;
import java.net.URISyntaxException;


public class Bridge extends WebViewClient {

    private static final String LOG_TAG = Bridge.class.getSimpleName();

    private static final String LOOPME = "loopme";
    private static final String WEBVIEW = "webview";
    private static final String VIDEO = "video";

    private static final String WEBVIEW_CLOSE = "/close";
    private static final String WEBVIEW_FAIL = "/fail";
    private static final String WEBVIEW_SUCCESS = "/success";
    private static final String WEBVIEW_VIBRATE = "/vibrate";
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
    private Context mContext;

    public Bridge(Listener listener, Context context) {
        if (listener != null) {
            mListener = listener;
            mContext = context;
        } else {
            Logging.out(LOG_TAG, "VideoBridgeListener should not be null");
        }
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
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Logging.out(LOG_TAG, "shouldOverrideUrlLoading " + url);

        if (TextUtils.isEmpty(url)) {
            LoopMeTracker.post("Broken redirect in bridge: " + url, Constants.ErrorType.JS);
            return false;
        }

        Context context = view.getContext();
        URI redirect;
        try {
            redirect = new URI(url);
        } catch (URISyntaxException e) {
            Logging.out(LOG_TAG, e.getMessage());
            e.printStackTrace();
            LoopMeTracker.post("Broken redirect in bridge: " + url, Constants.ErrorType.JS);
            handleExtraUrl(url);
            return false;
        }

        String protocol = redirect.getScheme();
        if (TextUtils.isEmpty(protocol)) {
            return false;
        }

        if (protocol.equalsIgnoreCase(LOOPME)) {
            ((AdView) view).sendNativeCallFinished();
            String host = redirect.getHost();
            String path = redirect.getPath();
            if (TextUtils.isEmpty(host) || TextUtils.isEmpty(path)) {
                return false;
            }
            if (host.equalsIgnoreCase(WEBVIEW)) {
                handleWebviewCommands(path, url, context);
            } else if (host.equalsIgnoreCase(VIDEO)) {
                handleVideoCommands(path, url);
            }
        } else {
            handleNonLoopMe(url);
        }

        return true;
    }

    private void handleExtraUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (startActivity(intent)) {
            onLeaveApp();
        }
    }

    private boolean startActivity(Intent intent) {
        try {
            if (mContext != null) {
                mContext.startActivity(intent);
                return true;
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        return false;

    }

    private void onLeaveApp() {
        if(mListener != null){
            mListener.onLeaveApp();
        }
    }

    private void handleWebviewCommands(String command, String url, Context context) {
        if (TextUtils.isEmpty(command) || mListener == null) {
            return;
        }
        switch (command) {
            case WEBVIEW_CLOSE: {
                onJsClose();
                break;
            }
            case WEBVIEW_VIBRATE: {
                handleVibrate(context);
                break;
            }
            case WEBVIEW_FAIL: {
                onJsLoadFail("Ad received specific URL loopme://webview/fail");
                break;
            }
            case WEBVIEW_FULLSCREEN: {
                handleFullscreenMode(url);
                break;
            }
            case WEBVIEW_SUCCESS: {
                onJsLoadSuccess();
                break;
            }
            default:
                break;
        }
    }

    private void handleVideoCommands(String command, String url) {
        if (command == null || mListener == null) {
            return;
        }
        Uri uri;
        try {
            uri = Uri.parse(url);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        switch (command) {
            case VIDEO_LOAD: {
                handleVideoLoad(uri);
                break;
            }
            case VIDEO_MUTE: {
                handleVideoMute(uri);
                break;
            }
            case VIDEO_PLAY: {
                handleVideoPlay(uri);
                break;
            }
            case VIDEO_PAUSE:
                handleVideoPause(uri);
                break;

            case VIDEO_ENABLE_STRETCH:
                onJsVideoStretch(true);
                break;

            case VIDEO_DISABLE_STRETCH:
                onJsVideoStretch(false);
                break;

            default:
                break;
        }
    }

    private void handleFullscreenMode(String url) {
        try {
            Uri uri = Uri.parse(url);
            String modeString = detectQueryParameter(uri, QUERY_PARAM_FULLSCREEN_MODE);
            if (!isValidBooleanParameter(modeString)) {
                LoopMeTracker.post("Empty parameter in js command: fullscreen mode", Constants.ErrorType.JS);
            } else {
                onJsFullscreenMode(Boolean.parseBoolean(modeString));
            }
        } catch (NullPointerException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidBooleanParameter(String param) {
        return !TextUtils.isEmpty(param) &&
                (param.equalsIgnoreCase(Boolean.TRUE.toString()) ||
                        param.equalsIgnoreCase(Boolean.FALSE.toString()));
    }

    private void handleVibrate(Context context) {
        Utils.vibrate(context);
    }

    private void handleVideoPause(Uri uri) {
        String pauseString = detectQueryParameter(uri, QUERY_PARAM_CURRENT_TIME);
        int pauseTime = 0;
        if (pauseString != null) {
            pauseTime = Integer.parseInt(pauseString);
        }
        onJsVideoPause(pauseTime);
    }

    private void handleVideoPlay(Uri uri) {
        String playString = detectQueryParameter(uri, QUERY_PARAM_CURRENT_TIME);
        int playTime = 0;
        if (playString != null) {
            playTime = Integer.parseInt(playString);
        }
        onJsVideoPlay(playTime);

    }

    private void handleVideoMute(Uri uri) {
        String muteString = detectQueryParameter(uri, QUERY_PARAM_MUTE);
        if (isValidBooleanParameter(muteString)) {
            onJsVideoMute(Boolean.parseBoolean(muteString));
        } else {
            LoopMeTracker.post("Empty parameter in js command: mute", Constants.ErrorType.JS);
        }
    }

    private void handleVideoLoad(Uri uri) {
        String videoUrl = detectQueryParameter(uri, QUERY_PARAM_SRC);
        if (!TextUtils.isEmpty(videoUrl)) {
            onJsVideoLoad(videoUrl);
        } else {
            LoopMeTracker.post("Empty parameter in js command: src", Constants.ErrorType.JS);
        }
    }

    private String detectQueryParameter(Uri uri, String parameter) {
        String result = null;
        try {
            result = uri.getQueryParameter(parameter);
        } catch (NullPointerException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void onJsFullscreenMode(boolean mode) {
        if(mListener != null){
            mListener.onJsFullscreenMode(mode);
        }
    }

    private void onJsVideoMute(boolean mute) {
        if (mListener != null) {
            mListener.onJsVideoMute(mute);
        }
    }

    private void onJsVideoPlay(int time) {
        if (mListener != null) {
            mListener.onJsVideoPlay(time);
        }
    }

    private void onJsVideoPause(int pauseTime) {
        if(mListener != null) {
            mListener.onJsVideoPause(pauseTime);
        }
    }

    private void onJsVideoLoad(String videoUrl) {
        if (mListener != null) {
            mListener.onJsVideoLoad(videoUrl);
        }
    }

    private void onJsLoadFail(String description) {
        if (mListener != null) {
            mListener.onJsLoadFail("onReceivedError " + description);
        }
    }

    private void onJsVideoStretch(boolean stretch) {
        if (mListener != null) {
            mListener.onJsVideoStretch(stretch);
        }
    }

    private void onJsLoadSuccess() {
        if(mListener != null){
            mListener.onJsLoadSuccess();
        }
    }

    private void onJsClose() {
        if(mListener != null){
            mListener.onJsClose();
        }
    }

    private void handleNonLoopMe(String url) {
        if (mListener != null) {
            mListener.onNonLoopMe(url);
        }
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

        void onLeaveApp();
    }

}
