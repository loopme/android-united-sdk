package com.loopme.controllers.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loopme.R;
import com.loopme.Constants;
import com.loopme.controllers.display.DisplayControllerVpaid;
import com.loopme.utils.ImageUtils;

public class ViewControllerVpaid implements View.OnClickListener {

    private final DisplayControllerVpaid mDisplayControllerVpaid;

    private WebView mWebView;
    private View mEndCardLayout;
    private FrameLayout mContainerView;
    private ImageView mEndCardView;

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
    }

    private void configureViews() {
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mContainerView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setListeners() {
        (mEndCardLayout.findViewById(R.id.close_imageview)).setOnClickListener(this);
        (mEndCardLayout.findViewById(R.id.replay_imageview)).setOnClickListener(this);
    }

    private void initViews(Context context) {
        mEndCardLayout = LayoutInflater.from(context).inflate(R.layout.end_card, mContainerView, false);
        mEndCardLayout.setVisibility(View.GONE);
        mEndCardView = (ImageView) mEndCardLayout.findViewById(R.id.end_card_imageview);
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
        if (id == R.id.close_imageview) {
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
}
