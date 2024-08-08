package com.loopme.loaders;

import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.network.GetResponse;
import com.loopme.network.LoopMeAdService;

public class AdFetchTaskByUrl extends AdFetchTask {
    private final String mUrl;

    public AdFetchTaskByUrl(LoopMeAd loopMeAd, AdFetcherListener adFetcherListener, String url) {
        super(loopMeAd, adFetcherListener);
        mUrl = url;
    }

    @Override
    public void run() {
        try {
            GetResponse<ResponseJsonModel> response = LoopMeAdService.getInstance().fetchAdByUrl(mUrl);
            Logging.out(LOG_TAG, "response received");
            parseResponse(response);
        } catch (Exception e) {
            handleBadResponse(e.getMessage());
        }
    }
}
