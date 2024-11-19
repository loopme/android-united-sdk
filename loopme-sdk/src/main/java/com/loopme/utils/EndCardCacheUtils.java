package com.loopme.utils;

import android.content.Context;
import com.loopme.tracker.MediaPlayerTracker;
import java.io.File;

public class EndCardCacheUtils {

    public static void cacheEndCard(String endCardUrl, Context context) {
            FileUtils.startCaching(endCardUrl, context, new FileUtils.Listener() {
                @Override
                public void onError(Exception e) {
                    MediaPlayerTracker.trackCacheError(e, endCardUrl);
                }

                @Override
                public void onSuccess(String cachedEndCardUrl) {
                }
            });
    }

    public static String getCachedPath(String endCardUrl, Context context) {
        File cachedFile = FileUtils.getCachedFile(endCardUrl, context);
        if (cachedFile != null && cachedFile.exists()) {
            return cachedFile.getAbsolutePath();
        }
        return null;
    }
}