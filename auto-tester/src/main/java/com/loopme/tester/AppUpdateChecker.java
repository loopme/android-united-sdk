package com.loopme.tester;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.loopme.tester.testfairy.TestFairyServiceImpl;
import com.loopme.tester.utils.Utils;

public class AppUpdateChecker implements TestFairyServiceImpl.OnUpdateListener {
    private Activity mActivity;

    public AppUpdateChecker(Activity activity) {
        mActivity = activity;
    }

    public void checkUpdate() {
        new TestFairyServiceImpl(this).checkUpdate();
    }

    @Override
    public void onUpdate() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.update_dialog_title)
                .setMessage(R.string.update_dialog_message)
                .setPositiveButton(R.string.update_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.showUpdate(mActivity);
                        mActivity = null;
                    }
                })
                .setNegativeButton(R.string.update_dialog_close_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity = null;
                    }
                })
                .show();
    }
}
