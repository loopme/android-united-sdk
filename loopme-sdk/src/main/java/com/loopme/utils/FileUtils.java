package com.loopme.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.loopme.Constants;
import com.loopme.Logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by vynnykiakiv on 4/20/17.
 */

public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();
    private static final int ZERO = 0;

    private FileUtils() {
    }

    public static void deleteExpiredFiles(Context context) {
        Logging.out(LOG_TAG, "Delete expired files from cache");
        File externalFilesDirectory = getExternalFilesDir(context);
        if (externalFilesDirectory != null) {
            File[] files = externalFilesDirectory.listFiles();
            deleteAllFiles(files, true);
        }
    }

    public static String calculateChecksum(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(url.getBytes());
            return Utils.bytesToHex(md.digest());
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
            return null;
        }
    }

    public static File getExternalFilesDir(Context context) {
        return context == null ? null : context.getExternalFilesDir(Constants.VIDEO_FOLDER);
    }

    public static void clearCache(Context context) {
        Logging.out(LOG_TAG, "Clear cache");
        File externalFilesDirectory = getExternalFilesDir(context);
        if (externalFilesDirectory != null) {
            deleteAllFiles(externalFilesDirectory.listFiles(), false);
        }
    }

    private static int deleteAllFiles(File[] files, boolean deleteOnlyExpired) {
        if (files != null) {
            int deletedFilesCounter = 0;
            for (File file : files) {
                if (delete(file, deleteOnlyExpired)) {
                    deletedFilesCounter++;
                }
            }
            Logging.out(LOG_TAG, "Deleted " + deletedFilesCounter + " file(s)");
            return deletedFilesCounter;
        } else {
            return ZERO;
        }
    }

    private static boolean delete(File file, boolean deleteOnlyExpired) {
        if (isValidFile(file)) {
            if (deleteOnlyExpired) {
                if (isFileExpired(file)) {
                    return deleteFile(file);
                }
            } else {
                return deleteFile(file);
            }
        }
        return false;
    }

    private static boolean deleteFile(File file) {
        if (file != null) {
            Logging.out(LOG_TAG, "Deleted cached file: " + file.getAbsolutePath());
            return file.delete();
        } else {
            return false;
        }
    }

    private static boolean isFileExpired(File file) {
        long creationTime = file.lastModified();
        long currentTime = System.currentTimeMillis();
        return creationTime + Constants.CACHED_VIDEO_LIFE_TIME < currentTime;
    }

    public static File checkIfFileExists(String filename, Context context) {
        File parentDir = getExternalFilesDir(context);
        if (parentDir == null)
            return null;

        Logging.out(LOG_TAG, "Cache dir: " + parentDir.getAbsolutePath());

        File[] files = parentDir.listFiles();
        if (files == null)
            return null;

        for (File file : files) {
            if (isValidFile(file) && file.getName().equalsIgnoreCase(filename))
                return file;
        }

        return null;
    }

    private static boolean isValidFile(File file) {
        return file != null && !file.isDirectory();
    }

    public static String getFileName(String fileUrl) {
        if (!TextUtils.isEmpty(fileUrl)) {
            String[] components = fileUrl.split("/");
            return components[components.length - 1];
        } else {
            return "";
        }
    }
}
