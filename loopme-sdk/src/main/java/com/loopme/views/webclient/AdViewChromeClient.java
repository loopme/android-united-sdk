package com.loopme.views.webclient;

import static com.loopme.debugging.Params.ERROR_CONSOLE;
import static com.loopme.debugging.Params.ERROR_CONSOLE_LEVEL;
import static com.loopme.debugging.Params.ERROR_CONSOLE_SOURCE_ID;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;

import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.HashMap;

public class AdViewChromeClient extends WebChromeClient {
    private static final String LOG_TAG = AdViewChromeClient.class.getSimpleName();
    private static final String UNCAUGHT_ERROR = "Uncaught";
    private static final String VIDEO_SOURCE = "VIDEO_SOURCE";

    private OnErrorFromJsCallback mCallback;
    private String mPrevErrorMessage = "";

    private PermissionResolver permissionResolveListener;
    private PermissionRequest permissionRequest;

    private String locationPermissionOrigin;
    private GeolocationPermissions.Callback geolocationPermissionsCallback;

    public AdViewChromeClient() { }

    public AdViewChromeClient(OnErrorFromJsCallback callback) {
        this.mCallback = callback;
    }

    public void setPermissionResolveListener(PermissionResolver permissionResolveListener) {
        this.permissionResolveListener = permissionResolveListener;
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

    public void setGeneralPermissionsResponse(String[] grantedPermissions) {
        if (permissionRequest == null) return;

        if (grantedPermissions == null || grantedPermissions.length == 0) {
            permissionRequest.deny();
        } else {
            permissionRequest.grant(grantedPermissions);
        }

        permissionRequest = null;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (permissionResolveListener == null) {
            callback.invoke(origin, false, false);
            return;
        }

        locationPermissionOrigin = origin;
        geolocationPermissionsCallback = callback;

        permissionResolveListener.onRequestLocationPermission(origin);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        locationPermissionOrigin = null;
        geolocationPermissionsCallback = null;

        if (permissionResolveListener != null) {
            permissionResolveListener.onCancelLocationPermissionRequest();
        }
    }

    public void setLocationPermissionGranted(boolean granted) {
        if (locationPermissionOrigin == null || geolocationPermissionsCallback == null) return;

        geolocationPermissionsCallback.invoke(locationPermissionOrigin, granted, false);

        locationPermissionOrigin = null;
        geolocationPermissionsCallback = null;
    }

    private String getSourceUrl(String message) {
        String result = "";
        if (message != null) {
            String[] tokens = message.split(":");
            if (tokens.length >= 3) {
                return tokens[tokens.length - 1];
            }
        }
        return result;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR ||
                consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.WARNING) {
            Logging.out(LOG_TAG, "Console Message: " + consoleMessage.message() + " " + consoleMessage.sourceId());
        }
        if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            onErrorFromJs(consoleMessage);
        }
        if (isVideoSourceEvent(consoleMessage.message())) {
            onVideoSource(getSourceUrl(consoleMessage.message()));
        }
        return super.onConsoleMessage(consoleMessage);
    }

    private boolean isVideoSourceEvent(String message) {
        String[] tokens = message.split(":");
        return TextUtils.equals(tokens[0], VIDEO_SOURCE);
    }

    private void onErrorFromJs(@NonNull ConsoleMessage message) {
        if (mCallback != null && message.message().contains(UNCAUGHT_ERROR) && isNewError(message.message())) {
            mCallback.onErrorFromJs(message.message());
        } else if (mCallback == null) {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "Error from js console: ");
            errorInfo.put(ERROR_CONSOLE, message.message());
            errorInfo.put(ERROR_CONSOLE_SOURCE_ID, message.sourceId());
            errorInfo.put(ERROR_CONSOLE_LEVEL, message.messageLevel().toString());
            errorInfo.put(ERROR_TYPE, Constants.ErrorType.JS);
            LoopMeTracker.post(errorInfo);
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

    public interface PermissionResolver {
        void onRequestGeneralPermissions(String[] permissions);
        void onCancelGeneralPermissionsRequest();
        void onRequestLocationPermission(String origin);
        void onCancelLocationPermissionRequest();
    }
}