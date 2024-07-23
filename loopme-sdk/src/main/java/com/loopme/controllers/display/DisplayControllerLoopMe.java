package com.loopme.controllers.display;

import static com.loopme.Constants.StretchOption.NO_STRETCH;
import static com.loopme.Constants.StretchOption.STRETCH;
import static com.loopme.utils.FileUtils.loadAssetFileAsString;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iab.omid.library.loopme.adsession.AdEvents;
import com.iab.omid.library.loopme.adsession.AdSession;
import com.iab.omid.library.loopme.adsession.FriendlyObstructionPurpose;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeBannerGeneral;
import com.loopme.MinimizedMode;
import com.loopme.ViewAbilityUtils;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.Bridge;
import com.loopme.common.LoopMeError;
import com.loopme.controllers.MraidController;
import com.loopme.controllers.VideoController;
import com.loopme.controllers.interfaces.LoopMeDisplayController;
import com.loopme.controllers.view.IViewController;
import com.loopme.controllers.view.ViewControllerLoopMe;
import com.loopme.listener.Listener;
import com.loopme.loaders.FileLoaderNewImpl;
import com.loopme.loaders.Loader;
import com.loopme.models.Errors;
import com.loopme.models.Message;
import com.loopme.om.OmidEventTrackerWrapper;
import com.loopme.om.OmidHelper;
import com.loopme.utils.UiUtils;
import com.loopme.views.AdView;
import com.loopme.views.LoopMeWebView;
import com.loopme.views.MraidView;

public class DisplayControllerLoopMe extends BaseTrackableController implements LoopMeDisplayController {

    private static final String LOG_TAG = DisplayControllerLoopMe.class.getSimpleName();

    private static final int MRAID_WIDTH = 320;
    private static final int MRAID_HEIGHT = 480;
    private boolean mVideoPresented;

    private LoopMeAd mLoopMeAd;
    private AdParams mAdParams;
    private AdView mAdView;
    private MraidView mMraidView;
    private Loader mFileLoader;
    private VideoController mVideoController;
    private IViewController mViewController;
    private MraidController mMraidController;
    private DisplayModeResolver mDisplayModeResolver;

    private AdSession omidAdSession;
    private OmidEventTrackerWrapper omidEventTrackerWrapper;
    private boolean needWaitOmidJsLoad;

    public DisplayControllerLoopMe(@NonNull LoopMeAd loopMeAd) {
        super(loopMeAd);
        mLoopMeAd = loopMeAd;
        mAdParams = mLoopMeAd.getAdParams();
        mDisplayModeResolver = new DisplayModeResolver(this, loopMeAd);
        mLogTag = DisplayControllerLoopMe.class.getSimpleName();
        Logging.out(mLogTag);
    }

    private VideoController.Callback initVideoControllerCallback() {
        return new VideoController.Callback() {
            @Override
            public void onVideoReachEnd() {
                onAdCompleteEvent();
                if (mLoopMeAd != null) {
                    mLoopMeAd.onAdVideoDidReachEnd();
                }
            }
            @Override
            public void onFail(LoopMeError error) {
                onAdLoadFail(error);
            }
            @Override
            public void onVideoSizeChanged(int width, int height) {
                if (mViewController != null) {
                    mViewController.setVideoSize(width, height);
                }
            }
            @Override
            public void onPlaybackFinishedWithError() {
                boolean isBanner = mLoopMeAd != null && mLoopMeAd.isBanner();
                if (mLoopMeAd != null && isBanner) {
                    ((LoopMeBannerGeneral) mLoopMeAd).playbackFinishedWithError();
                }
            }
            @Override
            public void onVolumeChangedEvent(float volume, int currentPosition) {
                onAdVolumeChangedEvent(volume, currentPosition);
            }
            @Override
            public void onDurationChangedEvent(int currentPosition, int adDuration) {
                onAdDurationEvents(currentPosition, adDuration);
            }
        };
    }

    // TODO. Ugly.
    public void tryAddOmidFriendlyObstructionCloseButton(View view) {
        if (omidAdSession == null || view == null)
            return;
        try {
            omidAdSession.addFriendlyObstruction(view, FriendlyObstructionPurpose.CLOSE_AD, null);
        } catch (RuntimeException ex) {
            Logging.out(LOG_TAG, ex.toString());
        }
    }

    // TODO. Ugly.
    public void tryRemoveOmidFriendlyObstruction(View view) {
        if (omidAdSession == null || view == null)
            return;
        omidAdSession.removeFriendlyObstruction(view);
    }

