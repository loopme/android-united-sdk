package com.loopme.tester.loaders;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.loopme.tester.R;
import com.loopme.tester.enums.LoadType;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.model.FileModel;
import com.loopme.tester.model.Response;
import com.loopme.tester.utils.AdSpotValidator;
import com.loopme.tester.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by katerina on 2/27/17.
 */

public class FileLoaderManager {

    private Context mContext;

    private Handler mHandler = new Handler();
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private FileLoaderManagerCallback mFileLoaderManagerCallback = null;
    private FilesListLoaderCallback mFilesListLoaderCallback = null;

    public FileLoaderManager() {
        startBackgroundThread();
    }

    public FileLoaderManager(Context context, FileLoaderManagerCallback callback) {
        this();
        this.mContext = context;
        this.mFileLoaderManagerCallback = callback;
    }

    public FileLoaderManager(Context context, FilesListLoaderCallback callback) {
        this();
        this.mContext = context;
        this.mFilesListLoaderCallback = callback;
    }

    public void startLoad(LoadType loadType, File file) {
        startLoad(loadType, file, null);
    }

    public void startLoad(LoadType loadType, File file, ArrayList<AdSpot> adSpotList) {
        switch (loadType) {
            case FILE_SAVE: {
                startSaveFile(file, adSpotList);
                break;
            }
            case FILE_READ: {
                startReadFile(file);
                break;
            }
            case GET_ALL_FILES: {
                getAllFiles(file);
                break;
            }
        }
    }

    private void getAllFiles(File file) {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.postDelayed(new GetAllFilesRunnable(file), 0);
        } else {
            onError(mContext.getString(R.string.error));
        }
    }

    private void startReadFile(File file) {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.postDelayed(new StartReadFileRunnable(file), 0);
        } else {
            onError(mContext.getString(R.string.error));
        }
    }

    private void startSaveFile(File file, ArrayList<AdSpot> adSpotList) {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.postDelayed(new StartSaveFileRunnable(file, adSpotList), 0);
        } else {
            onError(mContext.getString(R.string.error));
        }
    }

    private class StartSaveFileRunnable implements Runnable {
        private File mFile;
        private ArrayList<AdSpot> mAdSpotList;

        StartSaveFileRunnable(File file, ArrayList<AdSpot> list) {
            mAdSpotList = list;
            mFile = file;
        }

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    saveFile();
                }
            });
        }

        private void saveFile() {
            if (isValidationSuccess(mFile)) {
                boolean saveSuccess = FileUtils.saveFile(mFile, mAdSpotList);
                if (saveSuccess) {
                    onSaveSuccess();
                }
            } else {
                onError(mContext.getString(R.string.save_file_error));
            }
        }
    }

    private void onSaveSuccess() {
        if (mFileLoaderManagerCallback != null) {
            mFileLoaderManagerCallback.onSaveFileSuccess();
        }
    }

    private boolean isValidationSuccess(File file) {
        return file != null;
    }

    private class StartReadFileRunnable implements Runnable {

        private File mFile;

        private StartReadFileRunnable(File file) {
            mFile = file;
        }

        @Override
        public void run() {
            final ArrayList<AdSpot> adSpotsList = FileUtils.readFromFile(mFile);
            final boolean readSuccess = adSpotsList.size() > 0;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (readSuccess) {
                        onReadSuccess(adSpotsList);
                    } else {
                        onError(mContext.getString(R.string.read_file_error));
                    }
                }
            });
        }
    }

    private class GetAllFilesRunnable implements Runnable {

        private File mParentDirectory;

        public GetAllFilesRunnable(File file) {
            mParentDirectory = file;
        }

        @Override
        public void run() {
            final boolean validParent = isValidParentDirectory(mParentDirectory);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    getFiles(validParent);
                }
            });
        }

        private void getFiles(boolean validParent) {
            if (validParent) {
                ArrayList<FileModel> list = FileUtils.getAllFiles(mParentDirectory);
                onGetAllFilesSuccess(list);
            } else {
                onError();
            }
        }
    }

    private void onError() {
        onError(mContext.getString(R.string.empty_string));
    }

    private void onGetAllFilesSuccess(ArrayList<FileModel> list) {
        if (mFilesListLoaderCallback != null) {
            mFilesListLoaderCallback.onGetAllFilesSuccess(list);
        }
    }

    private boolean isValidParentDirectory(File file) {
        return AdSpotValidator.isValidParentDirectory(file);
    }

    private void onReadSuccess(ArrayList<AdSpot> adSpotsList) {
        if (mFileLoaderManagerCallback != null) {
            mFileLoaderManagerCallback.onReadFileSuccess(adSpotsList);
        }
    }

    private void onError(String message) {
        Response response = new Response(false, message);
        if (mFileLoaderManagerCallback != null) {
            mFileLoaderManagerCallback.onError(response);
        }
    }

    private void startBackgroundThread() {
        if (mBackgroundThread == null) {
            mBackgroundThread = new HandlerThread("SearchResultsLoaderThread");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quit();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
    }

    public void onDestroy() {
        mFileLoaderManagerCallback = null;
        stopBackgroundThread();
    }

    public interface FileLoaderManagerCallback {
        void onSaveFileSuccess();

        void onReadFileSuccess(ArrayList<AdSpot> adSpotsList);

        void onError(Response response);

    }

    public interface FilesListLoaderCallback {
        void onGetAllFilesSuccess(ArrayList<FileModel> fileModelList);
    }
}
