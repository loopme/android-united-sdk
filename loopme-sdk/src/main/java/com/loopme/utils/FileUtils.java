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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vynnykiakiv on 4/20/17.
 */

public class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();
    private static final String NEW_LINE = "\n";
    private static final int ZERO = 0;
    private static final int MAX_FILE_NAME_LENGTH = 127 - 4;


    private FileUtils() {
    }

    public static void logToFile(String message, boolean append) {
        if (isExternalStorageAvailable() && !isExternalStorageReadOnly()) {
            File file = getCachedLogFile();
            try (FileWriter writer = new FileWriter(file, append)) {
                writer.write(message + NEW_LINE);
            } catch (IOException e) {
                Log.i(LOG_TAG, "Couldn't write log. Read-only file system.");
                e.printStackTrace();
            }
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static File getCachedLogFile() {
        File directoryPath = new File(Constants.sCacheDirectory);
        createDirectory(directoryPath);
        File logFile = new File(directoryPath, Constants.CACHED_LOG_FILE_NAME);
        createNewFile(logFile);
        return logFile;
    }

    private static void createDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private static void createNewFile(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteExpiredFiles(Context context) {
        Logging.out(LOG_TAG, "Delete expired files from cache");
        File externalFilesDirectory = getExternalFilesDir(context);
        if (externalFilesDirectory != null) {
            File[] files = externalFilesDirectory.listFiles();
            deleteAllFiles(files, true);
        }
    }

    public static String obtainHashName(String url) {
        if (!TextUtils.isEmpty(url)) {
            int urlHash = url.hashCode();
            return Long.toString(urlHash & 0xFFFFFFFFL);
        } else {
            return Long.toString(0L);
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
        if (parentDir == null) {
            return null;
        }
        Logging.out(LOG_TAG, "Cache dir: " + parentDir.getAbsolutePath());

        File[] files = parentDir.listFiles();
        if (files == null){
            return null;
        }
        for (File file : files) {
            if (isValidFile(file) && file.getName().startsWith(filename)) {
                return file;
            }
        }
        return null;
    }

    private static boolean isValidFile(File file) {
        return file != null && !file.isDirectory();
    }

    public static String detectFileName(String videoUrl) {
        String fileName = null;
        try {
            URL url = new URL(videoUrl);
            fileName = url.getFile();
            if (fileName != null && !fileName.isEmpty()) {
                if (!fileName.endsWith(Constants.MP4_FORMAT)) {
                    int urlHash = videoUrl.hashCode();
                    // return positive value
                    return Long.toString(urlHash & 0xFFFFFFFFL);
                } else {
                    fileName = fileName.replace(Constants.MP4_FORMAT, "");
                    int lastSlash = fileName.lastIndexOf("/");
                    int length = fileName.length();
                    fileName = fileName.substring(lastSlash + 1, length);

                    if (fileName.length() > MAX_FILE_NAME_LENGTH) {
                        fileName = fileName.substring(0, MAX_FILE_NAME_LENGTH);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
