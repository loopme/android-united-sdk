package com.loopme.gdpr;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

/**
 * Created by katerina on 5/7/18.
 */

public class GdprBridge {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private OnAnswerListener mListener;

    public GdprBridge(OnAnswerListener listener) {
        mListener = listener;
    }

    @JavascriptInterface
    public void onConsentReceived(final boolean isGdprAccepted) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onAnswer(isGdprAccepted);
                }
            }
        });
    }

    @JavascriptInterface
    public void onClose() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onClose();
                }
            }
        });
    }

    public interface OnAnswerListener {
        void onAnswer(boolean isGdprAccepted);

        void onClose();
    }
}
