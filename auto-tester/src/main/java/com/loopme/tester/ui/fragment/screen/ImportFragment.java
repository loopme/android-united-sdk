package com.loopme.tester.ui.fragment.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopme.tester.R;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.ui.fragment.FilesListFragment;
import com.loopme.tester.utils.FileUtils;

import java.io.File;

/**
 * Created by katerina on 2/15/17.
 */

public class ImportFragment extends BaseFragment implements
        View.OnClickListener {

    private OnImportFragmentListener mOnImportFragmentListener;
    private TextView mPathTextView;
    private File mSelectedFile;

    public static ImportFragment newInstance() {
        return new ImportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRootView(container);
        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChildFragments();

        view.findViewById(R.id.fragment_import_ok_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_import_cancel_button).setOnClickListener(this);

        mPathTextView = (TextView) view.findViewById(R.id.fragment_import_path_textview);

        File baseDirectory = FileUtils.getExternalStorageDirectory();
        mPathTextView.setText(baseDirectory.getAbsolutePath());
    }

    private void initChildFragments() {
        if (!areChildFragmentsAdded()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            addChildFragment(R.id.fragment_import_files_list_container, FilesListFragment.newInstance(), fragmentTransaction);
            makeTransaction(fragmentTransaction);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_import_ok_button:
                onOkButtonClicked();
                break;
            case R.id.fragment_import_cancel_button:
                closeScreen();
                break;

        }
    }

    private void onOkButtonClicked() {
        if (mSelectedFile != null) {
            onFileSelected(mSelectedFile);
        } else {
            Toast.makeText(getActivity(), "No file chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private void onFileSelected(File file) {
        if (mOnImportFragmentListener != null) {
            mOnImportFragmentListener.onImportFile(file);
        }
    }

    private void closeScreen() {
        if (mOnImportFragmentListener != null) {
            mOnImportFragmentListener.onCloseImportFragment();
        }
    }

    public void onUpdateFilePath(File file) {
        mPathTextView.setText(file.getAbsolutePath());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnImportFragmentListener = (OnImportFragmentListener) context;
    }

    @Override
    public void onDetach() {
        mOnImportFragmentListener = null;
        super.onDetach();
    }

    public void setFileSelected(File file) {
        onFileSelected(file);
    }

    public interface OnImportFragmentListener {

        void onImportFile(File file);

        void onCloseImportFragment();

    }

}
