package com.loopme.gdpr;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.loopme.R;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprDialog implements GdprWebView.GdprViewListener {

    private GdprWebView mGdprView;
    private final AlertDialog mDialog;
    private OnGdprDialogListener mGdprDialogListener;
    private Context mContext;

    public GdprDialog(Context context, OnGdprDialogListener onGdprDialogListener) {
        mGdprDialogListener = onGdprDialogListener;
        mContext = context;
        View view = buildView(context);
        mDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setOnDismissListener(mOnDismissListener)
                .setView(view)
                .create();
    }

    private DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            onClose();
        }
    };

    private View buildView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_gdpr_layout, null, false);
        mGdprView = (GdprWebView) view.findViewById(R.id.dialog_gdpr_view);
        mGdprView.setListener(this);
        mGdprView.setProgressbar((ProgressBar) view.findViewById(R.id.dialog_progress_bar));

        return view;
    }

    public void show(String url) {
        if (mGdprView != null) {
            mGdprView.loadPage(url);
        }
    }

    @Override
    public void onPageLoaded(Context context) {
        showDialog();
    }

    private void showDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void onClose() {
        if (mGdprDialogListener != null) {
            mGdprDialogListener.onCloseDialog();
        }
        dismissDialog();
    }

    private void dismissDialog() {
        if (mDialog != null && mContext instanceof Activity &&
                !((Activity) mContext).isFinishing()) {
            mDialog.dismiss();
        }
    }

    public interface OnGdprDialogListener {
        void onCloseDialog();
    }

}
