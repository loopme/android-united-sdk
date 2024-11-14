package com.loopme.tracker;

import static com.loopme.Constants.ERROR_URL;
import static com.loopme.debugging.Params.BUFFER_COUNT;
import static com.loopme.debugging.Params.DURATION;
import static com.loopme.debugging.Params.DURATION_AVG;
import static com.loopme.debugging.Params.ERROR_EXCEPTION;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.MEDIA_URL;
import static com.loopme.debugging.Params.SESSION_ID;

import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.SessionManager;
import com.loopme.utils.VideoSessionManager;

import java.util.HashMap;

public class MediaPlayerTracker {
    private static final String LOG_TAG = MediaPlayerTracker.class.getSimpleName();

    public static void trackCacheError(Exception e, String url) {
        LoopMeError error = Errors.CACHE_ERROR;
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(ERROR_MSG, error.getMessage());
        errorInfo.put(ERROR_TYPE, error.getErrorType());
        errorInfo.put(ERROR_URL, url);
        if (e != null) {
            Logging.out(LOG_TAG, "Exception: " + e.getMessage());
            errorInfo.put(ERROR_EXCEPTION, e.getMessage());
        }
        LoopMeTracker.post(errorInfo);
    }

    public static void trackBufferingStats(VideoSessionManager videoSessionManager) {
        long totalBufferingTime = videoSessionManager.getTotalBufferingTime();
        long averageBufferingTime = videoSessionManager.getAverageBufferingTime();
        int bufferCount = videoSessionManager.getBufferCount();
        String mediaUrl = videoSessionManager.getMediaUrl();

        LoopMeError bufferingEvent = new LoopMeError(Errors.VIDEO_BUFFERING_AVERAGE)
                .addParam(DURATION, String.valueOf(totalBufferingTime))
                .addParam(DURATION_AVG, String.valueOf(averageBufferingTime))
                .addParam(BUFFER_COUNT, String.valueOf(bufferCount))
                .addParam(MEDIA_URL, mediaUrl)
                .addParam(SESSION_ID, SessionManager.getInstance().getSessionId());

        LoopMeTracker.post(bufferingEvent);
    }
}
