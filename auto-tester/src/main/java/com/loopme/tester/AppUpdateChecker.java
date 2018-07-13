package com.loopme.tester;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.loopme.tester.testfairy.TestFairyServiceImpl;
import com.loopme.tester.utils.Utils;

public class AppUpdateChecker implements TestFairyServiceImpl.OnUpdateListener {
    private Activity mActivity;
    private LaunchMode mMode = LaunchMode.START_UP;

    public AppUpdateChecker(Activity activity, LaunchMode mode) {
        mActivity = activity;
        mMode = mode;
    }

    public void checkUpdate() {
        new TestFairyServiceImpl(this).checkUpdate();
    }

    @Override
    public void onUpdateAvailable() {
        if (mMode == LaunchMode.START_UP) {
            proposeToUpdate();
        } else {
            Utils.showUpdate(mActivity);
            mActivity = null;
        }
    }

    private void proposeToUpdate() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.update_dialog_title)
                .setMessage(R.string.update_dialog_message)
                .setPositiveButton(R.string.update_dialog_oupdate_button, new DialogInterface.OnClickListener() {
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

    @Override
    public void onUpdateNotAvailable() {
        if (mMode == LaunchMode.INFO) {
            showNoUpdatesDialog();
        }
    }

    private void showNoUpdatesDialog() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.update_dialog_title)
                .setMessage(R.string.update_dialog_no_updates_messege)
                .setPositiveButton(R.string.update_dialog_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity = null;
                    }
                })
                .show();
    }

    public enum LaunchMode {
        START_UP, INFO
    }
}
