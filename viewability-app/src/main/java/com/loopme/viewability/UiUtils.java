package com.loopme.viewability;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by vynnykiakiv on 7/3/17.
 */

public class UiUtils {
    private static final String PERCENT_SYMBOL = "%";

    public static void hideSoftKeyboard(View view, Context context) {
        if (view == null || context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String toPercent(double visibility) {
        return ((int) (visibility * 100)) + PERCENT_SYMBOL;
    }

}
