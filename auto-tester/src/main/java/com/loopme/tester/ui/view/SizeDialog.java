package com.loopme.tester.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.loopme.tester.R;

public class SizeDialog extends DialogFragment {
    private static final String HEIGHT = "HEIGHT";
    private static final String WIDTH = "WIDTH";
    private EditText mHeightEditText;
    private EditText mHWidthEditText;
    private Listener listener;

    public static SizeDialog newInstance(int width, int height) {
        Bundle bundle = new Bundle();
        bundle.putInt(WIDTH, width);
        bundle.putInt(HEIGHT, height);

        SizeDialog sizeDialog = new SizeDialog();
        sizeDialog.setArguments(bundle);
        return sizeDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_size_layout, null);
        mHeightEditText = (EditText) layout.findViewById(R.id.dialog_fragment_height_edit);
        mHWidthEditText = (EditText) layout.findViewById(R.id.dialog_fragment_width_edit);
        setSize();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New banner size");
        builder.setMessage("Set custom banner size");
        builder.setView(layout);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOkClicked();

            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

    private void onOkClicked() {
        int width = Integer.parseInt(mHWidthEditText.getText().toString());
        int height = Integer.parseInt(mHeightEditText.getText().toString());
        listener.onNewBannerSize(width, height);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void setSize() {
        if (getArguments() != null) {
            int width = getArguments().getInt(WIDTH);
            int height = getArguments().getInt(HEIGHT);
            mHeightEditText.setText("" + height);
            mHWidthEditText.setText("" + width);
        }
    }


    public interface Listener {
        void onNewBannerSize(int width, int height);
    }
}
