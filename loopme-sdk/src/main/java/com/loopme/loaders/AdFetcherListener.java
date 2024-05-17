package com.loopme.loaders;

import com.loopme.ad.AdParams;
import com.loopme.common.LoopMeError;

public interface AdFetcherListener {
    void onAdFetchCompleted(AdParams adParams);
    void onAdFetchFailed(LoopMeError error);
}
