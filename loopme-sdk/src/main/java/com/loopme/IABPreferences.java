package com.loopme;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.loopme.gdpr.ConsentType;

/**
 * Created by katerina on 4/27/18.
 */
public final class IABPreferences {

    private static final String IAB_TCF_TC_STRING = "IABTCF_TCString";
    private static final String IAB_TCF_CMP_SDK_VERSION = "IABTCF_CmpSdkVersion";
    private static final String IAB_TCF_GDPR_APPLIES = "IABTCF_gdprApplies";

    private String usPrivacy;
    private static final String IAB_US_PRIVACY_STRING = "IABUSPrivacy_String";
    private static final String IAB_US_PRIVACY_DOES_NOT_APPLY = "1---";

    private static String FLAG_GDPR;
    private static ConsentType FLAG_CONSENT_TYPE = ConsentType.LOOPME;

    private final SharedPreferences prefs;

    private static IABPreferences instance;

    private IABPreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static IABPreferences getInstance(Context context) {
        if (instance == null)
            instance = new IABPreferences(context);

        return instance;
    }

    public void setGdprState(String consent, ConsentType consentType) {
        FLAG_GDPR = consent;
        FLAG_CONSENT_TYPE = consentType;
    }

    public void setGdprState(boolean consent, ConsentType consentType) {
        FLAG_GDPR = consent ? "1" : "0";
        FLAG_CONSENT_TYPE = consentType;
    }

    public String getGdprState() {
        return FLAG_GDPR;
    }

    public int getConsentType() {
        return FLAG_CONSENT_TYPE.getType();
    }

    public String getIabTcfTcString() {
        try {
            return prefs.getString(IAB_TCF_TC_STRING, "");
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return "";
    }

    public boolean isIabTcfCmpSdkPresent() {
        final int unset = -1;

        try {
            return prefs.getInt(IAB_TCF_CMP_SDK_VERSION, unset) > unset;
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return false;
    }

    public int getIabTcfGdprApplies() {
        final int unset = -1;

        try {
            return prefs.getInt(IAB_TCF_GDPR_APPLIES, unset);
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return unset;
    }

    public boolean isIabTcfGdprAppliesPresent() {
        try {
            int gdprAppliesValue = getIabTcfGdprApplies();
            return gdprAppliesValue == 0 || gdprAppliesValue == 1;
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return false;
    }

    public void setUSPrivacy(String usPrivacy) {
        this.usPrivacy = usPrivacy;
    }

    public String getUSPrivacyString() {
        if (usPrivacy != null) return usPrivacy;
        try {
            return prefs.getString(IAB_US_PRIVACY_STRING, IAB_US_PRIVACY_DOES_NOT_APPLY);
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return IAB_US_PRIVACY_DOES_NOT_APPLY;
    }
}