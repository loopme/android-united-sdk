package com.loopme.controllers.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.loopme.Constants;
import com.loopme.R;
import com.loopme.utils.ImageUtils;
import com.loopme.utils.Utils;

public class EndCardLayout extends FrameLayout implements View.OnClickListener {
    private static final int END_CARD_VIEW_ID = View.generateViewId();
    private static final int CLOSE_BUTTON_ID = View.generateViewId();
    private static final int REPLAY_BUTTON_ID = View.generateViewId();
    private ImageView mEndCardImageView;
    private ImageView mCloseImageView;
    private ImageView mReplayImageView;
    private OnEndCardListener mListener;

    public EndCardLayout(@NonNull Context context, OnEndCardListener listener) {
        super(context);
        mListener = listener;
        configureViews();
        buildLayout();
    }

    private void buildLayout() {
        setLayoutParams(Utils.createMatchParentLayoutParams());
        addView(mEndCardImageView, 0);
        addView(mCloseImageView, 1);
        addView(mReplayImageView, 2);
        setVisibility(View.GONE);
    }

    private void configureViews() {
        configureEndCardImageView();
        configureCloseView();
        configureReplayView();
        setListeners();
    }

    private void setListeners() {
        mEndCardImageView.setOnClickListener(this);
        mCloseImageView.setOnClickListener(this);
        mReplayImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == CLOSE_BUTTON_ID) {
            onCloseClick();
        } else if (id == REPLAY_BUTTON_ID) {
            onReplayClick();
        } else if (id == END_CARD_VIEW_ID) {
            onEndCardClick();
        }
    }


    public void destroy() {
        if (mEndCardImageView != null) {
            mEndCardImageView.setImageDrawable(null);
        }
    }


    private void configureReplayView() {
        mReplayImageView = new ImageView(getContext());
        mReplayImageView.setId(REPLAY_BUTTON_ID);
        mReplayImageView.setScaleType(ImageView.ScaleType.CENTER);
        mReplayImageView.setImageResource(R.drawable.l_replay);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Constants.BUTTON_SIZE, Constants.BUTTON_SIZE, Gravity.START);
        mReplayImageView.setLayoutParams(params);
    }

    private void configureCloseView() {
        mCloseImageView = new ImageView(getContext());
        mCloseImageView.setId(CLOSE_BUTTON_ID);

        mCloseImageView.setScaleType(ImageView.ScaleType.CENTER);
        mCloseImageView.setImageResource(R.drawable.l_close);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Constants.BUTTON_SIZE, Constants.BUTTON_SIZE, Gravity.END);
        mCloseImageView.setLayoutParams(params);
    }

    private void configureEndCardImageView() {
        mEndCardImageView = new ImageView(getContext());
        mEndCardImageView.setId(END_CARD_VIEW_ID);
        mEndCardImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mEndCardImageView.setLayoutParams(Utils.createMatchParentLayoutParams());
    }

    public void setEndCard(String imageUri) {
        ImageUtils.setScaledImage(mEndCardImageView, imageUri);
    }

    private void onEndCardClick() {
        if (mListener != null) {
            mListener.onEndCardClick();
        }
    }

    private void onCloseClick() {
        if (mListener != null) {
            mListener.onCloseClick();
        }
    }

    private void onReplayClick() {
        if (mListener != null) {
            mListener.onReplayClick();
        }
    }

    public interface OnEndCardListener {
        void onEndCardClick();

        void onCloseClick();

        void onReplayClick();
    }
}
