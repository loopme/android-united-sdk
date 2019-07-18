package com.loopme.gdpr.dialog;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.loopme.R;
import com.loopme.gdpr.GdprBridge;
import com.loopme.gdpr.GdprWebView;

/**
 * Created by katerina on 4/27/18.
 */
// TODO. Refactor. Rename?
public final class GdprAlertDialogFactory {

    private GdprAlertDialogFactory() {

    }

    public static Result create(
            LayoutInflater inflater,
            GdprBridge.OnCloseListener onCloseListener,
            String url) {

        View container = inflater.inflate(R.layout.dialog_gdpr_layout, null);

        GdprWebView gdprWebView = container.findViewById(R.id.dialog_gdpr_view);
        gdprWebView.setListener(onCloseListener);
        gdprWebView.setProgressBar((ProgressBar) container.findViewById(R.id.dialog_progress_bar));

        gdprWebView.loadUrl(url);

        return new Result(
                gdprWebView,
                new AlertDialog.Builder(inflater.getContext())
                        .setCancelable(false)
                        .setView(container)
                        .create());
    }

    static class Result {
        private GdprWebView webView;
        private AlertDialog dialog;

        Result(GdprWebView webView, AlertDialog dialog) {
            this.webView = webView;
            this.dialog = dialog;
        }

        public GdprWebView getWebView() {
            return webView;
        }

        public void setWebView(GdprWebView webView) {
            this.webView = webView;
        }

        public AlertDialog getDialog() {
            return dialog;
        }

        public void setDialog(AlertDialog dialog) {
            this.dialog = dialog;
        }
    }
}
