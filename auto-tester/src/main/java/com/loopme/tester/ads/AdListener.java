package com.loopme.tester.ads;

public interface AdListener {

    void onLoadSuccess();

    void onLoadFail(String error);

    void onShow();

    void onExpired();

    void onHide();
}
