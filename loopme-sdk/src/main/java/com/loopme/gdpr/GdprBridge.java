package com.loopme.gdpr;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

/**
 * Created by katerina on 5/7/18.
 */

public class GdprBridge {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private OnCloseListener listener;

    GdprBridge(OnCloseListener listener) {
        this.listener = listener;
    }

    @JavascriptInterface
    public void close() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onGdprClose();
            }
        });
    }

    public interface OnCloseListener {
        void onGdprClose();
    }
}
