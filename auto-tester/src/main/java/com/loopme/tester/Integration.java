package com.loopme.tester;

import android.app.Activity;

import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.activity.BaseActivity;

/**
 * Created by katerina on 6/23/17.
 */

public class Integration {

    public static void insertDvTestKeys(Activity activity) {
        if (isInstanceOfBaseActivity(activity)) {
            ((BaseActivity) activity).insertAdSpot(addDVTestKey(activity));
        }
    }

    public static void insertMoatTestKeys(Activity activity) {
        if (isInstanceOfBaseActivity(activity)) {
            insertNativeAd(activity);
            insertHtmlAd(activity);
            insertNativeMpuAd(activity);
            insertHtmlMpuAd(activity);
            insert360Ad(activity);
        }
    }

    public static void insertLoopMeTestKeys(Activity activity) {
        if (isInstanceOfBaseActivity(activity)) {
            insertNativeAd(activity);
            insertRichMedia(activity);
            insertVpaidAd(activity);
            insertMopubAd(activity);
        }
    }

    public static void insertUserKeys(Activity activity) {
        if (isInstanceOfBaseActivity(activity)) {
            insertTestInterstitialP(activity);
            insertTestInterstitialL(activity);
            insertTest360(activity);
            insertTestMpu(activity);
            insertTestVpaid(activity);
        }
    }

    private static boolean isInstanceOfBaseActivity(Activity activity) {
        return activity != null && activity instanceof BaseActivity;
    }

    private static void insertTestVpaid(Activity activity) {
        AdSpot mopubAd = new AdSpot();
        mopubAd.setName(activity.getString(R.string.test_vpaid));
        mopubAd.setAppKey(Constants.Keys.KEY_TEST_VPAID);
        mopubAd.setTime(System.currentTimeMillis());
        mopubAd.setSdk(AdSdk.LMVPAID);
        mopubAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(mopubAd);
    }

    private static void insertTestMpu(Activity activity) {
        AdSpot mopubAd = new AdSpot();
        mopubAd.setName(activity.getString(R.string.test_mpu));
        mopubAd.setAppKey(Constants.Keys.KEY_TEST_MPU);
        mopubAd.setTime(System.currentTimeMillis());
        mopubAd.setSdk(AdSdk.LOOPME);
        mopubAd.setType(AdType.BANNER);
        ((BaseActivity) activity).insertAdSpot(mopubAd);
    }

    private static void insertTest360(Activity activity) {
        AdSpot mopubAd = new AdSpot();
        mopubAd.setName(activity.getString(R.string.test_360));
        mopubAd.setAppKey(Constants.Keys.KEY_TEST_360);
        mopubAd.setTime(System.currentTimeMillis());
        mopubAd.setSdk(AdSdk.LOOPME);
        mopubAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(mopubAd);
    }

    private static void insertTestInterstitialL(Activity activity) {
        AdSpot mopubAd = new AdSpot();
        mopubAd.setName(activity.getString(R.string.landscape_interstitial));
        mopubAd.setAppKey(Constants.Keys.KEY_TEST_INTERSTITIAL_L);
        mopubAd.setTime(System.currentTimeMillis());
        mopubAd.setSdk(AdSdk.LOOPME);
        mopubAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(mopubAd);
    }

    private static void insertTestInterstitialP(Activity activity) {
        AdSpot mopubAd = new AdSpot();
        mopubAd.setName(activity.getString(R.string.portrait_interstitial));
        mopubAd.setAppKey(Constants.Keys.KEY_TEST_INTERSTITIAL_P);
        mopubAd.setTime(System.currentTimeMillis());
        mopubAd.setSdk(AdSdk.LOOPME);
        mopubAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(mopubAd);
    }

    private static void insertMopubAd(Activity activity) {
        AdSpot mopubAd = new AdSpot();
        mopubAd.setName(activity.getString(R.string.mopub));
        mopubAd.setAppKey(Constants.Keys.KEY_MOPUB);
        mopubAd.setTime(System.currentTimeMillis());
        mopubAd.setSdk(AdSdk.MOPUB);
        mopubAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(mopubAd);
    }

