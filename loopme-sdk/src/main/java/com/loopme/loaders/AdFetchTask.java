package com.loopme.loaders;

import static com.loopme.utils.Utils.safelyRetrieve;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdType;
import com.loopme.ad.LoopMeAd;
import com.loopme.common.LoopMeError;
import com.loopme.debugging.Params;
import com.loopme.models.Errors;
import com.loopme.network.response.Bid;
import com.loopme.network.response.BidResponse;
import com.loopme.network.GetResponse;
import com.loopme.parser.ParseService;
import com.loopme.request.RequestBuilder;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.ExecutorHelper;
import com.loopme.network.LoopMeAdService;
import com.loopme.xml.vast4.VastInfo;
import com.loopme.xml.vast4.Wrapper;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AdFetchTask implements Runnable {

    protected static final String LOG_TAG = AdFetchTask.class.getSimpleName();
    private static final int RESPONSE_NO_ADS = 204;

    private Future mFetchTask;
    private final LoopMeAd mLoopMeAd;
    private VastWrapperFetcher mVastWrapperFetcher;
    private final ExecutorService mExecutorService;
    private volatile AdFetcherListener mAdFetcherListener;
    private final Handler mHandler = new Handler((Looper.getMainLooper()));
    private static final String UNEXPECTED = "Unexpected";

    public AdFetchTask(LoopMeAd loopMeAd, AdFetcherListener adFetcherListener) {
        mLoopMeAd = loopMeAd;
        mAdFetcherListener = adFetcherListener;
        mExecutorService = ExecutorHelper.getExecutor();
    }

    public void fetch() { mFetchTask = mExecutorService.submit(this); }

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
        long duration;
        long startTime = System.currentTimeMillis();
        try {
            JSONObject data = RequestBuilder.buildRequestJson(mLoopMeAd.getContext(), mLoopMeAd);
            if (Thread.interrupted()) {
                Logging.out(LOG_TAG, "Thread interrupted.");
                return;
            }
            GetResponse<BidResponse> response = LoopMeAdService.fetchAd(Constants.BASE_URL, data);
            Logging.out(LOG_TAG, "response received");
            parseResponse(response);
            duration = System.currentTimeMillis() - startTime;
            if (duration > 1000) {
                sendOrtbLatencyAlert(duration, true);
            }
        } catch (Exception e) {
            duration = System.currentTimeMillis() - startTime;
            Logging.out(LOG_TAG, e.toString());
            handleBadResponse(e.getMessage());
            if (duration > 1000) {
                sendOrtbLatencyAlert(duration, false);
            }
        }
    }

    protected void handleBadResponse(String message) {
        boolean isUnexpectedError = !TextUtils.isEmpty(message) && message.contains(UNEXPECTED);
        onErrorResult(isUnexpectedError ?
            Errors.SYNTAX_ERROR_IN_RESPONSE :
            new LoopMeError(message, Constants.ErrorType.SERVER)
        );
    }

    private void handleResponse(BidResponse bidResponse) {
        AdType adType = bidResponse.getCreativeType();
        boolean isVastVpaid = adType == AdType.VAST || adType == AdType.VPAID;
        String vastString = bidResponse.getAdm();
        boolean isVastWrapperCase =  isVastVpaid && VastInfo.getVastInfo(vastString).hasWrapper();

        if (!isVastWrapperCase) {
            if (isVastVpaid && !AdParams.isValidXml(bidResponse)) {
                onErrorResult(Errors.SYNTAX_ERROR_IN_XML);
                return;
            }
            Bid bid = safelyRetrieve(bidResponse::getBid, null);
            mLoopMeAd.setAdParams(
                ParseService.getAdParamsFromResponse(mLoopMeAd.getAdFormat(), adType, bid)
            );
            onSuccessResult(mLoopMeAd.getAdParams());
            return;
        }
        mVastWrapperFetcher = new VastWrapperFetcher(
            vastString, new VastWrapperFetcher.Listener() {
            @Override
            public void onCompleted(String vastString, List<Wrapper> wrapperList) {
                AdParams adParams = AdParams.getAdParams(vastString, wrapperList);
                adParams.setAdType(adType);
                adParams.setOrientation(bidResponse.getOrientation());
                onSuccessResult(adParams);
            }
            @Override
            public void onFailed(LoopMeError error) { onErrorResult(error); }
        });
        mVastWrapperFetcher.start();
    }

    protected void parseResponse(GetResponse<BidResponse> response) {
        if (response.isSuccessful()) {
            handleResponse(response.getBody());
            return;
        }
        if (response.getCode() == RESPONSE_NO_ADS) {
            onErrorResult(Errors.NO_ADS_FOUND);
            return;
        }
        String message = response.getCode() != 0 ?
            Constants.BAD_SERVERS_CODE + response.getCode() :
            response.getMessage();
        onErrorResult(new LoopMeError(message));
    }

    private void onSuccessResult(final AdParams adParams) {
        mHandler.post(() -> {
            if (mAdFetcherListener == null) return;
            if (adParams != null) mAdFetcherListener.onAdFetchCompleted(adParams);
            else mAdFetcherListener.onAdFetchFailed(Errors.FAILED_TO_PROCESS_AD);
        });
    }

    private void onErrorResult(final LoopMeError error) {
        mHandler.post(() -> {
            if (mAdFetcherListener != null) mAdFetcherListener.onAdFetchFailed(error);
        });
    }

    private void sendOrtbLatencyAlert(long duration, boolean isSuccess) {
        LoopMeTracker.post(
                new LoopMeError("ORTB request takes more then 1sec", Constants.ErrorType.LATENCY)
                        .addParam(Params.TIMEOUT, String.valueOf(duration))
                        .addParam(Params.STATUS, isSuccess ? Constants.SUCCESS : Constants.FAIL)
        );
    }
}
