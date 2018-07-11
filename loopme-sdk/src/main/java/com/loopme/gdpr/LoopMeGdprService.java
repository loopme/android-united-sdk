package com.loopme.gdpr;

import com.loopme.network.GetResponse;

/**
 * Created by katerina on 4/27/18.
 */

public interface LoopMeGdprService {
    GetResponse<GdprResponse> checkUserConsent();
}