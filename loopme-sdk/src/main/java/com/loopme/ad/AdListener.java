package com.loopme.ad;

import com.loopme.common.LoopMeError;

public interface AdListener {
    void onAdLoadedSuccess();

    void onAdLoadFail(LoopMeError loopMeError);

    void onAdShow();

    void onAdClicked();

    void onAdDidReachEnd();

    void onAdHide();

    void onAdLeaveApp();

    void onAdExpired();
}
