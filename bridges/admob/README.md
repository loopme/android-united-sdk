# Android AdMob Bridge #

1. **[Overview](#overview)**
2. **[Register on LoopMe network](#register-on-loopme-network)**
3. **[Adding LoopMe Android SDK](#adding-loopme-android-sdk)**
4. **[Adding LoopMe's AdMob Bridge](#adding-loopmes-admob-bridge)**
5. **[Initialization](#Initialization)**
6. **[Mediate from AdMob Interstitial to LoopMe Interstitial Ad](#mediate-from-admob-interstitial-to-loopme-interstitial-ad)**
7. **[Sample project](#sample-project)**

## Overview ##

LoopMe is the largest mobile video DSP and Ad Network, reaching over 1 billion consumers world-wide. LoopMe’s full-screen video and rich media ad formats deliver more engaging mobile advertising experiences to consumers on smartphones and tablets.
LoopMe supports SDK bridges to ad mediation platforms. The LoopMe SDK bridge allows you to control the use of the LoopMe SDK via your existing mediation platform.

`LoopMe Android bridge` allows publishers monetize applications using `AdMob mediation ad platform`.

<b>NOTE:</b> This page assumes you already have account on `AdMob` platform and integrated with the `AdMob` Android SDK

If you have questions please contact us at support@loopme.com.

## Register on LoopMe network ##

To use and setup the SDK bridge, register your app on the LoopMe network via the LoopMe Dashboard to retrieve a unique LoopMe app key for your app. The app key uniquely identifies your app in the LoopMe ad network (Example app key: 51bb33e7cb). Please ask POC in Loopme to register your app/placement and provide the appKey.<br>
You will need the app key during next steps of integration.

## Adding LoopMe Android SDK ##

* Add the following to your `build.gradle`:
```groovy
dependencies {
        implementation files('libs/loopme-sdk-release.aar')
        }
```

## Adding LoopMe's AdMob Bridge ##

Download `LoopMeAdMobBridge` class and move it to `com.integration.admob` package in your project.
NOTE: `LoopMeAdMobSampleActivity` is just a sample how to use `LoopMeAdMobBridge`. 

## Initialization ##

Make sure `LoopMeSdk` is [initialized](https://github.com/loopme/android-united-sdk/wiki/Initialization) before using AdMob.

## Mediate from AdMob Interstitial to LoopMe Interstitial Ad ##

<b>Configure Ad Network Mediation on AdMob</b>
<br><b>NOTE:</b> This page assumes you already have account on AdMob and Ad unit(s)
* Click <b>Edit/Create Mediation Group</b>
<p><img src="images/admob_create_group.png" /></p>

* Click <b>Add Custom Event.</b>
<p><img src="images/admob_add_event.png" /></p>
Enter the event name and real eCPM that you have got after LoopMe publisher team approval. Click Continue.<br>
Note: you find eCPM on the LoopMe Dashboard > Apps & Sites > Ad Spot information.

* Enter the <b>Class Name</b> and <b>Parameter</b>. You will get:
<p><img src="images/ConfigureAdUnits.png"  /></p>

__Class Name__ should be: 'com.integration.admob.LoopMeAdMobBridge'. <br>
__Parameter__: enter the app key value you received after registering your Ad Spot on the LoopMe dashboard. <br>E.g. '298f62c196'.<br>
* Click __Done__
* Allocate traffic by entering an eCPM value for the custom event. Your allocation options are the same as they are for ad networks
* Click __Save__

* Load 
```java
AdRequest adRequest = new AdRequest.Builder().build();
InterstitialAd.load(this, ADMOB_UNIT_ID, adRequest, interstitialAdLoadCallback)
```

* Show
```java
mInterstitialAd.show();
```

## Sample project ##

Check out our `admob-mediation-sample` as an integration example.
