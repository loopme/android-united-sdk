package com.loopme.tester;

import android.app.Activity;

import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.ui.activity.MainActivity;

/**
 * Created by katerina on 6/23/17.
 */

public class Integration {

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

    private static void insertHtmlRichMedia(Activity activity) {
        AdSpot htmlAdSpot = new AdSpot();
        htmlAdSpot.setName(activity.getString(R.string.test_html_rich_media_interstitial));
        htmlAdSpot.setAppKey(Constants.Keys.INTERSTITIAL_HTML_RICH_MEDIA);
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

    public static void insertIasKeys(BaseActivity activity) {
        AdSpot nativeKey = new AdSpot();
        nativeKey.setName(activity.getString(R.string.native_video));
        nativeKey.setAppKey(Constants.Keys.KEY_TEST_INTERSTITIAL_P);
        nativeKey.setTime(System.currentTimeMillis());
        nativeKey.setSdk(AdSdk.LOOPME);
        nativeKey.setType(AdType.INTERSTITIAL);
        nativeKey.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(nativeKey);

        AdSpot htmlGwd = new AdSpot();
        htmlGwd.setName(activity.getString(R.string.html_gwd));
        htmlGwd.setAppKey(Constants.Keys.KEY_HTML_GWD);
        htmlGwd.setTime(System.currentTimeMillis());
        htmlGwd.setSdk(AdSdk.LOOPME);
        htmlGwd.setType(AdType.INTERSTITIAL);
        htmlGwd.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(htmlGwd);

        AdSpot htmNoneGwd = new AdSpot();
        htmNoneGwd.setName(activity.getString(R.string.html_none_gwd));
        htmNoneGwd.setAppKey(Constants.Keys.KEY_HTML_NONE_GWD);
        htmNoneGwd.setTime(System.currentTimeMillis());
        htmNoneGwd.setSdk(AdSdk.LOOPME);
        htmNoneGwd.setType(AdType.INTERSTITIAL);
        htmNoneGwd.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(htmNoneGwd);

        AdSpot expBanner = new AdSpot();
        expBanner.setName(activity.getString(R.string.html_banner_expand));
        expBanner.setAppKey(Constants.Keys.KEY_MRAID_EXP_BANNER);
        expBanner.setTime(System.currentTimeMillis());
        expBanner.setSdk(AdSdk.LOOPME);
        expBanner.setType(AdType.BANNER);
        expBanner.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(expBanner);

        AdSpot imageInter = new AdSpot();
        imageInter.setName(activity.getString(R.string.image_fullscreen));
        imageInter.setAppKey(Constants.Keys.KEY_IMG_INTER);
        imageInter.setTime(System.currentTimeMillis());
        imageInter.setSdk(AdSdk.LOOPME);
        imageInter.setType(AdType.INTERSTITIAL);
        imageInter.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(imageInter);

        AdSpot imageBanner = new AdSpot();
        imageBanner.setName(activity.getString(R.string.image_banner));
        imageBanner.setAppKey(Constants.Keys.KEY_IMG_BANNER);
        imageBanner.setTime(System.currentTimeMillis());
        imageBanner.setSdk(AdSdk.LOOPME);
        imageBanner.setType(AdType.BANNER);
        imageBanner.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(imageBanner);
    }

    public static void insertDefaultKeys(MainActivity activity) {
        AdSpot htmlAd = new AdSpot();
        htmlAd.setName(activity.getString(R.string.appkey_name_html_ad));
        htmlAd.setAppKey(Constants.Keys.KEY_HTML_AD_DEFAULT);
        htmlAd.setTime(System.currentTimeMillis());
        htmlAd.setSdk(AdSdk.LOOPME);
        htmlAd.setType(AdType.INTERSTITIAL);
        htmlAd.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(htmlAd);

        AdSpot imageAd = new AdSpot();
        imageAd.setName(activity.getString(R.string.appkey_name_image_ad));
        imageAd.setAppKey(Constants.Keys.KEY_IMAGE_AD_DEFAULT);
        imageAd.setTime(System.currentTimeMillis());
        imageAd.setSdk(AdSdk.LOOPME);
        imageAd.setType(AdType.INTERSTITIAL);
        imageAd.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(imageAd);

        AdSpot vpaidAd = new AdSpot();
        vpaidAd.setName(activity.getString(R.string.appkey_name_vpaid_ad));
        vpaidAd.setAppKey(Constants.Keys.KEY_VPAID_AD_DEFAULT);
        vpaidAd.setTime(System.currentTimeMillis());
        vpaidAd.setSdk(AdSdk.LOOPME);
        vpaidAd.setType(AdType.INTERSTITIAL);
        vpaidAd.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(vpaidAd);

        AdSpot expandableBanner = new AdSpot();
        expandableBanner.setName(activity.getString(R.string.appkey_name_expandable_ad));
        expandableBanner.setAppKey(Constants.Keys.KEY_EXPANDABLE_BANNER_AD_DEFAULT);
        expandableBanner.setTime(System.currentTimeMillis());
        expandableBanner.setSdk(AdSdk.LOOPME);
        expandableBanner.setType(AdType.BANNER);
        expandableBanner.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(expandableBanner);

        AdSpot video360 = new AdSpot();
        video360.setName("Video 360");
        video360.setAppKey(Constants.Keys.KEY_VIDEO_360_AD_DEFAULT);
        video360.setTime(System.currentTimeMillis());
        video360.setSdk(AdSdk.LOOPME);
        video360.setType(AdType.INTERSTITIAL);
        video360.setBaseUrl(AdSpot.BASE_URL_DEFAULT_VALUE);
        activity.insertAdSpot(video360);
    }
}
