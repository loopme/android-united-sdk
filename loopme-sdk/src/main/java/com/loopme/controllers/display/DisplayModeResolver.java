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
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.utils.UiUtils;

public class DisplayModeResolver {
    private static final String LOG_TAG = DisplayModeResolver.class.getSimpleName();

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
        boolean isPreviousMinimizedMode =
            mPreviousDisplayMode == Constants.DisplayMode.MINIMIZED;
        boolean isPreviousNormalMode = mPreviousDisplayMode == Constants.DisplayMode.NORMAL;
        if (isPreviousMinimizedMode) {
            switchToMinimizedMode();
        } else if (isPreviousNormalMode) {
            switchToNormalMode();
        }
        setFullscreenMode(false);
    }

    private boolean isFirstCommand() {
        if (!mIsFirstFullScreenCommand) {
            return false;
        }
        mIsFirstFullScreenCommand = false;
        setFullscreenMode(false);
        return true;
    }

    public void switchToFullScreenMode(boolean isFullScreen) {
        if (isFirstCommand()) { //we should ignore first command
            return;
        }
        if (isFullScreen) {
            if (!isFullScreenMode()) {
                setDisplayMode(Constants.DisplayMode.FULLSCREEN);
                removeMinimizedView();
                AdUtils.startAdActivity(mLoopMeAd);
            }
        } else {
            UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
        }
        setFullscreenMode(isFullScreen);
    }

    public void destroy() {
        if (mMinimizedView == null) {
            return;
        }
        removeMinimizedView();
        mMinimizedView.removeAllViews();
        mMinimizedView = null;
    }

    public void switchToNormalMode() {
        if (mCurrentDisplayMode == Constants.DisplayMode.NORMAL) {
            return;
        }
        setDisplayMode(Constants.DisplayMode.NORMAL);
        removeMinimizedView();
        if (mLoopMeAd == null) {
            return;
        }
        FrameLayout initialView = mLoopMeAd.getContainerView();
        rebuildView(initialView, Constants.DisplayMode.NORMAL);
        initialView.setVisibility(View.VISIBLE);
    }

    public void switchToMinimizedMode() {
        if (mCurrentDisplayMode == Constants.DisplayMode.MINIMIZED) {
            return;
        }
        setDisplayMode(Constants.DisplayMode.MINIMIZED);
        mMinimizedView = UiUtils.createFrameLayout(mLoopMeAd.getContext(), mMinimizedMode.getDimensions());
        mMinimizedMode.addView(mMinimizedView);
        UiUtils.configMinimizedViewLayoutParams(mMinimizedView, mMinimizedMode);
        mMinimizedView.setOnTouchListener(initGestureListener());
        rebuildView(mMinimizedView, Constants.DisplayMode.MINIMIZED);
        setWebViewState(Constants.WebviewState.VISIBLE);
    }

    private void rebuildView(FrameLayout view, Constants.DisplayMode displayMode) { }

    private void removeMinimizedView() {
        if (mMinimizedView != null && mMinimizedView.getParent() != null) {
            ((ViewGroup) mMinimizedView.getParent()).removeView(mMinimizedView);
        }
    }

    public void setMinimizedMode(MinimizedMode mode) {
        mMinimizedMode = mode;
    }

    public boolean isMinimizedModeEnable() {
        return mMinimizedMode != null;
    }

    private View.OnTouchListener initGestureListener() {
        return new LoopMeGestureListener(mLoopMeAd.getContext(), new LoopMeGestureListener.Listener() {
            @Override
            public void onSwipe(boolean toRight) {
                setWebViewState(Constants.WebviewState.HIDDEN);
                setCurrentDisplayMode(Constants.DisplayMode.NORMAL);
                Animation anim = AnimationUtils.makeOutAnimation(mLoopMeAd.getContext(), toRight);
                long ANIMATION_DURATION = 200;
                anim.setDuration(ANIMATION_DURATION);
                mMinimizedView.startAnimation(anim);
                mMinimizedMode = null;
                if (mDisplayControllerLoopMe != null) {
                    mDisplayControllerLoopMe.dismissAd();
                }
            }
            @Override
            public void onClick() {
                if (mMinimizedMode != null) {
                    mMinimizedMode.onViewClicked();
                }
            }
        });
    }

    public boolean isFullScreenMode() {
        return mCurrentDisplayMode == Constants.DisplayMode.FULLSCREEN;
    }

    public AdSpotDimensions getMinimizedModeDimension() {
        return mMinimizedMode != null ?
            new AdSpotDimensions(mMinimizedMode.getDimensions()) : AdSpotDimensions.DEFAULT_DIMENSIONS;
    }

    private void setFullscreenMode(boolean mIsFirstFullScreen) { }

    private void setDisplayMode(Constants.DisplayMode mode) {
        mPreviousDisplayMode = mCurrentDisplayMode;
        setCurrentDisplayMode(mode);
        Logging.out(LOG_TAG, "switch to " + mode.name() + " mode");
    }

    private void setCurrentDisplayMode(Constants.DisplayMode displayMode) {
        mCurrentDisplayMode = displayMode;
    }

    private void setWebViewState(Constants.WebviewState webViewState) {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.setWebViewState(webViewState);
        }
    }

    public AdSpotDimensions getDimensionByDisplayMode() {
        AdSpotDimensions minimized = getMinimizedModeDimension();
        AdSpotDimensions normal = mLoopMeAd != null ? mLoopMeAd.getAdSpotDimensions() : AdSpotDimensions.DEFAULT_DIMENSIONS;
        AdSpotDimensions fullscreen = AdSpotDimensions.getFullscreen();
        switch (mCurrentDisplayMode) {
            case MINIMIZED: return minimized;
            case NORMAL: return normal;
            case FULLSCREEN: return fullscreen;
            default: return AdSpotDimensions.DEFAULT_DIMENSIONS;
        }
    }
}
