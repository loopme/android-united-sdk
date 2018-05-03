package com.loopme.tester.ui.fragment.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.ui.dialog.CreateFolderDialogFragment;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.ui.fragment.FilesListFragment;
import com.loopme.tester.utils.FileUtils;

import java.io.File;

public class ExportFragment extends BaseFragment implements
        View.OnClickListener {

    private static final String CREATE_NEW_FOLDER_FRAGMENT = "CREATE_NEW_FOLDER_FRAGMENT";

    private EditText mFileNameEditText;
    private TextView mPathTextView;
    private String mCurrentDirectory;
    private File mBaseDirectory;

    private OnExportFragmentListener mOnExportFragmentListener;
    private Context mContext;

    public static ExportFragment newInstance() {
        return new ExportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRootView(container);
        return inflater.inflate(R.layout.fragment_export, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChildFragments();

        view.findViewById(R.id.fragment_export_cancel_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_export_ok_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_export_new_folder_button).setOnClickListener(this);

        mFileNameEditText = (EditText) view.findViewById(R.id.fragment_export_file_name_edittext);
        mPathTextView = (TextView) view.findViewById(R.id.fragment_export_path_textview);

        mBaseDirectory = FileUtils.getExternalStorageDirectory();
        mPathTextView.setText(mBaseDirectory.getAbsolutePath());
        mCurrentDirectory = mBaseDirectory.getAbsolutePath();
        mFileNameEditText.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        mContext = null;
        mOnExportFragmentListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_export_ok_button: {
                onClickExportFile();
                break;
            }
            case R.id.fragment_export_new_folder_button: {
                onClickCreateFolder();
                break;
            }
            case R.id.fragment_export_cancel_button: {
                onCancel();
                break;
            }
        }
    }

    private void onCancel() {
        if (mOnExportFragmentListener != null) {
            mOnExportFragmentListener.onCloseExportFragment();
        }
    }

    private void onClickExportFile() {
        String path = mPathTextView.getText().toString();
        String chosenFileName = mFileNameEditText.getText().toString();
        onExportFile(path, chosenFileName);
    }

    private void initChildFragments() {
        if (!areChildFragmentsAdded()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            addChildFragment(R.id.fragment_export_files_list_container, FilesListFragment.newInstance(), fragmentTransaction);
            makeTransaction(fragmentTransaction);
        }
    }

    private void onExportFile(String path, String fileName) {
        if (mOnExportFragmentListener != null) {
            mOnExportFragmentListener.onExportFile(path, fileName);
        }
    }

    private void onClickCreateFolder() {
        String path = mPathTextView.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString(CreateFolderDialogFragment.CURRENT_DIRECTORY, path);

        CreateFolderDialogFragment dialogFragment = CreateFolderDialogFragment.newInstance(bundle);
        dialogFragment.show(getActivity().getSupportFragmentManager(), CREATE_NEW_FOLDER_FRAGMENT);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        mOnExportFragmentListener = (OnExportFragmentListener) context;
    }

    public void onUpdateFilePath(File directoryPath) {
        mCurrentDirectory = directoryPath.getAbsolutePath();
        mPathTextView.setText(mCurrentDirectory);
    }

    public void setFileSelected(File file) {
        if (mFileNameEditText != null) {
            mFileNameEditText.setText(file.getName());
        }
    }

    public interface OnExportFragmentListener {

        void onExportFile(String path, String fileName);

        void onCloseExportFragment();

    }
}


