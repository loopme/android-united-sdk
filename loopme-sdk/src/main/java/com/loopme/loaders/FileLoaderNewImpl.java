package com.loopme.loaders;

import static com.loopme.debugging.Params.ERROR_EXCEPTION;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.ERROR_URL;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.network.HttpUtils;
import com.loopme.utils.FileUtils;
import com.loopme.utils.ExecutorHelper;
import com.loopme.tracker.partners.LoopMeTracker;

import java.io.File;
import java.util.HashMap;

public class FileLoaderNewImpl {

    private static final String LOG_TAG = FileLoaderNewImpl.class.getSimpleName();

    private final String fileUrl;
    private String destFilePath;

    private Callback callback;
    private final Context context;

    private final Handler handler;

    public FileLoaderNewImpl(@NonNull String fileUrl, @NonNull Context context, @NonNull Callback callback) {
        this.fileUrl = fileUrl;
        this.context = context;
        this.callback = callback;
        handler = new Handler(Looper.getMainLooper());
        FileUtils.deleteExpiredFiles(this.context);
    }

    public void start() {
        Logging.out(LOG_TAG, "start()");
        Logging.out(LOG_TAG, "Use mobile network for caching: " + Constants.USE_MOBILE_NETWORK_FOR_CACHING);
        if (!HttpUtils.isOnline(context)) return;
        if (!HttpUtils.isWifiConnection(context) && !Constants.USE_MOBILE_NETWORK_FOR_CACHING) {
            onErrorTracking(Errors.MOBILE_NETWORK_ERROR, null, fileUrl);
            return;
        }

        String filename = FileUtils.getCachedFileName(fileUrl);
        File externalFilesDir = FileUtils.getExternalFilesDir(context);
        if (externalFilesDir == null) return;
        destFilePath = externalFilesDir.getAbsolutePath() + "/" + filename;

        if (FileUtils.checkIfFileExists(filename, context) != null) {
            handler.post(() -> {
                if (callback != null) callback.onFileFullLoaded(destFilePath);
            });
            return;
        }
        ExecutorHelper.getExecutor().submit(() -> {
            long startLoadingTime = System.currentTimeMillis();
            HttpUtils.cache(fileUrl, destFilePath, new HttpUtils.CacheListener() {
                @Override
                public void onError(Exception e) {
                    onErrorTracking(Errors.CACHE_ERROR, e, fileUrl);
                }
                @Override
                public void onSuccess() {
                    handler.post(() -> {
                        if (callback != null) callback.onFileFullLoaded(destFilePath);
                    });
                    Logging.out(LOG_TAG, "Asset successfully loaded (" + (System.currentTimeMillis() - startLoadingTime) + "ms)");
                }
            });
        });
    }

    public void stop() {
        callback = null;
    }

    private void onErrorTracking(final LoopMeError error, final Exception exception, final String fileUrl) {
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(ERROR_MSG, error.getMessage());
        errorInfo.put(ERROR_TYPE, error.getErrorType());
        errorInfo.put(ERROR_URL, fileUrl);
        if (exception != null) {
            Logging.out(LOG_TAG, "Exception: " + exception.getMessage());
            errorInfo.put(ERROR_EXCEPTION, exception.getMessage());
        }
        LoopMeTracker.post(errorInfo);
        handler.post(() -> {
            if (callback != null) callback.onError(error);
        });
    }

    public interface Callback {
        void onError(LoopMeError error);
        void onFileFullLoaded(String filePath);
    }
}
