package com.loopme.parser;

import static com.loopme.debugging.Params.ERROR_MSG;

import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdParamsBuilder;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.AdType;
import com.loopme.ad.LoopMeAd;
import com.loopme.models.Errors;
import com.loopme.models.response.Bid;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.tracker.partners.LoopMeTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class ParseService {

    private static final String LOG_TAG = ParseService.class.getSimpleName();
    private static final String DEFAULT = "default";
    private static final String PORTRAIT = "portrait";

    public interface RetrieveOperation<T> {
        T execute() throws Exception;
    }
    private static <T> T safelyRetrieve(RetrieveOperation<T> operation, T defaultValue) {
        try {
            return operation.execute();
        } catch (Exception ex) {
            Logging.out(LOG_TAG, ex.getMessage());
            return defaultValue;
        }
    }

    public static LoopMeAd getLoopMeAdFromResponse(LoopMeAd loopMeAd, ResponseJsonModel responseModel) {
        String creativeType = ResponseJsonModel.getCreativeType(responseModel);
        if (creativeType == null) {
            return null;
        }
        AdParams adParams = parse(responseModel, loopMeAd);
        if (isAdOfType(creativeType, AdType.HTML)) {
            setAdType(adParams, AdType.HTML);
        } else if (isAdOfType(creativeType, AdType.VAST)) {
            adParams = XmlParseService.parse(adParams, responseModel);
            setAdType(adParams, AdType.VAST);
        } else if (isAdOfType(creativeType, AdType.VPAID)) {
            adParams = XmlParseService.parse(adParams, responseModel);
            setAdType(adParams, AdType.VPAID);
        } else if (isAdOfType(creativeType, AdType.MRAID)) {
            setAdType(adParams, AdType.MRAID);
        }
        if (adParams == null) {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, Errors.PARSING_ERROR.getMessage());
            LoopMeTracker.post(errorInfo);
        }
        loopMeAd.setAdParams(adParams);
        return loopMeAd;
    }

    private static void setAdType(AdParams adParams, AdType adType) {
        if (adParams != null) {
            adParams.setAdType(adType);
        }
    }

    public static AdParams parse(ResponseJsonModel responseModel, LoopMeAd loopMeAd) {
        Bid bidObject = safelyRetrieve(() -> responseModel.getSeatbid().get(0).getBid().get(0), null);
        if (bidObject == null) {
            return null;
        }
        AdParamsBuilder adParamsBuilder = new AdParamsBuilder(retrieveAdFormat(loopMeAd))
            .html(safelyRetrieve(bidObject::getAdm, ""))
            .orientation(safelyRetrieve(() -> {
                String orientation = bidObject.getExt().getOrientation();
                return orientation.equalsIgnoreCase(DEFAULT) ? PORTRAIT : orientation;
            }, PORTRAIT))
            .token(safelyRetrieve(bidObject::getId, ""))
            .debug(safelyRetrieve(() -> bidObject.getExt().getDebug() == 1, false))
            .trackersList(safelyRetrieve(() -> bidObject.getExt().getMeasurePartners(), new ArrayList<>()))
            .packageIds(safelyRetrieve(() -> new ArrayList<>(bidObject.getExt().getPackageIds()), new ArrayList<>()))
            .mraid(false)
            .autoLoading(retrieveAutoLoadingWithDefaultTrue(bidObject))
            .adSpotDimensions(retrieveAdDimensionsForNoneVastOrDefault(bidObject)
        );
        return adParamsBuilder.isValidFormatValue() ? new AdParams(adParamsBuilder) : new AdParams();
    }

    private static AdSpotDimensions retrieveAdDimensionsForNoneVastOrDefault(Bid bidObject) {
        String creativeType = AdType.HTML.name();
        try {
            creativeType = bidObject.getExt().getCrtype();
        } catch (IllegalArgumentException | NullPointerException ex) {
            Logging.out(LOG_TAG, ex.getMessage());
        }
        if (!isAdOfType(creativeType, AdType.HTML) && !isAdOfType(creativeType, AdType.MRAID)) {
            return AdSpotDimensions.getDefaultBanner();
        }
        if (bidObject.getWidth() == 0 || bidObject.getHeight() == 0) {
            return AdSpotDimensions.getDefaultBanner();
        }
        return new AdSpotDimensions(bidObject.getWidth(), bidObject.getHeight());
    }

    private static boolean retrieveAutoLoadingWithDefaultTrue(Bid bidObject) {
        int autoLoadingValue = 1;

        try {
            autoLoadingValue = bidObject.getExt().getAutoLoading();
        } catch (IllegalStateException | NullPointerException ex) {
            Logging.out(LOG_TAG, ex.getMessage());
        }
        if (autoLoadingValue == Constants.AUTO_LOADING_ABSENCE) {
            Logging.out(ParseService.class.getSimpleName(), "autoLoadingValue is absence");
            return true;
        }

        return autoLoadingValue == 1;
    }

    private static String retrieveAdFormat(LoopMeAd loopMeAd) {
        if (loopMeAd.getAdFormat() == Constants.AdFormat.INTERSTITIAL) {
            return Constants.INTERSTITIAL_TAG;
        }
        return loopMeAd.getAdFormat() == Constants.AdFormat.BANNER ?
            Constants.BANNER_TAG : Constants.INTERSTITIAL_TAG;
    }

    public static boolean isAdOfType(String creativeType, AdType adType) {
        return !TextUtils.isEmpty(creativeType) && creativeType.equalsIgnoreCase(adType.name());
    }
}
