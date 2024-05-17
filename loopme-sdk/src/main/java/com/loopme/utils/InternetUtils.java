package com.loopme.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by katerina on 5/4/17.
 */

public class InternetUtils {
    public static boolean isOnline(Context context) {
        try {
            final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr == null) {
                return false;
            }
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            //TODO: See above
            return activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
