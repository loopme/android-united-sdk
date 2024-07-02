package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.controllers.display.DisplayControllerVast;
import com.loopme.utils.Utils;

public class ViewControllerVast {

    private final DisplayControllerVast mDisplayControllerVast;
    private final ViewControllerVastListener mListener;
    private FrameLayout mContainerView;
    private PlayerLayout mPlayerLayout;
    private EndCardLayout mEndCardLayout;
    private FrameLayout mAdLayout;

    public ViewControllerVast(DisplayControllerVast displayControllerVast, ViewControllerVastListener listener) {
        mDisplayControllerVast = displayControllerVast;
        mListener = listener;
    }

    public void buildVideoAdView(FrameLayout containerView, Context context, WebView webView) {
        if (containerView == null || context == null) {
            return;
        }

        mPlayerLayout = new PlayerLayout(context, webView, initOnPlayerListener());
        mEndCardLayout = new EndCardLayout(context, initOnEndCardListener());
        mAdLayout = new FrameLayout(containerView.getContext());
        mAdLayout.setLayoutParams(Utils.createMatchParentLayoutParams());
        mAdLayout.addView(mPlayerLayout);
        mAdLayout.addView(mEndCardLayout);

        mContainerView = containerView;
        mContainerView.removeAllViews();
        mContainerView.setBackgroundColor(Color.TRANSPARENT);
        mContainerView.addView(mAdLayout);
    }

    private EndCardLayout.OnEndCardListener initOnEndCardListener() {
        return new EndCardLayout.OnEndCardListener() {
            @Override
            public void onEndCardClick() {
                openUrl();
            }
            @Override
            public void onCloseClick() {
                if (mDisplayControllerVast != null) {
                    mDisplayControllerVast.closeSelf();
                }
            }
            @Override
            public void onReplayClick() {
                setEndCardVisibility(View.GONE);
                setVideoPlayerVisibility(View.VISIBLE);
                if (mDisplayControllerVast != null) {
                    mDisplayControllerVast.onPlay(Constants.START_POSITION);
                }
            }
        };
    }

    private PlayerLayout.OnPlayerListener initOnPlayerListener() {
        return new PlayerLayout.OnPlayerListener() {
            @Override
            public void onSurfaceTextureReady(Surface surface) {
                if (mListener != null) {
                    mListener.onSurfaceTextureReady(surface);
                }
            }
            @Override
            public void onPlayerClick() {
                openUrl();
            }
            @Override
            public void onMuteClick(boolean mute) {
                if (mDisplayControllerVast != null) {
                    mDisplayControllerVast.onVolumeMute(mute);
                }
            }
            @Override
            public void onSkipClick() {
                if (mDisplayControllerVast != null) {
                    mDisplayControllerVast.skipVideo();
                }
            }
        };
    }

    public void adjustLayoutParams(int width, int height, boolean isBanner) {
        if (mPlayerLayout != null) {
            mPlayerLayout.adjustLayoutParams(width, height, mContainerView.getWidth(), mContainerView.getHeight());
        }
        if (!isBanner) {
            return;
        }
        ViewGroup.LayoutParams paramFrom = mPlayerLayout.getPlayerView().getLayoutParams();
        ViewGroup.LayoutParams paramTo = mAdLayout.getLayoutParams();
        paramTo.width = paramFrom.width;
        paramTo.height = paramFrom.height;
    }

    public Surface getSurface() {
        return mPlayerLayout == null ? null : mPlayerLayout.getSurface();
    }

    public void showEndCard(String imageUri) {
        setEndCardVisibility(View.VISIBLE);
        setVideoPlayerVisibility(View.GONE);
        if (mEndCardLayout != null) {
            mEndCardLayout.setEndCard(imageUri);
        }
    }

    private void setVideoPlayerVisibility(int visibility) {
        if (mPlayerLayout != null) {
            mPlayerLayout.setVisibility(visibility);
        }
    }

    private void setEndCardVisibility(int visibility) {
        if (mEndCardLayout != null) {
            mEndCardLayout.setVisibility(visibility);
        }
    }

    public boolean isEndCard() {
        return mEndCardLayout != null && mEndCardLayout.getVisibility() == View.VISIBLE;
    }

    public void dismiss() {
        if (mContainerView != null) {
            mContainerView.removeAllViews();
        }
    }

    private void openUrl() {
        if (mDisplayControllerVast != null) {
            mDisplayControllerVast.onRedirect(null, null);
        }
    }

    public TextureView getPlayerView() {
        return mPlayerLayout == null ? null : mPlayerLayout.getPlayerView();
    }

    public void destroy() {
        if (mEndCardLayout != null) {
            mEndCardLayout.destroy();
        }
    }

    public void setMaxProgress(int duration) {
        if (mPlayerLayout != null) {
            mPlayerLayout.setMaxProgress(duration);
        }
    }

    public void setProgress(int duration) {
        if (mPlayerLayout != null) {
            mPlayerLayout.setProgress(duration);
        }
    }

    public void showSkipButton() {
        if (mPlayerLayout != null) {
            mPlayerLayout.showSkipButton();
        }
    }

    public boolean isMute() {
        return mPlayerLayout != null && mPlayerLayout.isMute();
    }

    public interface ViewControllerVastListener {
        void onSurfaceTextureReady(Surface surface);
    }
}
