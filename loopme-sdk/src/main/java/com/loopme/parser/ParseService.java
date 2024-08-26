package com.loopme.parser;

import static com.loopme.utils.Utils.safelyRetrieve;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.Constants.AdFormat;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdParamsBuilder;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.AdType;
import com.loopme.network.response.Bid;

import java.util.ArrayList;

public class ParseService {

    private static final String LOG_TAG = ParseService.class.getSimpleName();
    private static final String DEFAULT = "default";
    private static final String PORTRAIT = "portrait";

    @NonNull
    public static AdParams getAdParamsFromResponse(
        @NonNull AdFormat adFormat, @NonNull AdType creativeType, @Nullable Bid bid
    ) {
        AdParams adParams = bid == null ? new AdParams() : parse(bid, adFormat, creativeType);
        if (creativeType == AdType.VAST || creativeType == AdType.VPAID) {
            adParams = bid == null ? adParams : AdParams.parse(bid.getAdm(), adParams);
        }
        adParams.setAdType(creativeType);
        return adParams;
    }

    @NonNull
    private static AdParams parse(@NonNull Bid bid, @NonNull AdFormat adFormat, AdType type) {
        return new AdParams(new AdParamsBuilder()
            .cid(bid.getCid())
            .requestId(bid.getId())
            .crid(bid.getCrid())
            .format(AdParamsBuilder.getAdFormat(adFormat))
            .html(safelyRetrieve(bid::getAdm, ""))
            .orientation(safelyRetrieve(() -> {
                String orientation = bid.getExt().getOrientation();
                return DEFAULT.equalsIgnoreCase(orientation) ? PORTRAIT : orientation;
            }, PORTRAIT))
            .token(safelyRetrieve(bid::getId, ""))
            .debug(safelyRetrieve(() -> bid.getExt().getDebug() == 1, false))
            .trackersList(safelyRetrieve(() -> bid.getExt().getMeasurePartners(), new ArrayList<>()))
            .packageIds(safelyRetrieve(() -> new ArrayList<>(bid.getExt().getPackageIds()), new ArrayList<>()))
            .mraid(false)
            .autoLoading(retrieveAutoLoadingWithDefaultTrue(bid))
            .adSpotDimensions(retrieveAdDimension(bid, type))
        );
    }

    private static AdSpotDimensions retrieveAdDimension(@NonNull Bid bid, @NonNull AdType type) {
        if (type != AdType.HTML && type != AdType.MRAID) return AdSpotDimensions.getDefaultBanner();
        if (bid.getWidth() == 0 || bid.getHeight() == 0) return AdSpotDimensions.getDefaultBanner();
        return new AdSpotDimensions(bid.getWidth(), bid.getHeight());
    }

    private static boolean retrieveAutoLoadingWithDefaultTrue(Bid bid) {
        int autoLoadingValue = 1;
        try {
            autoLoadingValue = bid.getExt().getAutoLoading();
        } catch (IllegalStateException | NullPointerException ex) {
            Logging.out(LOG_TAG, ex.getMessage());
        }
        if (autoLoadingValue == Constants.AUTO_LOADING_ABSENCE) {
            Logging.out(LOG_TAG, "autoLoadingValue is absence");
            return true;
        }

        return autoLoadingValue == 1;
    }
}
