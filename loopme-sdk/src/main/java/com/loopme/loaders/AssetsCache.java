package com.loopme.loaders;

import static com.loopme.debugging.Params.ERROR_EXCEPTION;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.ERROR_URL;

import android.content.Context;

import androidx.annotation.NonNull;

import com.loopme.Logging;
import com.loopme.models.Errors;
import com.loopme.common.LoopMeError;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.FileUtils;

import java.util.HashMap;

public class AssetsCache {

    private static final String LOG_TAG = AssetsCache.class.getSimpleName();

    public static void loadVideo(String videoUrl, Context context, @NonNull Listener listener) {
        FileUtils.startCaching(videoUrl, context, new FileUtils.Listener() {
            @Override
            public void onError(Exception e) { listener.onError(trackError(e, videoUrl)); }
            @Override
            public void onSuccess(String videoUrl) { listener.onAssetsLoaded(videoUrl, null); }
        });
    }
    public static void loadVideoWithEndcard(String videoUrl, @NonNull String endCardUrl, Context context, @NonNull Listener listener) {
        FileUtils.startCaching(videoUrl, context, new FileUtils.Listener() {
            @Override
            public void onError(Exception e) { listener.onError(trackError(e, videoUrl)); }
            @Override
            public void onSuccess(String videoUrl) { loadEndCard(videoUrl, endCardUrl, context, listener); }
        });
    }

    public static void loadEndCard(String videoUrl, String endCardUrl, Context context, @NonNull Listener listener) {
        FileUtils.startCaching(endCardUrl, context, new FileUtils.Listener() {
            @Override
            public void onError(Exception e) {
                trackError(e, endCardUrl);
                listener.onPostWarning(Errors.COMPANION_ERROR);
                listener.onAssetsLoaded(videoUrl, null);
            }
            @Override
            public void onSuccess(String endCardUrl) { listener.onAssetsLoaded(videoUrl, endCardUrl); }
        });
    }

    private static LoopMeError trackError(Exception e, String url) {
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
        return error;
    }

    public interface Listener {
        void onAssetsLoaded(String videoUrl, String endCardUrl);
        void onError(LoopMeError error);
        void onPostWarning(LoopMeError error);
    }
}
