package com.loopme.controllers.display;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeGestureListener;
import com.loopme.MinimizedMode;
import com.loopme.ad.LoopMeAd;
import com.loopme.utils.UiUtils;

public class DisplayModeResolver {
    private static final String LOG_TAG = DisplayModeResolver.class.getSimpleName();
    private static final long ANIMATION_DURATION = 200;

    private boolean mIsFirstFullScreenCommand = true;
    private Constants.DisplayMode mCurrentDisplayMode = Constants.DisplayMode.NORMAL;
    private Constants.DisplayMode mPreviousDisplayMode = Constants.DisplayMode.NORMAL;
    private final LoopMeAd mLoopMeAd;
    private FrameLayout mMinimizedView;
    private MinimizedMode mMinimizedMode;
    private final DisplayControllerLoopMe mDisplayControllerLoopMe;

    public DisplayModeResolver(DisplayControllerLoopMe controllerLoopMe, LoopMeAd loopMeAd) {
        mDisplayControllerLoopMe = controllerLoopMe;
        mLoopMeAd = loopMeAd;
    }

    public void switchToPreviousMode() {
        if (isPreviousMinimizedMode()) {
            switchToMinimizedMode();
        } else if (isPreviousNormalMode()) {
            switchToNormalMode();
        }
        setFullscreenMode(false);
    }

    public void switchToFullScreenMode(boolean isFullScreen) {
        if (!isFirstCommand()) { //we should ignore first command
            switchToFullScreenModeInternal(isFullScreen);
        }
    }

    private void switchToFullScreenModeInternal(boolean isFullScreen) {
        if (isFullScreen) {
            switchToFullScreenMode();
        } else {
            broadcastDestroyIntent();
        }
        setFullscreenMode(isFullScreen);
    }


    public void destroy() {
        destroyMinimizedView();
    }

    private void switchToFullScreenMode() {
        if (isFullScreenMode()) {
            return;
        }
        setDisplayMode(Constants.DisplayMode.FULLSCREEN);
        removeMinimizedView();
        AdUtils.startAdActivity(mLoopMeAd);
    }

    public void switchToNormalMode() {
        if (isNormalMode()) {
            return;
        }
        setDisplayMode(Constants.DisplayMode.NORMAL);
        removeMinimizedView();
        rebuildView();
    }

    public void switchToMinimizedMode() {
        if (isMinimizedMode()) {
            if (isVideoPausedOrPlaying()) {
                setWebViewState(Constants.WebviewState.VISIBLE);
            }
            return;
        }
        setDisplayMode(Constants.DisplayMode.MINIMIZED);
        configureMinimizedView();
        mMinimizedView.setOnTouchListener(initGestureListener());
        rebuildView(mMinimizedView, Constants.DisplayMode.MINIMIZED);
        setWebViewState(Constants.WebviewState.VISIBLE);
    }

    private void configureMinimizedView() {
        mMinimizedView = UiUtils.createFrameLayout(mLoopMeAd.getContext(), mMinimizedMode.getMinimizedViewDims());
        mMinimizedMode.addView(mMinimizedView);
        UiUtils.configMinimizedViewLayoutParams(mMinimizedView, mMinimizedMode);
    }

    private void rebuildView() {
        if (mLoopMeAd == null) {
            return;
        }
        FrameLayout initialView = mLoopMeAd.getContainerView();
        rebuildView(initialView, Constants.DisplayMode.NORMAL);
        initialView.setVisibility(View.VISIBLE);
    }

