## Version 8.0.0 (28 of February, 2020)
- **Breaking change**: AndroidX migration, minor integration changes. See https://github.com/loopme/android-united-sdk/wiki/SDK-Integration#sdk-v800-breaking-changes for details
- OM SDK v1.3 integration
- TCF v2.0 integration
- deep/external link support refactoring for ad clicks

## Version 7.2.1 (25 of December, 2019)
- WebViewClient unsupported uri scheme fix

## Version 7.2.0 (06 of December, 2019)
- Added support of California Consumer Privacy Act (CCPA) 

## Version 7.1.1 (26 of November, 2019)
- VAST video caching fix

## Version 7.1.0 (13 of November, 2019)
- Html based ads can have access to camera, geolocation, microphone 
if corresponding permissions are **granted** to the app:
`ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`, `CAMERA`, `RECORD_AUDIO`.
Remove any of these permissions in your app's `AndroidManifest.xml` if you don't want html ads to use camera, microphone or location:
`<uses-permission android:name="android.permission.CAMERA" tools:node="remove"/>`.
**NOTE**: if an html ad asks for the permission, sdk will grant it automatically or pass the result of android permission request dialog. If the permission doesn't exist in merged `AndroidManifest.xml` or was denied, sdk will reject an html ad's permission request
- WebView scheme (`tel`, `geo`, `mailto`, etc) uri navigation fix
- Interstitial ads are displayed in [sticky immersive mode](https://developer.android.com/training/system-ui/immersive#sticky-immersive)
- Other minor bugfixes    

## Version 7.0.1 (24 of July, 2019)
- Initialization bug fix
- Updated bridges and samples

## Version 7.0.0 (18 of July, 2019)
- Added Open Measurement SDK (OM SDK) for viewability measurement
- Added VAST 4.1 support
- **Breaking change**: sdk must be [initialized](https://github.com/loopme/android-united-sdk/wiki/Initialization) before use

## Version 6.1.12 (25 of February, 2019)
- Potentially fixed "empty advertising id" bug and added logging to track the issue down if it occurs again
- Removed `multidex`
- Updated `gradle`
- Updated support and `google` libraries
- Replaced `play-services-ads` with `play-services-ads-identifier` library
- Removed unnecessary dependencies
- Other minor bugfixes   

## Version 6.1.10 (18 of January, 2019)
- Updated `RewardedVideoSampleActivity` bridge to support [*MoPub SDK* v5.x.x](https://developers.mopub.com/docs/android/initialization/)
- Fixed crash for banner/interstitial `getInstance` calls when android API level < 21: in this case method returns `null`. *Make sure your app code is aware of that*
- `new LoopMeInterstitial()` and `new LoopMeBanner()` constructor calls are no longer valid: use respective `getInstance` methods
- Fixed "Problem displaying media file" for VAST ads      
- Other minor bugfixes

## Version 6.1.9 (07 of December, 2018)
- Minor gradle bugfixes

## Version 6.1.8 (21 of November, 2018)
- Changed request parameter(android version)

## Version 6.1.7 (21 of November, 2018)
- Bug fixes related to the GDPR dialog 

## Version 6.1.6 (22 of August, 2018)
- Deliver ads via cellular connection as well as via Wi-Fi.
- Additional debug events
- Fixed bug with version naming

## Version 6.1.5 (17 of July, 2018)
- Retrofit is removed
- GDPR Consent with presented CMP SDK compatibility
  
## Version 6.1.2 (08 of June, 2018)
- Fixed NPE due parsing xml.

## Version 6.1.1 (23 of May, 2018)
- Changed gradle dependencies

## Version 6.1.0 (18 of May, 2018)
- Added GDPR support
- removed enableAapt options(as deprecated)

## Version 6.0.2 (19 of April, 2018)
- Added unsupported file format detection fro VPAID creatives
- Multiformat ad spots - combine several ad formats in a single spot to increase fill rate. 
- VAST 4.0 support
- Expandable banner ad format support
- Updated list view ad unit
- Updated samples 
- Debugging reporting improvements
- Access to read cookies for better HTML ads compliancy
- A minimum API level supported increased to 21 (Android 5.0)


## Version 6.0.1 (22 of October, 2017)
1. Support interstitial and banner ads of the following formats:
- VAST 2.0, 3.0, 4.0
- VPAID 2.0
- MRAID 2.0
2. Ads Smart Loading feature
3. Highly Reduced resource consumption
4. Supports MOAT Viewability measurement