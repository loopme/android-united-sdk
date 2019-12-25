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

import java.net.URI;
import java.net.URISyntaxException;

public class MraidBridge extends WebViewClientCompat {

    private static final String LOG_TAG = MraidBridge.class.getSimpleName();

    private static final String LOOPME_SCHEME = "loopme";
    private static final String MRAID_SCHEME = "mraid";
    private static final String CUSTOM_HTTP_SCHEME = "http";

    private static final String QUERY_PARAMETER_URI = "uri";
    private static final String QUERY_PARAMETER_URL = "url";
    private static final String QUERY_PARAMETER_WIDTH = "width";
    private static final String QUERY_PARAMETER_HEIGHT = "height";
    private static final String QUERY_PARAMETER_OFFSET_X = "offsetX";
    private static final String QUERY_PARAMETER_OFFSET_Y = "offsetY";
    private static final String QUERY_PARAMETER_CUSTOM_CLOSE = "shouldUseCustomClose";
    private static final String QUERY_PARAMETER_CUSTOM_CLOSE_POSITION = "customClosePosition";
    private static final String ALLOW_OFF_SCREEN = "allowOffscreen";
    private static final String SET_ORIENTATION_PROPERTIES = "setOrientationProperties";

    private static final String QUERY_PARAMETER_ALLOW_ORIENTATION_CHANGE = "allowOrientationChange";
    private static final String QUERY_PARAMETER_FORCE_ORIENTATION = "forceOrientation";

    private static final String CLOSE = "close";
    private static final String OPEN = "open";
    private static final String PLAY_VIDEO = "playVideo";
    private static final String RESIZE = "resize";
    private static final String EXPAND = "expand";
    private static final String USE_CUSTOM_CLOSE = "usecustomclose";
    private static final String WEBVIEW = "webview";
    private static final String WEBVIEW_SUCCESS = "/success";
    private static final String WEBVIEW_CLOSE = "/close";
    private static final String WEBVIEW_FAIL = "/fail";
    private static final int START_URLS_INDEX = 17;

    private OnMraidBridgeListener mOnMraidBridgeListener;
    private Listener adReadyListener;
    private Uri mCommandUri;

    // TODO. Refactor.
    public MraidBridge(
            OnMraidBridgeListener onMraidBridgeListener,
            Listener adReadyListener) {

        mOnMraidBridgeListener = onMraidBridgeListener;
        this.adReadyListener = adReadyListener;
    }

    @Override
    public boolean shouldOverrideUrlLoadingCompat(WebView view, String url) {
        if (TextUtils.isEmpty(url)) {
            notifyError(view, "Broken redirect in mraid: " + url);
            return false;
        }
        try {
            URI redirect = new URI(url);
            String protocol = redirect.getScheme();
            if (TextUtils.isEmpty(protocol)) {
                return false;
            }
            if (protocol.equalsIgnoreCase(MRAID_SCHEME)) {
                String host = redirect.getHost();
                handleMraidCommand(host, url);
                return true;
            }

            if (protocol.equalsIgnoreCase(LOOPME_SCHEME)) {
                String path = redirect.getPath();
                handleLoopMeCommand(path, url);
                return true;
            }

            if (isHttpProtocol(protocol)) {
                onOpen(url);
                return true;
            }
        } catch (URISyntaxException e) {
            notifyError(view, "Broken redirect in bridge: " + url);
            return false;
        }
        return false;
    }

