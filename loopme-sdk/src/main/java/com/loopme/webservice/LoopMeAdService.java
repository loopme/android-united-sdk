package com.loopme.webservice;

import com.loopme.models.response.ResponseJsonModel;
import com.loopme.network.GetResponse;

import org.json.JSONObject;

public interface LoopMeAdService {

    GetResponse<ResponseJsonModel> fetchAd(String url, JSONObject body);

    GetResponse<String> downloadResource(String url);

    GetResponse<ResponseJsonModel> fetchAdByUrl(String mUrl);
}

