package com.loopme.network;

import com.loopme.models.response.ResponseJsonModel;
import com.loopme.network.parser.ResponseParser;

import org.json.JSONObject;

public class LoopMeAdService {
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

    public GetResponse<ResponseJsonModel> fetchAd(String url, JSONObject body) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(url, HttpUtils.Method.POST, body.toString().getBytes());
        return ResponseParser.parse(httpRawResponse);
    }

    public GetResponse<String> downloadResource(String resUrl) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(resUrl, HttpUtils.Method.GET, null);
        return ResponseParser.parseStringBody(httpRawResponse);
    }

    public GetResponse<ResponseJsonModel> fetchAdByUrl(String mUrl) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(mUrl, HttpUtils.Method.GET, null);
        return ResponseParser.parse(httpRawResponse);
    }
}
