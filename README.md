# LoopMe-Android-SDK #

[点击阅读SDK对接中文说明](README_CHINESE.md)

1. **[Overview](#overview)**
2. **[Features](#features)**
3. **[Requirements](#requirements)**
4. **[SDK Integration](#sdk-integration)**
  * **[Interstitial ads](#interstitial-ads)**
  * **[Banner ads](#banner-ads)**
  * **[Native video ads](#native-video-ads)**
  * **[Check integration](#check-integration)**
5. **[Sample projects](#sample-projects)**
6. **[Advanced Settings](#advanced-settings)**
7. **[FAQ](#faq)**
8. **[What's new](#whats-new)**

## Overview ##

LoopMe is the largest mobile video DSP and Ad Network, reaching over 1 billion consumers world-wide. LoopMe’s full-screen and banner ad formats deliver more engaging mobile advertising experiences to consumers on smartphones and tablets.

If you have questions please contact us at support@loopmemedia.com.

## Features ##

* Interstitial, banner, native video ad formats
* Image / Rich media / Video / 360 video / VAST/ VPAID
* Preloaded video ads
* Minimized video mode
* Expand to fullscreen mode
* In-app ad reward notifications, including video view completed

## Requirements ##

Requires `Android` 4.4 and up

## SDK Integration ##

<h3>Download</h3>
There are 2 ways:<br>
1. Download latest version of SDK (`loopme-sdk-[version].aar` file) and put it in folder `libs` <br>
Add dependency to `loopme-sdk` in your project (`build.gradle` file):
```java
repositories {
    flatDir {
        dirs 'libs'
    }
}
dependencies {
    compile(name:'loopme-sdk-[version]', ext:'aar')
}
```
<br>2. LoopMe SDK is available as an AAR via jCenter; to use it, add the following to your `build.gradle`:
```
repositories {
    jcenter()
}

dependencies {
    compile 'com.loopme:loopme-sdk:6.0.2@aar'
}
```

An appKey is required to use the `loopme-sdk`. The appKey uniquely identifies your app to the LoopMe ad network. (Example appKey: 7643ba4d53.) To get an appKey visit the **[LoopMe Dashboard](http://loopme.me/)**. <br>

<b>Note</b>: For testing purposes better to use pre-installed app keys:<br>
For interstitial - `LoopMeInterstitial.TEST_PORT_INTERSTITIAL` and `LoopMeInterstitial.TEST_LAND_INTERSTITIAL`<br>
For banner and native video - `LoopMeBanner.TEST_MPU`
<br><br>Then if everything is ok, you can change pre-installed app key to your real app key.

<br>Integration instructions for different ad types (Image / Rich media / Video / 360 video) are same.

## Interstitial ads ##

<img src="screenshots/interstitial.png" alt="Interstitial" width="128" align="middle">

* Init and load
```java
public class YourActivity extends Activity implements LoopMeInterstitial.Listener {
  
  private LoopMeInterstitial mInterstitial;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	mInterstitial = LoopMeInterstitial.getInstance(YOUR_APPKEY, this);
	mInterstitial.setListener(this);
	
	mInterstitial.load();
  }
}
```

* Show interstitial ads

Displaying the `LoopMeInterstitial` can be user-initiated (e.g press on button) or publisher-initiated (e.g. end of game level)<br>
<b>NOTE:</b> This method should be triggered after receive `onLoopMeInterstitialLoadSuccess()` notification.
```java
  mInterstitial.show();
```

* Destroy interstitial

Destroy when it is no longer needed to clean up resources.
```java
  mInterstitial.destroy();
```

* Interstitial notifications:

Implement `LoopMeInterstitial.Listener` in order to receive notifications during the loading/displaying ad processes, that you can use to trigger subsequent in-app events:
 * `-onLoopMeInterstitialLoadSuccess`: triggered when interstitial has been loaded the ad content
 * `-onLoopMeInterstitialLoadFail`: triggered when interstitial failed to load the ad content
 * `-onLoopMeInterstitialShow`: triggered when interstitial ad appeared on the screen
 * `-onLoopMeInterstitialHide`: triggered when interstitial ad disappeared from the screen
 * `-onLoopMeInterstitialVideoDidReachEnd`: triggered when interstitial video ad has been completely watched
 * `-onLoopMeInterstitialClicked`: triggered when interstitial ad was clicked
 * `-onLoopMeInterstitialExpired`: triggered when interstitial ad is expired, it is recommended to re-load


## Banner ads ##

<img src="screenshots/banner.png" alt="Banner" width="128">

`LoopMeBanner` class provides facilities to display a custom size ads during natural transition points in your application.

* Update `AndroidManifest.xml`:
```xml
<activity android:name="ActivityWhereBannerLocated" android:hardwareAccelerated="true"/>
```
* Add `LoopMeBannerView` in layout xml
* Init `LoopMeBanner`
```java
public class SimpleBannerActivity extends AppCompatActivity implements LoopMeBanner.Listener {

    private LoopMeBanner mBanner;
    private LoopMeBannerView mAdSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //some code

        mAdSpace = (LoopMeBannerView) findViewById(R.id.video_ad_spot);

        mBanner = LoopMeBanner.getInstance(YOUR_APPKEY, this);
        mBanner.setListener(this);
        mBanner.bindView(mAdSpace);
        mBanner.load();
    }

    @Override
    protected void onPause() {
        mBanner.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mBanner.resume();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        mBanner.destroy();
        super.onBackPressed();
    }
}
```
* Display banner
<br><b>NOTE:</b> This method should be triggered after receive `onLoopMeBannerLoadSuccess()` notification.
```java
mBanner.show();
```

* `LoopMeBanner` notifications:

Implement `LoopMeBanner.Listener` in order to receive notifications during the loading/displaying ad processes, that you can use to trigger subsequent in-app events:
 * `-onLoopMeBannerLoadSuccess`: triggered when banner has been loaded
 * `-onLoopMeBannerLoadFail`: triggered when banner failed to load the ad content
 * `-onLoopMeBannerShow`: triggered when banner appeared on the screen
 * `-onLoopMeBannerHide`: triggered when banner disappeared from the screen
 * `-onLoopMeBannerVideoDidReachEnd`: triggered when video in banner has been completely watched
 * `-onLoopMeBannerClicked`: triggered when banner was clicked
 * `-onLoopMeBannerExpired`: triggered when banner is expired, it is recommended to re-load
 * `-onLoopMeBannerLeaveApp`: triggered if SDK initiated app switching. E.g after click on ad user is redirected to market (or any other native app)


## Native video ads ##

<img src="screenshots/normal.png" alt="Native video" width="128">

Native video ads used to show banner inside `ListView`/`RecyclerView`.

* Update `AndroidManifest.xml`:
```xml
<activity android:name="ActivityWhereBannerLocated" android:hardwareAccelerated="true"/>
```
* Create xml layout for ad.
```xml  
list_ad_row.xml

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <com.loopme.LoopMeBannerView
        android:id="@+id/lm_banner_view"
        android:layout_width="300dp"
        android:layout_height="250dp"
        android:layout_centerHorizontal="true"/>
</RelativeLayout>
```

* Init `NativeVideoAdapter`.
<br>In case of integration in `RecyclerView`, you need to use `NativeVideoRecyclerAdapter` class.

```java
public class YourActivity extends Activity implements LoopMeBanner.Listener {
  
  private ListView mListView;
  private NativeVideoAdapter mNativeVideoAdapter;
  private String mAppKey = YOUR_APPKEY;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
  
  	//...
  	
	YourCustomAdapter adapter = new YourCustomAdapter(this, mList);

        //Init LoopMe adapter
        mNativeVideoAdapter = new NativeVideoAdapter(adapter, this, mListView);
        mNativeVideoAdapter.putAdWithAppKeyToPosition(mAppKey, 1);
        mNativeVideoAdapter.setAdListener(this);
        NativeVideoBinder binder = new NativeVideoBinder.Builder(R.layout.list_ad_row)
                .setLoopMeBannerViewId(R.id.lm_banner_view)
                .build();
        mNativeVideoAdapter.setViewBinder(binder);

        mListView.setAdapter(mNativeVideoAdapter);
        mNativeVideoAdapter.loadAds();
  }
  
  @Override
    protected void onPause() {
        super.onPause();
        mNativeVideoAdapter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNativeVideoAdapter.onResume();
    }

    @Override
    public void onBackPressed() {
        mNativeVideoAdapter.destroy();
        super.onBackPressed();
    }
}
```
Ad will be shown automaticly after load complete. 


## Check integration ##

If everything done correctly you will get loadSuccess() notification and see ads. 
<br>If something wrong check LogCat.
In case of "No ads found" error - contact to your LoopMe manager (it is about campaign configuration).

## Sample projects ##

Check out our project samples:
- `banner-sample` as an example of integration banner and native video ads
- `interstitial-sample` as an example of interstitial integration

## Advanced Settings ##

* <b>Custom request parameters</b><br>
You can add new parameter(s) in request by calling:
`addCustomParameter(String param, String paramValue)` method from `LoopMeInterstitial` or `LoopMeBanner` instance.

* <b>Clear cache</b><br>
For remove all videos ads from cache call `clearCache()` method from `LoopMeInterstitial` or `LoopMeBanner` instance.

* <b>Check current ad status</b><br>
There are few commands to check current ad status:
`isLoading()`, `isReady()`, `isShowing()`.

* <b>Change cache storage time</b><br>
By default, video files stays in cache during 32 hours. If you need to change it call 
`setVideoCacheTimeInterval(long milliseconds)` value.

* <b>Targeting params</b><br>
Targeting settings include gender, year of birth and keywords. Define it with `setGender(String gender)`, `setYob(int yob)` and `setKeywords(String keywords)` methods from `LoopMeInterstitial` or `LoopMeBanner` instance.
They will be added in ad request.

* <b>Config minimized mode</b><br>
Minimized mode can be configured only for native video ads.
```java
// root_view - the root view in layout
RelativeLayout root = (RelativeLayout) findViewById(R.id.root_view);
MinimizedMode mode = new MinimizedMode(root);
mNativeVideoAdapter.setMinimizedMode(mode);
```
<img src="screenshots/normal.png" alt="Native video" width="128">
<img src="screenshots/shrink.png" alt="Minimized mode" width="128">

* <b>Config preloading settings</b><br>
By default video ad can be loaded only on wi-fi connection. To turn on it also for mobile network you need to call `useMobileNetworkForCaching(true)`

## FAQ ##
1. <b>Which API supports 'In-app ad reward notifications, including video view completed'?</b>
<br>For interstitial use `onLoopMeInterstitialVideoDidReachEnd()` notification (triggered when interstitial video ad has been completely watched).<br>
For banner nad native video - `onLoopMeBannerVideoDidReachEnd()` notification.

## What's new ##

Please meet our New United SDK. It combines multiple premium ads technologies with the highest performance and lean resource consumption.

**Version 6.0.1**

1. Support interstitial and banner ads of the following formats:
- VAST 2.0, 3.0, 4.0
- VPAID 2.0
- MRAID 2.0
2. Ads Smart Loading feature
3. Highly Reduced resource consumption
4. Supports MOAT Viewability measurement

**Version 6.0.2**
- Added unsupported file format detection fro VPAID creatives


See [Changelog](CHANGELOG.md)


## License ##

See [License](LICENSE.md)
