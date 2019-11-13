package com.loopme.utils;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public final class ViewUtils {

    private ViewUtils() {
    }

    public static View findVisibleView(View[] views, MotionEvent e) {
        if (views == null || e == null)
            return null;

        int x = Math.round(e.getRawX());
        int y = Math.round(e.getRawY());

        for (View v : views) {
            Rect r = new Rect();
            v.getGlobalVisibleRect(r);
            if (r.contains(x, y) && v.getVisibility() == View.VISIBLE)
                return v;
        }

        return null;
    }
}
