package com.loopme.tester.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by katerina on 1/28/17.
 */

public class CacheUtils {
    private static final String LOG_TAG = CacheUtils.class.getSimpleName();
    private static final String HIDDEN_DIR_NAME = File.separator + ".LoopMe";
    private static final String DIR_NAME = "Cache" + File.separator + "Loopme";
    private static final String DIR_ADS = "Ads";
    private static CacheUtils instance;

    private File mExternalPath;
    private File mExternalAdsPath;

    public static CacheUtils getInstance() {
        return getInstance(null);
    }

    public static CacheUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheUtils.class) {
                if (instance == null) {
                    instance = new CacheUtils(context);
                }
            }
        }
        return instance;
    }

    private CacheUtils(Context context) {
        if (isExternalStorageWritable()) {
            mExternalPath = new File(Environment.getExternalStorageDirectory(),
                    HIDDEN_DIR_NAME + File.separator + DIR_NAME);
        } else {
            mExternalPath = Environment.getDataDirectory();
        }
        mExternalAdsPath = new File(mExternalPath, DIR_ADS);
        if (!mExternalPath.exists()) {
            mExternalPath.mkdir();
        }

        if (!mExternalAdsPath.exists()) {
            mExternalAdsPath.mkdir();
        }
    }

    public File getAdsFolderPath() {
        return mExternalAdsPath;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getFilesDir();
            deleteDir(dir);
            dir = context.getExternalFilesDir(null);
            deleteDir(dir);
        } catch (Exception e) {
            Log.e(LOG_TAG, "deleteCache error", e);
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            boolean b = dir.delete();
            Log.d(LOG_TAG, "remove " + b + " " + dir.getAbsolutePath());
            return b;
        } else if (dir != null && dir.isFile()) {
            boolean b2 = dir.delete();
            Log.d(LOG_TAG, "remove " + b2 + " " + dir.getAbsolutePath());
            return b2;
        } else {
            return false;
        }
    }
}
