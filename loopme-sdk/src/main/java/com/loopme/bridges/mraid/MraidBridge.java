package com.loopme.bridges.mraid;

import static com.loopme.debugging.Params.CID;
import static com.loopme.debugging.Params.CRID;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_URL;
import static com.loopme.debugging.Params.REQUEST_ID;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.loopme.BidManager;
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

import java.util.HashMap;

public class MraidBridge extends WebViewClientCompat {

    private static final String LOG_TAG = MraidBridge.class.getSimpleName();

    private static final String DATA_SCHEME = "data";
    private static final String LOOPME_SCHEME = "loopme";
    private static final String MRAID_SCHEME = "mraid";
    private static final String QUERY_PARAMETER_URL = "url";
    private static final String QUERY_PARAMETER_URI = "uri";
    private static final String QUERY_PARAMETER_WIDTH = "width";
    private static final String QUERY_PARAMETER_HEIGHT = "height";
    private static final String QUERY_PARAMETER_ALLOW_ORIENTATION_CHANGE = "allowOrientationChange";
    private static final String QUERY_PARAMETER_FORCE_ORIENTATION = "forceOrientation";
    private static final String SET_ORIENTATION_PROPERTIES = "setOrientationProperties";
    private static final String CLOSE = "close";
    private static final String OPEN = "open";
    private static final String PLAY_VIDEO = "playVideo";
    private static final String RESIZE = "resize";
    private static final String EXPAND = "expand";
    private static final String USE_CUSTOM_CLOSE = "usecustomclose";
    private static final String WEBVIEW_SUCCESS = "/success";
    private static final String WEBVIEW_CLOSE = "/close";
    private static final String WEBVIEW_FAIL = "/fail";

    private final OnMraidBridgeListener mOnMraidBridgeListener;
    private final Listener adReadyListener;

    // TODO. Refactor.
    public MraidBridge(OnMraidBridgeListener onMraidBridgeListener, Listener adReadyListener) {
        mOnMraidBridgeListener = onMraidBridgeListener;
        this.adReadyListener = adReadyListener;
    }

    private String detectQueryParameter(Uri uri, String parameter) {
        try {
            return uri.getQueryParameter(parameter);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
            return null;
        }
    }

    private MraidOrientation detectOrientation(Uri uri) {
        String orientation = detectQueryParameter(uri, QUERY_PARAMETER_FORCE_ORIENTATION);
        if (Constants.ORIENTATION_PORT.equals(orientation))
            return MraidOrientation.PORTRAIT;
        if (Constants.ORIENTATION_LAND.equals(orientation))
            return MraidOrientation.LANDSCAPE;
        return MraidOrientation.NONE;
    }

    private void handleCommand(Uri uri, String command) {
        boolean useCustomClose = false;
        if (WEBVIEW_FAIL.equals(command)) {
            mOnMraidBridgeListener.onLoadFail(Errors.SPECIFIC_WEBVIEW_ERROR);
        }
        if (WEBVIEW_SUCCESS.equals(command)) {
            mOnMraidBridgeListener.onLoadSuccess();
            if (adReadyListener != null)
                adReadyListener.onCall();
        }
        if (USE_CUSTOM_CLOSE.equals(command)) {
            mOnMraidBridgeListener.onChangeCloseButtonVisibility(useCustomClose);
        }
        // TODO: temporary disable expandable banners
//        if (EXPAND.equals(command)) {
//            mOnMraidBridgeListener.expand(useCustomClose);
//        }
        if (RESIZE.equals(command)) {
            mOnMraidBridgeListener.resize(
                Utils.convertDpToPixel(Integer.parseInt(detectQueryParameter(uri, QUERY_PARAMETER_WIDTH))),
                Utils.convertDpToPixel(Integer.parseInt(detectQueryParameter(uri, QUERY_PARAMETER_HEIGHT)))
            );
        }
        if (WEBVIEW_CLOSE.equals(command)) {
            mOnMraidBridgeListener.close();
        }
        if (CLOSE.equals(command)) {
            mOnMraidBridgeListener.close();
        }
        if (OPEN.equals(command)) {
            mOnMraidBridgeListener.open(detectQueryParameter(uri, QUERY_PARAMETER_URL));
        }
        if (PLAY_VIDEO.equals(command)) {
            String videoUrl = detectQueryParameter(uri, QUERY_PARAMETER_URI);
            Logging.out(LOG_TAG, videoUrl);
            mOnMraidBridgeListener.playVideo(videoUrl);
        }
        if (SET_ORIENTATION_PROPERTIES.equals(command)) {
            boolean isAllowOrientationChange = Boolean
                .parseBoolean(detectQueryParameter(uri, QUERY_PARAMETER_ALLOW_ORIENTATION_CHANGE));
            mOnMraidBridgeListener.setOrientationProperties(isAllowOrientationChange, detectOrientation(uri));
        }
        mOnMraidBridgeListener.onMraidCallComplete(uri.toString());
        mOnMraidBridgeListener.onLoopMeCallComplete(uri.toString());
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
            errorInfo.put(REQUEST_ID, BidManager.getInstance().getRequestId());
            errorInfo.put(CID, BidManager.getInstance().getCurrentCid());
            errorInfo.put(CRID, BidManager.getInstance().getCurrentCrid());
            LoopMeTracker.post(errorInfo);
            Logging.out(LOG_TAG, errorMessage);
            ((MraidView) webView).notifyError();
            return true;
        }
        String scheme = uri.getScheme();
        // Do not handle data scheme or if listener is null
        if (mOnMraidBridgeListener == null || DATA_SCHEME.equalsIgnoreCase(scheme)) {
            return false;
        }
        String command = "";
        if (MRAID_SCHEME.equalsIgnoreCase(scheme)) {
            command = uri.getHost();
        }
        if (LOOPME_SCHEME.equalsIgnoreCase(scheme)) {
            command = uri.getPath();
        }
        if (!TextUtils.isEmpty(command)) {
            handleCommand(uri, command);
            return true;
        }
        mOnMraidBridgeListener.open(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mOnMraidBridgeListener.onLoadSuccess();
        if (adReadyListener != null)
            adReadyListener.onCall();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        ((MraidView) view).notifyError();
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