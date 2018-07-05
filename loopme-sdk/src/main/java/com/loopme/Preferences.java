package com.loopme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loopme.gdpr.ConsentType;

/**
 * Created by katerina on 4/27/18.
 */

public class Preferences {

    private static final String FLAG_GDPR = "gdpr";
    private static final String FLAG_GDPR_CONSENT_SET = "flag_gdpr_consent_set";
    private static final String FLAG_CONSENT_TYPE = "FLAG_CONSENT_TYPE";

    private static final String FLAG_IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String FLAG_IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";
    private static final String FLAG_IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";
    private static final String FLAG_SUBJECT_TO_GDPR_UNSET = "subject_to_gdpr_unset";

    private final SharedPreferences mPrefs;
    private static Preferences mInstance;

    private Preferences(Context context) {
        mPrefs = getPrefs(context);
    }

    public static Preferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Preferences(context);
        }
        return mInstance;
    }

    private SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setGdprState(boolean isAccepted, ConsentType consentType) {
        mPrefs.edit().putBoolean(FLAG_GDPR, isAccepted).apply();
        mPrefs.edit().putInt(FLAG_CONSENT_TYPE, consentType.getType()).apply();
        mPrefs.edit().putBoolean(FLAG_GDPR_CONSENT_SET, true).apply();
    }

    public boolean getGdprState() {
        return mPrefs.getBoolean(FLAG_GDPR, true);
    }

    public int getConsentType() {
        return mPrefs.getInt(FLAG_CONSENT_TYPE, ConsentType.LOOPME.getType());
    }

    public boolean isConsentSet() {
        return mPrefs.getBoolean(FLAG_GDPR_CONSENT_SET, false);
    }

    public String getIabConsentString() {
        return mPrefs.getString(FLAG_IAB_CONSENT_CONSENT_STRING, "");
    }

    public boolean isIabConsentCmpPresent() {
        return mPrefs.getBoolean(FLAG_IAB_CONSENT_CMP_PRESENT, false);
    }

    public String getIabConsentSubjectToGdpr() {
        return mPrefs.getString(FLAG_IAB_CONSENT_SUBJECT_TO_GDPR, FLAG_SUBJECT_TO_GDPR_UNSET);
    }

    public boolean isSubjectToGdprPresent() {
        return !mPrefs.getString(FLAG_IAB_CONSENT_SUBJECT_TO_GDPR, FLAG_SUBJECT_TO_GDPR_UNSET).equals(FLAG_SUBJECT_TO_GDPR_UNSET);
    }
}
