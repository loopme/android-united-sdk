package com.loopme.controllers.display;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayControllerLoopMe
        extends BaseTrackableController
        implements LoopMeDisplayController {

    private static final String LOG_TAG = DisplayControllerLoopMe.class.getSimpleName();

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

    public void initControllers() {
        if (isMraidAd()) {
            onMessage(Message.LOG, "initMraidController");
            mMraidController = new MraidController(mLoopMeAd);
            mMraidView = new MraidView(mLoopMeAd.getContext(), mMraidController, createAdReadyListener());
            return;
        }
        initLoopMeControllers();
        mVideoController = new VideoController(mAdView, initVideoControllerCallback(), mLoopMeAd.getAdFormat());
        initViewController();
    }

    private void initLoopMeControllers() {
        onMessage(Message.LOG, "initLoopMeSdkController");
        mAdView = new AdView(mLoopMeAd.getContext(), createAdReadyListener());
        mBridgeListener = initBridgeListener();
        mAdView.addBridgeListener(mBridgeListener);
        mOnTouchListener = (v, event) -> {
            mViewController.handleTouchEvent(event);
            return false;
        };
        mAdView.setOnTouchListener(mOnTouchListener);
    }

    public void initViewController() {
        ViewControllerLoopMe.Callback viewCallback = initViewControllerCallback();
        mViewController = new ViewControllerLoopMe(viewCallback);
    }

    private void buildMraidContainer(FrameLayout containerView) {
        if (mMraidController != null)
            mMraidController.buildMraidContainer(containerView);
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
        if (isMraidAd() && mLoopMeAd instanceof LoopMeBannerGeneral) {
            LoopMeBannerGeneral banner = (LoopMeBannerGeneral) mLoopMeAd;
            buildMraidContainer(banner.getBannerView());
            onCollapseBanner();
        }
    }

    private void onCollapseBanner() {
        if (mMraidController != null) {
            mMraidController.onCollapseBanner();
        }
    }

    private void handleVideoLoad(String videoUrl) {
        onMessage(Message.LOG, "JS command: resolve video " + videoUrl);
        mVideoPresented = true;
        FileLoaderNewImpl.Callback callback = new FileLoaderNewImpl.Callback() {
            @Override
            public void onError(LoopMeError error) {
                onAdLoadFail(error);
            }
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

    private boolean isInterstitial() { return mLoopMeAd != null && mLoopMeAd.isInterstitial(); }

    private boolean isBanner() { return mLoopMeAd != null && mLoopMeAd.isBanner(); }

    private void onAdVideoDidReachEnd() {
        if (mLoopMeAd != null) {
            mLoopMeAd.onAdVideoDidReachEnd();
        }
    }

    private void onAdLoadFail(LoopMeError error) {
        if (mLoopMeAd == null || error == null) {
            return;
        }
        error.setErrorType(Constants.ErrorType.CUSTOM);
        mLoopMeAd.onInternalLoadFail(error);
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

    private static String addMraidScript(String html) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }
        Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<\\s*script\\b[^>]*>");
        Matcher m = SCRIPT_TAG_PATTERN.matcher(html);
        if (!m.find()) {
            Logging.out(LOG_TAG, "Couldn't find <script>");
            return html;
        }
        // TODO. Performance?
        return new StringBuilder(html).insert(m.start(), Constants.MRAID_SCRIPT).toString();
    }

    private void preloadHtml() {
        onAdRegisterView(mLoopMeAd.getContext(), getWebView());
        injectTrackingJsForWeb();
        final String preInjectOmidHtml =
            isMraidAd() ? addMraidScript(mAdParams.getHtml()) : mAdParams.getHtml();

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
        omidEventTrackerWrapper = new OmidEventTrackerWrapper(
            AdEvents.createAdEvents(omidAdSession), null)
        ;
        omidAdSession.registerAdView(wv);
        omidAdSession.start();
        omidEventTrackerWrapper.sendLoaded();
    }

    private void injectTrackingJsForWeb() {
        onAdInjectJs(mLoopMeAd);
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
        if (isBanner()) {
            if (isFullScreen()) {
                setWebViewState(Constants.WebviewState.VISIBLE);
                return ;
            }
            if (!isBanner() || mLoopMeAd == null) {
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
            resumeViewController();
            if (mVideoController != null) {
                mVideoController.resumeVideo();
            }
            setWebViewState(Constants.WebviewState.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseViewController();
        if (mMraidView != null) {
            mMraidView.setIsViewable(false);
        }
        if (!isInterstitial()) {
            return;
        }
        pauseVideoController(false);
        setWebViewState(Constants.WebviewState.HIDDEN);
    }

    private void resumeViewController() {
        if (mViewController != null) {
            mViewController.onResume();
        }
    }

    private void pauseViewController() {
        if (mViewController != null) {
            mViewController.onPause();
        }
    }

    private void pauseVideoController(boolean isSkip) {
        if (mVideoController != null) {
            mVideoController.pauseVideo(isSkip);
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
        super.onDestroy();
        if (omidAdSession != null)
            omidAdSession.finish();
        omidAdSession = null;
        omidEventTrackerWrapper = null;
        needWaitOmidJsLoad = false;
        destroyMraidController();
        if (mVideoController != null) {
            mVideoController.destroy();
        }
        if (mFileLoader != null) {
            mFileLoader.stop();
        }
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mMraidView != null) {
            mMraidView.destroy();
        }
        mAdView = null;
        mMraidView = null;
        mBridgeListener = null;
        mOnTouchListener = null;
        mDisplayModeResolver.destroy();
    }

    private void destroyMraidController() {
        if (mMraidController != null) {
            mMraidController.destroyExpandedView();
        }
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.CLICK_INTENT);
        onAdClicked();
        super.onRedirect(url, loopMeAd);
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
        if (mMraidView == null || containerView == null) {
            return;
        }
        buildMraidContainer(containerView);
        mMraidView.setIsViewable(true);
        mMraidView.notifyStateChange();
    }

    @Override
    public void onRebuildView(FrameLayout containerView) {
        if (isMraidAd()) {
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
    public boolean isNativeAd() {
        return isVideoPresented();
    }

    @Override
    public boolean isFullScreen() {
        boolean isMraidExpandMode = mMraidController != null && mMraidController.isExpanded();
        return (mDisplayModeResolver != null && mDisplayModeResolver.isFullScreenMode()) || isMraidExpandMode;
    }

    public void setFullScreen(boolean isFullScreen) {
        mDisplayModeResolver.switchToFullScreenMode(isFullScreen);
    }

    @Override
    public int getOrientation() {
        if (isMraidAd()) {
            return getMraidOrientation();
        }
        if (isInterstitial()) {
            return getOrientationFromAdParams();
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
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
        if (mAdView != null)
            mAdView.setWebViewState(state);
        if (mMraidView != null)
            mMraidView.setWebViewState(state);
        if (omidEventTrackerWrapper != null && state == Constants.WebviewState.VISIBLE)
            omidEventTrackerWrapper.sendOneTimeImpression();
    }

    private Bridge.Listener initBridgeListener() {
        return new Bridge.Listener() {
            @Override
            public void onJsVideoPlay(int time) {
                onPlay(time);
            }
            @Override
            public void onJsVideoPause(final int time) {
                pauseViewController();
                pauseVideoController(true);
                onAdSkippedEvent();
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
                if (mLoopMeAd != null) {
                    mLoopMeAd.onAdLoadSuccess();
                }
            }
            @Override
            public void onJsClose() {
                onAdUserCloseEvent();
                destroyLoopMeAd();
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
                Constants.StretchOption stretch = videoStretch ?
                    Constants.StretchOption.STRETCH : Constants.StretchOption.NO_STRETCH;
                mViewController.setStretchParam(stretch);
            }
        };
    }

    public void switchToPreviousMode() {
        if (isBanner() && mDisplayModeResolver != null) {
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
                MinimizedMode minimizedMode = mDisplayModeResolver.getMinimizedMode();
                Constants.DisplayMode currentDisplayMode = mDisplayModeResolver.getDisplayMode();
                AdSpotDimensions viewSize = UiUtils.getViewSize(minimizedMode, mLoopMeAd, currentDisplayMode);
                mViewController.setViewSize(viewSize.getWidth(), viewSize.getHeight());
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

    private VideoController.Callback initVideoControllerCallback() {
        return new VideoController.Callback() {
            @Override
            public void onVideoReachEnd() {
                onAdCompleteEvent();
                onAdVideoDidReachEnd();
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
                playbackFinishedWithError();
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

    public void closeMraidAd() {
        if (mMraidController != null) {
            mMraidController.close();
        }
    }

    public void buildView(FrameLayout containerView) {
        if (isMraidAd()) {
            onBuildMraidView(containerView);
            return ;
        }
        if (isNativeAd()) {
            onBuildVideoAdView(containerView);
            return ;
        }
        onBuildStaticAdView(containerView);
        setVideoState(Constants.VideoState.PLAYING);
    }

    @Override
    public WebView getWebView() { return isMraidAd() ? mMraidView : mAdView; }

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

    public boolean isVideoPlaying() { return getCurrentVideoState() == Constants.VideoState.PLAYING; }

    public boolean isVideoPaused() { return getCurrentVideoState() == Constants.VideoState.PAUSED; }

    public int getMraidOrientation() {
        return mMraidController != null ?
            mMraidController.getForceOrientation() : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public void dismiss() { setWebViewState(Constants.WebviewState.CLOSED); }

    private boolean isMraidAd() { return mLoopMeAd != null && mLoopMeAd.isMraidAd(); }
}