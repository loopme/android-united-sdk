package com.loopme.controllers.display;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.MinimizedMode;
import com.loopme.MoatViewAbilityUtils;
import com.loopme.SwipeListener;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.Bridge;
import com.loopme.controllers.MraidController;
import com.loopme.controllers.VideoController;
import com.loopme.controllers.interfaces.LoopMeDisplayController;
import com.loopme.controllers.view.IViewController;
import com.loopme.controllers.view.View360Controller;
import com.loopme.controllers.view.ViewControllerLoopMe;
import com.loopme.loaders.FileLoaderNewImpl;
import com.loopme.loaders.Loader;
import com.loopme.models.Errors;
import com.loopme.common.LoopMeError;
import com.loopme.models.Message;
import com.loopme.utils.UiUtils;
import com.loopme.views.AdView;
import com.loopme.views.LoopMeWebView;
import com.loopme.views.MraidView;

public class DisplayControllerLoopMe extends BaseDisplayController implements LoopMeDisplayController {

    private static final int MRAID_WIDTH = 400;
    private static final int MRAID_HEIGHT = 600;
    private boolean mVideoPresented;

    private LoopMeAd mLoopMeAd;
    private AdParams mAdParams;
    private AdView mAdView;
    private MraidView mMraidView;
    private Loader mFileLoader;
    private VideoController mVideoController;
    private IViewController mViewController;
    private MraidController mMraidController;
    private volatile Bridge.Listener mBridgeListener;
    private View.OnTouchListener mOnTouchListener;
    private DisplayModeResolver mDisplayModeResolver;

    public DisplayControllerLoopMe(LoopMeAd loopMeAd) {
        super(loopMeAd);
        if (loopMeAd == null) {
            return;
        }
        mLoopMeAd = loopMeAd;
        mAdParams = mLoopMeAd.getAdParams();
        mDisplayModeResolver = new DisplayModeResolver(this, loopMeAd);
        mLogTag = DisplayControllerLoopMe.class.getSimpleName();
        Logging.out(mLogTag);
    }

    public void initControllers() {
        if (mAdParams.isMraidAd()) {
            initMraidControllers();
        } else {
            initLoopMeControllers();
        }
        initVideoController();
        initViewController();
    }

    private void initLoopMeControllers() {
        onMessage(Message.LOG, "initLoopMeSdkController");
        mAdView = new AdView(mLoopMeAd.getContext());
        mBridgeListener = initBridgeListener();
        mAdView.addBridgeListener(mBridgeListener);
        mOnTouchListener = initOnTouchListener();
        mAdView.setOnTouchListener(mOnTouchListener);
    }

    private void initMraidControllers() {
        onMessage(Message.LOG, "initMraidController");
        mMraidController = new MraidController(mLoopMeAd);
        mMraidView = new MraidView(mLoopMeAd.getContext(), mMraidController);
    }

    public void initVideoController() {
        VideoController.Callback callback = initVideoControllerCallback();
        mVideoController = new VideoController(mAdView, callback);
    }

    public void initViewController() {
        if (!mAdParams.isVideo360()) {
            ViewControllerLoopMe.Callback viewCallback = initViewControllerCallback();
            mViewController = new ViewControllerLoopMe(viewCallback);
        } else {
            View360Controller.Callback callback = initView360ControllerCallback();
            mViewController = new View360Controller(callback);
        }
    }


    public void setMraidWebViewState(Constants.WebviewState state) {
        if (mMraidView != null) {
            mMraidView.setWebViewState(state);
        }
    }

