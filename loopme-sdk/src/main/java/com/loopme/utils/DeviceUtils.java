package com.loopme.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by katerina on 8/28/17.
 */

public class DeviceUtils {

    public static String getAvailableInternalSpace(Context context){
        return Formatter.formatFileSize(context, getAvailableInternalSpace());
    }

    public static String getAvailableInternalSpace(Context context, long fileSize){
        return Formatter.formatFileSize(context, fileSize * 1000);
    }

    public static long getAvailableInternalSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    private static long getTotalSizeOfStorage(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static int getPercentOfAvailableStorageSpace() {
        return 100 - (int)((getAvailableInternalSpace() * 100) / getTotalSizeOfStorage());
    }
}
