package com.loopme.gdpr.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.loopme.Logging;

// TODO. Ugly. Refactor.
public final class GdprDialogFragmentHelper {

    private static final String LOG_TAG = GdprDialogFragmentHelper.class.getSimpleName();
    private static final String GDPR_DIALOG_FRAGMENT_TAG = "com.loopme.gdpr.dialog.tag";

    static final String GDPR_URL = "GDPR_URL";

    private GdprDialogFragmentHelper() {

    }

    public static void showGdprDialog(Activity activity, String url) {
        if (activity == null) {
            Logging.out(LOG_TAG, "activity is null. Can't show gdpr dialog");
            return;
        }

        tryCancelCurrentGdprDialog(activity);

        Bundle b = new Bundle();
        b.putString(GDPR_URL, url);

        if (activity instanceof FragmentActivity) {
            DialogFragment d = new GdprSupportDialogFragment();
            d.setArguments(b);
            d.show(((FragmentActivity) activity).getSupportFragmentManager(), GDPR_DIALOG_FRAGMENT_TAG);
            return;
        }

        android.app.DialogFragment d = new GdprNativeDialogFragment();
        d.setArguments(b);
        d.show(activity.getFragmentManager(), GDPR_DIALOG_FRAGMENT_TAG);
    }

    public static void tryCancelCurrentGdprDialog(Activity activity) {
        if (activity == null) {
            Logging.out(LOG_TAG, "activity is null. Can't cancel current gdpr dialog");
            return;
        }

        if (activity instanceof FragmentActivity) {
            Fragment df = ((FragmentActivity) activity).getSupportFragmentManager()
                    .findFragmentByTag(GDPR_DIALOG_FRAGMENT_TAG);

            if (df != null)
                ((GdprSupportDialogFragment) df).dismissSilently();
            return;
        }

        android.app.Fragment df = activity.getFragmentManager()
                .findFragmentByTag(GDPR_DIALOG_FRAGMENT_TAG);

        if (df != null)
            ((GdprNativeDialogFragment) df).dismissSilently();
    }
}
