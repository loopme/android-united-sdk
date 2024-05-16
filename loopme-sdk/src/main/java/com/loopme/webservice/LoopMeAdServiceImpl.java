package com.loopme.webservice;

import com.loopme.models.response.ResponseJsonModel;
import com.loopme.network.parser.ResponseParser;
import com.loopme.network.GetResponse;
import com.loopme.network.HttpRawResponse;
import com.loopme.network.HttpUtils;

import org.json.JSONObject;

public class LoopMeAdServiceImpl implements LoopMeAdService {
    private static LoopMeAdService sService;

    private LoopMeAdServiceImpl() { }

    public static LoopMeAdService getInstance() {
        if (sService == null) {
            synchronized (LoopMeAdServiceImpl.class) {
                sService = new LoopMeAdServiceImpl();
            }
        }
        return sService;
    }

    @Override
    public GetResponse<ResponseJsonModel> fetchAd(String url, JSONObject body) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(url, HttpUtils.Method.POST, body.toString().getBytes());
        return ResponseParser.parse(httpRawResponse);
    }


    @Override
    public GetResponse<String> downloadResource(String resUrl) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(resUrl, HttpUtils.Method.GET, null);
        return ResponseParser.parseStringBody(httpRawResponse);
    }

    @Override
    public GetResponse<ResponseJsonModel> fetchAdByUrl(String mUrl) {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(mUrl, HttpUtils.Method.GET, null);
        return ResponseParser.parse(httpRawResponse);
    }
}
