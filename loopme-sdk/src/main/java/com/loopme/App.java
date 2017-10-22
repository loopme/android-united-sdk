//package com.loopme;
//
//import android.app.Application;
//import android.content.Context;
//import android.support.multidex.MultiDexApplication;
//
//import com.moat.analytics.mobile.loo.MoatAnalytics;
//import com.moat.analytics.mobile.loo.MoatOptions;
//
///**
// * Created by katerina on 2/1/17.
// */
//
//public class App extends MultiDexApplication {
//
//    private static final String LOG_TAG = App.class.getSimpleName();
//    private static App sInstance;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        sInstance = this;
//
//        initMoatAnalytics();
//    }
//
//    private void initMoatAnalytics() {
//        MoatOptions options = new MoatOptions();
//        options.disableAdIdCollection = true;
//        MoatAnalytics.getInstance().start(options, this);
//    }
//
//    public Context getAppContext() {
//        return sInstance;
//    }
//
//}
