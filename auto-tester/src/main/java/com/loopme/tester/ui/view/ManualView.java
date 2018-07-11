package com.loopme.tester.ui.view;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopme.LoopMeSdk;
import com.loopme.ad.LoopMeAd;
import com.loopme.request.RequestParamsUtils;
import com.loopme.tester.R;
import com.loopme.tester.ads.Ad;
import com.loopme.tester.ads.AdListener;
import com.loopme.tester.ads.AdLoopMeBanner;
import com.loopme.tester.ads.AdLoopMeInterstitial;
import com.loopme.tester.ads.AdMopubBanner;
import com.loopme.tester.ads.AdMopubInterstitial;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.utils.Utils;
import com.mopub.mobileads.MoPubView;

public class ManualView implements View.OnClickListener, AdListener, AdapterView.OnItemSelectedListener, SizeDialog.Listener {
    private Ad mAd;
    private View mRootView;
    private AdSpot mAdSpot;
    private TextView mLoadingLabel;
    private TextView mLoadButton;
    private TextView mShowButton;
    private ScrollView mScrollView;
    private Activity mActivity;
    private boolean mIsFirstLaunch = true;
    private boolean mIsAutoLoadingEnabled;
    private FrameLayout mBanner;


    public ManualView(View layout, AdSpot adSpot, Activity activity) {
        mRootView = layout;
        mAdSpot = adSpot;
        mActivity = activity;

        mIsAutoLoadingEnabled = getAutoLoadingState();
        layout.findViewById(R.id.manual_view_relativelayout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callDialog();
                return true;
            }
        });
        LoopMeSdk.askGdprConsent(mActivity, null);
    }

    private void callDialog() {
        ViewGroup.LayoutParams layoutParams = mBanner.getLayoutParams();
        final int widthDp = RequestParamsUtils.convertPixelToDp(mActivity, layoutParams.width);
        final int heightDp = RequestParamsUtils.convertPixelToDp(mActivity, layoutParams.height);
        SizeDialog dialog = SizeDialog.newInstance(widthDp, heightDp);
        dialog.setListener(this);
        dialog.show(mActivity.getFragmentManager(), "Size Dialog");
    }

    @Override
    public void onNewBannerSize(int width, int height) {
        int widthDp = Utils.convertDpToPixel(width, mActivity);
        int heightDp = Utils.convertDpToPixel(height, mActivity);
        mBanner.setLayoutParams(new RelativeLayout.LayoutParams(widthDp, heightDp));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_ad_manual:
                loadAd();
                break;
            case R.id.show_ad_manual:
                showAd();
                break;
            default:
                break;
        }
    }

    public void initView() {
        mLoadButton = (TextView) mRootView.findViewById(R.id.load_ad_manual);
        mShowButton = (TextView) mRootView.findViewById(R.id.show_ad_manual);
        mScrollView = (ScrollView) mRootView.findViewById(R.id.scrollview);

        Spinner adTypeSpinner = (Spinner) mRootView.findViewById(R.id.ad_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity, R.array.ad_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adTypeSpinner.setAdapter(adapter);
        adTypeSpinner.setOnItemSelectedListener(this);

        mShowButton.setText(R.string.show);
        mLoadingLabel = (TextView) mRootView.findViewById(R.id.loading_label_manual);
        mLoadingLabel.setVisibility(View.VISIBLE);
        mLoadButton.setOnClickListener(this);
        mShowButton.setOnClickListener(this);
        setReadyToLoading();
        disableShowButton();
        mBanner = (FrameLayout) mRootView.findViewById(R.id.loopme_banner);
    }

    private void showAd() {
        if (isShowButton()) {
            doShowButtonLogic();
        } else {
            doHideButtonLogic();
        }
    }

    private void doHideButtonLogic() {
        if (mIsAutoLoadingEnabled && !Utils.isApi19()) {
            dismissBannerView();
        } else {
            hideAd();
        }
    }

    private void dismissBannerView() {
        if (isAdReady()) {
            dismissAd();
            setLoadedView();
        } else {
            showAdNotReadyLabel();
            hideAd();
        }
    }

    private void showAdNotReadyLabel() {
        if (mLoadingLabel != null) {
            mLoadingLabel.setText(mActivity.getString(R.string.ad_is_not_ready));
        }
    }

    private void doShowButtonLogic() {
        if (isShowButton()) {
            if (isAdReady()) {
                showAdvertisement();
            } else {
                enableShowButton(false);
                setLabel(mActivity.getString(R.string.loading));
            }
        }
    }

    private void showAdvertisement() {
        scrollToBanner();
        mAd.showAd();
        setShowingView();
    }

    @Override
    public void onLoadSuccess() {
        if (!isAdShowing()) {
            setLoadedView();
        }
    }

    @Override
    public void onShow() {
        setShowingView();
    }

    @Override
    public void onLoadFail(String error) {
        if (mLoadingLabel != null) {
            mLoadingLabel.setText(error);
        }
        enableLoadButton(true);
    }

    @Override
    public void onHide() {
        if (isAdReady()) {
            setLoadedView();
        } else {
            disableShowButton();
            mLoadingLabel.setText(mActivity.getString(R.string.ad_hidden_and_ready_to_load_again));
            enableLoadButton(true);
        }
    }

    private boolean isAdShowing() {
        return mAd != null && mAd.isShowing();
    }

    public void loadAd() {
        if (mAd == null) {
            initAd();
        }
        if (!isAdReady()) {
            setLoadingView();
            mAd.loadAd();
        } else {
            setLoadedView();
        }
    }

    private void hideAd() {
        dismissAd();
        disableShowButton();
        setReadyToLoading();
        enableLoadButton(true);
    }

    public void dismissAd() {
        if (mAd != null) {
            mAd.dismissAd();
        }
    }

    private void setLoadingView() {
        mLoadingLabel.setText(mActivity.getString(R.string.loading));
        disableShowButton();
        enableLoadButton(false);
    }

    private void setReadyToLoading() {
        mLoadingLabel.setText(mActivity.getString(R.string.ready_to_loading));
    }

    private void enableLoadButton(boolean enable) {
        if (enable) {
            mLoadButton.setTextColor(ContextCompat.getColor(mActivity, R.color.red));
        } else {
            mLoadButton.setTextColor(ContextCompat.getColor(mActivity, R.color.gray));
        }
        mLoadButton.setEnabled(enable);
    }

    private void scrollToBanner() {
        if (mAd instanceof AdLoopMeBanner || mAd instanceof AdMopubBanner) {
            scrollDown();
        }
    }

    private void setLoadedView() {
        mLoadingLabel.setText(R.string.ad_loaded);
        enableShowButton(true);
        enableLoadButton(false);
    }

    private void enableShowButton(boolean enable) {
        if (enable) {
            mShowButton.setText(mActivity.getString(R.string.show));
            mShowButton.setTextColor(ContextCompat.getColor(mActivity, R.color.red));
        } else {
            mShowButton.setTextColor(ContextCompat.getColor(mActivity, R.color.gray));
        }
        mShowButton.setEnabled(enable);
    }

    private void scrollDown() {
        if (mScrollView != null) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }

    private boolean isShowButton() {
        String showButton = mActivity.getResources().getString(R.string.show);
        return TextUtils.equals(mShowButton.getText(), showButton);

    }

    private boolean isAdReady() {
        return mAd != null && mAd.isReady();
    }

    @Override
    public void onExpired() {
        enableLoadButton(true);
        enableShowButton(false);
        setLabel(mActivity.getString(R.string.ad_expired));
    }

    private void setLabel(String text) {
        if (mLoadingLabel != null) {
            mLoadingLabel.setText(text);
        }
    }

    private void setShowingView() {
        mShowButton.setText(mActivity.getString(R.string.hide));
        mLoadButton.setEnabled(false);
        mLoadingLabel.setText(mActivity.getString(R.string.ad_is_shown));
    }

    private void disableShowButton() {
        mShowButton.setText(mActivity.getString(R.string.show));
        mShowButton.setTextColor(ContextCompat.getColor(mActivity, R.color.dark_gray));
        mShowButton.setEnabled(false);
    }

    public void onDestroy() {
        destroyAd();
        setInitialView();
    }

    private void setInitialView() {
        enableLoadButton(true);
        disableShowButton();
        setReadyToLoading();
    }

    private void destroyAd() {
        if (mAd != null) {
            mAd.destroyAd();
            mAd = null;
        }
    }

    public void onPause() {
        pauseAd();
    }

    public void onResume() {
        if (mAd != null) {
            mAd.onResume();
            if (!isAdReady()) {
                enableLoadButton(true);
                disableShowButton();
            }
        }
    }

    private void pauseAd() {
        if (mAd != null) {
            mAd.onPause();
        }
    }

    public void startLoadAd() {
        if (mIsFirstLaunch) {
            mIsFirstLaunch = false;
//            loadAd();
        }
    }

    public void setAdSpot(AdSpot mAdSpot) {
        this.mAdSpot = mAdSpot;
    }

    private boolean getAutoLoadingState() {
        return mActivity != null && mActivity instanceof BaseActivity && ((BaseActivity) mActivity).getAutoLoadingState();
    }

    private void initAd() {
        if (mAdSpot == null) {
            return;
        }
        AdSdk adSdk = mAdSpot.getSdk();
        AdType adType = mAdSpot.getType();

        if (adType == AdType.INTERSTITIAL && (adSdk == AdSdk.LOOPME || adSdk == AdSdk.LMVPAID)) {
            mAd = initializeLoopmeInterstitial();
        } else if (adType == AdType.INTERSTITIAL && adSdk == AdSdk.MOPUB) {
            mAd = initializeMopubInterstitial();
        } else if (adType == AdType.BANNER && (adSdk == AdSdk.LOOPME || adSdk == AdSdk.LMVPAID)) {
            mAd = initializeLoopmeBanner();
        } else if (adType == AdType.BANNER && adSdk == AdSdk.MOPUB) {
            mAd = initializeMopubBanner();
        }
    }

    private Ad initializeLoopmeInterstitial() {
        return new AdLoopMeInterstitial(mActivity, mAdSpot.getAppKey(), this, mIsAutoLoadingEnabled);
    }

    private Ad initializeLoopmeBanner() {
        FrameLayout banner = (FrameLayout) mRootView.findViewById(R.id.loopme_banner);
        return new AdLoopMeBanner(mActivity, mAdSpot.getAppKey(), banner, this, mIsAutoLoadingEnabled);
    }

    private Ad initializeMopubInterstitial() {
        return new AdMopubInterstitial(mActivity, mAdSpot.getAppKey(), this);
    }

    private Ad initializeMopubBanner() {
        MoPubView moPubView = new MoPubView(mActivity);
        ((ViewGroup) mRootView).addView(moPubView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) moPubView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        moPubView.setLayoutParams(params);
        return new AdMopubBanner(moPubView, mAdSpot.getAppKey(), this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setPreferredAd(parent.getItemAtPosition(position).toString());
    }

    private void setPreferredAd(String preferredAd) {
        LoopMeAd.Type type = LoopMeAd.Type.fromString(preferredAd);
        if (mAd != null) {
            mAd.setPreferredAd(type);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
