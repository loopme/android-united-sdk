package com.loopme.bridges;

import com.loopme.MraidOrientation;
import com.loopme.common.LoopMeError;

public interface MraidBridgeListener {
    void close();
    void onLoadSuccess();
    void onLoadFail(LoopMeError error);
    void open(String url);
    void resize(int width, int height);
    void playVideo(String videoUrl);
    void expand(boolean useCustomClose);
    void onChangeCloseButtonVisibility(boolean hasOwnCloseButton);
    void onMraidCallComplete(String command);
    void onLoopMeCallComplete(String command);
    void setOrientationProperties(boolean allowOrientationChange, MraidOrientation forceOrientation);
}
