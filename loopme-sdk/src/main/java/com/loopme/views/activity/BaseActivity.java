package com.loopme.views.activity;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
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
import com.loopme.utils.PermissionUtils;
import com.loopme.views.CloseButton;
import com.loopme.views.LoopMeWebView;
import com.loopme.views.webclient.AdViewChromeClient;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public final class BaseActivity extends FragmentActivity
        implements AdReceiver.Listener,
        MraidAdCloseButtonReceiver.MraidAdCloseButtonListener,
        SensorManagerExtension.OnLoopMeSensorListener, AdViewChromeClient.PermissionResolver {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private static final int START_DEFAULT_POSITION = 0;

    private static int REQUEST_GENERAL_PERMISSIONS = 0;
    private static int REQUEST_LOCATION_PERMISSIONS = 1;

    private BaseTrackableController mDisplayController;

    private AdReceiver mAdReceiver;
    private LoopMeAd mLoopMeAd;

    private FrameLayout mLoopMeContainerView;
    private boolean mFirstLaunch = true;

    // MRAID
    private CloseButton mMraidCloseButton;
    private MraidAdCloseButtonReceiver mMraidCloseButtonReceiver;
    private boolean mIsDestroyBroadcastReceived;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.out(LOG_TAG, "onCreate");

        mLoopMeAd = LoopMeAdHolder.getAd(getIntent());
        mDisplayController = mLoopMeAd == null ? null : mLoopMeAd.getDisplayController();

        if (mLoopMeAd == null || mLoopMeAd.getAdParams() == null || mDisplayController == null) {
            Logging.out(LOG_TAG, "Couldn't initialize BaseActivity");
            finish();
            return;
        }

        goHWAcceleratedFullscreenMode();

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

        if (mLoopMeAd.isMraidAd()) {
            initMraidCloseButton();
            registerMraidCloseButtonReceiver();
        }
    }

    private void goHWAcceleratedFullscreenMode() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }

    private void setContentView() {
        setContentView(R.layout.base_activity_main);

        // Debug obstruction visibility.
        Bundle b = getIntent().getExtras();

        View debugObstruction = findViewById(R.id.debug_obstruction);
        debugObstruction.setVisibility(
                b != null && b.getBoolean(Constants.EXTRAS_DEBUG_OBSTRUCTION_ENABLED)
                        ? View.VISIBLE
                        : View.GONE);

        mLoopMeContainerView = findViewById(R.id.loopme_container_view);
        mLoopMeAd.onNewContainer(mLoopMeContainerView);
    }

    // TODO. Ugly. For permission dialogs.
    private static void trySetPermissionResolveListener(
            WebView webView,
            AdViewChromeClient.PermissionResolver permissionResolver) {

        AdViewChromeClient chromeClient = tryGetAdViewChromeClient(webView);
        if (chromeClient != null)
            chromeClient.setPermissionResolveListener(permissionResolver);
    }

    private static AdViewChromeClient tryGetAdViewChromeClient(WebView webView) {
        LoopMeWebView lwv = webView instanceof LoopMeWebView ? (LoopMeWebView) webView : null;
        if (lwv == null)
            return null;

        WebChromeClient wcc = lwv.getWebChromeClientCompat();
        return wcc instanceof AdViewChromeClient ? (AdViewChromeClient) wcc : null;
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            hideSystemUI();
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

    @Override
    protected void onDestroy() {

        if (mDisplayController != null)
            trySetPermissionResolveListener(mDisplayController.getWebView(), null);

        if (mDisplayController instanceof DisplayControllerLoopMe)
            ((DisplayControllerLoopMe) mDisplayController)
                    .tryRemoveOmidFriendlyObstruction(mMraidCloseButton);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (mLoopMeContainerView != null)
            mLoopMeContainerView.removeAllViews();

        unregisterDestroyReceiver();
        unregisterMraidCloseButtonReceiver();

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

    private void initMraidCloseButton() {
        final DisplayControllerLoopMe dc = (DisplayControllerLoopMe) mDisplayController;

        mMraidCloseButton = new CloseButton(this);
        mMraidCloseButton.addInLayout(mLoopMeContainerView);
        mMraidCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoopMeAd.isInterstitial())
                    dc.closeMraidAd();
                else
                    dc.collapseMraidBanner();
            }
        });

        onCloseButtonVisibilityChanged(mLoopMeAd.getAdParams().isOwnCloseButton());

        dc.tryAddOmidFriendlyObstructionCloseButton(mMraidCloseButton);
    }

    @Override
    public void onCloseButtonVisibilityChanged(boolean customCloseButton) {
        mMraidCloseButton.setAlpha(customCloseButton ? 0 : 1);
    }

    @Override
    public void onAdShake() {
        mDisplayController.onAdShake();
    }

    private void registerDestroyReceiver() {
        mAdReceiver = new AdReceiver(this, mLoopMeAd.getAdId());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DESTROY_INTENT);
        filter.addAction(Constants.CLICK_INTENT);

        registerReceiver(mAdReceiver, filter);
    }

    private void unregisterDestroyReceiver() {
        if (mAdReceiver != null)
            unregisterReceiver(mAdReceiver);

        mAdReceiver = null;
    }

    private void registerMraidCloseButtonReceiver() {
        mMraidCloseButtonReceiver = new MraidAdCloseButtonReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.MRAID_NEED_CLOSE_BUTTON);

        registerReceiver(mMraidCloseButtonReceiver, intentFilter);
    }

    private void unregisterMraidCloseButtonReceiver() {
        if (mMraidCloseButtonReceiver != null)
            unregisterReceiver(mMraidCloseButtonReceiver);

        mMraidCloseButtonReceiver = null;
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

        if (requestCode == REQUEST_LOCATION_PERMISSIONS)
            chromeClient.setLocationPermissionGranted(
                    PermissionUtils
                            .groupPermissions(this, LOCATION_PERMISSIONS)
                            .getGrantedPermissions().size() > 0);
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
                this,
                deniedPermissions.toArray(new String[0]),
                REQUEST_GENERAL_PERMISSIONS);
    }

    private String[] generalPermissionsFromWebViewRequest;

    @Override
    public void onCancelGeneralPermissionsRequest() {
        generalPermissionsFromWebViewRequest = null;
    }

    @Override
    public void onRequestLocationPermission(String origin) {
        PermissionUtils.GroupedPermissions groupedPermissions =
                PermissionUtils.groupPermissions(this, LOCATION_PERMISSIONS);

        List<String> deniedPermissions = groupedPermissions.getDeniedPermissions();

        if (deniedPermissions.isEmpty()) {
            AdViewChromeClient chromeClient = tryGetAdViewChromeClient(mDisplayController.getWebView());
            if (chromeClient != null) {
                chromeClient.setLocationPermissionGranted(
                        groupedPermissions.getGrantedPermissions().size() > 0);
            }
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                deniedPermissions.toArray(new String[0]),
                REQUEST_LOCATION_PERMISSIONS);
    }

    @Override
    public void onCancelLocationPermissionRequest() {

    }

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION
    };
}