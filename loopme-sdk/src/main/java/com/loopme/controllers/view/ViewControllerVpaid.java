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
import com.loopme.utils.Utils;

public class ViewControllerVpaid implements View.OnClickListener {

    private final java.lang.String LOG_TAG = ViewControllerVpaid.class.getSimpleName();
    private static final int CLOSE_BUTTON_ID = View.generateViewId();
    private final DisplayControllerVpaid mDisplayControllerVpaid;

    private WebView mWebView;
    private View mEndCardLayout;

    private TimerWithPause mCloseButtonTimer;
    private ImageView mCloseImageView;

    public ViewControllerVpaid(DisplayControllerVpaid displayControllerVpaid) {
        mDisplayControllerVpaid = displayControllerVpaid;
    }

    public void buildVideoAdView(FrameLayout containerView, WebView webView, Context context) {
        mWebView = webView;
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setLayoutParams(Constants.Layout.MATCH_PARENT);
        if (mWebView.getParent() != null) {
            ((ViewGroup) mWebView.getParent()).removeAllViews();
        }
        containerView.removeAllViews();
        mEndCardLayout = LayoutInflater.from(context).inflate(R.layout.end_card, containerView, false);
        mEndCardLayout.setVisibility(View.GONE);
        mEndCardLayout.findViewById(R.id.close_imageview).setOnClickListener(this);
        mEndCardLayout.findViewById(R.id.replay_imageview).setOnClickListener(this);
        mEndCardLayout.setLayoutParams(Constants.Layout.MATCH_PARENT);
        containerView.setBackgroundColor(Color.TRANSPARENT);
        containerView.addView(mEndCardLayout, 0);
        containerView.addView(mWebView, 1);
        containerView.addView(getCloseView(context), 2);
    }

    private ImageView getCloseView(Context context) {
        int btnSizePx = Utils.convertDpToPixel(Constants.BUTTON_SIZE_DPI);
        mCloseImageView = new ImageView(context);
        mCloseImageView.setId(CLOSE_BUTTON_ID);
        mCloseImageView.setScaleType(ImageView.ScaleType.CENTER);
        mCloseImageView.setImageResource(R.drawable.l_close);
        mCloseImageView.setLayoutParams(new FrameLayout.LayoutParams(btnSizePx, btnSizePx, Gravity.END));
        mCloseImageView.setOnClickListener(this);
        enableCloseButton(false);
        return mCloseImageView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close_imageview || id == CLOSE_BUTTON_ID) {
            if (mDisplayControllerVpaid != null) {
                mDisplayControllerVpaid.closeSelf();
            }
        }
        if (id == R.id.replay_imageview) {
            mEndCardLayout.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            if (mDisplayControllerVpaid != null) {
                mDisplayControllerVpaid.onPlay(Constants.START_POSITION);
            }
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
            public void onFinish() { enableCloseButton(true); }
        };
        mCloseButtonTimer.create();
    }

    public void cancelCloseButtonTimer() {
        if (mCloseButtonTimer != null) {
            mCloseButtonTimer.cancel();
        }
    }

    public void enableCloseButton(boolean enable) {
        if (mCloseImageView == null) {
            return;
        }
        mCloseImageView.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void pause() {
        mDisplayControllerVpaid.runOnUiThread(() -> {
            if (mCloseButtonTimer != null) {
                mCloseButtonTimer.pause();
            }
        });
    }

    public void resume() {
        mDisplayControllerVpaid.runOnUiThread(() -> {
            if (mCloseButtonTimer != null) {
                mCloseButtonTimer.resume();
            }
        });
    }

    public void destroy() {
        mDisplayControllerVpaid.runOnUiThread(() -> {
            if (mCloseButtonTimer != null) {
                mCloseButtonTimer.cancel();
            }
        });
    }
}
