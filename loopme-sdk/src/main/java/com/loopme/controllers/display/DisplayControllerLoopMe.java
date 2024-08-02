package com.loopme.controllers.display;

import static com.loopme.utils.FileUtils.loadAssetFileAsString;

import android.content.pm.ActivityInfo;
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
import com.loopme.ad.LoopMeAd;
import com.loopme.bridges.MraidBridge;
import com.loopme.controllers.MraidController;
import com.loopme.controllers.interfaces.LoopMeDisplayController;
import com.loopme.om.OmidEventTrackerWrapper;
import com.loopme.om.OmidHelper;
import com.loopme.utils.UiUtils;
import com.loopme.views.MraidView;

public class DisplayControllerLoopMe extends BaseTrackableController implements LoopMeDisplayController {

    private static final String LOG_TAG = DisplayControllerLoopMe.class.getSimpleName();
    private final LoopMeAd mLoopMeAd;
    private final AdParams mAdParams;
    private final MraidView mMraidView;
    private final MraidController mMraidController;
    private final DisplayModeResolver mDisplayModeResolver;

    private AdSession omidAdSession;
    private OmidEventTrackerWrapper omidEventTrackerWrapper;
    private boolean needWaitOmidJsLoad;

    public DisplayControllerLoopMe(@NonNull LoopMeAd loopMeAd) {
        super(loopMeAd);
        mLoopMeAd = loopMeAd;
        mAdParams = mLoopMeAd.getAdParams();
        mDisplayModeResolver = new DisplayModeResolver(this, loopMeAd);
        mMraidView = new MraidView(mLoopMeAd.getContext());
        mMraidController = new MraidController(mLoopMeAd, mMraidView);
        mMraidView.setWebViewClient(new MraidBridge(mMraidController, this::tryCreateOmidAdSession));
    }

    // TODO. Ugly.
    public void tryAddOmidFriendlyObstructionCloseButton(@NonNull View view) {
        if (omidAdSession == null)
            return;
        try {
            omidAdSession.addFriendlyObstruction(view, FriendlyObstructionPurpose.CLOSE_AD, null);
        } catch (RuntimeException ex) {
            Logging.out(LOG_TAG, ex.toString());
        }
    }

    // TODO. Ugly.
    public void tryRemoveOmidFriendlyObstruction(@NonNull View view) {
        if (omidAdSession != null) {
            omidAdSession.removeFriendlyObstruction(view);
        }
    }

    public void collapseMraidBanner() {
        if (mLoopMeAd.isMraidAd() && mLoopMeAd instanceof LoopMeBannerGeneral) {
            mMraidController.buildMraidContainer(((LoopMeBannerGeneral)mLoopMeAd).getBannerView());
            mMraidController.onCollapseBanner();
        }
    }

    @Override
    public void onStartLoad() {
        super.onStartLoad();
        initTrackers();
        preloadHtml();
    }

    @Override
    public void onBuildVideoAdView(FrameLayout frameLayout) { }

    private void preloadHtml() {
        onAdRegisterView(mLoopMeAd.getContext(), mMraidView);
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
        mMraidView.loadHtml(html);
    }

    // TODO. Refactor.
    private void tryCreateOmidAdSession() {
        if (!needWaitOmidJsLoad)
            return;
        needWaitOmidJsLoad = false;
        if (omidAdSession != null)
            return;
        omidAdSession = OmidHelper.createWebDisplayAdSession(mMraidView);
        // Something went wrong with omid. See logs.
        if (omidAdSession == null)
            return;
        omidEventTrackerWrapper =
            new OmidEventTrackerWrapper(AdEvents.createAdEvents(omidAdSession), null);
        omidAdSession.registerAdView(mMraidView);
        omidAdSession.start();
        omidEventTrackerWrapper.sendLoaded();
    }

    @Override
    public void onPlay(int position) { onStartWebMeasuringDelayed(); }

    @Override
    public void onResume() {
        if (mLoopMeAd.isBanner()) {
            if (isFullScreen()) {
                setWebViewState(Constants.WebviewState.VISIBLE);
                return ;
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
            int WIDTH = 320;
            int HEIGHT = 480;
            mMraidView.notifySizeChangeEvent(WIDTH, HEIGHT);
            mMraidView.setIsViewable(true);
            setWebViewState(Constants.WebviewState.VISIBLE);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMraidView.setIsViewable(false);
        if (mLoopMeAd.isInterstitial()) {
            setWebViewState(Constants.WebviewState.HIDDEN);
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
        mMraidController.destroyExpandedView();
        mMraidView.destroy();
        mDisplayModeResolver.destroy();
    }

    @Override
    public void onRedirect(@Nullable String url, LoopMeAd loopMeAd) {
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.CLICK_INTENT);
        mLoopMeAd.onAdClicked();
        super.onRedirect(url, loopMeAd);
    }

    @Override
    public void onVolumeMute(boolean mute) { }

    @Override
    public void onBuildMraidView(@NonNull FrameLayout containerView) {
        mMraidController.buildMraidContainer(containerView);
        mMraidView.setIsViewable(true);
        mMraidView.notifyStateChange();
    }

    @Override
    public void onRebuildView(@NonNull FrameLayout containerView) {
        if (mLoopMeAd.isMraidAd()) {
            mMraidController.onRebuildView(containerView);
        }
    }

    @Override
    public boolean isFullScreen() {
        return mDisplayModeResolver.isFullScreenMode() || mMraidController.isExpanded();
    }

    @Override
    public int getOrientation() {
        if (mLoopMeAd.isMraidAd()) {
            return mMraidController.getForceOrientation();
        }
        if (mLoopMeAd.isInterstitial()) {
            return getOrientationFromAdParams();
        }
        return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public void setWebViewState(Constants.WebviewState state) {
        mMraidView.setWebViewState(state);
        if (omidEventTrackerWrapper != null && state == Constants.WebviewState.VISIBLE)
            omidEventTrackerWrapper.sendOneTimeImpression();
    }

    public void switchToPreviousMode() {
        if (mLoopMeAd.isBanner()) {
            mDisplayModeResolver.switchToPreviousMode();
        }
    }

    public void buildView(@NonNull FrameLayout containerView) {
        if (mLoopMeAd.isMraidAd()) {
            onBuildMraidView(containerView);
        }
    }

    @Override
    public WebView getWebView() { return mMraidView; }

    public void closeMraidAd() { mMraidController.close(); }
    public void setMinimizedMode(MinimizedMode mode) { mDisplayModeResolver.setMinimizedMode(mode); }
    public boolean isMinimizedModeEnable() { return mDisplayModeResolver.isMinimizedModeEnable(); }
    public void switchToMinimizedMode() { mDisplayModeResolver.switchToMinimizedMode(); }
    public void switchToNormalMode() { mDisplayModeResolver.switchToNormalMode(); }

    public void dismissAd() { mLoopMeAd.dismiss(); }
    public void dismiss() { setWebViewState(Constants.WebviewState.CLOSED); }
}