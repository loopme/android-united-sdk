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
    private final LoopMeAd mLoopMeAd;
    private String mOrientation;
    private VastWrapperFetcher mVastWrapperFetcher;
    private final ExecutorService mExecutorService;
    private volatile AdFetcherListener mAdFetcherListener;
    private final Handler mHandler = new Handler((Looper.getMainLooper()));
    private static final String UNEXPECTED = "Unexpected";
    private boolean mIsVastVpaidAd;
    private final Timers mTimers;
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
        if (!mFirstRequest || !isInterstitial()) {
            onErrorResult(Errors.NO_ADS_FOUND);
            return;
        }
        mFirstRequest = false;
        mLoopMeAd.setReversOrientationRequest();
        runFetchTask();
    }

    private boolean isInterstitial() {
        return mLoopMeAd != null && mLoopMeAd.isInterstitial();
    }

    @Override
    public void run() {
        try {
            JSONObject data = RequestBuilder.buildRequestJson(mLoopMeAd.getContext(), mLoopMeAd);
            if (Thread.interrupted()) {
                Logging.out(LOG_TAG, "Thread interrupted.");
                stopRequestTimer();
                return;
            }
            GetResponse<ResponseJsonModel> response = LoopMeAdServiceImpl.getInstance().fetchAd(Constants.BASE_URL, data);
            Logging.out(LOG_TAG, "response received");
            stopRequestTimer();
            parseResponse(response);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
            stopRequestTimer();
            handleBadResponse(e.getMessage());
        }
    }

    protected void handleBadResponse(String message) {
        if (TextUtils.isEmpty(message) || !message.contains(UNEXPECTED)) {
            onErrorResult(new LoopMeError(message, Constants.ErrorType.SERVER));
            return;
        }
        onErrorResult(Errors.SYNTAX_ERROR_IN_RESPONSE);
    }

    protected void parseResponse(GetResponse<ResponseJsonModel> response) {
        if (response.isSuccessful()) {
            handleResponse(response.getBody());
        } else {
            handleError(response);
        }
    }

    private void handleResponse(ResponseJsonModel body) {
        if (!isVastWrapperCase(body)) {
            handleNonWrapper(body);
            return;
        }
        saveParams(body);
        launchVastFetcher(body);
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
        if (adParams == null) {
            onErrorResult(Errors.PARSING_ERROR);
            return;
        }
        adParams.setAdType(mAdType);
        adParams.setOrientation(mOrientation);
        onSuccessResult(adParams);
    }

    private void launchVastFetcher(ResponseJsonModel body) {
        VastWrapperFetcher.Listener listener = new VastWrapperFetcher.Listener() {
            @Override
            public void onCompleted(AdParams adParams) {
                handleSuccess(adParams);
            }
            @Override
            public void onFailed(LoopMeError error) {
                onErrorResult(error);
            }
        };
        mVastWrapperFetcher = new VastWrapperFetcher(
            XmlParseService.getVastString(body), listener
        );
        mVastWrapperFetcher.start();
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
        if (!isVast(body)) {
            return false;
        }
        mIsVastVpaidAd = ParseService.isVastVpaidAd(body);
        String vastString = XmlParseService.getVastString(body);
        VastInfo vastInfo = XmlParseService.getVastInfo(vastString);
        return mIsVastVpaidAd && vastInfo != null && vastInfo.hasWrapper();
    }

    private void handleError(GetResponse<ResponseJsonModel> response) {
        if (response == null) {
            onErrorResult(Errors.NO_CONTENT);
            return;
        }
        if (response.getCode() == RESPONSE_NO_ADS) {
            handleNoAds();
            return;
        }
        LoopMeError error = response.getCode() != 0 ?
            new LoopMeError(Constants.BAD_SERVERS_CODE + response.getCode()) :
            new LoopMeError(response.getMessage());
        onErrorResult(error);
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof Timers && arg instanceof TimersType) {
            if ((arg == TimersType.REQUEST_TIMER)) {
                onRequestTimeout();
            }
        }
    }

    private void startRequestTimer() {
        mHandler.post(() -> {
            if (mTimers != null) {
                mTimers.startTimer(TimersType.REQUEST_TIMER);
            }
        });
    }

    protected void stopRequestTimer() {
        mHandler.post(() -> {
            if (mTimers != null) {
                mTimers.stopTimer(TimersType.REQUEST_TIMER);
            }
        });
    }

    private void onRequestTimeout() {
        onErrorResult(Errors.REQUEST_TIMEOUT);
    }

    public void onSuccessResult(final AdParams adParams) {
        mHandler.post(() -> {
            if (mAdFetcherListener != null && adParams != null) {
                mAdFetcherListener.onAdFetchCompleted(adParams);
            } else {
                onErrorResult(Errors.FAILED_TO_PROCESS_AD);
            }
        });
    }

    public void onErrorResult(final LoopMeError error) {
        mHandler.post(() -> {
            if (mAdFetcherListener != null) {
                mAdFetcherListener.onAdFetchFailed(error);
            }
        });
    }
}
