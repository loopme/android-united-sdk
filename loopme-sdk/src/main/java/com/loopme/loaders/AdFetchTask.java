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

    private void startRequestTimer() {
        mHandler.post(() -> {
            if (mTimers != null) {
                mTimers.startTimer(TimersType.REQUEST_TIMER);
            }
        });
    }

    private void runFetchTask() { mFetchTask = mExecutorService.submit(this); }
    public void fetch() {
        startRequestTimer();
        runFetchTask();
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

    private boolean isVastWrapperCase(ResponseJsonModel body) {
        if (!ParseService.isAdOfType(ResponseJsonModel.getCreativeType(body), AdType.VAST)) {
            return false;
        }
        String creativeType = ResponseJsonModel.getCreativeType(body);
        mIsVastVpaidAd =
            ParseService.isAdOfType(creativeType, AdType.VAST) ||
            ParseService.isAdOfType(creativeType, AdType.VPAID);
        String vastString = XmlParseService.getVastString(body);
        VastInfo vastInfo = XmlParseService.getVastInfo(vastString);
        return mIsVastVpaidAd && vastInfo != null && vastInfo.hasWrapper();
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
            public void onCompleted(AdParams adParams) { handleSuccess(adParams); }
            @Override
            public void onFailed(LoopMeError error) { onErrorResult(error); }
        };
        mVastWrapperFetcher = new VastWrapperFetcher(
            XmlParseService.getVastString(body), listener
        );
        mVastWrapperFetcher.start();
    }

    private void handleResponse(ResponseJsonModel body) {
        if (!isVastWrapperCase(body)) {
            handleNonWrapper(body);
            return;
        }
        mOrientation = XmlParseService.parseOrientation(body);
        mAdType = AdType.fromString(ResponseJsonModel.getCreativeType(body));
        launchVastFetcher(body);
    }

    protected void parseResponse(GetResponse<ResponseJsonModel> response) {
        if (response.isSuccessful()) {
            handleResponse(response.getBody());
        } else {
            handleError(response);
        }
    }

    private void handleNoAds() {
        boolean isInterstitial = mLoopMeAd != null && mLoopMeAd.isInterstitial();
        if (!mFirstRequest || !isInterstitial) {
            onErrorResult(Errors.NO_ADS_FOUND);
            return;
        }
        mFirstRequest = false;
        mLoopMeAd.setReversOrientationRequest();
        runFetchTask();
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
                onErrorResult(Errors.REQUEST_TIMEOUT);
            }
        }
    }

    protected void stopRequestTimer() {
        mHandler.post(() -> {
            if (mTimers != null) {
                mTimers.stopTimer(TimersType.REQUEST_TIMER);
            }
        });
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
