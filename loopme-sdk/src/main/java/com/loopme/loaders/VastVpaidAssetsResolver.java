package com.loopme.loaders;

import android.content.Context;
import android.os.CountDownTimer;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.models.Errors;
import com.loopme.common.LoopMeError;

public class VastVpaidAssetsResolver {

    private static final String LOG_TAG = VastVpaidAssetsResolver.class.getSimpleName();
    private static final long COMPANION_TIMEOUT = 3000;
    private int videoFileIndex;
    private int endCardFileIndex;
    private Context mContext;
    private AdParams mAdParams;
    private String mVideoFilePath;
    private Loader mFileLoader;
    private Loader mVideoLoader;
    private OnAssetsLoaded mListener;
    private volatile boolean mIsStopped;
    private CountDownTimer mCompanionTimer;

    public void resolve(AdParams adParams, Context context, OnAssetsLoaded assetsLoadListener) {
        mContext = context;
        mAdParams = adParams;
        mListener = assetsLoadListener;

        videoFileIndex = 0;
        endCardFileIndex = 0;
        mVideoFilePath = null;

        if (mAdParams.isVpaidAd()) {
            loadEndCard();
        } else {
            loadVideoAndEndCard();
        }
    }

    public void stop() {
        mListener = null;
        mIsStopped = true;
        stopLoader(mVideoLoader);
        stopLoader(mFileLoader);
    }

    private void loadVideoAndEndCard() {
        if (isEmptyList()) {
            onErrorOccurred(Errors.VAST_COULD_NOT_FIND_SUPPORTED_FORMAT);
            return;
        }

        mVideoLoader = new FileLoaderNewImpl(
                mAdParams.getVideoFileUrlsList().get(videoFileIndex),
                mContext,
                initVastFileLoaderCallback());

        mVideoLoader.start();
    }

    private boolean isEmptyList() {
        return mAdParams.getVideoFileUrlsList() == null || mAdParams.getVideoFileUrlsList().isEmpty();
    }

    private void loadEndCard() {
        if (mAdParams == null || mAdParams.getEndCardUrlList() == null || mAdParams.getEndCardUrlList().isEmpty()) {
            onAssetsLoaded(mVideoFilePath, null);
            return;
        }
        String endCardUrl = mAdParams.getEndCardUrlList().get(endCardFileIndex);
        mFileLoader = new FileLoaderNewImpl(endCardUrl, mContext, initVpaidFileLoaderCallback());
        startCompanionTimer();
        mFileLoader.start();
    }

    private void startCompanionTimer() {
        mCompanionTimer = new CountDownTimer(COMPANION_TIMEOUT, Constants.ONE_SECOND_IN_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onCompanionTimeout();
            }
        };
        mCompanionTimer.start();
    }

    private void onCompanionTimeout() {
        stopLoader(mFileLoader);
        handleVpaidError(Errors.COMPANION_ERROR);
    }

    private void stopLoader(Loader loader) {
        if (loader != null) {
            loader.stop();
        }
    }

    private FileLoaderNewImpl.Callback initVpaidFileLoaderCallback() {
        return new FileLoaderNewImpl.Callback() {
            @Override
            public void onError(LoopMeError error) {
                cancelCompanionTimer();
                handleVpaidError(Errors.COMPANION_ERROR);
            }

            @Override
            public void onFileFullLoaded(String filePath) {
                cancelCompanionTimer();
                onAssetsLoaded(mVideoFilePath, filePath);
            }
        };
    }

    private void cancelCompanionTimer() {
        if (mCompanionTimer != null) {
            mCompanionTimer.cancel();
            mCompanionTimer = null;
        }
    }

    private FileLoaderNewImpl.Callback initVastFileLoaderCallback() {
        return new FileLoaderNewImpl.Callback() {
            @Override
            public void onError(LoopMeError error) {
                handleVastError(error);
            }

            @Override
            public void onFileFullLoaded(String filePath) {
                Logging.out(LOG_TAG, "onFileFullLoaded");
                mVideoFilePath = filePath;
                loadEndCard();
            }
        };
    }

    private void handleVpaidError(LoopMeError error) {
        onPostWarning(error);
        endCardFileIndex++;
        if (endCardFileIndex < mAdParams.getEndCardUrlList().size()) {
            loadEndCard();
        } else {
            onAssetsLoaded(mVideoFilePath, null);
        }
    }

    private void handleVastError(LoopMeError error) {
        Logging.out(LOG_TAG, "Load video fail:" + error.getMessage());
        videoFileIndex++;
        if (videoFileIndex < mAdParams.getVideoFileUrlsList().size()) {
            onPostWarning(error);
            loadVideoAndEndCard();
        } else {
            onErrorOccurred(error);
        }
    }

    private void onPostWarning(LoopMeError error) {
        if (mListener != null) {
            mListener.onPostWarning(error);
        }
    }

    private void onAssetsLoaded(String videoFilePath, String filePath) {
        if (mListener != null && !mIsStopped) {
            mListener.onAssetsLoaded(videoFilePath, filePath);
        }
    }

    private void onErrorOccurred(LoopMeError error) {
        if (mListener != null) {
            mListener.onError(error);
        }
    }

    public interface OnAssetsLoaded {
        void onAssetsLoaded(String videoFilePath, String endCardFilePath);

        void onError(LoopMeError error);

        void onPostWarning(LoopMeError error);
    }
}
