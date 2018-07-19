package com.loopme.tester.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.loopme.tester.R;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.model.FileModel;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

public class UiUtils {

    private static final float SCRIM_START_ALPHA = 0.8f;
    private static final float SCRIM_END_ALPHA = 0f;
    private static final float VELOCITY_THRESHOLD = 2400;
    private static final float DISTANCE_THRESHOLD = 0.25f;
    private static final float EDGE_SIZE = 0.3f;

    public static int getSpecificFileImageResource(int fileType) {
        switch (fileType) {
            case FileModel.UP_FOLDER: {
                return R.drawable.up_folder;
            }
            case FileModel.DIRECTORY: {
                return R.drawable.folder;
            }
            case FileModel.FILE: {
                return R.drawable.file;
            }
            default: {
                return R.drawable.file;
            }
        }
    }

    public static int getSdkTypeIcon(AdSdk sdk) {
        if (sdk == AdSdk.MOPUB) {
            return R.drawable.icon_mopub;
        } else if (sdk == AdSdk.LOOPME) {
            return R.drawable.icon_loopme;
        } else if (sdk == AdSdk.LMVPAID) {
            return R.drawable.icon_lmvpaid;
        }
        return R.drawable.icon_loopme;
    }

    public static void hideSoftKeyboard(View view, Context context) {
        if (view != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void makeActivitySlidable(Activity activity) {
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(SCRIM_START_ALPHA)
                .scrimEndAlpha(SCRIM_END_ALPHA)
                .velocityThreshold(VELOCITY_THRESHOLD)
                .distanceThreshold(DISTANCE_THRESHOLD)
                .edge(true)
                .edgeSize(EDGE_SIZE)
                .build();
        Slidr.attach(activity, config);
    }

    public static int getScreenWidth(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(@NonNull Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
