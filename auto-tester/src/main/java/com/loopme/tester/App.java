//package com.loopme.tester;
//
//import android.app.Application;
//
//import com.loopme.tester.utils.CacheUtils;
//import com.testfairy.TestFairy;
//
//
///**
// * Created by katerina on 1/28/17.
// */
//
//public class App extends Application {
//
//    private static final String LOG_TAG = App.class.getSimpleName();
//    private static final String APP_TOKEN = "871ddfa3df68f49993b50409d7fc1f5666996ad9";
//
//    private static App sInstance;
//
//    public static App getContext() {
//        return sInstance;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        sInstance = App.this;
//        CacheUtils.getInstance();
//
//        TestFairy.begin(this, APP_TOKEN);
////        AdSpotHolder.init(this);
////        MoatAnalytics.getInstance().start(this);
//    }
//}
