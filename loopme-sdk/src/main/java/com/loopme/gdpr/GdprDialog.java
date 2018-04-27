package com.loopme.gdpr;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.loopme.R;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprDialog {


    private static final int PADDING = 30;
    private static final float TEXT_16_DP = 16;


    public void show(final Activity activity, final OnGdprDialogListener onGdprDialogListener) {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.gdpr_personalize_your_experience))
                .setView(initCustomMessageView(activity, R.string.gdpr_terms_of_service))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.gdpr_accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showThankYouPage(activity);
                        if (onGdprDialogListener != null) {
                            onGdprDialogListener.onGotGdprConsent(true);
                        }
                    }
                })
                .setNegativeButton(activity.getString(R.string.gdpr_reject), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showRefusePage(activity);
                        if (onGdprDialogListener != null) {
                            onGdprDialogListener.onGotGdprConsent(false);
                        }
                    }
                }).show();
    }

    private View initCustomMessageView(Activity activity, int resource) {
        TextView message = new TextView(activity);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        message.setText(resource);
        message.setTextSize(TEXT_16_DP);
        message.setTextColor(Color.BLACK);
        message.setLinkTextColor(Color.BLUE);
        message.setPadding(PADDING, PADDING, PADDING, PADDING);
        return message;
    }

    private void showRefusePage(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.gdpr_personalize_you_experience)
                .setView(initCustomMessageView(activity, R.string.gdpr_no_problem))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.gdpr_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void showThankYouPage(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.gdpr_personalize_you_experience)
                .setView(initCustomMessageView(activity, R.string.gdpr_thank_you_page_text))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.gdpr_close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public interface OnGdprDialogListener {
        void onGotGdprConsent(boolean isAccepted);
    }
}
