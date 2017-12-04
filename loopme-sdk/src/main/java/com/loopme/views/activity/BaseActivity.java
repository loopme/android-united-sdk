package com.loopme.views.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.R;
import com.loopme.SensorManagerExtension;
import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.controllers.display.BaseDisplayController;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.controllers.interfaces.DisplayController;
import com.loopme.receiver.AdReceiver;
import com.loopme.receiver.MraidAdCloseButtonReceiver;
import com.loopme.utils.Utils;
import com.loopme.views.CloseButton;
import com.loopme.views.MraidView;
import com.testfairy.TestFairy;

import io.fabric.sdk.android.Fabric;

public final class BaseActivity extends Activity
        implements AdReceiver.Listener,
        MraidAdCloseButtonReceiver.MraidAdCloseButtonListener,
        SensorManagerExtension.OnLoopMeSensorListener {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private static final int START_DEFAULT_POSITION = 0;
    private SensorManagerExtension mSensorManager;
    private DisplayController mDisplayController;

    private AdReceiver mAdReceiver;
    private LoopMeAd mLoopMeAd;
    private boolean mIs360;

    private int mInitialOrientation;
    private FrameLayout mLoopMeContainerView;
    private boolean mFirstLaunch = true;
    //mraid
    private boolean mIsCloseButtonPresent;
    private CloseButton mMraidCloseButton;
    private MraidAdCloseButtonReceiver mMraidCloseButtonReceiver;
    private boolean mIsDestroyBroadcastReceived;

    private MraidView mMraidView;

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
        initCrashlytics();
        if (mLoopMeAd != null && mLoopMeAd.getAdParams() != null) {
            retrieveParams();
            setContentView();
            initOrientation();
            initSensorManager();
            initDestroyReceiver();
            setMraidSettings();
        } else {
            finish();
        }
    }

    private void initCrashlytics() {
        Fabric.with(this, new Crashlytics());
        Appsee.start(getString(R.string.com_appsee_apikey));
        TestFairy.begin(this, "a44d0f385de2740ba8f9aefcf9c0eda6706acc0f");
    }

    private void setMraidSettings() {
        if (mLoopMeAd != null && mLoopMeAd.getAdParams().isMraidAd()) {
            initMraidCloseButton();
            initMraidCloseButtonReceiver();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAd();
        mSensorManager.registerListener();
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

    private void retrieveParams() {
        mIs360 = is360Video();
        if (mLoopMeAd != null) {
            mDisplayController = mLoopMeAd.getDisplayController();
            finishIfNull(mDisplayController);
        }
    }

    private boolean is360Video() {
        return mLoopMeAd != null && mLoopMeAd.getAdParams() != null && mLoopMeAd.getAdParams().isVideo360();
    }

    private void initOrientation() {
        mInitialOrientation = Utils.getScreenOrientation();
        if (!mLoopMeAd.isMraidAd()) {
            if (isInterstitial()) {
                if (!mIs360) {
                    applyOrientationFromAdParams();
                }
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        } else {
            applyMraidOrientation();
        }
    }

    private void applyMraidOrientation() {
        int orientation = parseOrientation();
        setRequestedOrientation(orientation);
    }

    private int parseOrientation() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getInt(Constants.EXTRAS_FORCE_ORIENTATION);
        } else {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        }
    }

    private void setContentView() {
        setContentView(R.layout.base_activity_main);
        mLoopMeContainerView = (FrameLayout) findViewById(R.id.loopme_container_view);
        if (mLoopMeAd.isInterstitial()) {
            mLoopMeAd.bindView(mLoopMeContainerView);
        } else if (mLoopMeAd.isMraidAd()) {
            mLoopMeContainerView.addView(buildLayout());
        } else {
            mLoopMeAd.rebuildView(mLoopMeContainerView);
            ((BaseDisplayController) mDisplayController).onAdEnteredFullScreenEvent();
        }
        Appsee.unmarkViewAsSensitive(mLoopMeContainerView);
    }

    private RelativeLayout buildLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (mDisplayController != null) {
            mMraidView = ((DisplayControllerLoopMe) mDisplayController).getMraidView();
        } else {
            Logging.out(LOG_TAG, "mAdController is null");
        }

        if (mMraidView != null) {
            Utils.removeParent(mMraidView);
            relativeLayout.addView(mMraidView, params);
            return relativeLayout;
        } else {
            return null;
        }
    }


    private void initSensorManager() {
        if (mLoopMeAd != null) {
            mSensorManager = new SensorManagerExtension().initSensor(mLoopMeAd.getContext(), this);
        }
    }

    private void initDestroyReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DESTROY_INTENT);
        filter.addAction(Constants.CLICK_INTENT);
        mAdReceiver = new AdReceiver(this, mLoopMeAd.getAdId());
        registerReceiver(mAdReceiver, filter);
    }

    /**
     * Apply orientation from AdParams.
     * Do nothing if orientation parameter absent in AdParams.
     */
    private void applyOrientationFromAdParams() {
        String orientation = mLoopMeAd.getAdParams().getAdOrientation();
        if (orientation == null) {
            return;
        }
        if (orientation.equalsIgnoreCase(Constants.ORIENTATION_PORT)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (orientation.equalsIgnoreCase(Constants.ORIENTATION_LAND)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
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
            startPlayInterstitial();
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

    private void startPlayInterstitial() {
        if (mLoopMeAd.isInterstitial()) {
            play();
        }
    }

    private void play() {
        if (mDisplayController != null) {
            mDisplayController.onPlay(START_DEFAULT_POSITION);
        }
    }

    @Override
    public void onBackPressed() {
        if (isBanner()) {
            switchLoopMeBannerToPreviousMode();
            ((BaseDisplayController) mDisplayController).onAdExitedFullScreenEvent();
            if (mLoopMeAd.getAdFormat() == Constants.AdFormat.BANNER) {
                collapseBanner();
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroyBroadcast() {
        Logging.out(LOG_TAG, "onDestroyBroadcast");
        mIsDestroyBroadcastReceived = true;
        if (isBanner()) {
            setRequestedOrientation(mInitialOrientation);
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
//        mMraidCloseButton.setVisibility(View.GONE);
    }

    private View.OnClickListener initMraidCloseButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoopMeAd.getAdFormat() == Constants.AdFormat.INTERSTITIAL) {
                    closeMraidAd();
                } else {
                    collapseBanner();
                }
            }
        };
    }

    private void closeMraidAd() {
        if (mDisplayController != null && mDisplayController instanceof DisplayControllerLoopMe) {
            DisplayControllerLoopMe displayControllerLoopMe = (DisplayControllerLoopMe) mDisplayController;
            displayControllerLoopMe.closeMraidAd();
            BaseActivity.this.finish();
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
            mMraidCloseButton.setVisibility(View.GONE);
        } else {
            mMraidCloseButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdShake() {
        if (mDisplayController != null) {
            mDisplayController.onAdShake();
        }
    }

    private void collapseBanner() {
        if (mDisplayController != null) {
            ((DisplayControllerLoopMe) mDisplayController).collapseMraidBanner();
        }
    }
}