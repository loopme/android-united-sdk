package com.loopme;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.loopme.gdpr.ConsentType;

/**
 * Created by katerina on 4/27/18.
 */

public class Preferences {

    private static final String FLAG_GDPR = "gdpr";
    private static final String FLAG_GDPR_CONSENT_SET = "flag_gdpr_consent_set";
    private static final String FLAG_CONSENT_TYPE = "FLAG_CONSENT_TYPE";

    private final SharedPreferences mPrefs;
    private static Preferences mInstance;

    private Preferences(Context context) {
        mPrefs = getPrefs(context);
    }

    public static Preferences getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new Preferences(activity);
        }
        return mInstance;
    }

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(Preferences.class.getName(), Application.MODE_PRIVATE);
    }

    public void setGdprState(boolean isAccepted, ConsentType consentType) {
        mPrefs.edit().putBoolean(FLAG_GDPR, isAccepted).apply();
        mPrefs.edit().putString(FLAG_CONSENT_TYPE, consentType.getType()).apply();
        mPrefs.edit().putBoolean(FLAG_GDPR_CONSENT_SET, true).apply();
    }

    public boolean getGdprState() {
        return mPrefs.getBoolean(FLAG_GDPR, true);
    }

    public String getConsentType() {
        return mPrefs.getString(FLAG_CONSENT_TYPE, ConsentType.LOOPME.getType());
    }

    public boolean isConsentSet() {
        return mPrefs.getBoolean(FLAG_GDPR_CONSENT_SET, false);
    }
}
