package com.loopme;

import android.app.Activity;

import com.loopme.gdpr.ConsentType;
import com.loopme.gdpr.GdprChecker;

/**
 * Created by katerina on 4/27/18.
 */

public class LoopMeSdk {
    private LoopMeSdk() {
    }

    /**
     * Use this method in case if you Publisher is  willing to ask GDPR consent with his own dialog, pass GDPR consent to this method.
     */
    public static void setGdprConsent(Activity activity, boolean userConsent) {
        Preferences.getInstance(activity).setGdprState(userConsent, ConsentType.PUBLISHER);
    }

    /**
     * Set Publisher consent String, also known as DaisyBit
     * @param activity
     * @param daisyBit
     */
    public static void setGdprConsent(Activity activity, String daisyBit) {
        Preferences.getInstance(activity).setGdprConsentString(daisyBit);
    }

    /**
     * Ask GDPR consent with LoopMe's SDK facilities, for internal use.
     */
    public static void askGdprConsent(Activity activity, GdprChecker.OnConsentListener listener) {
        new GdprChecker(activity, listener).check();
    }

    public static boolean isGdprConsentSet(Activity activity) {
        return Preferences.getInstance(activity).isConsentSet();
    }

    public static void init(Activity activity) {
        askGdprConsent(activity, null);
    }
}