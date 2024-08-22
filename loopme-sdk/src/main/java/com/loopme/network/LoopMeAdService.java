package com.loopme.network;

import androidx.annotation.NonNull;

import com.loopme.Logging;
import com.loopme.models.Errors;
import com.loopme.network.response.BidResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class LoopMeAdService {
    private static final String LOG_TAG = LoopMeAdService.class.getSimpleName();
    private static LoopMeAdService sService;

    private LoopMeAdService() { }

    public static LoopMeAdService getInstance() {
        if (sService == null) {
            synchronized (LoopMeAdService.class) {
                sService = new LoopMeAdService();
            }
        }
        return sService;
    }

    public GetResponse<BidResponse> fetchAd(String url, JSONObject body) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(url, HttpUtils.Method.POST, body.toString().getBytes());
        return parse(httpRawResponse);
    }

    public GetResponse<String> downloadResource(String resUrl) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(resUrl, HttpUtils.Method.GET, null);
        return parseStringBody(httpRawResponse);
    }

    public GetResponse<BidResponse> fetchAdByUrl(String mUrl) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(mUrl, HttpUtils.Method.GET, null);
        return parse(httpRawResponse);
    }

    @NonNull
    public static GetResponse<String> parseStringBody(HttpRawResponse rawResponse) {
        GetResponse<String> response = new GetResponse<>();
        int code = rawResponse.getCode();
        if (code != HttpURLConnection.HTTP_OK) {
            response.setCode(code);
            response.setMessage(rawResponse.getMessage());
            return response;
        }
        byte[] body = rawResponse.getBody();
        if (body == null) {
            response.setMessage(Errors.ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE);
            return response;
        }
        response.setCode(code);
        response.setBody(new String(body).intern());
        return response;
    }

    @NonNull
    public static GetResponse<BidResponse> parse(HttpRawResponse rawResponse) {
        GetResponse<BidResponse> response = new GetResponse<>();
        int code = rawResponse.getCode();
        if (code != HttpURLConnection.HTTP_OK) {
            response.setCode(code);
            response.setMessage(rawResponse.getMessage());
            return response;
        }
        try {
            response.setBody(
                    BidResponse.fromJSON(new JSONObject(new String(rawResponse.getBody())))
            );
            response.setCode(code);
            return response;
        } catch (JSONException e) {
            Logging.out(LOG_TAG, e.getMessage());
            response.setMessage(Errors.ERROR_MESSAGE_BROKEN_SERVERS_RESPONSE);
            return response;
        }
    }
}
