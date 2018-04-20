package com.loopme.loaders;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.utils.ConnectionUtils;
import com.loopme.utils.FileUtils;
import com.loopme.utils.IOUtils;
import com.loopme.utils.InternetUtils;
import com.loopme.webservice.ExecutorHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ExecutorService;


public class FileLoaderNewImpl implements Loader {

    private static final String SLASH = "/";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String LOG_TAG = FileLoaderNewImpl.class.getSimpleName();

    private static final int BUFFER_SIZE = 4096;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private String mFileUrl;

    private String mFileName;
    private volatile boolean mIsStopped;
    private volatile boolean mIsFileFullyDownloaded;

    private Callback mCallback;
    private Context mContext;
    private File mLoadingFile;
    private Handler mHandler;
    private volatile HttpURLConnection mConnection;
    private volatile FileOutputStream mOutputStream;

    public FileLoaderNewImpl(@NonNull String fileUrl, @NonNull Context context, @NonNull Callback callback) {
        mFileUrl = fileUrl;
        mContext = context;
        mCallback = callback;
        mHandler = new Handler(Looper.getMainLooper());
        FileUtils.deleteExpiredFiles(mContext);
    }

    @Override
    public void start() {
        Logging.out(LOG_TAG, "start()");
        Logging.out(LOG_TAG, "Use mobile network for caching: " + Constants.USE_MOBILE_NETWORK_FOR_CACHING);

        mFileName = FileUtils.getFileName(mFileUrl);
        File file = FileUtils.checkIfFileExists(mFileName, mContext);

        if (file != null) {
            handleFileExists();
        } else {
            handleFileDoesNotExist();
        }
    }

    @Override
    public void stop() {
        mCallback = null;
        disconnect();
        mIsStopped = true;
        deleteFileIfNotFullyDownloaded();
        Logging.out(LOG_TAG, "stop()");
    }

    private void load(String filename) {
        if (mIsStopped) {
            return;
        }
        long startLoadingTime = System.currentTimeMillis();
        InputStream inputStream = null;

        try {
            mConnection = openConnection(mFileUrl, HTTP_METHOD_GET);
            mConnection.setReadTimeout(READ_TIMEOUT);
            mConnection.setConnectTimeout(CONNECT_TIMEOUT);
            inputStream = new BufferedInputStream(mConnection.getInputStream());
            mOutputStream = createFileOutputStream(filename);
            writeStreamToFile(inputStream, mOutputStream);
            handleFileFullDownloaded();
            long time = System.currentTimeMillis() - startLoadingTime;
            Logging.out(LOG_TAG, "Asset successfully loaded (" + time + "ms)");

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            onError(Errors.REQUEST_TIMEOUT);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(Errors.BAD_ASSET);

        } catch (IOException e) {
            Logging.out(LOG_TAG, "Exception: " + e.getMessage());
            LoopMeError error = new LoopMeError(Errors.VAST_BAD_ASSET);
            error.addToMessage(mFileUrl);
            onError(error);

        } finally {
            IOUtils.closeQuietly(mOutputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void writeStreamToFile(InputStream stream, FileOutputStream outputStream) throws IOException {
        if (stream != null && outputStream != null && !mIsStopped) {
            int length;
            byte buffer[] = new byte[BUFFER_SIZE];

            while ((length = stream.read(buffer)) != -1 && !mIsStopped) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    private void handleFileDoesNotExist() {
        if (InternetUtils.isOnline(mContext)) {
            if (ConnectionUtils.isWifiConnection(mContext)) {
                preloadFile();
            } else {
                loadViaMobileNetwork();
            }
        }
    }

    private void loadViaMobileNetwork() {
        if (Constants.USE_MOBILE_NETWORK_FOR_CACHING) {
            preloadFile();
        } else {
            onError(Errors.MOBILE_NETWORK_ERROR);
        }
    }

    private void handleFileExists() {
        Logging.out(LOG_TAG, "File already exists");
        String filePath = FileUtils.getExternalFilesDir(mContext).getAbsolutePath() + SLASH + mFileName;
        onFileFullLoaded(filePath);
    }

    private HttpURLConnection openConnection(String fileUrl, String httpMethod) throws IOException, NullPointerException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(httpMethod);
        return connection;
    }

    private FileOutputStream createFileOutputStream(String filename) throws FileNotFoundException {
        mFileName = FileUtils.getExternalFilesDir(mContext).getAbsolutePath() + SLASH + filename;
        mLoadingFile = new File(mFileName);
        mOutputStream = new FileOutputStream(mLoadingFile);
        return mOutputStream;
    }

    private void preloadFile() {
        runInBackgroundThread(new Runnable() {
            @Override
            public void run() {
                load(mFileName);
            }
        });
    }

    private void deleteFileIfNotFullyDownloaded() {
        if (!mIsFileFullyDownloaded && mLoadingFile != null && mLoadingFile.exists()) {
            Logging.out(LOG_TAG, "remove bad file");
            mLoadingFile.delete();
        }
    }

    private void disconnect() {
        runInBackgroundThread(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                    Logging.out(LOG_TAG, "disconnect()");
                }
            }
        });
    }

    private void handleFileFullDownloaded() {
        mIsFileFullyDownloaded = true;
        onFileFullLoaded(mFileName);
    }

    private void runInBackgroundThread(Runnable runnable) {
        ExecutorHelper.getExecutor().submit(runnable);
    }

    private void runOnUiThread(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    private void onFileFullLoaded(final String filePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null && !mIsStopped) {
                    mCallback.onFileFullLoaded(filePath);
                }
            }
        });
    }

    private void onError(final LoopMeError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onError(error);
                }
            }
        });
    }

    public interface Callback {
        void onError(LoopMeError error);

        void onFileFullLoaded(String filePath);
    }
}
