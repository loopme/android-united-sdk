package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.R;
import com.loopme.controllers.display.DisplayControllerVpaid;
import com.loopme.time.TimerWithPause;
import com.loopme.utils.ImageUtils;
import com.loopme.utils.Utils;

public class ViewControllerVpaid implements View.OnClickListener {

    private java.lang.String LOG_TAG = ViewControllerVpaid.class.getSimpleName();
    private static final int CLOSE_BUTTON_ID = View.generateViewId();
    private final DisplayControllerVpaid mDisplayControllerVpaid;

    private WebView mWebView;
    private View mEndCardLayout;
    private FrameLayout mContainerView;
    private ImageView mEndCardView;
    private TimerWithPause mCloseButtonTimer;
    private ImageView mCloseImageView;

    public ViewControllerVpaid(DisplayControllerVpaid displayControllerVpaid) {
        mDisplayControllerVpaid = displayControllerVpaid;
    }

    public void buildVideoAdView(FrameLayout containerView, WebView webView, Context context) {
        mContainerView = containerView;
        mWebView = webView;

        clearViews();
        initViews(context);
        setListeners();
        configureViews();
        addViewsToContainer();
    }

    private void addViewsToContainer() {
        FrameLayout.LayoutParams params = createParams();
        mEndCardLayout.setLayoutParams(params);
        mWebView.setLayoutParams(params);
        mContainerView.addView(mEndCardLayout, 0);
        mContainerView.addView(mWebView, 1);
        mContainerView.addView(mCloseImageView, 2);
    }

    private void configureViews() {
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mContainerView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setListeners() {
        (mEndCardLayout.findViewById(R.id.close_imageview)).setOnClickListener(this);
        (mEndCardLayout.findViewById(R.id.replay_imageview)).setOnClickListener(this);
        mCloseImageView.setOnClickListener(this);
    }

    private void initViews(Context context) {
        mEndCardLayout = LayoutInflater.from(context).inflate(R.layout.end_card, mContainerView, false);
        mEndCardLayout.setVisibility(View.GONE);
        mEndCardView = (ImageView) mEndCardLayout.findViewById(R.id.end_card_imageview);
        configureCloseView(context);
    }

    private void configureCloseView(Context context) {
        mCloseImageView = new ImageView(context);
        mCloseImageView.setId(CLOSE_BUTTON_ID);
        mCloseImageView.setScaleType(ImageView.ScaleType.CENTER);
        mCloseImageView.setImageResource(R.drawable.l_close);

        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI, context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                btnSizePx,
                btnSizePx,
                Gravity.END);

        mCloseImageView.setLayoutParams(params);
        enableCloseButton(false);
    }

    private FrameLayout.LayoutParams createParams() {
        return new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private void clearViews() {
        if (mWebView.getParent() != null) {
            ((ViewGroup) mWebView.getParent()).removeAllViews();
        }
        mContainerView.removeAllViews();
    }

    private void showControls() {
        mEndCardLayout.setVisibility(View.GONE);
        mWebView.setVisibility(View.VISIBLE);
    }

    public void showEndCard(String imageUri) {
        mEndCardLayout.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        ImageUtils.setScaledImage(mEndCardView, imageUri);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close_imageview || id == CLOSE_BUTTON_ID) {
            closeSelf();
        } else if (id == R.id.replay_imageview) {
            onReplayButtonClicked();
        }
    }

    private void onReplayButtonClicked() {
        showControls();
        onPlay();
    }

    private void onPlay() {
        if (mDisplayControllerVpaid != null) {
            mDisplayControllerVpaid.onPlay(Constants.START_POSITION);
        }
    }

    private void closeSelf() {
        if (mDisplayControllerVpaid != null) {
            mDisplayControllerVpaid.closeSelf();
        }
    }

    public void startCloseButtonTimer(long duration) {
        cancelCloseButtonTimer();
        Logging.out(LOG_TAG, "startCloseButtonTimer");
        mCloseButtonTimer = new TimerWithPause(duration, Constants.ONE_SECOND_IN_MILLIS, true) {
            @Override
            public void onTick(long millisUntilFinished) {
                Logging.out(LOG_TAG, "Till extra close button " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                enableCloseButton(true);
            }
        };
        mCloseButtonTimer.create();
    }

    public void cancelCloseButtonTimer() {
        if (mCloseButtonTimer != null) {
            mCloseButtonTimer.cancel();
        }
    }

    public void enableCloseButton(boolean enable) {
        if (mCloseImageView != null) {
            if (enable) {
                mCloseImageView.setVisibility(View.VISIBLE);
            } else {
                mCloseImageView.setVisibility(View.GONE);
            }
        }
    }

    public void pause() {
        mDisplayControllerVpaid.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCloseButtonTimer != null) {
                    mCloseButtonTimer.pause();
                }
            }
        });
    }

    public void resume() {
        mDisplayControllerVpaid.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCloseButtonTimer != null) {
                    mCloseButtonTimer.resume();
                }
            }
        });
    }

    public void destroy() {
        mDisplayControllerVpaid.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCloseButtonTimer != null) {
                    mCloseButtonTimer.cancel();
                }
            }
        });
    }
}
