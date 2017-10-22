package com.loopme.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.loopme.Constants;

/**
 * Created by katerina on 7/10/17.
 */

public class ConnectionUtils {

    public static boolean isWifiConnection(Context context) {
        int connectionType = getConnectionType(context);
        return connectionType == Constants.ConnectionType.WIFI;

    }

    public static int getConnectionType(Context context) {
        if (context == null) {
            return Constants.ConnectionType.UNKNOWN;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return Constants.ConnectionType.UNKNOWN;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            return Constants.ConnectionType.UNKNOWN;
        }

        int type = networkInfo.getType();
        return getConnectionType(type, context);
    }


    private static int getConnectionType(int type, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return Constants.ConnectionType.WIFI;
        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
            return Constants.ConnectionType.ETHERNET;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return Constants.ConnectionType.UNKNOWN;
            }

            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return Constants.ConnectionType.MOBILE_2G;

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return Constants.ConnectionType.MOBILE_3G;

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return Constants.ConnectionType.MOBILE_4G;

                default:
                    return Constants.ConnectionType.MOBILE_UNKNOWN_GENERATION;
            }
        } else {
            return Constants.ConnectionType.UNKNOWN;
        }
    }

}
