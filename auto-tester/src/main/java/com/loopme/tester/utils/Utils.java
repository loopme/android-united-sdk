package com.loopme.tester.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.TypedValue;

import com.loopme.tester.Constants;
import com.loopme.tester.loaders.AdSpotCursorLoader;
import com.loopme.tester.model.AdSpot;

import java.util.ArrayList;


public class Utils {
    private static final long TIME_DELAY = 200;
    private static final long TIME_DELAY_LG = 800;
    private static final String REGEX = "\\d{1,9}";

    public static boolean isLg() {
        return TextUtils.equals(Build.MANUFACTURER, Constants.MANUFACTURER_LGE)
                || TextUtils.equals(Build.MANUFACTURER, Constants.MANUFACTURER_LG);
    }

    public static boolean isInt(String number) {
        return number.matches(REGEX);
    }

    public static int parseToInt(String number) {
        try {
            return Integer.valueOf(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean hasPermission(Activity activity, String permissionType) {
        int permission = ContextCompat.checkSelfPermission(activity, permissionType);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permissionType, int requestCode) {
        if (!Utils.hasPermission(activity, permissionType)) {
            ActivityCompat.requestPermissions(activity, new String[]{permissionType}, requestCode);
        }
    }

    public static long getTimeDelay() {
        return TIME_DELAY_LG;
    }

    public static long getAdSpotId(Loader loader) {
        if (loader instanceof AdSpotCursorLoader) {
            ArrayList<AdSpot> adSpotList = ((AdSpotCursorLoader) loader).getAdSpotModelList();
            if (adSpotList.size() > 0) {
                return adSpotList.get(0).getAdSpotId();
            }
        }
        return Constants.AD_SPOT_DOES_NOT_EXIST_ID;
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        return resources != null ? (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()) : 0;
    }

    public static boolean isApi19() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    }

}
