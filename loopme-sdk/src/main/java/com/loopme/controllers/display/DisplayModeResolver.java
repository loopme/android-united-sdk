package com.loopme.controllers.display;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.loopme.AdUtils;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.MinimizedMode;
import com.loopme.SwipeListener;
import com.loopme.ad.LoopMeAd;
import com.loopme.utils.UiUtils;

public class DisplayModeResolver {
    private static final String LOG_TAG = DisplayModeResolver.class.getSimpleName();
    private static final long ANIMATION_DURATION = 200;

    private boolean mIsFirstFullScreenCommand = true;
    private Constants.DisplayMode mCurrentDisplayMode = Constants.DisplayMode.NORMAL;
    private Constants.DisplayMode mPreviousDisplayMode = Constants.DisplayMode.NORMAL;
    private LoopMeAd mLoopMeAd;
    private FrameLayout mMinimizedView;
    private MinimizedMode mMinimizedMode;
    private DisplayControllerLoopMe mDisplayControllerLoopMe;

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
        if (!isFullScreenMode()) {
            setDisplayMode(Constants.DisplayMode.FULLSCREEN);
            removeMinimizedView();
            AdUtils.startAdActivity(mLoopMeAd);
        }
    }

    public void switchToNormalMode() {
        if (!isNormalMode()) {
            setDisplayMode(Constants.DisplayMode.NORMAL);
            removeMinimizedView();
            rebuildView();
        }
    }

    public void switchToMinimizedMode() {
        if (isMinimizedMode()) {
            if (isVideoPausedOrPlaying()) {
                setWebViewState(Constants.WebviewState.VISIBLE);
            }
        } else {
            setDisplayMode(Constants.DisplayMode.MINIMIZED);
            configureMinimizedView();
            rebuildView(mMinimizedView);
            setWebViewState(Constants.WebviewState.VISIBLE);
        }
    }

    private void configureMinimizedView() {
        mMinimizedView = UiUtils.createFrameLayout(mLoopMeAd.getContext(), mMinimizedMode.getMinimizedViewDims());
        UiUtils.addBordersToView(mMinimizedView);
        mMinimizedMode.addView(mMinimizedView);
        UiUtils.configMinimizedViewLayoutParams(mMinimizedView, mMinimizedMode);
        mDisplayControllerLoopMe.setOnTouchListener(initSwipeListener());
    }

    private void rebuildView() {
        if (mLoopMeAd != null) {
            FrameLayout initialView = mLoopMeAd.getContainerView();
            rebuildView(initialView);
            initialView.setVisibility(View.VISIBLE);
        }
    }

    private void rebuildView(FrameLayout view) {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.rebuildView(view);
        }
    }

    private void removeMinimizedView() {
        if (mMinimizedView != null && mMinimizedView.getParent() != null) {
            ((ViewGroup) mMinimizedView.getParent()).removeView(mMinimizedView);
        }
    }

    private void destroyMinimizedView() {
        if (mMinimizedView != null) {
            removeMinimizedView();
            mMinimizedView.removeAllViews();
            mMinimizedView = null;
        }
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

    private SwipeListener initSwipeListener() {
        return new SwipeListener(mMinimizedMode.getWidth(), new SwipeListener.Listener() {
            @Override
            public void onSwipe(boolean toRight) {
                dismissMinimizedView(toRight);
            }
        });
    }

    private void dismissMinimizedView(boolean toRight) {
        setWebViewState(Constants.WebviewState.HIDDEN);
        setCurrentDisplayMode(Constants.DisplayMode.NORMAL);
        animateDismissOnSwipe(toRight);
        mMinimizedMode = null;
        dismissAd();
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
        if (mIsFirstFullScreenCommand) {
            mIsFirstFullScreenCommand = false;
            setFullscreenMode(false);
            return true;
        } else {
            return false;
        }
    }
}
