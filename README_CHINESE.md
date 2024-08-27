# LoopMe-Android-SDK #



1. **[概览](#概览)**
2. **[特点](#特点)**
3. **[要求](#要求)**
4. **[SDK对接](#SDK对接)**
  * **[全屏插屏广告](#全屏插屏广告)**
  * **[横幅（原生视频广告）](#横幅（原生视频广告))**
5. **[示例](#示例)**
6. **[更新](#更新)**

## 概览 ##

LoopMe是最大的移动视频DSP和广告网络平台，全球覆盖受众超过10亿。LoopMe的全屏视频和富媒体广告格式给受众带来互动性强的移动广告经验。

如果您有任何问题，请联系support@loopmemedia.com.

## 特点 ##

* 全屏图片插屏广告
* 全屏富媒体插屏广告
* 预加载视频广告
* 横幅（原生视频广告）
* 最小化视频模式
* 应用内置奖励提醒（包括视频完整浏览）

## 要求 ##

在您使用`loopme-android-sdk`之前，您需要前往我们的**[系统后台](http://loopme.me/)**注册并获取appKey。appKey是用来在我们的广告网络中识别您的应用的。（示例appKey：7643ba4d53）

要求`Android` 4.0 及更高系统

## SDK对接 ##

* 下载最新版本的loopme-sdk
* 在loopme-sdk项目中加入依赖性
* 许可更新 `AndroidManifest.xml`
```xml
//需要的许可
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_BASIC_PHONE_STATE" />
<uses-permission android:name="com.google.android.gms.permission.AD_ID" />

```
活动：
```xml
<activity android:name="com.loopme.AdActivity"
            android:theme="@android:style/Theme.Translucent"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:hardwareAccelerated="true"/>
<activity android:name="com.loopme.AdBrowserActivity" />
```
## 全屏插屏广告 ##
* 创建 `LoopMeInterstitial` 实例并且检索广告
```java
public class YourActivity extends Activity implements LoopMeInterstitial.Listener {

  private LoopMeInterstitial mInterstitial;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	/**
   * 初始化插屏广告
   * 使用你在LoopMe后台开通app时获得的appKey
   * 作为测试，你能使用LoopMeInterstitial.java中定义的测试appKey常数
   */
   mInterstitial = LoopMeInterstitial.getInstance(YOUR_APPKEY, getApplicationContext());
 	mInterstitial.setListener(this);

	/**
   * 开始载入广告内容
   * 建议提前触发以便准备好插屏广告
   * 并且能立即在您的应用里展现
  */
 mInterstitial.load();
 }
}
```

* 展示插屏广告

LoopMe插屏广告`LoopMeInterstitial`的展示可以为用户发起的（比如：点击播放按钮）或开发者发起（如游戏回合结束后）
```java
  mInterstitial.show();
```

* 清除插屏广告

当不需要时可以清除以便清理资源
```java
  mInterstitial.destroy();
```

* 插屏广告通知:

* 实现 `LoopMeInterstitial.Listener`在载入/展示广告过程中接受通知，以便您触发随后的应用内置事件
 * `-onLoopMeInterstitialLoadSuccess`:  当插屏广告载入广告内容时触发
 * `-onLoopMeInterstitialLoadFail`: 当插屏广告载入广告内容失败时触发
 * `-onLoopMeInterstitialShow`: 当插屏广告完成展示时触发
 * `-onLoopMeInterstitialHide`: 当插屏广告在屏幕消失时触发
 * `-onLoopMeInterstitialVideoDidReachEnd`: 当插屏视频广告被完整观看时触发
 * `-onLoopMeInterstitialClicked`: 当插屏广告被点击时触发
 * `-onLoopMeInterstitialExpired`: 当插屏广告失效时触发，推荐重新载入

 ## 横幅（原生视频广告） ##

 `LoopMeBanner`在您的应用中自然过渡点提供一个可自定义尺寸的广告.

 <b>注意:</b> 此对接文档将指导您如何在可滑动内容中展现横幅（原生视频广告）。
 `LoopMeSDK`不会覆盖`ListView`/`GridView/RecyclerView`适配器。

 * 更新`AndroidManifest.xml`
 ```xml
 <activity android:name="ActivityWhereBannerLocated" android:hardwareAccelerated="true"/>
 ```

 * 为广告创建xml布局

 * 在您的自定义适配器里实现`LoopMeAdapter` 界面

 * 创建`LoopMeBanner` 并取回广告

 ```java
 public class YourActivity extends Activity implements LoopMeBanner.Listener {
   private LoopMeBanner mBanner;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
 	super.onCreate(savedInstanceState);

   /**
    * 初始化LoopMeBanner
    * 使用你在LoopMe后台开通app时获得的appKey
    * 作为测试用途，你能使用LoopMeBanner.java中定义的测试appKey常数  
    */
  	mBanner = LoopMeBanner.getInstance(YOUR_APPKEY, getApplicationContext());

    /*
    * 当原始视频不在视窗内，而你想展现一个最小化视频时:
    * 你需要定义`MinimzedMode` 并让它在`setMinimizedMode`方法中通过。
    * 备注: 你可以改变化最小化视频的尺寸和边缘。
    */
    MinimizedMode mode = new MinimizedMode(root); // root is parent layout where `MinimizedVideo` will be displayed
    mBanner.setMinimizedMode(mode);

  mBanner.setListener(this);
	mBanner.load();
  }
}
```
 * 绑定视图横幅（原生视频广告）

 ```java
 /**
  * 您需要在展示前绑定视图到横幅（原生视频广告）
  */
  LoopMeBannerView mView = (LoopMeBannerView) findViewById(R.id.banner_ad_spot);
  mBanner.bindView(mView);
  ```

 * 展示横幅（原生视频广告）

 如果你在可滑动内容里展示广告，订阅滚动通知
 ```java
 mListView.setOnScrollListener(this);
 ```
并触发`onScroll()`和`onLoopMeBannerLoadSuccess`里的`show()`

`show()`方法有一些变体:<br>
`show(null, null)` - 用来在不可滑动的内容里展示横幅（原生视频广告）<br>
`show(null, scrollView)` - 用来在`ScrollView`中展示(manage visibility) 横幅（原生视频广告）<br>
`show(loopmeAdapter, listView)` - 用来在`ListView`中展现(manage visibility) 横幅（原生视频广告）<br>
`show(loopmeAdapter, recyclerView)` - 用来在`RecyclerView`中展示(manage visibility) 横幅（原生视频广告）<br>
它通过自动计算出广告区域的可见性，来管理可滑动内容中广告。如果广告的可见性低于50%，将会暂停广告（不论是视频还是动画），否则继续。
```java
@Override
public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	mBanner.show(mCustomAdapter, mListView);
}

@Override
 public void onLoopMeBannerLoadSuccess(LoopMeBanner arg0) {
  	arg0.show(mCustomAdapter, mListView);
 }
```

 在合适的生命周期方法中触发 `LoopMeBanner`的pause()和`show()`方法
 它能暂停/继续横幅广告里的任何行为（例如：暂停视频回放）
```java
 	@Override
 	protected void onPause() {
 		mBanner.pause();
 		super.onPause();
 	}


 	@Override
    private void onResume() {
 		mBanner.show(mCustomAdapter, mListView);
 		super.onResume();
 	}
```

 * 清除横幅（原生视频广告）.

 当不再需要广告时，触发 destroy()方法来清理资源。它可以在Activity onDestroy() 方法中实现。
 ```java
 mBanner.destroy();
 ```

 * 横幅（原生视频广告）通知:

 实现`LoopMeBanner.Listener` 在载入/展示广告时接收通知，以便您触发随后的应用内置事件：
  * `-onLoopMeBannerLoadSuccess`: 当横幅（原生视频广告）被载入时触发
  * `-onLoopMeBannerLoadFail`: 当横幅（原生视频广告）载入失败时触发
  * `-onLoopMeBannerShow`: 当横幅（原生视频广告）在屏幕上出现时触发
  * `-onLoopMeBannerHide`: 当横幅（原生视频广告）从屏幕上消失时触发
  * `-onLoopMeBannerVideoDidReachEnd`: 当横幅（原生视频广告）被完整收看时触发
  * `-onLoopMeBannerClicked`: 当横幅（原生视频广告）被点击时触发
  * `-onLoopMeBannerExpired`: 当横幅（原生视频广告）失效时触发，推荐再次载入
  * `-onLoopMeBannerLeaveApp`: 当接入了SDK的应用被转换，例如点击后广告后跳转到应用商城（或其他原生应用里）时

## 示例 ##

请查看我们的样本:
- `banner-sample`是`LoopMeBanner`在`ListView`、`ScrollView`和`RecyclerView`中的对接示例
- `interstitial-sample`是`LoopMeInterstitial`的对接示例

## 更新 ##
**版本4.6.0(2015年11月23日)**

- 可扩张到全屏视频
- 改善加载的过程 (需要2个权限 `android.permission.DOWNLOAD_WITHOUT_NOTIFICATION` 和`android.permission.WRITE_EXTERNAL_STORAGE`)
- 实时调试
- 对广告结束页面的修复，wi-fi名称

请见[Changelog](CHANGELOG.md)

## 许可 ##

详见 [License](LICENSE.md)
