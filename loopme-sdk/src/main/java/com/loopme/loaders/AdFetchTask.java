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
import com.loopme.models.Errors;
import com.loopme.network.response.Bid;
import com.loopme.network.response.BidResponse;
import com.loopme.network.GetResponse;
import com.loopme.parser.ParseService;
import com.loopme.parser.XmlParseService;
import com.loopme.request.RequestBuilder;
import com.loopme.utils.ExecutorHelper;
import com.loopme.network.LoopMeAdService;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AdFetchTask implements Runnable {

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

    public AdFetchTask(LoopMeAd loopMeAd, AdFetcherListener adFetcherListener) {
        mLoopMeAd = loopMeAd;
        mAdFetcherListener = adFetcherListener;
        mExecutorService = ExecutorHelper.getExecutor();
    }

    public void fetch() {
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

    @Override
    public void run() {
        try {
            JSONObject data = RequestBuilder.buildRequestJson(mLoopMeAd.getContext(), mLoopMeAd);
            if (Thread.interrupted()) {
                Logging.out(LOG_TAG, "Thread interrupted.");
                return;
            }
            GetResponse<BidResponse> response = LoopMeAdService.getInstance().fetchAd(Constants.BASE_URL, data);
            Logging.out(LOG_TAG, "response received");
            parseResponse(response);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
            handleBadResponse(e.getMessage());
        }
    }

    protected void handleBadResponse(String message) {
        boolean isUnexpectedError = !TextUtils.isEmpty(message) && message.contains(UNEXPECTED);
        onErrorResult(isUnexpectedError ?
            Errors.SYNTAX_ERROR_IN_RESPONSE :
            new LoopMeError(message, Constants.ErrorType.SERVER)
        );
    }

    private boolean isVastWrapperCase(BidResponse body) {
        AdType creativeType = BidResponse.getCreativeType(body);
        mIsVastVpaidAd = creativeType == AdType.VAST || creativeType == AdType.VPAID;
        return mIsVastVpaidAd && XmlParseService
            .getVastInfo(XmlParseService.getVastString(body))
            .hasWrapper();
    }

    private void handleResponse(BidResponse body) {
        if (!isVastWrapperCase(body)) {
            if (mIsVastVpaidAd && !XmlParseService.isValidXml(body)) {
                onErrorResult(Errors.SYNTAX_ERROR_IN_XML);
                return;
            }
            AdType creativeType = BidResponse.getCreativeType(body);
            Bid bid = safelyRetrieve(() -> body.getSeatbid().get(0).getBid().get(0), null);
            mLoopMeAd.setAdParams(
                ParseService.getAdParamsFromResponse(mLoopMeAd.getAdFormat(), creativeType, bid)
            );
            onSuccessResult(mLoopMeAd.getAdParams());
            return;
        }
        mOrientation = XmlParseService.parseOrientation(body);
        mAdType = BidResponse.getCreativeType(body);
        mVastWrapperFetcher = new VastWrapperFetcher(
            XmlParseService.getVastString(body), new VastWrapperFetcher.Listener() {
            @Override
            public void onCompleted(AdParams adParams) {
                if (adParams == null) {
                    onErrorResult(Errors.PARSING_ERROR);
                    return;
                }
                adParams.setAdType(mAdType);
                adParams.setOrientation(mOrientation);
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
}
