package com.loopme.views;

import android.content.Context;
import android.webkit.WebSettings;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.bridges.Bridge;
import com.loopme.bridges.BridgeCommandBuilder;
import com.loopme.bridges.BridgeInterface;
import com.loopme.listener.Listener;
import com.loopme.utils.Utils;


public class AdView extends LoopMeWebView implements BridgeInterface, Bridge.Listener {

    private static final String LOG_TAG = AdView.class.getSimpleName();
    private Constants.VideoState mCurrentVideoState = Constants.VideoState.IDLE;

    private Bridge.Listener mBridgeListener;

    public AdView(Context context, Listener adReadyListener) {
        super(context);
        Logging.out(LOG_TAG, "AdView created");
        setWebViewClient(new Bridge(this, adReadyListener));
        setDefaultWebChromeClient();
        modifyUserAgentForKrPano();
    }

    // This helps Omid to send sessionFinish js event when ad is about to be destroyed.
    @Override
    public void destroy() {
        mBridgeListener = null;
        destroyGracefully();
    }

    public void addBridgeListener(Bridge.Listener listener) {
        mBridgeListener = listener;
    }

    public Constants.VideoState getCurrentVideoState() {
        return mCurrentVideoState;
    }

    private void modifyUserAgentForKrPano() {
        String userString = WebSettings.getDefaultUserAgent(getContext());
        String modifiedUserString = Utils.makeChromeShortCut(userString);
        getSettings().setUserAgentString(modifiedUserString);
    }

    @Override
    public void setFullscreenMode(boolean mode) {
        String command = BridgeCommandBuilder.fullscreenMode(mode);
        Logging.out(LOG_TAG, "setFullscreenMode(): " + mode);
        loadUrl(command);
    }

    @Override
    public void send360Event(String event) {
        String command = BridgeCommandBuilder.event360(event);
        Logging.out(LOG_TAG, "send360Event(): " + event);
        loadUrl(command);
    }

    @Override
    public void setVideoState(Constants.VideoState state) {
        if (mCurrentVideoState != state) {
            mCurrentVideoState = state;
            String command = BridgeCommandBuilder.videoState(state);
            Logging.out(LOG_TAG, "setVideoState(): " + state.name());
            loadUrl(command);
        }
    }

    @Override
    public void setVideoDuration(int duration) {
        String command = BridgeCommandBuilder.videoDuration(duration);
        Logging.out(LOG_TAG, "setVideoDuration(): " + duration);
        loadUrl(command);
    }

    @Override
    public void setVideoCurrentTime(int currentTime) {
        String command = BridgeCommandBuilder.videoCurrentTime(currentTime);
        loadUrl(command);
    }

    @Override
    public void setVideoMute(boolean mute) {
        String command = BridgeCommandBuilder.videoMute(mute);
        Logging.out(LOG_TAG, "MUTE : " + mute);
        loadUrl(command);
    }

    public void shake() {
        String command = BridgeCommandBuilder.shake(true);
        Logging.out(LOG_TAG, "SHAKE");
        loadUrl(command);
    }

    @Override
    public void sendNativeCallFinished() {
        String command = BridgeCommandBuilder.isNativeCallFinished(true);
        Logging.out(LOG_TAG, "sendNativeCallFinished()");
        loadUrl(command);
    }

    @Override
    public void onJsClose() {
        if (mBridgeListener != null) {
            mBridgeListener.onJsClose();
        }
    }

    @Override
    public void onJsLoadSuccess() {
        if (mBridgeListener != null) {
            mBridgeListener.onJsLoadSuccess();
        }
    }

    @Override
    public void onJsLoadFail(String mess) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsLoadFail(mess);
        }
    }

    @Override
    public void onJsFullscreenMode(boolean isFullScreen) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsFullscreenMode(isFullScreen);
        }
    }

    @Override
    public void onNonLoopMe(String url) {
        if (mBridgeListener != null) {
            mBridgeListener.onNonLoopMe(url);
        }
    }

    @Override
    public void onJsVideoLoad(String videoUrl) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoLoad(videoUrl);
        }
    }

    @Override
    public void onJsVideoMute(boolean mute) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoMute(mute);
        }
    }

    @Override
    public void onJsVideoPlay(int time) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoPlay(time);
        }
    }

    @Override
    public void onJsVideoPause(int time) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoPause(time);
        }
    }

    @Override
    public void onJsVideoStretch(boolean b) {
        if (mBridgeListener != null) {
            mBridgeListener.onJsVideoStretch(b);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        //do nothing
    }

    @Override
    public void computeScroll() {
        //do nothing
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                   int maxOverScrollY, boolean isTouchEvent) {
        return false;
    }

}