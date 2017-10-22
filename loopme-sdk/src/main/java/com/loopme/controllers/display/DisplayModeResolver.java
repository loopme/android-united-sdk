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
import com.loopme.utils.Utils;

/**
 * Created by katerina on 7/26/17.
 */

public class DisplayModeResolver {
    private static final String LOG_TAG = DisplayModeResolver.class.getSimpleName();
    private static final long ANIMATION_DURATION = 200;

    private boolean mIsFirstFullScreenCommand = true;
    private Constants.DisplayMode mCurrentDisplayMode = Constants.DisplayMode.NORMAL;
    private Constants.DisplayMode mPreviousDisplayMode = Constants.DisplayMode.NORMAL;

    private LoopMeAd mLoopMeAd;
    private FrameLayout mMinimizedView;
    private boolean mIsBackFromExpand;

    public MinimizedMode getMinimizedMode() {
        return mMinimizedMode;
    }

    private MinimizedMode mMinimizedMode;
    private DisplayControllerLoopMe mDisplayControllerLoopMe;


    public DisplayModeResolver(DisplayControllerLoopMe controllerLoopMe, LoopMeAd loopMeAd) {
        mDisplayControllerLoopMe = controllerLoopMe;
        mLoopMeAd = loopMeAd;
    }

    public void switchToPreviousMode() {
        if (mPreviousDisplayMode == Constants.DisplayMode.MINIMIZED) {
            switchToMinimizedMode();
        } else if (mPreviousDisplayMode == Constants.DisplayMode.NORMAL) {
            switchToNormalMode();
        }
    }

    public void switchToFullScreenMode(boolean isFullScreen) {
        //we should ignore first command
        if (isFirstCommand()) {
            return;
        }
        switchToFullScreenModeInternal(isFullScreen);
    }

    private void switchToFullScreenModeInternal(boolean isFullScreen) {
        if (isFullScreen) {
            switchToFullScreenMode();
        } else {
            broadcastDestroyIntent();
        }
        mDisplayControllerLoopMe.setFullscreenMode(isFullScreen);
    }

    private boolean isFirstCommand() {
        if (mIsFirstFullScreenCommand) {
            mIsFirstFullScreenCommand = false;
            mDisplayControllerLoopMe.setFullscreenMode(false);
            return true;
        } else {
            return false;
        }
    }


    public void destroy() {
        destroyMinimizedView();
    }

    private void switchToFullScreenMode() {
        if (!isFullScreenMode()) {
            setPreviousDisplayMode(mCurrentDisplayMode);
            setCurrentDisplayMode(Constants.DisplayMode.FULLSCREEN);
            if (mPreviousDisplayMode == Constants.DisplayMode.MINIMIZED) {
                removeMinimizedView();
            }
            AdUtils.startAdActivity(mLoopMeAd);
            Logging.out(LOG_TAG, "switch to fullscreen mode");
        }
    }

    public boolean isFullScreenMode() {
        return mCurrentDisplayMode == Constants.DisplayMode.FULLSCREEN;
    }

    private void setCurrentDisplayMode(Constants.DisplayMode displayMode) {
        mCurrentDisplayMode = displayMode;
    }

    public void switchToNormalMode() {
        if (mCurrentDisplayMode == Constants.DisplayMode.NORMAL) {
            return;
        }

        if (isFullScreenMode()) {
            mIsBackFromExpand = true;
        }
        Logging.out(LOG_TAG, "switch to normal mode");
        setPreviousDisplayMode(mCurrentDisplayMode);
        setCurrentDisplayMode(Constants.DisplayMode.NORMAL);
        rebuildView();
        removeMinimizedView();
        mDisplayControllerLoopMe.setFullscreenMode(false);
    }

