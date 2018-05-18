package com.loopme.gdpr;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

/**
 * Created by katerina on 5/7/18.
 */

public class GdprBridge {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private OnCloseListener mListener;

    public GdprBridge(OnCloseListener listener) {
        mListener = listener;
    }

    @JavascriptInterface
    public void close() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onClose();
                }
            }
        });
    }

    public interface OnCloseListener {
        void onClose();
    }
}