    private static void insertVpaidAd(Activity activity) {
        AdSpot vpaidAd = new AdSpot();
        vpaidAd.setName(activity.getString(R.string.vpaid_video));
        vpaidAd.setAppKey(Constants.Keys.KEY_VPAID_VIDEO);
        vpaidAd.setTime(System.currentTimeMillis());
        vpaidAd.setSdk(AdSdk.LMVPAID);
        vpaidAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(vpaidAd);
    }

    private static void insertRichMedia(Activity activity) {
        AdSpot htmlAd = new AdSpot();
        htmlAd.setName(activity.getString(R.string.rich_media));
        htmlAd.setAppKey(Constants.Keys.KEY_HTML_RICH_MEDIA);
        htmlAd.setTime(System.currentTimeMillis());
        htmlAd.setSdk(AdSdk.LOOPME);
        htmlAd.setType(AdType.INTERSTITIAL);
        ((BaseActivity) activity).insertAdSpot(htmlAd);
    }

    private static void insert360Ad(Activity activity) {
        AdSpot adSpot360 = new AdSpot();
        adSpot360.setName(activity.getString(R.string.test_interstitial_360));
        adSpot360.setAppKey(Constants.Keys.KEY_360);
        adSpot360.setTime(System.currentTimeMillis());
        adSpot360.setSdk(AdSdk.LOOPME);
        adSpot360.setType(AdType.INTERSTITIAL);
        adSpot360.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        ((BaseActivity) activity).insertAdSpot(adSpot360);
    }

    private static void insertHtmlMpuAd(Activity activity) {
        AdSpot htmlMpuAdSpot = new AdSpot();
        htmlMpuAdSpot.setName(activity.getString(R.string.test_html_mpu));
        htmlMpuAdSpot.setAppKey(Constants.Keys.KEY_HTML_MPU);
        htmlMpuAdSpot.setTime(System.currentTimeMillis());
        htmlMpuAdSpot.setSdk(AdSdk.LOOPME);
        htmlMpuAdSpot.setType(AdType.BANNER);
        htmlMpuAdSpot.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        ((BaseActivity) activity).insertAdSpot(htmlMpuAdSpot);
    }

    private static void insertNativeMpuAd(Activity activity) {
        AdSpot nativeMpuAdSpot = new AdSpot();
        nativeMpuAdSpot.setName(activity.getString(R.string.test_native_mpu));
        nativeMpuAdSpot.setAppKey(Constants.Keys.KEY_NATIVE_MPU);
        nativeMpuAdSpot.setTime(System.currentTimeMillis());
        nativeMpuAdSpot.setSdk(AdSdk.LOOPME);
        nativeMpuAdSpot.setType(AdType.BANNER);
        nativeMpuAdSpot.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        ((BaseActivity) activity).insertAdSpot(nativeMpuAdSpot);
    }

    private static void insertHtmlAd(Activity activity) {
        AdSpot htmlAdSpot = new AdSpot();
        htmlAdSpot.setName(activity.getString(R.string.test_html_interstitial));
        htmlAdSpot.setAppKey(Constants.Keys.KEY_HTML_INTERSTITIAL);
        htmlAdSpot.setTime(System.currentTimeMillis());
        htmlAdSpot.setSdk(AdSdk.LOOPME);
        htmlAdSpot.setType(AdType.INTERSTITIAL);
        htmlAdSpot.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        ((BaseActivity) activity).insertAdSpot(htmlAdSpot);
    }

    private static void insertNativeAd(Activity activity) {
        AdSpot nativeAdSpot = new AdSpot();
        nativeAdSpot.setName(activity.getString(R.string.test_native_interstital));
        nativeAdSpot.setAppKey(Constants.Keys.KEY_NATIVE_INTERSTITIAL);
        nativeAdSpot.setTime(System.currentTimeMillis());
        nativeAdSpot.setSdk(AdSdk.LOOPME);
        nativeAdSpot.setType(AdType.INTERSTITIAL);
        nativeAdSpot.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        ((BaseActivity) activity).insertAdSpot(nativeAdSpot);

    }

    private static AdSpot addDVTestKey(Activity activity) {
        AdSpot adSpot = new AdSpot();
        adSpot.setName(activity.getString(R.string.test_dv_vast));
        adSpot.setAppKey(Constants.Keys.KEY_DV);
        adSpot.setTime(System.currentTimeMillis());
        adSpot.setSdk(AdSdk.LMVPAID);
        adSpot.setType(AdType.INTERSTITIAL);
        adSpot.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        return adSpot;
    }

}
