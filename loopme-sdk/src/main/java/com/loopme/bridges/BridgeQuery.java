package com.loopme.bridges;

import android.net.Uri;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.MraidOrientation;

public final class BridgeQuery {
    public static final String LOG_TAG = BridgeQuery.class.getSimpleName();

    // MRAID FIELDS
    public static final String URL = "url";
    public static final String URI = "uri";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String ALLOW_ORIENTATION_CHANGE = "allowOrientationChange";
    public static final String FORCE_ORIENTATION = "forceOrientation";

    public static String detect(Uri uri, String parameter) {
        String result = null;
        try {
            result = uri.getQueryParameter(parameter);
        } catch (NullPointerException | UnsupportedOperationException e) {
            Logging.out(LOG_TAG, e.toString());
        }
        return result;
    }

    public static MraidOrientation detectOrientation(Uri uri) {
        String orientation = detect(uri, FORCE_ORIENTATION);
        if (Constants.ORIENTATION_PORT.equals(orientation))
            return MraidOrientation.PORTRAIT;
        if (Constants.ORIENTATION_LAND.equals(orientation))
            return MraidOrientation.LANDSCAPE;
        return MraidOrientation.NONE;
    }
}
