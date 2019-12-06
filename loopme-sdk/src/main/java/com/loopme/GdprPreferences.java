package com.loopme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loopme.gdpr.ConsentType;

import java.util.Objects;

/**
 * Created by katerina on 4/27/18.
 */
// TODO. Rename.
public final class GdprPreferences {

    private static final String FLAG_IAB_CONSENT_CONSENT_STRING = "IABConsent_ConsentString";
    private static final String FLAG_IAB_CONSENT_CMP_PRESENT = "IABConsent_CMPPresent";
    private static final String FLAG_IAB_CONSENT_SUBJECT_TO_GDPR = "IABConsent_SubjectToGDPR";
    private static final String FLAG_SUBJECT_TO_GDPR_UNSET = "subject_to_gdpr_unset";

    private static final String FLAG_IAB_US_PRIVACY_STRING = "IABUSPrivacy_String";
    private static final String FLAG_IAB_US_PRIVACY_DOES_NOT_APPLY = "1---";

    private static boolean FLAG_GDPR = false;
    private static ConsentType FLAG_CONSENT_TYPE = ConsentType.LOOPME;

    private final SharedPreferences prefs;

    private static GdprPreferences instance;

    private GdprPreferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static GdprPreferences getInstance(Context context) {
        if (instance == null)
            instance = new GdprPreferences(context);

        return instance;
    }

    public void setGdprState(boolean isAccepted, ConsentType consentType) {
        FLAG_GDPR = isAccepted;
        FLAG_CONSENT_TYPE = consentType;
    }

    public boolean getGdprState() {
        return FLAG_GDPR;
    }

    public int getConsentType() {
        return FLAG_CONSENT_TYPE.getType();
    }

    public String getIabConsentString() {
        try {
            return prefs.getString(FLAG_IAB_CONSENT_CONSENT_STRING, "");
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return "";
    }

    public boolean isIabConsentCmpPresent() {
        try {
            return prefs.getBoolean(FLAG_IAB_CONSENT_CMP_PRESENT, false);
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return false;
    }

    public String getIabConsentSubjectToGdpr() {
        try {
            return prefs.getString(FLAG_IAB_CONSENT_SUBJECT_TO_GDPR, FLAG_SUBJECT_TO_GDPR_UNSET);
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return FLAG_SUBJECT_TO_GDPR_UNSET;
    }

    public boolean isSubjectToGdprPresent() {
        try {
            return !Objects.equals(getIabConsentSubjectToGdpr(), FLAG_SUBJECT_TO_GDPR_UNSET);
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return false;
    }

    public String getUSPrivacyString() {
        try {
            return prefs.getString(FLAG_IAB_US_PRIVACY_STRING, FLAG_IAB_US_PRIVACY_DOES_NOT_APPLY);
        } catch (ClassCastException ex) {
            Logging.out(ex.toString());
        }

        return FLAG_IAB_US_PRIVACY_DOES_NOT_APPLY;
    }
}