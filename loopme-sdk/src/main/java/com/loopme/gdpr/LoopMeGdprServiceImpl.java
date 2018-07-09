package com.loopme.gdpr;

import android.os.Handler;
import android.os.Looper;

import com.loopme.BuildConfig;
import com.loopme.network.GetResponse;
import com.loopme.network.HttpRawResponse;
import com.loopme.network.HttpUtils;
import com.loopme.network.parser.ResponseParser;
import com.loopme.utils.ExecutorHelper;

public class LoopMeGdprServiceImpl implements LoopMeGdprService, Runnable {

    private Callback mCallback;
    private final Handler HANDLER = new Handler(Looper.getMainLooper());
    private final String mUrl;

    public LoopMeGdprServiceImpl(String advId, Callback callback) {
        mCallback = callback;
        mUrl = BuildConfig.LOOPME_GDPR_URL + "consent_check?device_id=" + advId;
    }

    public void start() {
        ExecutorHelper.getExecutor().submit(this);
    }

    @Override
    public void run() {
        GetResponse<GdprResponse> response = checkUserConsent();
        if (response.isSuccessful()) {
            onSuccess(response.getBody());
        } else {
            onFail("Server code: " + response.getCode() + "; message:" + response.getMessage());
        }
    }

    @Override
    public GetResponse<GdprResponse> checkUserConsent() {
        HttpRawResponse httpRawResponse = HttpUtils.doRequest(mUrl, HttpUtils.Method.GET, null);
        return ResponseParser.parsGdprResponse(httpRawResponse);
    }

    private void onFail(final String message) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onFail(message);
                }
            }
        });
    }

    private void onSuccess(final GdprResponse response) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onSuccess(response);
                }
            }
        });
    }

    public interface Callback {
        void onSuccess(GdprResponse response);

        void onFail(String message);
    }
}