    public void collapseMraidBanner() {
        boolean isMraidAd = mLoopMeAd != null && mLoopMeAd.isMraidAd();
        if (isMraidAd && mLoopMeAd instanceof LoopMeBannerGeneral) {
            LoopMeBannerGeneral banner = (LoopMeBannerGeneral) mLoopMeAd;
            if (mMraidController != null) {
                mMraidController.buildMraidContainer(banner.getBannerView());
                mMraidController.onCollapseBanner();
            }
        }
    }

    private void onAdLoadFail(LoopMeError error) {
        if (mLoopMeAd == null || error == null) {
            return;
        }
        error.setErrorType(Constants.ErrorType.CUSTOM);
        mLoopMeAd.onInternalLoadFail(error);
    }

    private Bridge.Listener initBridgeListener() {
        return new Bridge.Listener() {
            @Override
            public void onJsVideoPlay(int time) {
                onPlay(time);
            }
            @Override
            public void onJsVideoPause(final int time) {
                if (mViewController != null) {
                    mViewController.onPause();
                }
                if (mVideoController != null) {
                    mVideoController.pauseVideo(true);
                }
                onAdSkippedEvent();
            }
            @Override
            public void onJsVideoMute(boolean mute) {
                onVolumeMute(mute);
            }
            @Override
            public void onJsVideoLoad(final String videoUrl) {
                onMessage(Message.LOG, "JS command: resolve video " + videoUrl);
                mVideoPresented = true;
                FileLoaderNewImpl.Callback callback = new FileLoaderNewImpl.Callback() {
                    @Override
                    public void onError(LoopMeError error) { onAdLoadFail(error); }
                    @Override
                    public void onFileFullLoaded(String filePath) {
                        onMessage(Message.LOG, "fullVideoLoaded: " + filePath);
                        if (mVideoController != null) {
                            mVideoController.fullVideoLoaded(filePath, mAdParams.getPartPreload(), mLoopMeAd.isShowing());
                        }
                    }
                };
                mFileLoader = new FileLoaderNewImpl(videoUrl, mLoopMeAd.getContext(), callback);
                mFileLoader.start();
            }
            @Override
            public void onJsLoadSuccess() {
                if (mLoopMeAd != null) {
                    mLoopMeAd.onAdLoadSuccess();
                }
            }
            @Override
            public void onJsClose() {
                onAdUserCloseEvent();
                if (mLoopMeAd != null) {
                    mLoopMeAd.dismiss();
                }
            }
            @Override
            public void onJsLoadFail(String mess) {
                onAdLoadFail(Errors.FAILED_TO_PROCESS_AD);
            }
            @Override
            public void onJsFullscreenMode(boolean isFullScreen) {
                if (mDisplayModeResolver != null) {
                    mDisplayModeResolver.switchToFullScreenMode(isFullScreen);
                }
            }
            @Override
            public void onNonLoopMe(String url) {
                onRedirect(url, mLoopMeAd);
            }
            @Override
            public void onJsVideoStretch(boolean videoStretch) {
                onMessage(Message.LOG, "JS command: stretch video ");
                mViewController.setStretchParam(videoStretch ? STRETCH : NO_STRETCH);
            }
        };
    }

    public void initControllers() {
        boolean isMraidAd = mLoopMeAd != null && mLoopMeAd.isMraidAd();
        if (isMraidAd) {
            onMessage(Message.LOG, "initMraidController");
            mMraidController = new MraidController(mLoopMeAd);
            mMraidView = new MraidView(mLoopMeAd.getContext(), mMraidController, createAdReadyListener());
            return;
        }
        onMessage(Message.LOG, "initLoopMeSdkController");
        mAdView = new AdView(mLoopMeAd.getContext(), createAdReadyListener());
        mAdView.addBridgeListener(initBridgeListener());
        mAdView.setOnTouchListener((v, event) -> {
            mViewController.handleTouchEvent(event);
            return false;
        });
        mVideoController = new VideoController(mAdView, initVideoControllerCallback(), mLoopMeAd.getAdFormat());
        mViewController = new ViewControllerLoopMe(initViewControllerCallback());
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        initControllers();
        initTrackers();
        preloadHtml();
    }

