# Android Mopub Bridge #

1. **[Overview](#overview)**
2. **[Register on LoopMe network](#register-on-loopme-network)**
3. **[Adding LoopMe Android SDK](#adding-loopme-android-sdk)**
4. **[Create Custom Native Network on Mopub dashboard](#create-custom-native-network-on-mopub-dashboard)**
5. **[Initialization](#initialization)**
6. **[Mediate from Mopub Interstitial to LoopMe Interstitial Ad](#mediate-from-mopub-interstitial-to-loopme-interstitial-ad)**
7. **[Mediate from Mopub banner to LoopMe Native Video Ad](#mediate-from-mopub-banner-to-loopme-native-video-ad)**
8. **[Mediate from Mopub Rewarded Video to LoopMe Rewarded Video Ad](#mediate-from-mopub-rewarded-video-to-loopme-rewarded-video-ad)**
9. **[Sample project](#sample-project)**

## Overview ##

LoopMe is the largest mobile video DSP and Ad Network, reaching over 1 billion consumers world-wide. LoopMe’s full-screen video and rich media ad formats deliver more engaging mobile advertising experiences to consumers on smartphones and tablets.
LoopMe supports SDK bridges to ad mediation platforms. The LoopMe SDK bridge allows you to control the use of the LoopMe SDK via your existing mediation platform.

`LoopMe Android bridge` allows publishers monitize applications using `Mopub mediation ad platform`.

<b>NOTE:</b> This page assumes you already have account on `Mopub` platform and integrated with the `Mopub` Android SDK

If you have questions please contact us at support@loopme.com.

## Register on LoopMe network ##

To use and setup the SDK bridge, register your app on the LoopMe network via the LoopMe Dashboard to retrieve a unique LoopMe app key for your app. The app key uniquely identifies your app in the LoopMe ad network (Example app key: 51bb33e7cb). Please ask POC in Loopme to register your app/placement and provide the appKey.<br>
You will need the app key during next steps of integration.

## Adding LoopMe Android SDK ##

Add the following to your build.gradle:
```java
dependencies {
        implementation files('libs/loopme-sdk-release.aar')
}
```

## Create Custom Native Network on Mopub dashboard ##

<b>NOTE:</b> `LoopMe` is not available as a predefine network in the Mopub tool, SDK bridge mediation needs to be manually configured with Mopub "Custom Native Network" option.

* On Mopub dashboard click Networks -> add a network. Choose “Custom Native Network”

## Initialization ##

Starting from MoPub SDK v5.x.x you have to initialize MoPub SDK and LoopMe mediation. To do so copy `LoopMeAdapterConfiguration.java` to the `com.mopub.mobileads` package in your app and insert this piece of code in your `Activity`:

```
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> loopMeConf = new HashMap<>();

        // Use this method in case if you Publisher is willing to ask GDPR consent with your own or
        // MoPub dialog AND don't want LoopMe consent dialog to be shown - pass GDPR consent to this method.
        //LoopMeAdapterConfiguration.setPublisherConsent(loopMeConf, true);

        // Unfortunately, MoPub SDK v5.5.x doesn't pass Activity context to initializeNetwork method:
        // pass it here, before initializing MoPub SDK.
        LoopMeAdapterConfiguration.setWeakActivity(this);

        MoPub.initializeSdk(this,
                new SdkConfiguration.Builder(BuildConfig.AD_UNIT_ID_BANNER)
                        .withAdditionalNetwork(
                                LoopMeAdapterConfiguration.class.getName())
                        .withMediatedNetworkConfiguration(
                                LoopMeAdapterConfiguration.class.getName(), loopMeConf)
                        .withLogLevel(MoPubLog.LogLevel.INFO)
                        .build(),
                this);
}
```

## Mediate from Mopub Interstitial to LoopMe Interstitial Ad ##

<br><b>Configure Custom Native Network on Mopub dashboard </b>
<p><img src="images\custom event interstitial.png"  /></p>
<br>Or you can configure custom native network in line item
<p><img src="images\custom line interstitial.png"  /></p>
<br> Instead of test_interstitial_l put your LoopMe app key.

* Download and copy `LoopMeMoPubInterstitial` bridge class to the `com.mopub.mobileads` package in your project. 
* Load `MoPubInterstitial` as usual
```java
mInterstitial = new MoPubInterstitial(LoopMeMoPubInterstitialSampleActivity.this, AD_UNIT_ID);
mInterstitial.setInterstitialAdListener(LoopMeMoPubInterstitialSampleActivity.this);
mInterstitial.load();
```
* implement in your Activity `MoPubInterstitial.InterstitialAdListener` interface
* Show interstitial ad
```java
mInterstitial.show();
```

## Mediate from Mopub banner to LoopMe Native Video Ad ##

Displaying `LoopMe` native video ads requires extra integration steps in order to manage ad activity, e.g. to enable video playback or HTML animation.

<br><b>1 Configure Custom Native Network on Mopub dashboard </b>
<p><img src="images/custom event banner.png"  /></p>
<br>Or you can configure custom native network in line item
<p><img src="images\custom line banner.png"  /></p>
<br> Instead of test_mpu put your LoopMe app key.

* Download and copy `LoopMeMoPubBanner` bridge class to the `com.mopub.mobileads` package in your project. 
* Define `FrameLayout` in xml
* Init `MoPubView`
```java
MoPubView mBanner = (MoPubView) findViewById(R.id.mopubView);
mBanner.setAdUnitId(AD_UNIT_ID);
mBanner.setBannerAdListener(this);
```
* Add `FrameLayout` in localExtras as "bannerView"
```java
FrameLayout loopmeView = findViewById(R.id.loopme_view);
Map<String, Object> extras = new HashMap<>();
extras.put("bannerView", loopmeView);
mBanner.setLocalExtras(extras);
```
* Load ad
```java
mBanner.loadAd();
```
* Add `LoopMeMoPubBanner.pause()` in onPause() to pause video
```java
@Override
private void onPause() {
    LoopMeMoPubBanner.pause();
    super.onPause();
}
```
* Add `LoopMeMoPubBanner.resume()` in onResume() to resume video
```java
@Override
private void onResume() {
    LoopMeMoPubBanner.resume();
    super.onResume();
}
```

## Mediate from Mopub Rewarded Video to LoopMe Rewarded Video Ad ##
<br><b>Configure Custom Native Network on Mopub dashboard </b>
<p><img src="images/custom event rewarded.png"  /></p>
<br> Instead of <b>test_interstitial_l</b> put your LoopMe app key.
<br> Instead of <b>bitcoin</b> put your rewarded currency.
<br> Instead of <b>10</b> put your rewarded amount.

* Download and copy `LoopMeMoPubRewardedVideo` bridge class to the `com.mopub.mobileads` package in your project. 
* **(For MoPub SDK below v5.x.x)** Initialize `MoPubRewardedVideos` in method `onCreate()` of your `Activity.class` 
```java
      MoPubRewardedVideos.initializeRewardedVideo(this);
```
* implement in your Activity `MoPubRewardedVideoListener` interface and set it to `MoPubRewardedVideos` "
```java
        MoPubRewardedVideos.setRewardedVideoListener(this);
```
* Load `MoPubRewardedVideos` as below
```java
        MoPubRewardedVideos.loadRewardedVideo(AD_UNIT_ID_REWARDED_VIDEO);
```
<br> There `AD_UNIT_ID_REWARDED_VIDEO` is your  ad unit id for rewarded video.
* Show rewarded video ad
```java
        MoPubRewardedVideos.showRewardedVideo(AD_UNIT_ID_REWARDED_VIDEO);
```

<br> If you see in log next warning  while testing your integration  
```java
Could not load custom event because Activity reference was null. Call MoPubRewardedVideoManager#updateActivity before requesting more rewarded ads.
```
<br> Jast call method  `updateActivity(Activity activity)` before load rewarded video like this:
```java
        MoPubRewardedVideoManager.updateActivity(mActivity);
        MoPubRewardedVideos.loadRewardedVideo(AD_UNIT_ID_REWARDED_VIDEO);
```
<br> There `mActivity` is the reference on your Activity.

## Sample project ##

Check out our `mopub-mediation-sample` for implementation details.