    @Override
    protected boolean canHandleCustomScheme(String scheme) {
        return scheme.equalsIgnoreCase(MRAID_SCHEME) ||
                scheme.equalsIgnoreCase(LOOPME_SCHEME);
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

    private void handleLoopMeCommand(String command, String url) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.onLoopMeCallComplete(url);
        }
        switch (command) {
            case WEBVIEW_FAIL: {
                onLoadFail(Errors.SPECIFIC_WEBVIEW_ERROR);
                break;
            }
            case WEBVIEW_SUCCESS: {
                onLoadSuccess();
                break;
            }
            case WEBVIEW_CLOSE: {
                onClose();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleMraidCommand(String command, String url) {
        mCommandUri = Uri.parse(url);
        switch (command) {
            case USE_CUSTOM_CLOSE: {
                handleUseCustomClose();
                break;
            }
            case EXPAND: {
                handleExpand();
                break;
            }
            case RESIZE: {
                handleResize();
                break;
            }
            case CLOSE: {
                handleClose();
                break;
            }
            case OPEN: {
                handleOpen(url);
                break;
            }
            case PLAY_VIDEO: {
                handlePlayVideo();
                break;
            }

            case SET_ORIENTATION_PROPERTIES: {
                handleOrientationProperties();
                break;
            }
            default:
                break;
        }
        onNativeCallCompleted(url);
    }

    private void handleOrientationProperties() {
        boolean allowOrientationChange = detectBooleanQueryParameter(mCommandUri, QUERY_PARAMETER_ALLOW_ORIENTATION_CHANGE);
        MraidOrientation forceOrientation = detectOrientation(QUERY_PARAMETER_FORCE_ORIENTATION);
        mOnMraidBridgeListener.setOrientationProperties(allowOrientationChange, forceOrientation);
    }

    private void onNativeCallCompleted(String command) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.onMraidCallComplete(command);
        }
    }

    private void onLoadFail(LoopMeError error) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.onLoadFail(error);
        }
    }

    private void handleClose() {
        onClose();
    }

    private void handlePlayVideo() {
        String videoUrl = detectQueryParameter(mCommandUri, QUERY_PARAMETER_URI);
        Logging.out(LOG_TAG, String.valueOf(videoUrl));
        onPlayVideo(videoUrl);
    }

    private void handleOpen(String url) {
        String openUrl = url.substring(START_URLS_INDEX);
        Logging.out(LOG_TAG, String.valueOf(openUrl));
        onOpen(openUrl);
    }

    private void handleExpand() {
        boolean useCustomClose = detectBooleanQueryParameter(mCommandUri, QUERY_PARAMETER_CUSTOM_CLOSE);
        onExpand(useCustomClose);
    }

    private void handleResize() {
        String widthString = detectQueryParameter(mCommandUri, QUERY_PARAMETER_WIDTH);
        int widthInteger = Integer.parseInt(widthString);
        String heightString = detectQueryParameter(mCommandUri, QUERY_PARAMETER_HEIGHT);
        int heightInteger = Integer.parseInt(heightString);
        onResize(Utils.convertDpToPixel(widthInteger), Utils.convertDpToPixel(heightInteger));
    }

    private void handleUseCustomClose() {
        boolean hasOwnCloseButton = detectBooleanQueryParameter(mCommandUri, QUERY_PARAMETER_CUSTOM_CLOSE);
        onCustomClose(hasOwnCloseButton);
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

    private boolean detectBooleanQueryParameter(Uri uri, String parameter) {
        String result = detectQueryParameter(uri, parameter);
        return Boolean.parseBoolean(result);
    }

    private void onClose() {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.close();
        }
    }

    private void onOpen(String openUrl) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.open(openUrl);
        }
    }

    private void onPlayVideo(String videoUrl) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.playVideo(videoUrl);
        }
    }

    private void onExpand(boolean useCustomClose) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.expand(useCustomClose);
        }
    }

    private void onResize(int width, int height) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.resize(width, height);
        }
    }

    private void onCustomClose(boolean hasOwnCloseButton) {
        if (mOnMraidBridgeListener != null) {
            mOnMraidBridgeListener.onChangeCloseButtonVisibility(hasOwnCloseButton);
        }
    }

    private void onLoadSuccess() {
        if (mOnMraidBridgeListener != null)
            mOnMraidBridgeListener.onLoadSuccess();

        if (adReadyListener != null)
            adReadyListener.onCall();
    }

    private String detectStringQueryParameter(String parameter) {
        return detectQueryParameter(mCommandUri, parameter);
    }

    private MraidOrientation detectOrientation(String parameter) {
        String orientation = detectStringQueryParameter(parameter);
        if (Constants.ORIENTATION_PORT.equals(orientation)) {
            return MraidOrientation.PORTRAIT;
        } else if (Constants.ORIENTATION_LAND.equals(orientation)) {
            return MraidOrientation.LANDSCAPE;
        } else {
            return MraidOrientation.NONE;
        }
    }

    private boolean isHttpProtocol(String protocol) {
        return TextUtils.equals(protocol, Constants.HTTP_PROTOCOL) || TextUtils.equals(protocol, Constants.HTTPS_SCHEME);
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
