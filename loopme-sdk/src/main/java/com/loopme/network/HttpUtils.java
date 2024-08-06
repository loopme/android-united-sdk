package com.loopme.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    private static final String LOG_TAG = HttpUtils.class.getSimpleName();
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 15000;
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
        } catch (IOException e) {
            response.setMessage("Exception(" + e.getClass().getSimpleName() +"): " + e.getMessage());
        }
        return response;
    }

    public static void simpleRequest(@NonNull String url, @Nullable String body) {
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
}