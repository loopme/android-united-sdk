package com.loopme.gdpr;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.loopme.R;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprDialog implements GdprView.GdprViewListener {

    private GdprView mGdprView;
    private final AlertDialog mDialog;
    private OnGdprDialogListener mGdprDialogListener;

    public GdprDialog(Context context, OnGdprDialogListener onGdprDialogListener) {
        mGdprDialogListener = onGdprDialogListener;
        View view = buildView(context);
        mDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();
    }

    private View buildView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_gdpr_layout, null, false);
        mGdprView = (GdprView) view.findViewById(R.id.dialog_gdpr_view);
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

    @Override
    public void onAnswer(boolean isGdprAccepted) {
        dismissDialog();
        if (mGdprDialogListener != null) {
            mGdprDialogListener.onGotGdprConsent(isGdprAccepted);
        }
    }

    @Override
    public void onClose() {
        dismissDialog();
    }

    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public interface OnGdprDialogListener {
        void onGotGdprConsent(boolean isAccepted);
    }
}
