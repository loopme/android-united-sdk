package com.loopme.tester.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.loopme.tester.R;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.model.FileModel;
import com.loopme.tester.ui.activity.QReaderActivity;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

public class UiUtils {

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
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void makeActivitySlidable(Activity activity) {
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .edge(true)
                .edgeSize(0.3f)
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
