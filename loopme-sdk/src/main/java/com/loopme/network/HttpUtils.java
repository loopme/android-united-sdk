package com.loopme.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.models.Errors;
import com.loopme.utils.IOUtils;
import com.loopme.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
        Logging.out(LOG_TAG, "Making request by: " + url);
        HttpRawResponse response = new HttpRawResponse();
        HttpURLConnection connection = null;
        try {
            connection = createConnection(url);
            connection.setRequestMethod(httpMethod.name());
            if (httpMethod == Method.POST) {
                connection.setRequestProperty(HEADER_CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
                writeBody(connection, body);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response.setBody(IOUtils.inputStreamToByteArray(connection.getInputStream()));
            }
            response.setCode(responseCode);
            response.setMessage(connection.getResponseMessage());
            Logging.out(LOG_TAG, "responseCode " + responseCode);
        } catch (MalformedURLException | ProtocolException e) {
            Logging.out(LOG_TAG, e.getMessage());
            response.setMessage(e.getMessage());
        } catch (IOException e) {
            Logging.out(LOG_TAG, e.getMessage());
            response.setMessage(Errors.ERROR_MESSAGE_NETWORK_OPERATION);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    public static void simpleRequest(@NonNull String url, @Nullable String body) {
        Logging.out(LOG_TAG, "Making request by: " + url);
        HttpURLConnection connection = null;
        try {
            connection = createConnection(url);
            if (body != null) {
                connection.setRequestMethod(Method.POST.name());
                connection.addRequestProperty(HEADER_CONTENT_TYPE, APPLICATION_X_WWW_FORM);
                writeBody(connection, body.getBytes());
            }
            int responseCode = connection.getResponseCode();
            Logging.out(LOG_TAG, "responseCode " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void writeBody(HttpURLConnection connection, byte[] body) throws IOException {
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(body);
        IOUtils.closeQuietly(outputStream);
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
}