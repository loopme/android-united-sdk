package com.loopme.loaders;

import com.loopme.Logging;
import com.loopme.ad.LoopMeAd;
import com.loopme.network.response.BidResponse;
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
        long duration;
        long startTime = System.currentTimeMillis();
        try {
            GetResponse<BidResponse> response = LoopMeAdService.fetchAdByUrl(mUrl);
            Logging.out(LOG_TAG, "response received");
            parseResponse(response);
            duration = System.currentTimeMillis() - startTime;
            if (duration > 1000) {
                sendOrtbLatencyAlert(duration, true);
            }
        } catch (Exception e) {
            duration = System.currentTimeMillis() - startTime;
            handleBadResponse(e.getMessage());
            if (duration > 1000) {
                sendOrtbLatencyAlert(duration, false);
            }
        }
    }
}
