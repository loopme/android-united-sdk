package com.loopme.tester.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopme.tester.R;
import com.loopme.tester.enums.LoadType;
import com.loopme.tester.loaders.FileLoaderManager;
import com.loopme.tester.model.FileModel;
import com.loopme.tester.ui.adapters.FileModelListAdapter;
import com.loopme.tester.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

import static com.loopme.tester.model.FileModel.DIRECTORY;
import static com.loopme.tester.model.FileModel.FILE;
import static com.loopme.tester.model.FileModel.UP_FOLDER;

/**
 * Created by katerina on 2/15/17.
 */

public class FilesListFragment extends BaseFragment implements
        FileModelListAdapter.FileModelListAdapterCallback,
        FileLoaderManager.FilesListLoaderCallback {

    private FileModelListAdapter mFileModelListAdapter;
    private ArrayList<FileModel> mSubDirectories = new ArrayList<>();
    private FileLoaderManager mFileLoaderManager;
    private Context mContext;

    private File mBaseDirectory = FileUtils.getExternalStorageDirectory();
    private File mCurrentDirectory = mBaseDirectory;

    private OnFilesListFragmentListener mOnFilesListFragmentListener;

    public FilesListFragment() {
    }

    public static FilesListFragment newInstance() {
        return new FilesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileLoaderManager = new FileLoaderManager(mContext, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView fileListRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_files_list_recycler_view);
        mFileModelListAdapter = new FileModelListAdapter(null, this);
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fileListRecyclerView.setAdapter(mFileModelListAdapter);
        reloadFilesList(mBaseDirectory);
    }

    @Override
    public void onFileItemSelected(FileModel fileModel) {
        File file = new File(mCurrentDirectory, fileModel.getFileName());
        switch (fileModel.getFileType()) {
            case UP_FOLDER: {
                getOutFromFolder();
                break;
            }
            case DIRECTORY: {
                goIntoFolder(file);
                break;
            }
            case FILE: {
                setFileSelected(file);
                break;
            }
        }
    }

    public void goIntoFolder(File folder) {
        mCurrentDirectory = folder;
        updateDirectoryPath(mCurrentDirectory);
        reloadFilesList(mCurrentDirectory);
    }

    private void reloadFilesList(File directory) {
        if (mFileLoaderManager != null) {
            mFileLoaderManager.startLoad(LoadType.GET_ALL_FILES, directory);
        }
    }

    private void getOutFromFolder() {
        mCurrentDirectory = mCurrentDirectory.getParentFile();
        updateDirectoryPath(mCurrentDirectory);
        reloadFilesList(mCurrentDirectory);

    }

    private void updateDirectoryPath(File directory) {
        if (mOnFilesListFragmentListener != null) {
            mOnFilesListFragmentListener.onUpdateFilePath(directory);
        }
    }

    private void setFileSelected(File file) {
        if (mOnFilesListFragmentListener != null) {
            mOnFilesListFragmentListener.setFileSelected(file);
        }
    }

    @Override
    public void onDestroy() {
        mOnFilesListFragmentListener = null;
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        mOnFilesListFragmentListener = (OnFilesListFragmentListener) context;
    }

    @Override
    public void onGetAllFilesSuccess(ArrayList<FileModel> fileModelList) {
        if (mSubDirectories != null) {
            mSubDirectories.clear();
            mSubDirectories.addAll(fileModelList);
            mFileModelListAdapter.setList(mSubDirectories);
        }
    }

    public interface OnFilesListFragmentListener {
        void onUpdateFilePath(File directoryPath);

        void setFileSelected(File file);
    }
}
