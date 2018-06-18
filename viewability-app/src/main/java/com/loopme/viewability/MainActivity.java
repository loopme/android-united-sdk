package com.loopme.viewability;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopme.LoopMeBanner;
import com.loopme.LoopMeInterstitial;
import com.loopme.common.LoopMeError;
import com.loopme.utils.Utils;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        LoopMeInterstitial.Listener,
        CompoundButton.OnCheckedChangeListener,
        LoopMeBanner.Listener {

    private static final int READY = R.drawable.round_button_ready;
    private static final int NOT_READY = R.drawable.round_button_not_ready;
    private static final double VISIBILITY_THRESHOLD = 0.5;
    private static final long TIME_DELAY = 100;
    private static final String MOVE_RIGHT = "MOVE_RIGHT";
    private static final String MOVE_LEFT = "MOVE_LEFT";
    private static final int DEFAULT_SHIFT_VALUE = 100;

    private Button mLoadAdButton;
    private Button mShowInterstitialButton;
    private Button mStatusButton;
    private Button mOverlappingButton;
    private TextView mLoadingTextView;
    private TextView mVisibilityTextView;
    private EditText mShiftValueEdit;
    private EditText mSetAppKeyEdit;
    private CheckBox mRightShiftButton;
    private CheckBox mLeftShiftButton;
    private CheckBox mTopShiftButton;
    private CheckBox mBottomShiftButton;
    private CheckBox mFullScreenButton;
    private CheckBox mLargeAdButton;
    private CheckBox mSmallAdButton;
    private CheckBox[] mAllCheckboxArray;

    private LoopMeInterstitial mInterstitial;
    private LoopMeBanner mBanner;
    private FrameLayout mLoopMeBannerView;
    private ViewGroup.LayoutParams mDefaultLayoutParams;

    private String mCurrentAppKey = "";
    private int mShiftValue = DEFAULT_SHIFT_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.init(this);
        setScreenSize();
        initViews();
        mAllCheckboxArray = new CheckBox[]{
                mRightShiftButton, mLeftShiftButton, mTopShiftButton, mBottomShiftButton,
                mFullScreenButton, mLargeAdButton, mSmallAdButton};

        mDefaultLayoutParams = mLoopMeBannerView.getLayoutParams();
        setLoadButtonActive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeBanner();
        removeKeyBoard();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseBanner();
    }

    @Override
    protected void onDestroy() {
        destroyInterstitial();
        destroyBanner();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.load_interstitial_button: {
                prepareInterstitial();
                break;
            }
            case R.id.show_interstitital_button: {
                showInterstitial();
                break;
            }
            case R.id.load_banner_button: {
                prepareBanner();
                break;
            }
            case R.id.hide_banner_button: {
                hideBanner();
                break;
            }
            case R.id.move_left_imageview: {
                move(MOVE_LEFT);
                break;
            }
            case R.id.move_right_imageview: {
                move(MOVE_RIGHT);
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.top_shift_button: {
                mSmallAdButton.setChecked(false);
                mBottomShiftButton.setChecked(false);
                mFullScreenButton.setChecked(false);
                mTopShiftButton.setChecked(isChecked);
                break;
            }
            case R.id.bottom_shift_button: {
                mSmallAdButton.setChecked(false);
                mTopShiftButton.setChecked(false);
                mFullScreenButton.setChecked(false);
                mBottomShiftButton.setChecked(isChecked);
                break;
            }
            case R.id.left_shift_button: {
                mSmallAdButton.setChecked(false);
                mRightShiftButton.setChecked(false);
                mFullScreenButton.setChecked(false);
                mLeftShiftButton.setChecked(isChecked);
                break;
            }
            case R.id.right_shift_button: {
                mSmallAdButton.setChecked(false);
                mLeftShiftButton.setChecked(false);
                mFullScreenButton.setChecked(false);
                mRightShiftButton.setChecked(isChecked);
                break;
            }
            case R.id.fullscreen_button: {
                disableAllButtonsExceptOne(mFullScreenButton);
                mFullScreenButton.setChecked(isChecked);
                break;
            }

            case R.id.large_ad_button: {
                mFullScreenButton.setChecked(false);
                mSmallAdButton.setChecked(false);
                mLargeAdButton.setChecked(isChecked);
                break;
            }
            case R.id.small_ad_button: {
                disableAllButtonsExceptOne(mSmallAdButton);
                mSmallAdButton.setChecked(isChecked);
                break;
            }

            case R.id.overlapping_checkbox: {
                onOverlappingCheckBoxClicked(isChecked);
                break;
            }
        }
    }

    private void initViews() {
        mLoopMeBannerView = (FrameLayout) findViewById(R.id.loopme_banner_view);

        mStatusButton = (Button) findViewById(R.id.status_button);
        mLoadAdButton = (Button) findViewById(R.id.load_interstitial_button);
        mShowInterstitialButton = (Button) findViewById(R.id.show_interstitital_button);
        mOverlappingButton = (Button) findViewById(R.id.overlapping_button);

        mLeftShiftButton = (CheckBox) findViewById(R.id.left_shift_button);
        mRightShiftButton = (CheckBox) findViewById(R.id.right_shift_button);
        mTopShiftButton = (CheckBox) findViewById(R.id.top_shift_button);
        mBottomShiftButton = (CheckBox) findViewById(R.id.bottom_shift_button);
        mFullScreenButton = (CheckBox) findViewById(R.id.fullscreen_button);
        mFullScreenButton.setChecked(true);

        mBottomShiftButton.setOnCheckedChangeListener(this);
        mTopShiftButton.setOnCheckedChangeListener(this);
        mLeftShiftButton.setOnCheckedChangeListener(this);
        mRightShiftButton.setOnCheckedChangeListener(this);
        mFullScreenButton.setOnCheckedChangeListener(this);

        mShiftValueEdit = (EditText) findViewById(R.id.set_shifting_edit);
        mSetAppKeyEdit = (EditText) findViewById(R.id.set_app_key_edit);
        mSetAppKeyEdit.setText("c86c5dc282");
        mLoadingTextView = (TextView) findViewById(R.id.loading_textview);
        mVisibilityTextView = (TextView) findViewById(R.id.visibility_status_textview);

        mLoadAdButton.setOnClickListener(this);
        mShowInterstitialButton.setOnClickListener(this);

        mLargeAdButton = (CheckBox) findViewById(R.id.large_ad_button);
        mSmallAdButton = (CheckBox) findViewById(R.id.small_ad_button);
        mLargeAdButton.setOnCheckedChangeListener(this);
        mSmallAdButton.setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.overlapping_checkbox)).setOnCheckedChangeListener(this);

        findViewById(R.id.hide_banner_button).setOnClickListener(this);
        findViewById(R.id.move_left_imageview).setOnClickListener(this);
        findViewById(R.id.move_right_imageview).setOnClickListener(this);
        findViewById(R.id.load_banner_button).setOnClickListener(this);
        mOverlappingButton.setOnClickListener(this);
    }

    private void setAppKey() {
        if (mSetAppKeyEdit != null) {
            String appKey = mSetAppKeyEdit.getText().toString();
            if (!TextUtils.isEmpty(appKey)) {
                mCurrentAppKey = appKey;
            } else {
                Toast.makeText(this, "App key is empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void prepareBanner() {
        if (mBanner != null && mBanner.isReady()) {
            Toast.makeText(this, "Banner is ready", Toast.LENGTH_SHORT).show();
            return;
        }
        setAppKey();
        initBanner();
        loadBanner();
    }

    private void initBanner() {
        mBanner = LoopMeBanner.getInstance(mCurrentAppKey, this);
        if (mBanner != null) {
            mBanner.bindView(mLoopMeBannerView);
            mBanner.setListener(this);
        }
    }

    private void hideBanner() {
        destroyBanner();
        setDefaultLayoutParams();
        countAndShowVisibility();
    }

    private void loadBanner() {
        if (mBanner != null && mLoadingTextView != null) {
            mBanner.load();
            mLoadingTextView.setVisibility(View.VISIBLE);
        }
    }

    private void resumeBanner() {
        if (mBanner != null) {
            mBanner.resume();
        }
    }

    private void pauseBanner() {
        if (mBanner != null) {
            mBanner.pause();
        }
    }

    private void showBanner() {
        if (mBanner != null && mBanner.isReady()) {
            mBanner.show();
        }
    }

    private void destroyBanner() {
        if (mBanner != null) {
            mBanner.destroy();
        }
        setStatus(NOT_READY);
    }

    private boolean isBannerVisible() {
        return mLoopMeBannerView != null && mLoopMeBannerView.getVisibility() == View.VISIBLE;
    }

    private void prepareInterstitial() {
        if (mInterstitial != null && mInterstitial.isReady()) {
            Toast.makeText(this, "Interstitial is ready", Toast.LENGTH_SHORT).show();
            return;
        }
        setAppKey();
        initInterstitial();
        loadInterstitial();
    }

    private void initInterstitial() {
        mInterstitial = LoopMeInterstitial.getInstance(mCurrentAppKey, this);
        if (mInterstitial != null) {
            mInterstitial.setListener(this);
        }
    }

    private void loadInterstitial() {
        if (mInterstitial != null && mLoadingTextView != null) {
            mInterstitial.load();
            mLoadingTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showInterstitial() {
        if (mInterstitial != null && mInterstitial.isReady()) {
            setNewShiftValue();
            mInterstitial.show(getRightShift(), getLeftShift(), getTopShift(), getBottomShift(), mLargeAdButton.isChecked(), mSmallAdButton.isChecked());
        } else {
            Toast.makeText(this, "Interstitial is not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void destroyInterstitial() {
        if (mInterstitial != null) {
            mInterstitial.dismiss();
            mInterstitial.destroy();
        }
    }

    private void setDefaultLayoutParams() {
        if (mLoopMeBannerView != null) {
            mLoopMeBannerView.setLayoutParams(mDefaultLayoutParams);
        }
    }

    private void move(String direction) {
        if (!isBannerVisible()) {
            return;
        }
        setNewShiftValue();

        int mShift = 0;
        switch (direction) {
            case MOVE_LEFT: {
                mShift = (int) mLoopMeBannerView.getX() - mShiftValue;
                break;
            }
            case MOVE_RIGHT: {
                mShift = (int) mLoopMeBannerView.getX() + mShiftValue;
                break;
            }
        }
        mLoopMeBannerView.setX(mShift);
        countAndShowVisibility();
    }

    private void setNewShiftValue() {
        if (mShiftValueEdit != null && !TextUtils.isEmpty(mShiftValueEdit.getText())) {
            mShiftValue = Integer.parseInt(mShiftValueEdit.getText().toString());
        }
    }

    private void countAndShowVisibility() {
        MoatViewAbilityUtils.ViewAbilityInfo info = MoatViewAbilityUtils.calculateViewAbilityInfo(mLoopMeBannerView);
        showVisibilityResults(info.getVisibility());
    }

    private void showVisibilityResults(double visibility) {
        showStatus(visibility);
        if (mVisibilityTextView != null) {
            mVisibilityTextView.setText(UiUtils.toPercent(visibility));
        }
    }

    private void showStatus(double visible) {
        if (visible < VISIBILITY_THRESHOLD) {
            setStatus(NOT_READY);
        } else {
            setStatus(READY);
        }
    }

    private void removeKeyBoard() {
        mShiftValueEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                UiUtils.hideSoftKeyboard(getWindow().getDecorView().getRootView(), MainActivity.this);
            }
        }, TIME_DELAY);
    }

    private void disableAllButtonsExceptOne(CheckBox enabledButton) {
        for (CheckBox checkBox : mAllCheckboxArray) {
            if (checkBox != enabledButton) {
                checkBox.setChecked(false);
            }
        }
    }

    private int getBottomShift() {
        if (mBottomShiftButton.isChecked()) {
            return mShiftValue;
        } else {
            return 0;
        }
    }

    private int getTopShift() {
        if (mTopShiftButton.isChecked()) {
            return mShiftValue;
        } else {
            return 0;
        }
    }

    private int getLeftShift() {
        if (mLeftShiftButton.isChecked()) {
            return mShiftValue;
        } else {
            return 0;
        }
    }

    private int getRightShift() {
        if (mRightShiftButton.isChecked()) {
            return mShiftValue;
        } else {
            return 0;
        }
    }

    private void setShowButtonActive() {
        mLoadAdButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disabled_buttons, null));
    }

    private void setLoadButtonActive() {
        mLoadAdButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.background_buttons, null));
    }

    private void setStatus(int statusColor) {
        if (mStatusButton != null) {
            mStatusButton.setBackground(ResourcesCompat.getDrawable(getResources(), statusColor, null));
        }
    }

    private void onOverlappingCheckBoxClicked(boolean checked) {
        changeVisibilityOverlappingButton(checked);
        showVisibilityDelayed();
    }

    private void changeVisibilityOverlappingButton(boolean checked) {
        if (checked) {
            mOverlappingButton.setVisibility(View.VISIBLE);
        } else {
            mOverlappingButton.setVisibility(View.GONE);
        }
    }

    private void setScreenSize() {
        int mScreenHeight = Utils.getScreenHeight();
        int mScreenWidth = Utils.getScreenWidth();
        ((TextView) findViewById(R.id.screen_size_text)).setText(mScreenWidth + " x " + mScreenHeight);
    }

    private void showVisibilityDelayed() {
        if (mVisibilityTextView != null) {
            mVisibilityTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    countAndShowVisibility();
                    setBannerSize();
                }
            }, TIME_DELAY);
        }
    }

    @Override
    public void onLoopMeInterstitialLoadSuccess(LoopMeInterstitial interstitial) {
        Toast.makeText(this, "onLoopMeInterstitialLoadSuccess", Toast.LENGTH_LONG).show();
        mLoadingTextView.setVisibility(View.INVISIBLE);
        setShowButtonActive();
        setStatus(READY);
    }

    @Override
    public void onLoopMeInterstitialLoadFail(LoopMeInterstitial interstitial, LoopMeError error) {
        Toast.makeText(this, "Interstitial Load Fail:" + error.getMessage(), Toast.LENGTH_LONG).show();
        mLoadingTextView.setVisibility(View.INVISIBLE);
        setLoadButtonActive();
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeInterstitialShow(LoopMeInterstitial interstitial) {
        mLoadingTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoopMeInterstitialHide(LoopMeInterstitial interstitial) {
        setLoadButtonActive();
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeInterstitialClicked(LoopMeInterstitial interstitial) {
    }

    @Override
    public void onLoopMeInterstitialLeaveApp(LoopMeInterstitial interstitial) {
        mLoadingTextView.setVisibility(View.INVISIBLE);
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeInterstitialExpired(LoopMeInterstitial interstitial) {
        mLoadingTextView.setVisibility(View.INVISIBLE);
        setStatus(NOT_READY);

    }

    @Override
    public void onLoopMeInterstitialVideoDidReachEnd(LoopMeInterstitial interstitial) {
        mLoadingTextView.setVisibility(View.INVISIBLE);
        setLoadButtonActive();
    }

    @Override
    public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
        showBanner();
        setStatus(READY);
        showVisibilityDelayed();
    }

    @Override
    public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
        Toast.makeText(this, "Banner Load Fail:" + error.getMessage(), Toast.LENGTH_LONG).show();
        mLoadingTextView.setVisibility(View.INVISIBLE);
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner banner) {
        mLoadingTextView.setVisibility(View.GONE);
    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner banner) {
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {
        setStatus(NOT_READY);
    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner banner) {
        setStatus(NOT_READY);
    }

    @Override
    public void onBackPressed() {
        if (isInterstitialLoading() || isBannerLoading()) {
            destroyAd();
        } else {
            super.onBackPressed();
        }
    }

    private void destroyAd() {
        destroyBanner();
        destroyInterstitial();
        Toast.makeText(this, "Stop loading Ad", Toast.LENGTH_SHORT).show();
        mLoadingTextView.setVisibility(View.GONE);
    }

    private boolean isInterstitialLoading() {
        return mInterstitial != null && mInterstitial.isLoading();
    }

    private boolean isBannerLoading() {
        return mBanner != null && mBanner.isLoading();
    }

    private void setBannerSize() {
        findViewById(R.id.banner_size_linearlayout).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.banner_size_text)).setText(getBannerSize());
    }

    private String getBannerSize() {
        return String.valueOf(mLoopMeBannerView.getWidth() + "x" + mLoopMeBannerView.getHeight());
    }
}