    private void buildMraidContainer(FrameLayout containerView) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        containerView.addView(mMraidView, layoutParams);
    }


    private void preloadMraidAd() {
        if (mAdParams.isMraidAd()) {
            mMraidView.loadData(mAdParams.getHtml());
        }
    }

    private void preloadLoopMeAd() {
        if (!mAdParams.isMraidAd()) {
            String html = mAdParams.getHtml();
            loadHtml(html);
        }
    }

    private void handleVideoLoad(String videoUrl) {
        onMessage(Message.LOG, "JS command: resolve video " + videoUrl);
        mVideoPresented = true;
        mVideoController.setVideo360(mAdParams.isVideo360());
        loadVideoFile(videoUrl);
    }

    private void loadVideoFile(final String videoUrl) {
        boolean preload = mAdParams.getPartPreload();
        Context context = mLoopMeAd.getContext();
        mFileLoader = new FileLoaderNewImpl(videoUrl, context, initFileLoaderCallback(preload));
        mFileLoader.start();
    }

    private FileLoaderNewImpl.Callback initFileLoaderCallback(final boolean preload) {
        return new FileLoaderNewImpl.Callback() {

            @Override
            public void onError(LoopMeError error) {
                onAdLoadFail(error);
            }

            @Override
            public void onFileFullLoaded(String filePath) {
                onMessage(Message.LOG, "fullVideoLoaded: " + filePath);
                fullVideoLoaded(filePath, preload, mLoopMeAd.isShowing());
            }
        };
    }

    private void fullVideoLoaded(String filePath, boolean preload, boolean adShowing) {
        if (mVideoController != null) {
            mVideoController.fullVideoLoaded(filePath, preload, adShowing);
        }
    }

    private boolean isInterstitial() {
        return mLoopMeAd != null && mLoopMeAd.isInterstitial();
    }

    private boolean isBanner() {
        return mLoopMeAd != null && mLoopMeAd.isBanner();
    }

    private void handleLoadSuccess() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdLoadSuccess();
        }
    }

    private void onAdVideoDidReachEnd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdVideoDidReachEnd();
        }
    }

    private void onAdLoadFail(LoopMeError error) {
        if (mLoopMeAd != null && error != null) {
            error.setErrorType(Constants.ErrorType.CUSTOM);
            mLoopMeAd.onInternalLoadFail(error);
        }
    }

    private void playbackFinishedWithError() {
        if (mLoopMeAd != null && isBanner()) {
            ((LoopMeBannerGeneral) mLoopMeAd).playbackFinishedWithError();
        }
    }

    private void destroyLoopMeAd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.dismiss();
        }
    }

    private void onAdClicked() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdClicked();
        }
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        initControllers();
        initTrackers();
        preloadHtml();
    }

    private void preloadHtml() {
        if (mAdParams.isMraidAd()) {
            preloadMraidAd();
        } else {
            preloadLoopMeAd();
        }
    }

    @Override
    public void onPlay(int position) {
        if (mVideoController != null) {
            resumeViewController();
            mVideoController.playVideo(position);
        }
        onStartWebMeasuringDelayed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBanner()) {
            resumeBanner();
        } else {
            resumeInterstitial();
        }
    }

    private void resumeBanner() {
        if (isFullScreen()) {
            setWebViewState(Constants.WebviewState.VISIBLE);
        } else {
            checkBannerVisibility();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseControllers();
    }

    private void resumeInterstitial() {
        resumeVideoController();
        resumeViewController();
        resumeMraid();
        if (isInterstitial()) {
            setWebViewState(Constants.WebviewState.VISIBLE);
        }
    }

    private void pauseControllers() {
        pauseViewController();
        pauseMraid();
        pauseVideoController();
        if (isInterstitial()) {
            setWebViewState(Constants.WebviewState.HIDDEN);
        }
    }

    private void resumeMraid() {
        if (mMraidView != null) {
            mMraidView.notifySizeChangeEvent(MRAID_WIDTH, MRAID_HEIGHT);
            mMraidView.setIsViewable(true);
            if (isBanner()) {
                mMraidView.setState(Constants.MraidState.EXPANDED);
            }
        }
    }

    private void resumeVideoController() {
        if (mVideoController != null) {
            mVideoController.resumeVideo();
        }
    }

    private void resumeViewController() {
        if (mViewController != null) {
            mViewController.onResume();
        }
    }

    private void pauseMraid() {
        if (mMraidView != null) {
            mMraidView.setIsViewable(false);
        }
    }

    private void pauseViewController() {
        if (mViewController != null) {
            mViewController.onPause();
        }
    }

    private void pauseVideoController() {
        if (mVideoController != null) {
            mVideoController.pauseVideo();
        }
    }

    @Override
    public void onVolumeMute(boolean mute) {
        if (mAdView != null) {
            mAdView.setVideoMute(mute);
        }
        if (mVideoController != null) {
            mVideoController.muteVideo(mute);
        }
    }

    @Override
    public void onBuildVideoAdView(FrameLayout containerView) {
        if (mViewController != null) {
            mViewController.buildVideoAdView(mLoopMeAd.getContext(), containerView, mAdView);
        }
    }

    @Override
    public void onDestroy() {
        setMraidWebViewState(Constants.WebviewState.CLOSED);
        setWebViewState(Constants.WebviewState.CLOSED);
        destroyVideoController();
        stopVideoLoader();
        clearWebView(mAdView);
        clearWebView(mMraidView);

        mAdView = null;
        mMraidView = null;
        mBridgeListener = null;
        mOnTouchListener = null;
        mDisplayModeResolver.destroy();
        super.onDestroy();
    }

    public void destroyMinimizedView() {
        if (mDisplayModeResolver != null) {
            mDisplayModeResolver.destroy();
        }
    }

    @Override
    public boolean onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.CLICK_INTENT);
        onAdClicked();
        if (super.onRedirect(url, loopMeAd)) {
            setWebViewState(Constants.WebviewState.HIDDEN);
            return true;
        }
        return false;
    }

    @Override
    public Constants.VideoState getCurrentVideoState() {
        return mAdView.getCurrentVideoState();
    }

    @Override
    public void onBuildStaticAdView(FrameLayout containerView) {
        if (containerView == null || mAdView == null) {
            return;
        }
        mAdView.setBackgroundColor(Color.BLACK);
        containerView.addView(mAdView);
    }

    public void setVideoState(Constants.VideoState state) {
        if (mAdView != null) {
            mAdView.setVideoState(state);
        }
    }

    @Override
    public void onBuildMraidView(FrameLayout containerView) {
        if (mMraidView != null && containerView != null) {
            buildMraidContainer(containerView);
            mMraidView.setIsViewable(true);
            mMraidView.notifyStateChange();
        }
    }

    @Override
    public void onRebuildView(FrameLayout containerView) {
        if (mViewController != null) {
            mViewController.rebuildView(containerView, mAdView);
        }
    }

    @Override
    public boolean isNativeAd() {
        return isVideoPresented();
    }

    private void checkBannerVisibility() {
        if (isBanner() && mLoopMeAd != null) {
            MoatViewAbilityUtils.calculateViewAbilitySyncDelayed(mLoopMeAd.getContainerView(), new MoatViewAbilityUtils.OnResultListener() {
                @Override
                public void onResult(MoatViewAbilityUtils.ViewAbilityInfo info) {
                    if (info.isVisibleMore50Percents()) {
                        setWebViewState(Constants.WebviewState.VISIBLE);
                    } else {
                        setWebViewState(Constants.WebviewState.HIDDEN);
                    }
                }
            });
        }
    }

    @Override
    public boolean isFullScreen() {
        return mDisplayModeResolver != null && mDisplayModeResolver.isFullScreenMode();
    }

    private void handleVideoStretch(boolean videoStretch) {
        onMessage(Message.LOG, "JS command: stretch video ");
        Constants.StretchOption stretch = videoStretch ?
                Constants.StretchOption.STRETCH :
                Constants.StretchOption.NO_STRETCH;
        mViewController.setStretchParam(stretch);
    }


    private void loadHtml(String html) {
        if (mAdView != null) {
            mAdView.loadHtml(html);
        } else {
            onAdLoadFail(Errors.HTML_LOADING_ERROR);
        }
    }

    public boolean isVideoPresented() {
        return mVideoPresented;
    }

    public void onAdShake() {
        if (mAdView != null) {
            mAdView.shake();
        }
    }

    public void setWebViewState(Constants.WebviewState state) {
        if (mAdView != null) {
            mAdView.setWebViewState(state);
        }
        if (mMraidView != null) {
            mMraidView.setWebViewState(state);
        }
    }

    private View.OnTouchListener initOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewController.handleTouchEvent(event);
                return false;
            }
        };
    }

    private Bridge.Listener initBridgeListener() {
        return new Bridge.Listener() {

            @Override
            public void onJsVideoPlay(int time) {
                onPlay(time);
            }

            @Override
            public void onJsVideoPause(final int time) {
                onPause();
            }

            @Override
            public void onJsVideoMute(boolean mute) {
                onVolumeMute(mute);
            }

            @Override
            public void onJsVideoLoad(final String videoUrl) {
                handleVideoLoad(videoUrl);
            }

            @Override
            public void onJsLoadSuccess() {
                handleLoadSuccess();
            }

            @Override
            public void onJsClose() {
                destroyLoopMeAd();
            }

            @Override
            public void onJsLoadFail(String mess) {
                onAdLoadFail(Errors.FAILED_TO_PROCESS_AD);
            }

            @Override
            public void onJsFullscreenMode(boolean isFullScreen) {
                switchToFullScreenMode(isFullScreen);
            }

            @Override
            public void onNonLoopMe(String url) {
                onRedirect(url, mLoopMeAd);
            }

            @Override
            public void onLeaveApp() {
                onAdLeaveApp();
            }

            @Override
            public void onJsVideoStretch(boolean b) {
                handleVideoStretch(b);
            }
        };
    }

    private void switchToFullScreenMode(boolean isFullScreen) {
        if (mDisplayModeResolver != null) {
            mDisplayModeResolver.switchToFullScreenMode(isFullScreen);
        }
    }

    public void switchToPreviousMode() {
        if (mLoopMeAd.isBanner() && mDisplayModeResolver != null) {
            mDisplayModeResolver.switchToPreviousMode();
        }
    }

    private void onAdLeaveApp() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdLeaveApp();
        }
    }

    private View360Controller.Callback initView360ControllerCallback() {
        return new View360Controller.Callback() {
            @Override
            public void onSurfaceReady(Surface surface) {
                onMessage(Message.LOG, "onSurfaceReady ####");
                if (mVideoController != null) {
                    mVideoController.setSurface(surface);
                }
            }

            @Override
            public void onEvent(String event) {
                if (mAdView != null) {
                    mAdView.send360Event(event);
                }
            }
        };
    }

    private ViewControllerLoopMe.Callback initViewControllerCallback() {
        return new ViewControllerLoopMe.Callback() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture) {
                onMessage(Message.LOG, "onSurfaceTextureAvailable");
                surfaceTextureAvailable(surfaceTexture);
                setViewSize();
            }

            @Override
            public void onSurfaceTextureDestroyed() {
                onMessage(Message.LOG, "onSurfaceTextureDestroyed");
                surfaceTextureDestroyed();
            }
        };
    }

    private void setViewSize() {
        MinimizedMode minimizedMode = mDisplayModeResolver.getMinimizedMode();
        Constants.DisplayMode currentDisplayMode = mDisplayModeResolver.getDisplayMode();
        AdSpotDimensions viewSize = UiUtils.getViewSize(minimizedMode, mLoopMeAd, currentDisplayMode);
        mViewController.setViewSize(viewSize.getWidth(), viewSize.getHeight());
    }

    private void surfaceTextureAvailable(SurfaceTexture surfaceTexture) {
        if (mVideoController != null) {
            mVideoController.setSurfaceTextureAvailable(true);
            mVideoController.setSurfaceTexture(surfaceTexture);
        }
    }

    private void surfaceTextureDestroyed() {
        if (mVideoController != null) {
            mVideoController.surfaceTextureDestroyed();
        }
    }

    private VideoController.Callback initVideoControllerCallback() {
        return new VideoController.Callback() {
            @Override
            public void onVideoReachEnd() {
                onAdVideoDidReachEnd();
            }

            @Override
            public void onFail(LoopMeError error) {
                onAdLoadFail(error);
            }

            @Override
            public void onVideoSizeChanged(int width, int height) {
                setVideoSize(width, height);
            }

            @Override
            public void onPlaybackFinishedWithError() {
                playbackFinishedWithError();
            }

            @Override
            public void onVolumeChangedEvent(double volume, int currentPosition) {
                onAdVolumeChangedEvent(volume, currentPosition);
            }
        };
    }

    private void setVideoSize(int width, int height) {
        if (mViewController != null) {
            mViewController.setVideoSize(width, height);
        }
    }

    private void clearWebView(LoopMeWebView webView) {
        if (webView != null) {
            webView.destroy();
        }
    }

    public void closeMraidAd() {
        if (mMraidController != null) {
            mMraidController.close();
        }
    }

    public void buildView(FrameLayout containerView) {
        if (mAdParams.isMraidAd()) {
            onBuildMraidView(containerView);
        } else {
            if (isNativeAd()) {
                onBuildVideoAdView(containerView);
            } else {
                onBuildStaticAdView(containerView);
                setVideoState(Constants.VideoState.PLAYING);
            }
        }
        if (mAdParams.isVideo360()) {
            initVr360();
        }
    }

    public void initVr360() {
        mViewController.initVRLibrary(mLoopMeAd.getContext());
        mViewController.onResume();
    }

    private void destroyVideoController() {
        if (mVideoController != null) {
            mVideoController.destroy();
        }
    }

    private void stopVideoLoader() {
        if (mFileLoader != null) {
            mFileLoader.stop();
        }
    }

    @Override
    public WebView getWebView() {
        if (mAdView != null) {
            return mAdView;
        } else {
            return mMraidView;
        }
    }

    public void setFullscreenMode(boolean isFullScreen) {
        if (mAdView != null) {
            mAdView.setFullscreenMode(isFullScreen);
        }
    }

    public void rebuildView(FrameLayout view) {
        if (mVideoController != null) {
            mViewController.rebuildView(view, mAdView);
        }
    }

    public void setOnTouchListener(SwipeListener swipeListener) {
        if (mAdView != null) {
            mAdView.setOnTouchListener(swipeListener);
        }
    }

    public void setMinimizedMode(MinimizedMode mode) {
        if (mDisplayModeResolver != null) {
            mDisplayModeResolver.setMinimizedMode(mode);
        }
    }

    public boolean isMinimizedModeEnable() {
        return mDisplayModeResolver != null && mDisplayModeResolver.isMinimizedModeEnable();
    }

    public void switchToMinimizedMode() {
        if (mDisplayModeResolver != null) {
            mDisplayModeResolver.switchToMinimizedMode();
        }
    }

    public void switchToNormalMode() {
        if (mDisplayModeResolver != null) {
            mDisplayModeResolver.switchToNormalMode();
        }
    }

    public void dismissAd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.dismiss();
        }
    }

    public boolean isVideoPlaying() {
        return getCurrentVideoState() == Constants.VideoState.PLAYING;
    }

    public boolean isVideoPaused() {
        return getCurrentVideoState() == Constants.VideoState.PAUSED;
    }

}