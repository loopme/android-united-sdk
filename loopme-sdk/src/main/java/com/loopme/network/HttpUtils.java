package com.loopme.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.utils.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    private static final String LOG_TAG = HttpUtils.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;
    private static final String HTTP_METHOD_GET = "GET";
    private static final String APPLICATION_X_WWW_FORM = "application/x-www-form-urlencoded";
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_OPEN_RTB_VER = "x-openrtb-version";

    private HttpUtils() { }

    @NonNull
    public static HttpRawResponse doRequest(String url, Method httpMethod, byte[] body) {
        Log.d(LOG_TAG, "Making request by: " + url);
        HttpRawResponse response = new HttpRawResponse();
        try {
            HttpURLConnection connection = createConnection(url);
            connection.setRequestMethod(httpMethod.name());
            if (httpMethod == Method.POST) {
                connection.setRequestProperty(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
                connection.setDoOutput(true);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body);
                }
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response.setBody(inputStreamToByteArray(connection.getInputStream()));
            }
            response.setCode(responseCode);
            response.setMessage(connection.getResponseMessage());
            Log.d(LOG_TAG, "HttpRawResponse message: " + connection.getResponseMessage());
        } catch (IOException e) {
            response.setMessage("Exception(" + e.getClass().getSimpleName() +"): " + e.getMessage());
            Log.d(LOG_TAG, "HttpRawResponse Exception(" + e.getClass().getSimpleName() +"): " + e.getMessage());
        }
        return response;
    }

    public static void track(@NonNull String url, @Nullable String body) {
        Log.d(LOG_TAG, "Making request by: " + url);
        try {
            HttpURLConnection connection = createConnection(url);
            if (body != null) {
                connection.setRequestMethod(Method.POST.name());
                connection.setRequestProperty(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM);
                connection.setDoOutput(true);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body.getBytes());
                }
            }
            Log.d(LOG_TAG, "responseCode " + connection.getResponseCode());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception: " + e.getMessage());
        }
    }

    private static HttpURLConnection createConnection(String urlStr) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) (new URL(urlStr)).openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestProperty(HEADER_USER_AGENT, Utils.getUserAgent());
        connection.setRequestProperty(HEADER_OPEN_RTB_VER, Constants.OPEN_RTB_VERSION);
        return connection;
    }

    public enum Method { GET, POST }

    private static byte[] inputStreamToByteArray(InputStream inputStream) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (InputStream is = inputStream) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException ignored) { }
        return os.toByteArray();
    }

    public static boolean isOffline(@NonNull Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return true;
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable();
    }

    public static boolean isWifiConnection(Context context) {
        return getConnectionType(context) == Constants.ConnectionType.WIFI;
    }

    public static int getConnectionType(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (cm == null || cm.getActiveNetworkInfo() == null || telephonyManager == null) ?
            Constants.ConnectionType.UNKNOWN :
            getConnectionType(cm.getActiveNetworkInfo().getType(), telephonyManager);
    }

    private static int getConnectionType(int type, @NonNull TelephonyManager telephonyManager) {
        if (type == ConnectivityManager.TYPE_WIFI) return Constants.ConnectionType.WIFI;
        if (type == ConnectivityManager.TYPE_ETHERNET) return Constants.ConnectionType.ETHERNET;
        if (type != ConnectivityManager.TYPE_MOBILE) return Constants.ConnectionType.UNKNOWN;

        int networkType = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                // Because it is static method we can't use permission check here
                networkType = telephonyManager.getDataNetworkType();
            } catch (SecurityException e) {
                return Constants.ConnectionType.UNKNOWN;
            }
        }
        return getConnectionType(networkType);
    }

    private static int getConnectionType(int networkType) {
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
    }

    public static void cache(@NonNull Context context, @NonNull String source, @NonNull String destination, @NonNull CacheListener listener) {
        File file = new File(destination + "_download");
        try {
            Logging.out(LOG_TAG, "Use mobile network for caching: " + Constants.USE_MOBILE_NETWORK_FOR_CACHING);
            if (HttpUtils.isOffline(context)) {
                throw new IllegalStateException("No internet connection");
            }
            if (!HttpUtils.isWifiConnection(context) && !Constants.USE_MOBILE_NETWORK_FOR_CACHING) {
                throw new IllegalStateException("No wifi connection");
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(source).openConnection();
            connection.setRequestMethod(HTTP_METHOD_GET);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECT_TIMEOUT);

            try (InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream outputStream = new FileOutputStream(file, false)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                if (file.renameTo(new File(destination))) {
                    listener.onSuccess();
                }
            } catch (IOException e) {
                listener.onError(e);
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            listener.onError(e);
        }
    }

    public interface CacheListener {
        void onError(Exception e);
        void onSuccess();
    }
}