    private void rebuildView(FrameLayout view, Constants.DisplayMode displayMode) {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.rebuildView(view, displayMode);
        }
    }

    private void removeMinimizedView() {
        if (mMinimizedView != null && mMinimizedView.getParent() != null) {
            ((ViewGroup) mMinimizedView.getParent()).removeView(mMinimizedView);
        }
    }

    private void destroyMinimizedView() {
        if (mMinimizedView == null) {
            return;
        }
        removeMinimizedView();
        mMinimizedView.removeAllViews();
        mMinimizedView = null;
    }

    private boolean isVideoPausedOrPlaying() {
        return isCurrentVideoStatePaused() || isCurrentVideoStatePlaying();
    }

    public void setMinimizedMode(MinimizedMode mode) {
        mMinimizedMode = mode;
    }

    public boolean isMinimizedModeEnable() {
        return mMinimizedMode != null && mMinimizedMode.getRootView() != null;
    }

    private void dismissMinimizedView(boolean toRight) {
        setWebViewState(Constants.WebviewState.HIDDEN);
        setCurrentDisplayMode(Constants.DisplayMode.NORMAL);
        animateDismissOnSwipe(toRight);
        mMinimizedMode = null;
        dismissAd();
    }

    private View.OnTouchListener initGestureListener() {
        return new LoopMeGestureListener(mLoopMeAd.getContext(), new LoopMeGestureListener.Listener() {
            @Override
            public void onSwipe(boolean toRight) {
                dismissMinimizedView(toRight);
            }
            @Override
            public void onClick() {
                onMinimizedViewClicked();
            }
        });
    }

    private void onMinimizedViewClicked() {
        if (mMinimizedMode != null) {
            mMinimizedMode.onViewClicked();
        }
    }

    private void animateDismissOnSwipe(boolean toRight) {
        Animation anim = AnimationUtils.makeOutAnimation(mLoopMeAd.getContext(), toRight);
        anim.setDuration(ANIMATION_DURATION);
        mMinimizedView.startAnimation(anim);
    }

    private void dismissAd() {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.dismissAd();
        }
    }

    private void broadcastDestroyIntent() {
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
    }

    public Constants.DisplayMode getDisplayMode() {
        return mCurrentDisplayMode;
    }

    public boolean isMinimizedMode() {
        return mCurrentDisplayMode == Constants.DisplayMode.MINIMIZED;
    }

    private boolean isNormalMode() {
        return mCurrentDisplayMode == Constants.DisplayMode.NORMAL;
    }

    public boolean isFullScreenMode() {
        return mCurrentDisplayMode == Constants.DisplayMode.FULLSCREEN;
    }

    private boolean isPreviousNormalMode() {
        return mPreviousDisplayMode == Constants.DisplayMode.NORMAL;
    }

    private boolean isPreviousMinimizedMode() {
        return mPreviousDisplayMode == Constants.DisplayMode.MINIMIZED;
    }

    public MinimizedMode getMinimizedMode() {
        return mMinimizedMode;
    }

    private void setFullscreenMode(boolean mIsFirstFullScreen) {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.setFullscreenMode(mIsFirstFullScreen);
        }
    }

    private void setDisplayMode(Constants.DisplayMode mode) {
        setPreviousDisplayMode(mCurrentDisplayMode);
        setCurrentDisplayMode(mode);
        Logging.out(LOG_TAG, "switch to " + mode.name() + " mode");
    }

    private void setCurrentDisplayMode(Constants.DisplayMode displayMode) {
        mCurrentDisplayMode = displayMode;
    }

    private void setPreviousDisplayMode(Constants.DisplayMode displayMode) {
        mPreviousDisplayMode = displayMode;
    }

    private boolean isCurrentVideoStatePlaying() {
        return mDisplayControllerLoopMe != null && mDisplayControllerLoopMe.isVideoPlaying();
    }

    private boolean isCurrentVideoStatePaused() {
        return mDisplayControllerLoopMe != null && mDisplayControllerLoopMe.isVideoPaused();
    }

    private void setWebViewState(Constants.WebviewState webViewState) {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.setWebViewState(webViewState);
        }
    }

    private boolean isFirstCommand() {
        if (!mIsFirstFullScreenCommand) {
            return false;
        }
        mIsFirstFullScreenCommand = false;
        setFullscreenMode(false);
        return true;
    }
}
