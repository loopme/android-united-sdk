package com.loopme.views.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.R;
import com.loopme.SensorManagerExtension;
import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.controllers.display.BaseTrackableController;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.receiver.AdReceiver;
import com.loopme.receiver.MraidAdCloseButtonReceiver;
import com.loopme.views.CloseButton;


public final class BaseActivity extends Activity
        implements AdReceiver.Listener,
        MraidAdCloseButtonReceiver.MraidAdCloseButtonListener,
        SensorManagerExtension.OnLoopMeSensorListener {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private static final int START_DEFAULT_POSITION = 0;
    private SensorManagerExtension mSensorManager;
    private BaseTrackableController mDisplayController;

    private AdReceiver mAdReceiver;
    private LoopMeAd mLoopMeAd;

    private FrameLayout mLoopMeContainerView;
    private boolean mFirstLaunch = true;
    //mraid
    private boolean mIsCloseButtonPresent;
    private CloseButton mMraidCloseButton;
    private MraidAdCloseButtonReceiver mMraidCloseButtonReceiver;
    private boolean mIsDestroyBroadcastReceived;
    private boolean mIsPlaying = true;
    private Button mPlayPauseButton;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.out(LOG_TAG, "onCreate");
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (getIntent() == null) {
            return;
        }
        requestSystemFlags();
        retrieveLoopMeAdOrFinish();
        if (mLoopMeAd != null && mLoopMeAd.getAdParams() != null) {
            setDisplayController();
            setContentView();
            setOrientation();
            initSensorManager(mLoopMeAd.getContext());
            initDestroyReceiver();
            setMraidSettings();
        } else {
            finish();
        }
    }

    private void setContentView() {
        setContentView(R.layout.base_activity_main);
        mLoopMeContainerView = (FrameLayout) findViewById(R.id.loopme_container_view);
        mLoopMeAd.onNewContainer(mLoopMeContainerView);
        mDisplayController.onAdRegisterView(this, mDisplayController.getWebView());
        mDisplayController.postImpression();
        mDisplayController.onAdEnteredFullScreenEvent();
        mDisplayController.onNewActivity(this);


        mPlayPauseButton = new Button(this);
        mPlayPauseButton.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mPlayPauseButton.setText("Pause");
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPlaying) {
                    mLoopMeAd.pause();
                    mIsPlaying = false;
                    mPlayPauseButton.setText("Play");
                } else {
                    mLoopMeAd.resume();
                    mIsPlaying = true;
                    mPlayPauseButton.setText("Pause");

                }
            }
        });
        mLoopMeContainerView.addView(mPlayPauseButton, 1);
    }

    private void setMraidSettings() {
        if (mLoopMeAd != null && mLoopMeAd.isMraidAd()) {
            initMraidCloseButton();
            initMraidCloseButtonReceiver();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAd();
        registerListener();
    }

    private void registerListener() {
        if (mSensorManager != null) {
            mSensorManager.registerListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAd();
        unregisterSensorListener();
    }

    private void pauseAd() {
        if (!mIsDestroyBroadcastReceived) {
            mLoopMeAd.pause();
        }
    }

    @Override
    protected void onDestroy() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        clearLayout();
        destroyReceivers();
        super.onDestroy();
    }

    private void requestSystemFlags() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }

    private void retrieveLoopMeAdOrFinish() {
        mLoopMeAd = LoopMeAdHolder.getAd(getIntent());
        finishIfNull(mLoopMeAd);
        if (mLoopMeAd.getAdParams() != null) {
            mIsCloseButtonPresent = mLoopMeAd.getAdParams().isOwnCloseButton();
        }
    }

    private void finishIfNull(Object object) {
        if (object == null) {
            Logging.out(LOG_TAG, "Couldn't initialize BaseActivity");
            finish();
        }
    }

    private void setDisplayController() {
        if (mLoopMeAd != null) {
            mDisplayController = mLoopMeAd.getDisplayController();
            finishIfNull(mDisplayController);
        }
    }

    private void setOrientation() {
        if (mDisplayController != null) {
            setRequestedOrientation(mDisplayController.getOrientation());
        }
    }

    private void initSensorManager(Activity activity) {
        if (activity != null) {
            mSensorManager = new SensorManagerExtension().initSensor(activity, this);
        }
    }

    private void initDestroyReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DESTROY_INTENT);
        filter.addAction(Constants.CLICK_INTENT);
        mAdReceiver = new AdReceiver(this, mLoopMeAd.getAdId());
        registerReceiver(mAdReceiver, filter);
    }

    private boolean isInterstitial() {
        return mLoopMeAd != null && mLoopMeAd.isInterstitial();
    }

    private boolean isBanner() {
        return mLoopMeAd != null && mLoopMeAd.isBanner();
    }

    private void clearLayout() {
        if (mLoopMeContainerView != null) {
            mLoopMeContainerView.removeAllViews();
        }
    }

    private void destroyReceivers() {
        if (mAdReceiver != null) {
            unregisterReceiver(mAdReceiver);
        }
        if (mMraidCloseButtonReceiver != null) {
            unregisterReceiver(mMraidCloseButtonReceiver);
        }
    }

    private void unregisterSensorListener() {
        if (mSensorManager != null) {
            mSensorManager.pauseSensor();
        }
    }

    private void resumeAd() {
        if (mFirstLaunch) {
            startPlayNoneLoopMeInterstitial();
            resumeLoopMeController();
            mFirstLaunch = false;
        } else {
            mLoopMeAd.resume();
        }
    }

    private void resumeLoopMeController() {
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            mLoopMeAd.resume();
        }
    }

    private void startPlayNoneLoopMeInterstitial() {
        if (mLoopMeAd.isInterstitial()) {
            if (mDisplayController != null) {
                mDisplayController.onPlay(START_DEFAULT_POSITION);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isBanner()) {
            collapseMraidBanner();
            switchLoopMeBannerToPreviousMode();
            ((BaseTrackableController) mDisplayController).onAdExitedFullScreenEvent();
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroyBroadcast() {
        Logging.out(LOG_TAG, "onDestroyBroadcast");
        mIsDestroyBroadcastReceived = true;
        if (isBanner()) {
            switchLoopMeBannerToPreviousMode();
        }
        unregisterAdReceiver();
        finish();
    }

    private void switchLoopMeBannerToPreviousMode() {
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).switchToPreviousMode();
        }
    }

    private void unregisterAdReceiver() {
        if (mAdReceiver != null) {
            unregisterReceiver(mAdReceiver);
            mAdReceiver = null;
        }
    }

    @Override
    public void onClickBroadcast() {
        Logging.out(LOG_TAG, "onClickBroadcast()");
    }

    private void initMraidCloseButton() {
        mMraidCloseButton = new CloseButton(this);
        mMraidCloseButton.addInLayout(mLoopMeContainerView);
        mMraidCloseButton.setOnClickListener(initMraidCloseButtonListener());
        onCloseButtonVisibilityChanged(mIsCloseButtonPresent);
    }

    private View.OnClickListener initMraidCloseButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoopMeAd.isInterstitial()) {
                    closeMraidAd();
                } else {
                    collapseMraidBanner();
                }
            }
        };
    }

    private void closeMraidAd() {
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            ((DisplayControllerLoopMe) mDisplayController).closeMraidAd();
        }
    }

    private void initMraidCloseButtonReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.MRAID_NEED_CLOSE_BUTTON);
        mMraidCloseButtonReceiver = new MraidAdCloseButtonReceiver(this);
        registerReceiver(mMraidCloseButtonReceiver, intentFilter);
    }

    @Override
    public void onCloseButtonVisibilityChanged(boolean customCloseButton) {
        if (customCloseButton) {
            mMraidCloseButton.setAlpha(0);
        } else {
            mMraidCloseButton.setAlpha(1);
        }
    }

    @Override
    public void onAdShake() {
        if (mDisplayController != null) {
            mDisplayController.onAdShake();
        }
    }

    private void collapseMraidBanner() {
        if (mDisplayController != null) {
            ((DisplayControllerLoopMe) mDisplayController).collapseMraidBanner();
        }
    }
}