package com.loopme.loaders;

import static com.loopme.debugging.Params.ERROR_MSG;
import static com.loopme.debugging.Params.ERROR_TYPE;
import static com.loopme.debugging.Params.ERROR_URL;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.utils.ConnectionUtils;
import com.loopme.utils.FileUtils;
import com.loopme.utils.IOUtils;
import com.loopme.utils.InternetUtils;
import com.loopme.utils.ExecutorHelper;
import com.loopme.tracker.partners.LoopMeTracker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

public class FileLoaderNewImpl implements Loader {

    private static final String SLASH = "/";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String LOG_TAG = FileLoaderNewImpl.class.getSimpleName();

    private static final int BUFFER_SIZE = 4096;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;

    private final String fileUrl;
    private String destFilePath;

    private volatile boolean isStopped;

    private Callback callback;
    private final Context context;

    private final Handler handler;

    private volatile HttpURLConnection connection;

    public FileLoaderNewImpl(@NonNull String fileUrl, @NonNull Context context, @NonNull Callback callback) {
        this.fileUrl = fileUrl;
        this.context = context;
        this.callback = callback;
        handler = new Handler(Looper.getMainLooper());
        FileUtils.deleteExpiredFiles(this.context);
    }

    @Override
    public void start() {
        Logging.out(LOG_TAG, "start()");
        Logging.out(LOG_TAG, "Use mobile network for caching: " + Constants.USE_MOBILE_NETWORK_FOR_CACHING);

        String filename = FileUtils.calculateChecksum(fileUrl);
        if (filename == null)
            filename = FileUtils.getFileName(fileUrl);

        destFilePath = createFilePath(context, filename);

        File file = FileUtils.checkIfFileExists(filename, context);
        if (file == null)
            handleFileDoesNotExist();
        else
            onFileFullLoaded();
    }

    @Override
    public void stop() {
        callback = null;
        disconnect();
        isStopped = true;
        Logging.out(LOG_TAG, "stop()");
    }

    private void load() {
        if (isStopped)
            return;

        long startLoadingTime = System.currentTimeMillis();
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            connection = openConnection(fileUrl);
            File file = new File(destFilePath + "_download");
            inputStream = new BufferedInputStream(connection.getInputStream());
            outputStream = new FileOutputStream(file, false);
            // If download was stopped.
            if (!writeStreamToFile(inputStream, outputStream))
                return;
            if (file.renameTo(new File(destFilePath))) {
                onFileFullLoaded();
                long time = System.currentTimeMillis() - startLoadingTime;
                Logging.out(LOG_TAG, "Asset successfully loaded (" + time + "ms)");
                return;
            }
            Logging.out(LOG_TAG, "Couldn't rename downloaded file");
            LoopMeError error = new LoopMeError(Errors.VAST_BAD_ASSET);
            error.addToMessage(fileUrl);
            onError(error);
        } catch (SocketTimeoutException e) {
            onErrorTracking(Errors.REQUEST_TIMEOUT, e);
        } catch (MalformedURLException e) {
            onErrorTracking(Errors.BAD_ASSET, e);
        } catch (IOException e) {
            Logging.out(LOG_TAG, "Exception: " + e.getMessage());
            LoopMeError error = new LoopMeError(Errors.VAST_BAD_ASSET);
            error.addToMessage(fileUrl);
            onErrorTracking(error, e);
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
            connection.disconnect();
        }
    }

    private boolean writeStreamToFile(InputStream stream, FileOutputStream outputStream) throws IOException {
        if (isStopped)
            return false;
        int length;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((length = stream.read(buffer)) != -1) {
            if (isStopped)
                return false;
            outputStream.write(buffer, 0, length);
        }
        return true;
    }

    private void handleFileDoesNotExist() {
        if (!InternetUtils.isOnline(context)) {
            return;
        }
        if (ConnectionUtils.isWifiConnection(context))
            preloadFile();
        else
            loadViaMobileNetwork();
    }

    private void loadViaMobileNetwork() {
        if (Constants.USE_MOBILE_NETWORK_FOR_CACHING)
            preloadFile();
        else
            onError(Errors.MOBILE_NETWORK_ERROR);
    }

    private HttpURLConnection openConnection(String fileUrl) throws IOException, NullPointerException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestMethod(HTTP_METHOD_GET);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        return connection;
    }

    private void preloadFile() {
        runInBackgroundThread(() -> load());
    }

    private void disconnect() {
        runInBackgroundThread(() -> {
            if (connection == null) {
                return;
            }
            connection.disconnect();
            Logging.out(LOG_TAG, "disconnect()");
        });
    }

    private void runInBackgroundThread(Runnable runnable) {
        ExecutorHelper.getExecutor().submit(runnable);
    }

    private void runOnUiThread(Runnable runnable) {
        if (handler != null)
            handler.post(runnable);
    }

    private void onFileFullLoaded() {
        runOnUiThread(() -> {
            if (callback != null && !isStopped)
                callback.onFileFullLoaded(destFilePath);
        });
    }

    private void onError(final LoopMeError error) {
        runOnUiThread(() -> {
            if (callback != null)
                callback.onError(error);
        });
    }

    private void onErrorTracking(final LoopMeError error, final Exception exception) {
        if (exception != null) {
            Logging.out(LOG_TAG, "Exception: " + exception.getMessage());
        }
        HashMap<String, String> errorInfo = new HashMap<>();
        errorInfo.put(ERROR_MSG, error.getMessage());
        errorInfo.put(ERROR_TYPE, error.getErrorType());
        errorInfo.put(ERROR_URL, fileUrl);
        LoopMeTracker.post(errorInfo);
        onError(error);
    }

    private static String createFilePath(Context context, String filename) {
        return FileUtils.getExternalFilesDir(context).getAbsolutePath() + SLASH + filename;
    }

    public interface Callback {
        void onError(LoopMeError error);
        void onFileFullLoaded(String filePath);
    }
}
