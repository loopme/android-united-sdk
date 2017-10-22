package com.loopme;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.loopme.utils.Utils;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;

    public static class Response {

        private int responseCode;
        private String response;
        private Exception exception;

        private Response() {
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponse() {
            return response;
        }

        public Exception getException() {
            return exception;
        }

    }

    public static Response sendRequest(@NonNull String url,
                                       @Nullable Map<String, String> headers,
                                       @Nullable String request) {
        Response result = new Response();
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET"); // optional, GET already by default

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (!TextUtils.isEmpty(request)) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST"); // optional, setDoOutput(true) set value to POST
                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(request);
                outputStream.flush();
                outputStream.close();
            }

            int responseCode = urlConnection.getResponseCode();
            result.responseCode = responseCode;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result.response = Utils.getStringFromStream(urlConnection.getInputStream());

            }
        } catch (Exception e) {
            result.exception = e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public static String obtainRequestString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        try {
            boolean firstTime = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    result.append("&");
                }
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException | NullPointerException e) {
            Logging.out("HttpUtil", "UnsupportedEncoding: UTF-8");
        }
        return String.valueOf(result);
    }

    public interface OnResponseListener {
        void onValidUrl(boolean valid);
    }
}
