package com.loopme.network.parser;

import androidx.annotation.NonNull;

import com.loopme.Logging;
import com.loopme.models.Errors;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.network.GetResponse;
import com.loopme.network.HttpRawResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class ResponseParser {

    private static final String LOG_TAG = ResponseParser.class.getSimpleName();
    @NonNull
    public static GetResponse<String> parseStringBody(HttpRawResponse rawResponse) {
        GetResponse<String> response = new GetResponse<>();
        if (rawResponse.getCode() != HttpURLConnection.HTTP_OK) {
            response.setCode(rawResponse.getCode());
            response.setMessage(rawResponse.getMessage());
            return response;
        }
        if (rawResponse.getBody() == null) {
            response.setMessage(Errors.ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE);
            return response;
        }
        response.setCode(rawResponse.getCode());
        response.setBody(new String(rawResponse.getBody()).intern());
        return response;
    }

    @NonNull
    public static GetResponse<ResponseJsonModel> parse(HttpRawResponse rawResponse) {
        GetResponse<ResponseJsonModel> response = new GetResponse<>();
        int code = rawResponse.getCode();
        byte[] body = rawResponse.getBody();
        if (code != HttpURLConnection.HTTP_OK) {
            response.setCode(code);
            response.setMessage(rawResponse.getMessage());
            return response;
        }
        String bodyAsString = new String(body).intern();
        ResponseJsonModel responseJsonModel = convertToResponseJsonModel(bodyAsString);
        boolean isBodyParcelable =
            body != null &&
            parseToJsonObject(bodyAsString) != null &&
            responseJsonModel != null;
        if (!isBodyParcelable) {
            response.setMessage(Errors.ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE);
            return response;
        }
        response.setCode(code);
        response.setBody(responseJsonModel);
        return response;
    }

    private static JSONObject parseToJsonObject(String body) {
        try {
            return new JSONObject(body);
        } catch (JSONException | UnsupportedOperationException e) {
            Logging.out(LOG_TAG, e.getMessage());
            return null;
        }
    }

    private static ResponseJsonModel convertToResponseJsonModel(String body) {
        try {
            return (new ResponseJsonModelParser()).parse(new JSONObject(body));
        } catch (JSONException e) {
            Logging.out(LOG_TAG, e.getMessage());
        }
        return null;
    }
}
