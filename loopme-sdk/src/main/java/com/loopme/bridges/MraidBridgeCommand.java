package com.loopme.bridges;

import android.net.Uri;

import com.loopme.Logging;
import com.loopme.utils.Utils;

final class MraidBridgeCommand {
    private static final String LOG_TAG = MraidBridgeCommand.class.getSimpleName();
    static final String SET_ORIENTATION_PROPERTIES = "setOrientationProperties";
    static final String CLOSE = "close";
    static final String OPEN = "open";
    static final String PLAY_VIDEO = "playVideo";
    static final String RESIZE = "resize";
    static final String EXPAND = "expand";
    static final String USE_CUSTOM_CLOSE = "usecustomclose";

    static void handleCommand(MraidBridgeListener mMraidBridgeListener, Uri uri, String command) {
        boolean useCustomClose = false;
        if (MraidBridgeCommand.USE_CUSTOM_CLOSE.equals(command)) {
            mMraidBridgeListener.onChangeCloseButtonVisibility(useCustomClose);
        }
        // TODO: temporary disable expandable banners
//        if (EXPAND.equals(command)) {
//            mOnMraidBridgeListener.expand(useCustomClose);
//        }
        if (MraidBridgeCommand.RESIZE.equals(command)) {
            mMraidBridgeListener.resize(
                    Utils.convertDpToPixel(Integer.parseInt(BridgeQuery.detect(uri, BridgeQuery.WIDTH))),
                    Utils.convertDpToPixel(Integer.parseInt(BridgeQuery.detect(uri, BridgeQuery.HEIGHT)))
            );
        }
        if (MraidBridgeCommand.CLOSE.equals(command)) {
            mMraidBridgeListener.close();
        }
        if (MraidBridgeCommand.OPEN.equals(command)) {
            mMraidBridgeListener.open(BridgeQuery.detect(uri, BridgeQuery.URL));
        }
        if (MraidBridgeCommand.PLAY_VIDEO.equals(command)) {
            String videoUrl = BridgeQuery.detect(uri, BridgeQuery.URI);
            Logging.out(LOG_TAG, videoUrl);
            mMraidBridgeListener.playVideo(videoUrl);
        }
        if (MraidBridgeCommand.SET_ORIENTATION_PROPERTIES.equals(command)) {
            boolean isAllowOrientationChange = Boolean
                    .parseBoolean(BridgeQuery.detect(uri, BridgeQuery.ALLOW_ORIENTATION_CHANGE));
            mMraidBridgeListener.setOrientationProperties(isAllowOrientationChange, BridgeQuery.detectOrientation(uri));
        }
        mMraidBridgeListener.onMraidCallComplete(uri.toString());
    }
}
