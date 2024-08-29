package com.loopme.views.activity;

import static com.loopme.Constants.SKIP_DELAY_INTERSTITIAL;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.loopme.Constants;
import com.loopme.Constants.Layout;
import com.loopme.Logging;
import com.loopme.R;
import com.loopme.ad.LoopMeAd;
import com.loopme.ad.LoopMeAdHolder;
import com.loopme.controllers.display.BaseTrackableController;
import com.loopme.controllers.display.DisplayControllerLoopMe;
import com.loopme.receiver.AdReceiver;
import com.loopme.utils.PermissionUtils;
import com.loopme.views.CloseButton;
import com.loopme.views.MraidView;
import com.loopme.views.webclient.AdViewChromeClient;

import java.util.List;


public final class BaseActivity extends FragmentActivity implements AdViewChromeClient.PermissionResolver {
    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private static final int START_DEFAULT_POSITION = 0;
    private static final int REQUEST_GENERAL_PERMISSIONS = 0;

    private BaseTrackableController mDisplayController;

    private AdReceiver mAdReceiver;
    private LoopMeAd mLoopMeAd;

    private FrameLayout mLoopMeContainerView;
    private boolean mFirstLaunch = true;

    // MRAID
    private CloseButton mMraidCloseButton;
    private boolean mIsDestroyBroadcastReceived;
    private String[] generalPermissionsFromWebViewRequest;

