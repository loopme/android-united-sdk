package com.loopme.tester.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.loopme.tester.R;
import com.loopme.tester.utils.FileUtils;
import com.loopme.tester.utils.UiUtils;

import java.io.File;

/**
 * Created by katerina on 1/28/17.
 */

public class CreateFolderDialogFragment extends DialogFragment implements
        View.OnClickListener {

    public static final String CURRENT_DIRECTORY = "CURRENT_DIRECTORY";
    private static final String FILE_NAME = "FILE_NAME";
    private OnCreateFolderDialogFragmentListener mListener;
    private EditText mNewFolderNameTextView;
    private String mPathToSave;

    public CreateFolderDialogFragment() {
    }

    public static CreateFolderDialogFragment newInstance(Bundle bundle) {
        CreateFolderDialogFragment fragment = new CreateFolderDialogFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_folder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mPathToSave = getArguments().getString(CURRENT_DIRECTORY);
        }
        view.findViewById(R.id.create_ok_button).setOnClickListener(this);
        view.findViewById(R.id.create_cancel_button).setOnClickListener(this);
        mNewFolderNameTextView = (EditText) view.findViewById(R.id.fragment_new_folder_edit);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILE_NAME, mNewFolderNameTextView.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && mNewFolderNameTextView != null) {
            mNewFolderNameTextView.setText(savedInstanceState.getString(FILE_NAME));
        }
    }

    private void validate() {
        String newFolderName = mNewFolderNameTextView.getText().toString();
        if (!TextUtils.isEmpty(newFolderName)) {
            createFolder(newFolderName);
        } else {
            Toast.makeText(getContext(), getString(R.string.enter_folder_name), Toast.LENGTH_LONG).show();
        }
    }

    private void createFolder(String newFolderName) {
        if (FileUtils.makeDirectory(mPathToSave, newFolderName)) {
            onSuccess(new File(mPathToSave, newFolderName));
        } else {
            Toast.makeText(getContext(), getString(R.string.folder_already_exists), Toast.LENGTH_LONG).show();
        }
    }

    private void onSuccess(File newFolder) {
        if (mListener != null) {
            mListener.onSuccess(newFolder);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_ok_button: {
                validate();
                break;
            }
        }
        UiUtils.hideSoftKeyboard(mNewFolderNameTextView, getActivity());
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateFolderDialogFragmentListener) {
            mListener = (OnCreateFolderDialogFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface OnCreateFolderDialogFragmentListener {
        void onSuccess(File newFolder);
    }
}
