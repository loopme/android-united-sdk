package com.loopme.gdpr;

import android.os.Handler;
import android.os.Looper;

import com.loopme.BuildConfig;
import com.loopme.network.GetResponse;
import com.loopme.network.HttpUtils;
import com.loopme.network.parser.ResponseParser;
import com.loopme.utils.ExecutorHelper;

public class LoopMeGdprService implements Runnable {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String url;

    private Callback callback;

    public LoopMeGdprService(String advId, Callback callback) {
        this.callback = callback;
        url = BuildConfig.LOOPME_GDPR_URL + "consent_check?device_id=" + advId;
    }

    public void start() {
        ExecutorHelper.getExecutor().submit(this);
    }

    @Override
    public void run() {
        GetResponse<GdprResponse> response =
                ResponseParser.parseGdprResponse(
                        HttpUtils.doRequest(url, HttpUtils.Method.GET, null));

        if (response.isSuccessful())
            onSuccess(response.getBody());
        else
            onFail("Server code: " + response.getCode() + "; message:" + response.getMessage());
    }

    private void onFail(final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onLoopMeGdprResponseFail(message);
            }
        });
    }

    private void onSuccess(final GdprResponse response) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null)
                    callback.onLoopMeGdprResponseSuccess(response);
            }
        });
    }

    public interface Callback {
        void onLoopMeGdprResponseSuccess(GdprResponse response);

        void onLoopMeGdprResponseFail(String message);
    }
}
