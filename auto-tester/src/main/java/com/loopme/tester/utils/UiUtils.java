package com.loopme.tester.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.loopme.tester.R;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.model.FileModel;

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
}
