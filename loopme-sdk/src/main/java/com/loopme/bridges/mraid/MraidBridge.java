package com.loopme.bridges.mraid;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.MraidOrientation;
import com.loopme.common.LoopMeError;
import com.loopme.listener.Listener;
import com.loopme.models.Errors;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;
import com.loopme.views.MraidView;
import com.loopme.views.webclient.WebViewClientCompat;

public class MraidBridge extends WebViewClientCompat {

    private static final String LOG_TAG = MraidBridge.class.getSimpleName();

    private static final String LOOPME_SCHEME = "loopme";
    private static final String MRAID_SCHEME = "mraid";

    private static final String QUERY_PARAMETER_URL = "url";
    private static final String QUERY_PARAMETER_URI = "uri";
    private static final String QUERY_PARAMETER_WIDTH = "width";
    private static final String QUERY_PARAMETER_HEIGHT = "height";
    private static final String QUERY_PARAMETER_CUSTOM_CLOSE = "shouldUseCustomClose";
    private static final String SET_ORIENTATION_PROPERTIES = "setOrientationProperties";

    private static final String QUERY_PARAMETER_ALLOW_ORIENTATION_CHANGE = "allowOrientationChange";
    private static final String QUERY_PARAMETER_FORCE_ORIENTATION = "forceOrientation";

    private static final String CLOSE = "close";
    private static final String OPEN = "open";
    private static final String PLAY_VIDEO = "playVideo";
    private static final String RESIZE = "resize";
    private static final String EXPAND = "expand";
    private static final String USE_CUSTOM_CLOSE = "usecustomclose";
    private static final String WEBVIEW_SUCCESS = "/success";
    private static final String WEBVIEW_CLOSE = "/close";
    private static final String WEBVIEW_FAIL = "/fail";

    private OnMraidBridgeListener mOnMraidBridgeListener;
    private Listener adReadyListener;

    // TODO. Refactor.
    public MraidBridge(
            OnMraidBridgeListener onMraidBridgeListener,
            Listener adReadyListener) {

        mOnMraidBridgeListener = onMraidBridgeListener;
        this.adReadyListener = adReadyListener;
    }

    @Override
    public boolean shouldOverrideUrlLoadingCompat(WebView webView, String url) {
        Uri uri;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            notifyError(webView, "Broken redirect in bridge: " + url);
            return true;
        }

        String scheme = uri.getScheme();

        if (MRAID_SCHEME.equalsIgnoreCase(scheme)) {
            handleMraidCommand(uri);
            return true;
        }

        if (LOOPME_SCHEME.equalsIgnoreCase(scheme)) {
            handleLoopMeCommand(uri);
            return true;
        }