    private void preloadHtml() {
        onAdRegisterView(mLoopMeAd.getContext(), getWebView());
        onAdInjectJs(mLoopMeAd);
        boolean isMraid = mAdParams.getHtml().contains("mraid.js");
        String mraid = "<script>" + loadAssetFileAsString(mLoopMeAd.getContext(), "mraid.js") + "</script>";
        final String preInjectOmidHtml = isMraid ? mraid + mAdParams.getHtml() : mAdParams.getHtml();

        OmidHelper.injectScriptContentIntoHtmlAsync(
            mLoopMeAd.getContext().getApplicationContext(),
            preInjectOmidHtml,
            new OmidHelper.ScriptInjectListener() {
                @Override
                public void onReady(String injectedOmidHtml) {
                    onOmidScriptInjectResult(injectedOmidHtml, null);
                }
                @Override
                public void onError(String injectOmidError) {
                    onOmidScriptInjectResult(preInjectOmidHtml, injectOmidError);
                }
            }
        );
    }

    private void onOmidScriptInjectResult(String html, String injectOmidError) {
        Logging.out(LOG_TAG, injectOmidError);
        // Omid has been injected successfully.
        // Wait for html loading and then create omid ad session.
        if (TextUtils.isEmpty(injectOmidError))
            needWaitOmidJsLoad = true;
        // Start loading html.
        WebView wv = getWebView();
        if (wv instanceof LoopMeWebView)
            ((LoopMeWebView) wv).loadHtml(html);
    }

    // TODO. Ugly. Use LoopMeAd.onLoadSuccess at least.
    private Listener createAdReadyListener() {
        return this::tryCreateOmidAdSession;
    }

    // TODO. Refactor.
    private void tryCreateOmidAdSession() {
        if (!needWaitOmidJsLoad)
            return;
        needWaitOmidJsLoad = false;
        if (omidAdSession != null)
            return;
        WebView wv = getWebView();
        omidAdSession = OmidHelper.createWebDisplayAdSession(wv);
        // Something went wrong with omid. See logs.
        if (omidAdSession == null)
            return;
        omidEventTrackerWrapper =
            new OmidEventTrackerWrapper(AdEvents.createAdEvents(omidAdSession), null);
        omidAdSession.registerAdView(wv);
        omidAdSession.start();
        omidEventTrackerWrapper.sendLoaded();
    }

    @Override
    public void onPlay(int position) {
        if (mVideoController != null) {
            mViewController.onResume();
            mVideoController.playVideo(position);
        }
        onStartWebMeasuringDelayed();
    }

