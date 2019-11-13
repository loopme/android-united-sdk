You can find integration guide in our github [wiki](https://github.com/loopme/android-united-sdk/wiki) page.

## What's new ##

**Version 7.1.0**
- Html based ads can have access to camera, geolocation, microphone 
if corresponding permissions are **granted** to the app:
`ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`, `CAMERA`, `RECORD_AUDIO`.
Remove any of these permissions in your app's `AndroidManifest.xml` if you don't want html ads to use camera, microphone or location:
`<uses-permission android:name="android.permission.CAMERA" tools:node="remove"/>`.
**NOTE**: if an html ad asks for the permission, sdk will grant it automatically or pass the result of android permission request dialog. If the permission doesn't exist in merged `AndroidManifest.xml` or was denied, sdk will reject an html ad's permission request
- WebView scheme (`tel`, `geo`, `mailto`, etc) uri navigation fix
- Interstitial ads are displayed in [sticky immersive mode](https://developer.android.com/training/system-ui/immersive#sticky-immersive)
- Other minor bugfixes  

Please view the [changelog](CHANGELOG.md) for details.

## License ##

see [License](LICENSE.md)