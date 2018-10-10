package com.loopme.loaders;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdType;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.network.GetResponse;
import com.loopme.parser.ParseService;
import com.loopme.parser.XmlParseService;
import com.loopme.request.RequestBuilder;
import com.loopme.time.Timers;
import com.loopme.time.TimersType;
import com.loopme.utils.ExecutorHelper;
import com.loopme.webservice.LoopMeAdServiceImpl;
import com.loopme.xml.vast4.VastInfo;

import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AdFetchTask implements Runnable, Observer {

    protected static final String LOG_TAG = AdFetchTask.class.getSimpleName();
    private static final int RESPONSE_NO_ADS = 204;

    private AdType mAdType;
    private Future mFetchTask;
    private LoopMeAd mLoopMeAd;
    private String mOrientation;
    private VastWrapperFetcher mVastWrapperFetcher;
    private ExecutorService mExecutorService;
    private volatile AdFetcherListener mAdFetcherListener;
    private Handler mHandler = new Handler((Looper.getMainLooper()));
    private static final String UNEXPECTED = "Unexpected";
    private boolean mIsVastVpaidAd;
    private Timers mTimers;
    private volatile boolean mFirstRequest = true;

    public AdFetchTask(LoopMeAd loopMeAd, AdFetcherListener adFetcherListener) {
        mLoopMeAd = loopMeAd;
        mAdFetcherListener = adFetcherListener;
        mTimers = new Timers(this);
        mExecutorService = ExecutorHelper.getExecutor();
    }

    public void fetch() {
        startRequestTimer();
        runFetchTask();
    }

    private void runFetchTask() {
        mFetchTask = mExecutorService.submit(this);
    }

    public void stopFetch() {
        mAdFetcherListener = null;
        if (mFetchTask != null) {
            mFetchTask.cancel(true);
            mFetchTask = null;
        }
        if (mVastWrapperFetcher != null) {
            mVastWrapperFetcher.cancel();
            mVastWrapperFetcher = null;
        }
    }

    private void handleNoAds() {
        if (mFirstRequest && isInterstitial()) {
            mFirstRequest = false;
            mLoopMeAd.setReversOrientationRequest();
            runFetchTask();
        } else {
            onErrorResult(Errors.NO_ADS_FOUND);
        }
    }

    private boolean isInterstitial() {
        return mLoopMeAd != null && mLoopMeAd.isInterstitial();
    }

    @Override
    public void run() {
        try {
            JSONObject data = RequestBuilder.buildRequestJson(mLoopMeAd.getContext(), mLoopMeAd);
            GetResponse<ResponseJsonModel> response = LoopMeAdServiceImpl.getInstance().fetchAd(Constants.BASE_URL, data);
            Logging.out(LOG_TAG, "response received");
            stopRequestTimer();
            parseResponse(response);
        } catch (Exception e) {
            stopRequestTimer();
            handelBadResponse(e.getMessage());
        }
    }

    protected void handelBadResponse(String message) {
        if (!TextUtils.isEmpty(message) && message.contains(UNEXPECTED)) {
            onErrorResult(Errors.SYNTAX_ERROR_IN_RESPONSE);
        } else {
            onErrorResult(new LoopMeError(message, Constants.ErrorType.SERVER));
        }
    }

    protected void parseResponse(GetResponse<ResponseJsonModel> response) {
        if (response.isSuccessful()) {
            handleResponse(response.getBody());
        } else {
            handleError(response);
        }
    }

    private void handleResponse(ResponseJsonModel body) {
        if (isVastWrapperCase(body)) {
            saveParams(body);
            launchVastFetcher(body);
        } else {
            handleNonWrapper(body);
        }
    }

    private boolean isVast(ResponseJsonModel body) {
        return ParseService.isVastAd(body);
    }

    private void handleNonWrapper(ResponseJsonModel body) {
        if (mIsVastVpaidAd && !XmlParseService.isValidXml(body)) {
            onErrorResult(Errors.SYNTAX_ERROR_IN_XML);
            return;
        }
        LoopMeAd loopMeAd = ParseService.getLoopMeAdFromResponse(mLoopMeAd, body);
        if (loopMeAd != null) {
            onSuccessResult(loopMeAd.getAdParams());
        }
    }

    private void handleSuccess(AdParams adParams) {
        if (adParams != null) {
            adParams.setAdType(mAdType);
            adParams.setOrientation(mOrientation);
            onSuccessResult(adParams);
        } else {
            onErrorResult(Errors.PARSING_ERROR);
        }
    }

    private void launchVastFetcher(ResponseJsonModel body) {
        String vastString = XmlParseService.getVastString(body);
        VastWrapperFetcher.Listener listener = initVastFetcherListener();
        mVastWrapperFetcher = new VastWrapperFetcher(vastString, listener);
        mVastWrapperFetcher.start();
    }

    private VastWrapperFetcher.Listener initVastFetcherListener() {
        return new VastWrapperFetcher.Listener() {
            @Override
            public void onCompleted(AdParams adParams) {
                handleSuccess(adParams);
            }

            @Override
            public void onFailed(LoopMeError error) {
                onErrorResult(error);
            }
        };
    }

    private void saveParams(ResponseJsonModel body) {
        saveOrientation(body);
        saveAdType(body);
    }

    private void saveAdType(ResponseJsonModel body) {
        mAdType = ParseService.parseCreativeType(body);
    }

    private void saveOrientation(ResponseJsonModel body) {
        mOrientation = XmlParseService.parseOrientation(body);
    }

    private boolean isVastWrapperCase(ResponseJsonModel body) {
        if (isVast(body)) {
            mIsVastVpaidAd = ParseService.isVastVpaidAd(body);
            String vastString = XmlParseService.getVastString(body);
            VastInfo vastInfo = XmlParseService.getVastInfo(vastString);
            return mIsVastVpaidAd && vastInfo != null && vastInfo.hasWrapper();
        } else {
            return false;
        }
    }

    private void handleError(GetResponse<ResponseJsonModel> response) {
        if (response == null) {
            onErrorResult(Errors.NO_CONTENT);
        } else if (response.getCode() == RESPONSE_NO_ADS) {
            handleNoAds();
        } else {
            if (response.getCode() != 0) {
                onErrorResult(new LoopMeError(Constants.BAD_SERVERS_CODE + response.getCode()));
            } else {
                onErrorResult(new LoopMeError(response.getMessage()));
            }
        }
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable != null && observable instanceof Timers
                && arg != null && arg instanceof TimersType) {
            if ((arg == TimersType.REQUEST_TIMER)) {
                onRequestTimeout();
            }
        }
    }

    private void startRequestTimer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTimers != null) {
                    mTimers.startTimer(TimersType.REQUEST_TIMER);
                }
            }
        });
    }

    protected void stopRequestTimer() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTimers != null) {
                    mTimers.stopTimer(TimersType.REQUEST_TIMER);
                }
            }
        });
    }

    private void onRequestTimeout() {
        onErrorResult(Errors.REQUEST_TIMEOUT);
    }

    public void onSuccessResult(final AdParams adParams) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdFetcherListener != null && adParams != null) {
                    mAdFetcherListener.onAdFetchCompleted(adParams);
                } else {
                    onErrorResult(Errors.FAILED_TO_PROCESS_AD);
                }
            }
        });
    }

    public void onErrorResult(final LoopMeError error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdFetcherListener != null) {
                    mAdFetcherListener.onAdFetchFailed(error);
                }
            }
        });
    }
}
