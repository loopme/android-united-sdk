package com.loopme.loaders;

import static com.loopme.Constants.RESPONSE_NO_ADS;
import static com.loopme.models.Errors.BAD_SERVERS_CODE;
import static com.loopme.utils.Utils.safelyRetrieve;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.LoopMeInterstitialGeneral;
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
import com.loopme.request.InvalidOrtbRequestException;
import com.loopme.request.RequestBuilder;
import com.loopme.request.RequestUtils;
import com.loopme.request.RequestValidator;
import com.loopme.request.ValidationDataExtractor;
import com.loopme.request.validation.Invalidation;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.ExecutorHelper;
import com.loopme.network.LoopMeAdService;
import com.loopme.xml.vast4.VastInfo;
import com.loopme.xml.vast4.Wrapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AdFetchTask implements Runnable {

    protected static final String LOG_TAG = AdFetchTask.class.getSimpleName();

    private static final String UNEXPECTED = "Unexpected";
    private static final String INDEX_OUT_OF_BOUNDS = "Index 0 out of bounds for length 0";

    private Future mFetchTask;
    private final LoopMeAd mLoopMeAd;
    private VastWrapperFetcher mVastWrapperFetcher;
    private final ExecutorService mExecutorService;
    private volatile AdFetcherListener mAdFetcherListener;
    private final Handler mHandler = new Handler((Looper.getMainLooper()));
    private final RequestUtils requestUtils;
    private final ValidationDataExtractor validationDataExtractor;
    private final RequestValidator requestValidator;

    public AdFetchTask(LoopMeAd loopMeAd, AdFetcherListener adFetcherListener) {
        mLoopMeAd = loopMeAd;
        mAdFetcherListener = adFetcherListener;
        mExecutorService = ExecutorHelper.getExecutor();
        requestUtils = new RequestUtils(mLoopMeAd.getContext(), mLoopMeAd);
        validationDataExtractor = new ValidationDataExtractor();
        requestValidator = new RequestValidator();
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
            AdRequestType adRequestType = getAdRequestType();
            JSONObject data = RequestBuilder.buildRequestJson(adRequestType, mLoopMeAd, mLoopMeAd.getContext(), requestUtils);
            ArrayList<Invalidation> invalidations = requestValidator.validate(validationDataExtractor.prepare(data, adRequestType));
            if (!invalidations.isEmpty()) {
                throw new InvalidOrtbRequestException(invalidations.toString(), data.toString());
            }

            if (Thread.interrupted()) {
                Logging.out(LOG_TAG, "Thread interrupted.");
                return;
            }
            GetResponse<BidResponse> response = mLoopMeAd.isCustomAdUrl() ?
                LoopMeAdService.fetchAdByUrl(mLoopMeAd.getCustomAdUrl()) :
                LoopMeAdService.fetchAd(Constants.BASE_URL, data);
            Logging.out(LOG_TAG, "response received");
            parseResponse(response);
            duration = System.currentTimeMillis() - startTime;
            if (duration > 1000) {
                sendOrtbLatencyAlert(duration, true);
            }
        } catch (Exception e) {
            duration = System.currentTimeMillis() - startTime;
            Logging.out(LOG_TAG, e.toString());
            if (duration > 1000) { sendOrtbLatencyAlert(duration, false); }
            handleException(e);
        }
    }

    protected void handleException(Exception exception) {
        String message = exception.getMessage();
        boolean isUnexpectedError = !TextUtils.isEmpty(message) && message.contains(UNEXPECTED);
        boolean isResponseEmptyData = exception instanceof IndexOutOfBoundsException && message.contains(INDEX_OUT_OF_BOUNDS);
        boolean isInvalidOrtbRequestException = exception instanceof InvalidOrtbRequestException;

        LoopMeError error = new LoopMeError(Errors.AD_LOAD_ERROR);

        if (isUnexpectedError) {
            error = new LoopMeError(Errors.ERROR_MESSAGE_RESPONSE_SYNTAX_ERROR);
        } else if (isInvalidOrtbRequestException) {
            error.addParam(Params.REQUEST, ((InvalidOrtbRequestException) exception).getRequest());
        } else if (isResponseEmptyData) {
            error = new LoopMeError(Errors.RESPONSE_EMPTY_DATA);
        }

        error.addParam(Params.ERROR_EXCEPTION, message);
        onErrorResult(error);
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
        onErrorResult(
                new LoopMeError(
                        response.getCode() != 0
                        ? BAD_SERVERS_CODE.getMessage() + response.getCode()
                        : response.getMessage()
        ));
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
                new LoopMeError(Errors.ORTB_REQUEST_TAKES_MORE_THEN_ONE_SEC)
                        .addParam(Params.TIMEOUT, String.valueOf(duration))
                        .addParam(Params.STATUS, isSuccess ? Constants.SUCCESS : Constants.FAIL)
        );
    }

    private AdRequestType getAdRequestType(){
        LoopMeAd.Type adType = mLoopMeAd.getPreferredAdType();
        boolean isBanner = LoopMeAd.Type.ALL == adType || LoopMeAd.Type.HTML == adType;
        boolean isFullscreenSize = requestUtils.isFullscreenSize();
        boolean isVideo = isFullscreenSize && (LoopMeAd.Type.ALL == adType || LoopMeAd.Type.VIDEO == adType);
        boolean isRewarded = mLoopMeAd instanceof LoopMeInterstitialGeneral && ((LoopMeInterstitialGeneral) mLoopMeAd).isRewarded();
        return new AdRequestType(isBanner, isVideo, isRewarded);
    }
}