    public void switchToMinimizedMode() {
        if (mCurrentDisplayMode == Constants.DisplayMode.MINIMIZED) {
            if (isCurrentVideoStatePaused()) {
                setWebViewState(Constants.WebviewState.VISIBLE);
            }
            return;
        }
        Logging.out(LOG_TAG, "switch to minimized mode");
        setPreviousDisplayMode(mCurrentDisplayMode);
        setCurrentDisplayMode(Constants.DisplayMode.MINIMIZED);
        int width = mMinimizedMode.getWidth();
        int height = mMinimizedMode.getWidth();
        mMinimizedView = UiUtils.createFrameLayout(mLoopMeAd.getContext(), width, height);

        mDisplayControllerLoopMe.rebuildView(mMinimizedView);
        UiUtils.addBordersToView(mMinimizedView);

        if (isWebViewStateHidden() && mMinimizedView != null) {
            mMinimizedView.setAlpha(0);
        }

        mMinimizedMode.getRootView().addView(mMinimizedView);
        UiUtils.configMinimizedViewLayoutParams(mMinimizedView, mMinimizedMode);

        setWebViewState(Constants.WebviewState.VISIBLE);
        mDisplayControllerLoopMe.setOnTouchListener(initSwipeListener(width));
    }

    private void rebuildView() {
        FrameLayout initialView = mLoopMeAd.getContainerView();
        initialView.setVisibility(View.VISIBLE);
        mDisplayControllerLoopMe.rebuildView(initialView);
    }

    private void removeMinimizedView() {
        if (mMinimizedView != null && mMinimizedView.getParent() != null) {
            ((ViewGroup) mMinimizedView.getParent()).removeView(mMinimizedView);
        }
    }

    private void setPreviousDisplayMode(Constants.DisplayMode displayMode) {
        mPreviousDisplayMode = displayMode;
    }

    private boolean isCurrentVideoStatePaused() {
        return mDisplayControllerLoopMe.getCurrentVideoState() == Constants.VideoState.PAUSED;
    }

    private boolean isWebViewStateHidden() {
        return mDisplayControllerLoopMe != null && mDisplayControllerLoopMe.isWebViewStateHidden();
    }

    private void setWebViewState(Constants.WebviewState webViewState) {
        if (mDisplayControllerLoopMe != null) {
            mDisplayControllerLoopMe.setWebViewState(webViewState);
        }
    }

    private void animate(boolean toRight) {
        Animation anim = AnimationUtils.makeOutAnimation(mLoopMeAd.getContext(), toRight);
        anim.setDuration(ANIMATION_DURATION);
        mMinimizedView.startAnimation(anim);
    }

    private void destroyMinimizedView() {
        if (mMinimizedView != null) {
            removeMinimizedView();
            mMinimizedView.removeAllViews();
            mMinimizedView = null;
        }
    }

    public void setMinimizedMode(MinimizedMode mode) {
        mMinimizedMode = mode;
    }

    public boolean isMinimizedModeEnable() {
        return mMinimizedMode != null && mMinimizedMode.getRootView() != null;
    }

    private SwipeListener initSwipeListener(int width) {
        return new SwipeListener(width, new SwipeListener.Listener() {
            @Override
            public void onSwipe(boolean toRight) {
                setWebViewState(Constants.WebviewState.HIDDEN);
                animate(toRight);
                switchToNormalMode();
                mMinimizedMode = null;
            }
        });
    }

    private void broadcastDestroyIntent() {
        UiUtils.broadcastIntent(mLoopMeAd.getContext(), Constants.DESTROY_INTENT, mLoopMeAd.getAdId());
    }

    public Constants.DisplayMode getDisplayMode() {
        return mCurrentDisplayMode;
    }

    public void animateAppear() {
        if (isMinimizedMode()) {
            Utils.animateAppear(mMinimizedView);
        }
    }

    public boolean isMinimizedMode() {
        return mCurrentDisplayMode == Constants.DisplayMode.MINIMIZED;
    }

    public Constants.DisplayMode getCurrentDisplayMode() {
        return mCurrentDisplayMode;
    }

    public boolean isBackFromExpand() {
        return mIsBackFromExpand;
    }
}