        onOpen(url);
        return true;
    }

    private void notifyError(View view, String errorMessage) {
        LoopMeTracker.post(errorMessage);
        ((MraidView) view).notifyError();
        Logging.out(LOG_TAG, errorMessage);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        ((MraidView) view).notifyError();
    }

    private void handleLoopMeCommand(Uri uri) {
        String command = uri.getPath();
        if (TextUtils.isEmpty(command))
            return;

        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.onLoopMeCallComplete(uri.toString());

        switch (command) {
            case WEBVIEW_FAIL:
                onLoadFail(Errors.SPECIFIC_WEBVIEW_ERROR);
                break;

            case WEBVIEW_SUCCESS:
                onLoadSuccess();
                break;

            case WEBVIEW_CLOSE:
                onClose();
                break;
        }
    }

    private void handleMraidCommand(Uri uri) {

        String command = uri.getHost();
        if (TextUtils.isEmpty(command))
            return;

        switch (command) {
            case USE_CUSTOM_CLOSE:
                handleUseCustomClose(uri);
                break;

            case EXPAND:
                handleExpand(uri);
                break;

            case RESIZE:
                handleResize(uri);
                break;

            case CLOSE:
                handleClose();
                break;

            case OPEN:
                onOpen(detectQueryParameter(uri, QUERY_PARAMETER_URL));
                break;

            case PLAY_VIDEO:
                handlePlayVideo(uri);
                break;

            case SET_ORIENTATION_PROPERTIES:
                handleOrientationProperties(uri);
                break;
        }

        onNativeCallCompleted(uri.toString());
    }

    private void handleOrientationProperties(Uri uri) {
        boolean allowOrientationChange = detectBooleanQueryParameter(
                uri,
                QUERY_PARAMETER_ALLOW_ORIENTATION_CHANGE);

        MraidOrientation forceOrientation = detectOrientation(uri, QUERY_PARAMETER_FORCE_ORIENTATION);
        mOnMraidBridgeListener.setOrientationProperties(allowOrientationChange, forceOrientation);
    }

    // TODO. Something old. Remove?
    private void onNativeCallCompleted(String command) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.onMraidCallComplete(command);
    }

    private void onLoadFail(LoopMeError error) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.onLoadFail(error);
    }

    private void handleClose() {
        onClose();
    }

    private void handlePlayVideo(Uri uri) {
        String videoUrl = detectQueryParameter(uri, QUERY_PARAMETER_URI);
        Logging.out(LOG_TAG, videoUrl);
        onPlayVideo(videoUrl);
    }

    private void handleExpand(Uri uri) {
        boolean useCustomClose = detectBooleanQueryParameter(uri, QUERY_PARAMETER_CUSTOM_CLOSE);
        onExpand(useCustomClose);
    }

    private void handleResize(Uri uri) {
        String widthString = detectQueryParameter(uri, QUERY_PARAMETER_WIDTH);
        int widthInteger = Integer.parseInt(widthString);
        String heightString = detectQueryParameter(uri, QUERY_PARAMETER_HEIGHT);
        int heightInteger = Integer.parseInt(heightString);
        onResize(Utils.convertDpToPixel(widthInteger), Utils.convertDpToPixel(heightInteger));
    }

    private void handleUseCustomClose(Uri uri) {
        boolean hasOwnCloseButton = detectBooleanQueryParameter(uri, QUERY_PARAMETER_CUSTOM_CLOSE);
        onCustomClose(hasOwnCloseButton);
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

    private boolean detectBooleanQueryParameter(Uri uri, String parameter) {
        String result = detectQueryParameter(uri, parameter);
        return Boolean.parseBoolean(result);
    }

    private void onClose() {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.close();
    }

    private void onOpen(String openUrl) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.open(openUrl);
    }

    private void onPlayVideo(String videoUrl) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.playVideo(videoUrl);
    }

    private void onExpand(boolean useCustomClose) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.expand(useCustomClose);
    }

    private void onResize(int width, int height) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.resize(width, height);
    }

    private void onCustomClose(boolean hasOwnCloseButton) {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.onChangeCloseButtonVisibility(hasOwnCloseButton);
    }

    private void onLoadSuccess() {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.onLoadSuccess();

        if (adReadyListener != null)
            adReadyListener.onCall();
    }

    private String detectStringQueryParameter(Uri uri, String parameter) {
        return detectQueryParameter(uri, parameter);
    }

    private MraidOrientation detectOrientation(Uri uri, String parameter) {
        String orientation = detectStringQueryParameter(uri, parameter);
        if (Constants.ORIENTATION_PORT.equals(orientation))
            return MraidOrientation.PORTRAIT;
        else if (Constants.ORIENTATION_LAND.equals(orientation))
            return MraidOrientation.LANDSCAPE;
        else
            return MraidOrientation.NONE;
    }

    public interface OnMraidBridgeListener {

        void close();

        void open(String url);

        void resize(int width, int height);

        void playVideo(String videoUrl);

        void expand(boolean useCustomClose);

        void onLoadSuccess();

        void onLoadFail(LoopMeError error);

        void onChangeCloseButtonVisibility(boolean hasOwnCloseButton);

        void onMraidCallComplete(String command);

        void onLoopMeCallComplete(String command);

        void setOrientationProperties(boolean allowOrientationChange, MraidOrientation forceOrientation);
    }
}