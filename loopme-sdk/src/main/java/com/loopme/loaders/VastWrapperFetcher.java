package com.loopme.loaders;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.models.Errors;
import com.loopme.common.LoopMeError;
import com.loopme.parser.XmlParseService;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.vast.WrapperParser;
import com.loopme.xml.vast4.VastInfo;
import com.loopme.ResourceInfo;
import com.loopme.utils.Utils;
import com.loopme.webservice.HttpService;
import com.loopme.xml.vast4.Wrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class VastWrapperFetcher {

    private static final String LOG_TAG = VastWrapperFetcher.class.getSimpleName();
    private static final int VAST_4_MAX_WRAPPERS_COUNT = 5;
    private static final long WRAPPER_REQUEST_TIMEOUT = 10000;
    private boolean mIsFollowAdditionalWrapper = true;
    private int mCurrentWrapper = 1;
    private String mVastString;
    private Listener mListener;
    private HttpService mHttpService;
    private List<Wrapper> mWrappersList = new ArrayList<>();
    private CountDownTimer mWrapperTimer;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean mIsStopped;

    public VastWrapperFetcher(String vastString, Listener listener) {
        mVastString = vastString;
        mListener = listener;
        mHttpService = new HttpService();
    }

    public void start() {
        handleVastWrapperCase(mVastString);
    }

    public void cancel() {
        mListener = null;
        mIsStopped = true;
        stopTimer();
        stopHttpService();
    }

    private void stopHttpService() {
        if (mHttpService != null) {
            mHttpService.cancel();
        }
    }

    private boolean hasWrapper(VastInfo vastInfo) {
        return vastInfo != null && vastInfo.hasWrapper();
    }

    private void handleVastWrapperCase(String vastString) {
        VastInfo vastInfo = XmlParseService.getVastInfo(vastString);
        if (vastInfo.hasError()) {
            onFailed(Errors.NO_VAST_RESPONSE_AFTER_WRAPPER);
            return;
        }
        if (hasWrapper(vastInfo)) {
            handleWrapper(vastInfo);
        } else {
            AdParams adParams = XmlParseService.parse(vastString);
            parseWrappers(adParams);
            onCompleted(adParams);
        }
    }

    private void handleWrapper(VastInfo vastInfo) {
        Logging.out(LOG_TAG, "wrapper in response " + mCurrentWrapper);
        collectWrappers(vastInfo.getWrapper());
        doWrapperRequest(vastInfo.getWrapper());
    }

    private void doWrapperRequest(Wrapper wrapper) {
        if (wrapper == null) {
            return;
        }
        if (isWrapperLimitReached()) {
            onFailed(Errors.WRAPPER_LIMIT_REACHED);
            Logging.out(LOG_TAG, Errors.WRAPPER_LIMIT_REACHED.getMessage());
            return;
        }

        if (mIsFollowAdditionalWrapper) {
            mCurrentWrapper++;
            mIsFollowAdditionalWrapper = wrapper.isFollowAdditionalWrappers();
            Logging.out(LOG_TAG, "doWrapperRequest()");
            proceed(wrapper.getVastTagUrl());
        } else {
            Logging.out(LOG_TAG, "mIsFollowAdditionalWrapper = false");
            Logging.out(LOG_TAG, Errors.NO_VAST_RESPONSE_AFTER_WRAPPER.getMessage());
            onFailed(Errors.NO_VAST_RESPONSE_AFTER_WRAPPER);
        }
    }

    private void proceed(String vastTagUrl) {
        if (isVastTagUriAvailable(vastTagUrl) && !mIsStopped) {
            Logging.out(LOG_TAG, "vast url: " + vastTagUrl);
            startTimer();
            doRequest(vastTagUrl);
        }
    }

    private void doRequest(String vastTagUrl) {
        try {
            ResourceInfo resourceInfo = Utils.getResourceInfo(vastTagUrl);
            Response<String> response = mHttpService.downLoadSync(resourceInfo);
            stopTimer();
            parseVastResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            stopTimer();
        }
    }

    private void parseVastResponse(Response<String> response) {
        if (response.code() == Constants.RESPONSE_SUCCESS && !mIsStopped) {
            handleVastWrapperCase(response.body());
        } else {
            onFailed(Errors.NO_VAST_RESPONSE_AFTER_WRAPPER);
        }
    }

    private boolean isVastTagUriAvailable(String vastTagUrl) {
        if (!TextUtils.isEmpty(vastTagUrl)) {
            return true;
        } else {
            Logging.out(LOG_TAG, Errors.TIMEOUT_OF_VAST_URI.getMessage());
            onFailed(Errors.TIMEOUT_OF_VAST_URI);
            return false;
        }
    }

    private boolean isWrapperLimitReached() {
        return mCurrentWrapper > VAST_4_MAX_WRAPPERS_COUNT;
    }

    private void parseWrappers(AdParams adParams) {
        if (adParams != null) {
            adParams.parseWrappers(mWrappersList);
        }
    }

    private void collectWrappers(Wrapper wrapper) {
        if (wrapper != null) {
            mWrappersList.add(wrapper);
        }
    }

    private void startTimer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWrapperTimer = new CountDownTimer(WRAPPER_REQUEST_TIMEOUT, Constants.ONE_SECOND_IN_MILLIS) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Logging.out(LOG_TAG, Errors.TIMEOUT_OF_VAST_URI.getMessage());
                        onFailed(Errors.TIMEOUT_OF_VAST_URI);
                    }
                };
                mWrapperTimer.start();
            }
        });
    }

    private void stopTimer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mWrapperTimer != null) {
                    mWrapperTimer.cancel();
                    mWrapperTimer = null;
                }
            }
        });
    }

    private void trackByWrapperUrl(LoopMeError error) {
        WrapperParser parser = new WrapperParser(mWrappersList);
        for (String errorUrl : parser.getErrorUrlList()) {
            String code = String.valueOf(error.getErrorCode());
            VastVpaidEventTracker.trackVastEvent(errorUrl, code);
        }
    }

    private void onFailed(LoopMeError error) {
        trackByWrapperUrl(error);
        if (mListener != null) {
            mListener.onFailed(error);
        }
        cancel();
    }

    private void onCompleted(AdParams adParams) {
        if (mListener != null) {
            mListener.onCompleted(adParams);
        }
    }

    public interface Listener {
        void onCompleted(AdParams adParams);

        void onFailed(LoopMeError error);
    }
}
