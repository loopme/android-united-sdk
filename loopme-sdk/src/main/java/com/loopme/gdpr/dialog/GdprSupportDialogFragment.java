package com.loopme.gdpr.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.loopme.gdpr.GdprBridge;
import com.loopme.gdpr.GdprChecker;
import com.loopme.gdpr.GdprWebView;

// TODO. Ugly. Duplicate of GdprNativeDialogFragment.
public class GdprSupportDialogFragment
        extends DialogFragment
        implements GdprBridge.OnCloseListener {

    private static final String DISMISS_SILENTLY = "DISMISS_SILENTLY";

    private GdprWebView webView;
    private boolean dismissSilently;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
            dismissSilently = savedInstanceState.getBoolean(DISMISS_SILENTLY, false);

        if (dismissSilently) {
            dismiss();
            return super.onCreateDialog(savedInstanceState);
        }

        GdprAlertDialogFactory.Result result = GdprAlertDialogFactory.create(
                getActivity().getLayoutInflater(),
                this,
                getArguments().getString(GdprDialogFragmentHelper.GDPR_URL));

        webView = result.getWebView();

        return result.getDialog();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DISMISS_SILENTLY, dismissSilently);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // TODO. Workaround for now.
        //  Android bug? onDismiss is called after onDestroy on orientation change.
        if (getActivity() == null || dismissSilently)
            return;

        GdprChecker.onGdprDialogDismissed();
    }

    @Override
    public void onGdprClose() {
        dismiss();
    }

    public void dismissSilently() {
        dismissSilently = true;
        dismiss();
    }
}
