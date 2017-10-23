package com.loopme.tester;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesService {
    public static final String IS_AUTO_LOADING_ENABLED = "is_auto_loading_enabled";


    private final Context mContext;
    private final SharedPreferences mPrefs;

    public PreferencesService(Context context) {
        mContext = context;
        mPrefs = getPrefs();
    }

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PreferencesService.class.getName(), Application.MODE_PRIVATE);
    }

    public void setAutoLoadingState(final boolean autoloadingState) {
        mPrefs.edit().putBoolean(IS_AUTO_LOADING_ENABLED, autoloadingState).apply();
    }

    public boolean getAutoLoadingState() {
        return mPrefs.getBoolean(IS_AUTO_LOADING_ENABLED, false);
    }
}