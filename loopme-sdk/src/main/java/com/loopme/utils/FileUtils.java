package com.loopme.utils;

import static com.loopme.debugging.Params.ERROR_EXCEPTION;
import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.ERROR_URL;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.network.HttpUtils;
import com.loopme.tracker.partners.LoopMeTracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * Created by vynnykiakiv on 4/20/17.
 */

public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    private FileUtils() { }

    public static void deleteCachedFiles(Context context, boolean deleteOnlyExpired) {
        Logging.out(LOG_TAG, "Delete expired files from cache");
        File externalFilesDirectory = getExternalFilesDir(context);
        if (externalFilesDirectory == null) return;
        File[] files = externalFilesDirectory.listFiles();
        if (files == null) return;
        int deletedFilesCounter = 0;
        for (File file : files) {
            if (file == null || file.isDirectory()) continue;
            String currentFilePath = file.getAbsolutePath();
            long creationTime = file.lastModified();
            long currentTime = System.currentTimeMillis();
            boolean isFileExpired = creationTime + Constants.CACHED_VIDEO_LIFE_TIME < currentTime;
            boolean isDeleted = (!deleteOnlyExpired || isFileExpired) && file.delete();
            if (isDeleted) {
                Logging.out(LOG_TAG, "Deleted cached file: " + currentFilePath);
                deletedFilesCounter++;
            }
        }
        Logging.out(LOG_TAG, "Deleted " + deletedFilesCounter + " file(s)");
    }

    public static String bytesToHex(byte[] bytes) {
        char[] HEX = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX[v >>> 4];
            hexChars[j * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getCachedFileName(String fileUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(fileUrl.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(md.digest());
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.getMessage());
            return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        }
    }

    @Nullable
    public static File getExternalFilesDir(Context context) {
        return context == null ? null : context.getExternalFilesDir(Constants.VIDEO_FOLDER);
    }

    public static File getCachedFile(String fileUrl, Context context) {
        String filename = FileUtils.getCachedFileName(fileUrl);
        File externalFilesDirectory = getExternalFilesDir(context);
        if (externalFilesDirectory == null) return null;
        Logging.out(LOG_TAG, "Cache dir: " + externalFilesDirectory.getAbsolutePath());

        File[] files = externalFilesDirectory.listFiles();
        if (files == null) return null;

        for (File file : files) {
            if (file != null && !file.isDirectory() && filename.equalsIgnoreCase(file.getName()))
                return file;
        }
        return null;
    }

    public static String getDestinationPath(String fileUrl, Context context) {
        String filename = FileUtils.getCachedFileName(fileUrl);
        File externalFilesDir = FileUtils.getExternalFilesDir(context);
        if (externalFilesDir == null) return null;
        return externalFilesDir.getAbsolutePath() + "/" + filename;
    }

    public static String loadAssetFileAsString(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void startCaching(@NonNull String fileUrl, @NonNull Context context, @NonNull Listener listener) {
        deleteCachedFiles(context, true);
        Handler handler = new Handler(Looper.getMainLooper());

        File destinationFile = getCachedFile(fileUrl, context);
        if (destinationFile != null) {
            String destinationPath = destinationFile.getAbsolutePath();
            handler.post(() -> listener.onSuccess(destinationPath));
            return;
        }
        String destinationPath = getDestinationPath(fileUrl, context);
        if (destinationPath == null) {
            handler.post(() -> listener.onError(new IOException("Can't reach cache directory")));
            return;
        }
        ExecutorHelper.getExecutor().submit(() -> {
            long startLoadingTime = System.currentTimeMillis();
            HttpUtils.cache(context, fileUrl, destinationPath, new HttpUtils.CacheListener() {
                @Override
                public void onError(Exception e) {
                    handler.post(() -> listener.onError(e));
                }
                @Override
                public void onSuccess() {
                    handler.post(() -> listener.onSuccess(destinationPath));
                    Logging.out(LOG_TAG, "Asset " + fileUrl + " successfully cached (" + (System.currentTimeMillis() - startLoadingTime) + "ms)");
                }
            });
        });
    }

    public interface Listener {
        void onError(Exception e);
        void onSuccess(String filePath);
    }
}
