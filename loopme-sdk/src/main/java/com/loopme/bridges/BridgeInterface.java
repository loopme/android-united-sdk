package com.loopme.bridges;

import com.loopme.Constants;

public interface BridgeInterface {

    //webview commands
    void setWebViewState(Constants.WebviewState state);

    void setFullscreenMode(boolean mode);

    void send360Event(String str);

    //video commands
    void setVideoState(Constants.VideoState state);

    void setVideoDuration(int duration);

    void setVideoCurrentTime(int currentTime);

    void setVideoMute(boolean mute);

    void sendNativeCallFinished();
}
