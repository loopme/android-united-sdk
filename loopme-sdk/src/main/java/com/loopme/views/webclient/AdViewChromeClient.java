package com.loopme.views.webclient;

import android.Manifest;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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

    public void setPermissionResolveListener(PermissionResolver permissionResolveListener) {
        onPermissionRequestCanceled(null);
        onGeolocationPermissionsHidePrompt();
        this.permissionResolveListener = permissionResolveListener;
    }

    private PermissionResolver permissionResolveListener;

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        onPermissionRequestCanceled(null);

        if (permissionResolveListener == null) {
            request.deny();
            return;
        }

        String[] androidPermissions = toAndroidPermissions(request.getResources());
        if (androidPermissions == null || androidPermissions.length == 0) {
            request.deny();
            return;
        }

        permissionRequest = request;

        permissionResolveListener.onRequestGeneralPermissions(androidPermissions);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        permissionRequest = null;

        if (permissionResolveListener != null)
            permissionResolveListener.onCancelGeneralPermissionsRequest();
    }

    public void setGeneralPermissionsResponse(String[] grantedAndroidPermissions) {
        if (permissionRequest == null)
            return;

        String[] webkitPermissions = toWebkitPermissions(grantedAndroidPermissions);
        if (webkitPermissions == null || webkitPermissions.length == 0)
            permissionRequest.deny();
        else
            permissionRequest.grant(webkitPermissions);

        permissionRequest = null;
    }

    private PermissionRequest permissionRequest;

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        onGeolocationPermissionsHidePrompt();

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

        if (permissionResolveListener != null)
            permissionResolveListener.onCancelLocationPermissionRequest();
    }

    public void setLocationPermissionGranted(boolean granted) {
        if (locationPermissionOrigin == null || geolocationPermissionsCallback == null)
            return;

        geolocationPermissionsCallback.invoke(locationPermissionOrigin, granted, false);

        locationPermissionOrigin = null;
        geolocationPermissionsCallback = null;
    }

    private String locationPermissionOrigin;
    private GeolocationPermissions.Callback geolocationPermissionsCallback;

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

    private static String[] toAndroidPermissions(String[] webkitPermissions) {
        if (webkitPermissions == null || webkitPermissions.length == 0)
            return null;

        List<String> result = new ArrayList<>();
        for (String wkPermission : webkitPermissions) {
            switch (wkPermission) {
                case PermissionRequest.RESOURCE_AUDIO_CAPTURE:
                    result.add(Manifest.permission.RECORD_AUDIO);
                    break;
                case PermissionRequest.RESOURCE_VIDEO_CAPTURE:
                    result.add(Manifest.permission.CAMERA);
                    break;
            }
        }

        return result.toArray(new String[0]);
    }

    private static String[] toWebkitPermissions(String[] androidPermissions) {
        if (androidPermissions == null || androidPermissions.length == 0)
            return null;

        List<String> result = new ArrayList<>();
        for (String androidPermission : androidPermissions) {
            switch (androidPermission) {
                case Manifest.permission.RECORD_AUDIO:
                    result.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE);
                    break;

                case Manifest.permission.CAMERA:
                    result.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE);
                    break;
            }
        }

        return result.toArray(new String[0]);
    }

    public interface PermissionResolver {
        void onRequestGeneralPermissions(String[] androidPermissions);

        void onCancelGeneralPermissionsRequest();

        void onRequestLocationPermission(String origin);

        void onCancelLocationPermissionRequest();
    }
}