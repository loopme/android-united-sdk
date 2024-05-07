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

    @NonNull
    public static GetResponse<String> parseStringBody(HttpRawResponse rawResponse) {
        GetResponse<String> response = new GetResponse<>();
        if (rawResponse.getCode() == HttpURLConnection.HTTP_OK) {
            if (rawResponse.getBody() != null) {
                String bodyAsString = new String(rawResponse.getBody()).intern();
                response.setCode(rawResponse.getCode());
                response.setBody(bodyAsString);
            } else {
                response.setMessage(Errors.ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE);
            }
        } else {
            response.setCode(rawResponse.getCode());
            response.setMessage(rawResponse.getMessage());
        }
        return response;
    }

    @NonNull
    public static GetResponse<ResponseJsonModel> parse(HttpRawResponse rawResponse) {
        GetResponse<ResponseJsonModel> response = new GetResponse<>();
        if (rawResponse.getCode() == HttpURLConnection.HTTP_OK) {
            if (isBodyParcelable(rawResponse)) {
                String bodyAsString = new String(rawResponse.getBody()).intern();
                ResponseJsonModel responseJsonModel = convertToResponseJsonModel(bodyAsString);
                response.setCode(rawResponse.getCode());
                response.setBody(responseJsonModel);
            } else {
                response.setMessage(Errors.ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE);
            }
        } else {
            response.setCode(rawResponse.getCode());
            response.setMessage(rawResponse.getMessage());
        }
        return response;
    }

    @NonNull
    private static boolean isBodyParcelable(HttpRawResponse response) {
        byte[] body = response.getBody();
        return body != null && isParcelableAsJson(body) && isParcelableAsResponseJsonModel(body);
    }

    private static boolean isParcelableAsResponseJsonModel(byte[] data) {
        String bodyAsString = new String(data).intern();
        ResponseJsonModel responseJsonModel = convertToResponseJsonModel(bodyAsString);
        return responseJsonModel != null;
    }

    private static boolean isParcelableAsJson(byte[] data) {
        return parseToJsonObject(data) != null;
    }

    private static JSONObject parseToJsonObject(byte[] data) {
        try {
            String bodyAsString = new String(data).intern();
            return new JSONObject(bodyAsString);
        } catch (JSONException e) {
            Logging.out(ResponseParser.class.getSimpleName(), e.getMessage());
        } catch (UnsupportedOperationException e) {
            Logging.out(ResponseParser.class.getSimpleName(), e.getMessage());
        }
        return null;
    }

    private static ResponseJsonModel convertToResponseJsonModel(String body) {
        try {
            ResponseJsonModelParser parser = new ResponseJsonModelParser();
            return parser.parse(new JSONObject(body));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