    @Override
    public void onResume() {
        boolean isBanner = mLoopMeAd != null && mLoopMeAd.isBanner();
        if (isBanner) {
            if (isFullScreen()) {
                setWebViewState(Constants.WebviewState.VISIBLE);
                return ;
            }
            if (mLoopMeAd == null) {
                return;
            }
            ViewAbilityUtils.calculateViewAbilitySyncDelayed(mLoopMeAd.getContainerView(), info -> {
                if (!info.isVisibleMore50Percents()) {
                    setWebViewState(Constants.WebviewState.HIDDEN);
                    return;
                }
                setWebViewState(Constants.WebviewState.VISIBLE);
                onNewActivity(mLoopMeAd.getContext());
                onStartWebMeasuringDelayed();
            });
        } else {
            if (mMraidView != null) {
                mMraidView.notifySizeChangeEvent(MRAID_WIDTH, MRAID_HEIGHT);
                mMraidView.setIsViewable(true);
            }
            if (mVideoController != null) {
                mViewController.onResume();
                mVideoController.resumeVideo();
            }
            setWebViewState(Constants.WebviewState.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewController != null) {
            mViewController.onPause();
        }
        if (mMraidView != null) {
            mMraidView.setIsViewable(false);
        }
        boolean isInterstitial = mLoopMeAd != null && mLoopMeAd.isInterstitial();
        if (!isInterstitial) {
            return;
        }
        if (mVideoController != null) {
            mVideoController.pauseVideo(false);
        }
        setWebViewState(Constants.WebviewState.HIDDEN);
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
        super.onDestroy();
        if (omidAdSession != null)
            omidAdSession.finish();
        omidAdSession = null;
        omidEventTrackerWrapper = null;
        needWaitOmidJsLoad = false;
        if (mMraidController != null) {
            mMraidController.destroyExpandedView();
        }
        if (mVideoController != null) {
            mVideoController.destroy();
        }
        if (mFileLoader != null) {
            mFileLoader.stop();
        }
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
        if (mMraidView != null) {
            mMraidView.destroy();
            mMraidView = null;
        }
        mDisplayModeResolver.destroy();
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.CLICK_INTENT);
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdClicked();
        }
        super.onRedirect(url, loopMeAd);
    }

    @Override
    public void onBuildStaticAdView(FrameLayout containerView) {
        if (containerView == null || mAdView == null) {
            return;
        }
        mAdView.setBackgroundColor(Color.BLACK);
        containerView.addView(mAdView);
    }

    @Override
    public void onBuildMraidView(FrameLayout containerView) {
        if (mMraidView == null || containerView == null) {
            return;
        }
        if (mMraidController != null)
            mMraidController.buildMraidContainer(containerView);
        mMraidView.setIsViewable(true);
        mMraidView.notifyStateChange();
    }

    @Override
    public void onRebuildView(FrameLayout containerView) {
        if (mLoopMeAd != null && mLoopMeAd.isMraidAd()) {
            if (mMraidController != null) {
                mMraidController.onRebuildView(containerView);
            }
        } else {
            if (mViewController != null) {
                mViewController.rebuildView(containerView, mAdView, Constants.DisplayMode.NORMAL);
            }
        }
    }

    @Override
    public boolean isFullScreen() {
        boolean isMraidExpandMode = mMraidController != null && mMraidController.isExpanded();
        return (mDisplayModeResolver != null && mDisplayModeResolver.isFullScreenMode()) || isMraidExpandMode;
    }

    @Override
    public int getOrientation() {
        if (mLoopMeAd != null && mLoopMeAd.isMraidAd() && mMraidController != null) {
            return mMraidController.getForceOrientation();
        }
        if (mLoopMeAd != null && mLoopMeAd.isInterstitial()) {
            return getOrientationFromAdParams();
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public void setWebViewState(Constants.WebviewState state) {
        if (mAdView != null)
            mAdView.setWebViewState(state);
        if (mMraidView != null)
            mMraidView.setWebViewState(state);
        if (omidEventTrackerWrapper != null && state == Constants.WebviewState.VISIBLE)
            omidEventTrackerWrapper.sendOneTimeImpression();
    }

    public void switchToPreviousMode() {
        if (mLoopMeAd != null && mLoopMeAd.isBanner() && mDisplayModeResolver != null) {
            mDisplayModeResolver.switchToPreviousMode();
        }
    }

    private ViewControllerLoopMe.Callback initViewControllerCallback() {
        return new ViewControllerLoopMe.Callback() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture) {
                onMessage(Message.LOG, "onSurfaceTextureAvailable");
                if (mVideoController != null) {
                    mVideoController.setSurfaceTextureAvailable(true);
                    mVideoController.setSurfaceTexture(surfaceTexture);
                }
                AdSpotDimensions current = mDisplayModeResolver.getDimensionByDisplayMode();
                mViewController.setViewSize(current.getWidth(), current.getHeight());
            }
            @Override
            public void onSurfaceTextureDestroyed() {
                onMessage(Message.LOG, "onSurfaceTextureDestroyed");
                if (mVideoController != null) {
                    mVideoController.surfaceTextureDestroyed();
                }
            }
        };
    }

    public void closeMraidAd() {
        if (mMraidController != null) {
            mMraidController.close();
        }
    }

    public void buildView(FrameLayout containerView) {
        if (mLoopMeAd != null && mLoopMeAd.isMraidAd()) {
            onBuildMraidView(containerView);
            return ;
        }
        if (mVideoPresented) {
            onBuildVideoAdView(containerView);
            return ;
        }
        onBuildStaticAdView(containerView);
        if (mAdView != null) {
            mAdView.setVideoState(Constants.VideoState.PLAYING);
        }
    }

    @Override
    public WebView getWebView() {
        return mLoopMeAd != null && mLoopMeAd.isMraidAd() ? mMraidView : mAdView;
    }

    public void setFullscreenMode(boolean isFullScreen) {
        if (mAdView != null) {
            mAdView.setFullscreenMode(isFullScreen);
        }
    }

    public void rebuildView(FrameLayout view, Constants.DisplayMode displayMode) {
        if (mVideoController != null) {
            mViewController.rebuildView(view, mAdView, displayMode);
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

    public boolean isVideoPlaying() { return mAdView.getCurrentVideoState() == Constants.VideoState.PLAYING; }
    public boolean isVideoPaused() { return mAdView.getCurrentVideoState() == Constants.VideoState.PAUSED; }
    public void dismiss() { setWebViewState(Constants.WebviewState.CLOSED); }
}