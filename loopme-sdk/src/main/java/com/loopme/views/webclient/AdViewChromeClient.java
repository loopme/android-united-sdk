package com.loopme.views.webclient;

import static android.webkit.ConsoleMessage.MessageLevel;
import static android.webkit.ConsoleMessage.MessageLevel.ERROR;
import static android.webkit.ConsoleMessage.MessageLevel.WARNING;
import static com.loopme.debugging.Params.CID;
import static com.loopme.debugging.Params.CRID;
import static com.loopme.debugging.Params.ERROR_CONSOLE;
import static com.loopme.debugging.Params.ERROR_CONSOLE_LEVEL;
import static com.loopme.debugging.Params.ERROR_CONSOLE_SOURCE_ID;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.REQUEST_ID;

import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.debugging.Params;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

public class AdViewChromeClient extends WebChromeClient {
    private static final String LOG_TAG = AdViewChromeClient.class.getSimpleName();

    private final OnErrorFromJsCallback mCallback;
    private final LoopMeAd mLoopMeAd;
    private String mPrevErrorMessage = "";

    private PermissionResolver permissionResolveListener;
    public void setPermissionResolveListener(PermissionResolver listener) {
        permissionResolveListener = listener;
    }

    private PermissionRequest permissionRequest;

    public AdViewChromeClient(@NonNull OnErrorFromJsCallback callback,  @NonNull LoopMeAd loopMeAd) {
        mCallback = callback;
        mLoopMeAd = loopMeAd;
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (permissionResolveListener == null) {
            request.deny();
            return;
        }
        permissionRequest = request;
        permissionResolveListener.onRequestGeneralPermissions(request.getResources());
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        permissionRequest = null;
        if (permissionResolveListener != null) {
            permissionResolveListener.onCancelGeneralPermissionsRequest();
        }
    }

    public void setGeneralPermissionsResponse(@NonNull String[] grantedPermissions) {
        if (permissionRequest == null) return;
        if (grantedPermissions.length == 0) {
            permissionRequest.deny();
        } else {
            permissionRequest.grant(grantedPermissions);
        }
        permissionRequest = null;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        String message = consoleMessage.message();
        MessageLevel messageLevel = consoleMessage.messageLevel();
        if (messageLevel == ERROR || messageLevel == WARNING) {
            Logging.out(LOG_TAG, "Console Message: " + message + " " + consoleMessage.sourceId());
        }
        if (messageLevel == ERROR && message.contains("Uncaught") && !TextUtils.equals(message, mPrevErrorMessage)) {
            mPrevErrorMessage = message;
            mCallback.onErrorFromJs(message);
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Error from js console: ");
            errorInfo.put(ERROR_CONSOLE, message);
            errorInfo.put(ERROR_CONSOLE_SOURCE_ID, consoleMessage.sourceId());
            errorInfo.put(ERROR_CONSOLE_LEVEL, messageLevel.toString());
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.JS);
            errorInfo.put(CID, mLoopMeAd.getCurrentCid());
            errorInfo.put(CRID, mLoopMeAd.getCurrentCrid());
            errorInfo.put(REQUEST_ID, mLoopMeAd.getRequestId());

            LoopMeTracker.post(errorInfo);
        }

        // TODO: Find a better way to pass video source from HTML to Player for VPAID ads.
        // VIDEO_SOURCE: https://example.com/video.mp4
        // Used in ./assets/loopmeAd.html on video 'canplay' event with console.log
        // video.addEventListener('canplay', function(event) {
        //     console.log('VIDEO_SOURCE: ' + video.src);
        // }, false);
        if (mCallback instanceof OnErrorFromJsCallbackVpaid) {
            String[] tokens = message.split(":");
            if (tokens.length >= 3 && TextUtils.equals(tokens[0], "VIDEO_SOURCE")) {
                ((OnErrorFromJsCallbackVpaid) mCallback).onVideoSource(tokens[tokens.length - 1]);
            }
        }
        return super.onConsoleMessage(consoleMessage);
    }

    public interface OnErrorFromJsCallback {
        void onErrorFromJs(String message);
    }

    public interface OnErrorFromJsCallbackVpaid extends OnErrorFromJsCallback {
        void onVideoSource(String source);
    }

    public interface PermissionResolver {
        void onRequestGeneralPermissions(String[] permissions);
        void onCancelGeneralPermissionsRequest();
    }
}