    private void registerDestroyReceiver() {
        mAdReceiver = new AdReceiver(new AdReceiver.Listener() {
            @Override
            public void onDestroyBroadcast() {
                Logging.out(LOG_TAG, "onDestroyBroadcast");
                mIsDestroyBroadcastReceived = true;
                if (mLoopMeAd.isBanner())
                    ((DisplayControllerLoopMe) mDisplayController).switchToPreviousMode();
                unregisterDestroyReceiver();
                finish();
            }

            @Override
            public void onClickBroadcast() {
                Logging.out(LOG_TAG, "onClickBroadcast()");
            }
        }, mLoopMeAd.getAdId());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DESTROY_INTENT);
        filter.addAction(Constants.CLICK_INTENT);
        ContextCompat.registerReceiver(this, mAdReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.out(LOG_TAG, "onCreate");
        Intent intent = getIntent();
        mLoopMeAd = intent == null ? null : LoopMeAdHolder.getAd(intent);
        mDisplayController = mLoopMeAd == null ? null : mLoopMeAd.getDisplayController();

        if (mLoopMeAd == null || mLoopMeAd.getAdParams() == null || mDisplayController == null) {
            Logging.out(LOG_TAG, "Couldn't initialize BaseActivity");
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setRequestedOrientation(mDisplayController.getOrientation());

        setContentView();

        mDisplayController.onAdRegisterView(this, mDisplayController.getWebView());
        mDisplayController.postImpression();
        mDisplayController.onAdEnteredFullScreenEvent();
        mDisplayController.onNewActivity(this);

        // TODO. Ugly. For permission dialogs.
        trySetPermissionResolveListener(mDisplayController.getWebView(), this);

        registerDestroyReceiver();

        if (!mLoopMeAd.isMraidAd()) {
            return;
        }
        mMraidCloseButton = new CloseButton(this);
        mMraidCloseButton.registerReceiver();
        DisplayControllerLoopMe dc = (DisplayControllerLoopMe) mDisplayController;
        mMraidCloseButton.setOnClickListener(mLoopMeAd.isInterstitial() ? v -> dc.closeMraidAd() : v -> dc.collapseMraidBanner());
        dc.tryAddOmidFriendlyObstructionCloseButton(mMraidCloseButton);

        new Handler().postDelayed(() -> {
            mLoopMeContainerView.addView(mMraidCloseButton, Layout.WRAP_CONTENT_END);
        }, SKIP_DELAY_INTERSTITIAL);
    }

    private void setContentView() {
        setContentView(R.layout.base_activity_main);
        Bundle b = getIntent().getExtras();
        boolean isDebugObstructionEnabled = b != null && b.getBoolean(Constants.EXTRAS_DEBUG_OBSTRUCTION_ENABLED);
        findViewById(R.id.debug_obstruction).setVisibility(isDebugObstructionEnabled ? View.VISIBLE : View.GONE);
        mLoopMeContainerView = findViewById(R.id.loopme_container_view);
        if (mLoopMeContainerView != null) {
            mLoopMeAd.onNewContainer(mLoopMeContainerView);
        }
    }

    // TODO. Ugly. For permission dialogs.
    private static void trySetPermissionResolveListener(
        WebView webView, AdViewChromeClient.PermissionResolver permissionResolver
    ) {
        AdViewChromeClient chromeClient = tryGetAdViewChromeClient(webView);
        if (chromeClient != null)
            chromeClient.setPermissionResolveListener(permissionResolver);
    }

    private static AdViewChromeClient tryGetAdViewChromeClient(WebView webView) {
        MraidView lwv = webView instanceof MraidView ? (MraidView) webView : null;
        if (lwv == null)
            return null;

        WebChromeClient wcc = lwv.getWebChromeClientCompat();
        return wcc instanceof AdViewChromeClient ? (AdViewChromeClient) wcc : null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mFirstLaunch) {
            mLoopMeAd.resume();
            return;
        }
        mFirstLaunch = false;
        if (mLoopMeAd.isInterstitial())
            mDisplayController.onPlay(START_DEFAULT_POSITION);
        if (mDisplayController instanceof DisplayControllerLoopMe)
            mLoopMeAd.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsDestroyBroadcastReceived)
            mLoopMeAd.pause();
    }

    private void unregisterDestroyReceiver() {
        if (mAdReceiver != null)
            unregisterReceiver(mAdReceiver);
        mAdReceiver = null;
    }

    @Override
    protected void onDestroy() {
        if (mDisplayController != null)
            trySetPermissionResolveListener(mDisplayController.getWebView(), null);
        if (mDisplayController instanceof DisplayControllerLoopMe) {
            if (mMraidCloseButton != null) {
                ((DisplayControllerLoopMe) mDisplayController)
                    .tryRemoveOmidFriendlyObstruction(mMraidCloseButton);
                mMraidCloseButton.unregisterReceiver();
            }
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (mLoopMeContainerView != null)
            mLoopMeContainerView.removeAllViews();
        unregisterDestroyReceiver();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mLoopMeAd.isBanner())
            return;
        DisplayControllerLoopMe dc = (DisplayControllerLoopMe) mDisplayController;
        dc.collapseMraidBanner();
        dc.switchToPreviousMode();
        dc.onAdExitedFullScreenEvent();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AdViewChromeClient chromeClient = tryGetAdViewChromeClient(mDisplayController.getWebView());
        if (chromeClient == null)
            return;

        if (requestCode == REQUEST_GENERAL_PERMISSIONS) {
            chromeClient.setGeneralPermissionsResponse(
                PermissionUtils.groupPermissions(
                    this,
                    generalPermissionsFromWebViewRequest)
                    .getGrantedPermissions()
                    .toArray(new String[0])
            );
            generalPermissionsFromWebViewRequest = null;
        }
    }

    @Override
    public void onRequestGeneralPermissions(String[] androidPermissions) {
        PermissionUtils.GroupedPermissions groupedPermissions =
            PermissionUtils.groupPermissions(this, androidPermissions);

        List<String> deniedPermissions = groupedPermissions.getDeniedPermissions();

        if (deniedPermissions.isEmpty()) {
            AdViewChromeClient chromeClient = tryGetAdViewChromeClient(mDisplayController.getWebView());
            if (chromeClient != null) {
                chromeClient.setGeneralPermissionsResponse(
                        groupedPermissions.getGrantedPermissions().toArray(new String[0]));
            }
            return;
        }

        generalPermissionsFromWebViewRequest = androidPermissions;

        ActivityCompat.requestPermissions(
            this, deniedPermissions.toArray(new String[0]), REQUEST_GENERAL_PERMISSIONS
        );
    }

    @Override
    public void onCancelGeneralPermissionsRequest() {
        generalPermissionsFromWebViewRequest = null;
    }
}