package com.loopme.parser;

import android.content.Context;
import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.AdType;
import com.loopme.ad.LoopMeAd;
import com.loopme.models.Errors;
import com.loopme.models.response.Bid;
import com.loopme.models.response.Ext;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.tracker.AdIds;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.ValidationHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseService extends JsonParser {

    private static final String V360 = "v360";
    private static final String DEFAULT = "default";
    private static final String PORTRAIT = "portrait";

    private static final int FIRST_ELEMENT = 0;

    public static LoopMeAd getLoopMeAdFromResponse(LoopMeAd loopMeAd, ResponseJsonModel responseModel) {
        String creativeType = parseCreativeTypeFromModel(responseModel);
        if (creativeType == null) {
            return null;
        }
        AdParams adParams = parse(responseModel, loopMeAd);
        if (isHtmlAd(creativeType)) {
            setAdType(adParams, AdType.HTML);
        } else if (isVastAd(creativeType)) {
            adParams = XmlParseService.parse(adParams, responseModel);
            setAdType(adParams, AdType.VAST);
        } else if (isVpaidAd(creativeType)) {
            adParams = XmlParseService.parse(adParams, responseModel);
            setAdType(adParams, AdType.VPAID);
        } else if (isMraidAd(creativeType)) {
            setAdType(adParams, AdType.MRAID);
        }
        sendErrorIfParamsNull(loopMeAd, adParams);
        loopMeAd.setAdParams(adParams);
        return loopMeAd;
    }

    private static void sendErrorIfParamsNull(LoopMeAd loopMeAd, AdParams adParams) {
        if (adParams == null) {
            LoopMeTracker.post(Errors.PARSING_ERROR.getMessage());
        }
    }

    private static void setAdType(AdParams adParams, AdType adType) {
        if (adParams != null) {
            adParams.setAdType(adType);
        }
    }

    public static AdParams parse(ResponseJsonModel responseModel, LoopMeAd loopMeAd) {
        Bid bidObject = retrieveBidObject(responseModel);
        if (bidObject == null) {
            return null;
        }
        String html = retrieveAdm(bidObject);
        String orientation = retrieveOrientation(bidObject);
        String adid = retrieveAdid(bidObject);
        boolean v360 = retrieveV360(bidObject);
        boolean debug = retrieveDebug(bidObject);
        List<String> measurePartners = retrieveMeasurePartners(bidObject);
        AdIds adIds = parseAdIds(bidObject, loopMeAd.getContext());
        boolean autoLoadingValue = retrieveAutoLoadingWithDefaultTrue(bidObject);
        List<String> packageIds = retrievePackageIds(bidObject.getExt());
        AdSpotDimensions adSpotDimensions = retrieveAdDimensionsForNoneVastOrDefault(bidObject);

        return new AdParams.AdParamsBuilder(retrieveAdFormat(loopMeAd))
                .html(html)
                .orientation(orientation)
                .token(adid)
                .debug(debug)
                .trackersList(measurePartners)
                .packageIds(packageIds)
                .video360(v360)
                .mraid(false)
                .adIds(adIds)
                .autoLoading(autoLoadingValue)
                .adSpotDimensions(adSpotDimensions)
                .build();
    }

    private static AdSpotDimensions retrieveAdDimensionsForNoneVastOrDefault(Bid bidObject) {
        AdSpotDimensions adSpotDimensions = new AdSpotDimensions(Constants.DEFAULT_BANNER_WIDTH, Constants.DEFAULT_BANNER_HEIGHT);

        String creativeType = parseCreativeTypeFromBid(bidObject);
        if (isHtmlAd(creativeType) || isMraidAd(creativeType)) {
            String html = retrieveAdm(bidObject);
            HtmlParser parser = new HtmlParser(html);
            int adWidth = parser.getAdWidth();
            int adHeight = parser.getAdHeight();
            adSpotDimensions = new AdSpotDimensions(adWidth, adHeight);
        }

        return adSpotDimensions;
    }

    private static List<String> retrievePackageIds(Ext ext) {
        List<String> packageIds = new ArrayList<>();
        try {
            packageIds.addAll(ext.getPackageIds());
        } catch (IllegalStateException | NullPointerException ex) {
            Logging.out("param", "package ids is absence");
        }
        return packageIds;
    }

    private static boolean retrieveAutoLoadingWithDefaultTrue(Bid bidObject) {
        int autoLoadingValue = 1;

        try {
            autoLoadingValue = (int) bidObject.getExt().getAutoLoading();
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        if (autoLoadingValue == Constants.AUTO_LOADING_ABSENCE) {
            Logging.out(ParseService.class.getSimpleName(), "autoLoadingValue is absence");
            return true;
        }

        return autoLoadingValue == 1;
    }

    private static AdIds parseAdIds(Bid bid, Context context) {
        AdIds adIds = new AdIds();

        if (bid == null || bid.getExt() == null) {
            return adIds;
        }

        Ext ext = bid.getExt();
        adIds.setAdvertiserId(ext.getAdvertiser());
        adIds.setCampaignId(ext.getCampaign());
        adIds.setLineItemId(ext.getLineitem());
        adIds.setAppId(ext.getAppname());
        adIds.setCreativeId(bid.getCrid());
        adIds.setPlacementId(bid.getAdid());
        adIds.setCompany(ext.getCompany());
        adIds.setDeveloper(ext.getDeveloper());
        adIds.setAppName(ext.getAppname());
        adIds.setAppBundle(context.getPackageName());

        return adIds;
    }

    private static String retrieveAdFormat(LoopMeAd loopMeAd) {
        if (loopMeAd.getAdFormat() == Constants.AdFormat.INTERSTITIAL) {
            return Constants.INTERSTITIAL_TAG;
        } else if (loopMeAd.getAdFormat() == Constants.AdFormat.BANNER) {
            return Constants.BANNER_TAG;
        } else {
            return Constants.INTERSTITIAL_TAG;
        }
    }

    private static String retrieveAdid(Bid bidObject) {
        try {
            return bidObject.getAdid();
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static Bid retrieveBidObject(ResponseJsonModel responseModel) {
        try {
            return responseModel.getSeatbid().get(FIRST_ELEMENT).getBid().get(FIRST_ELEMENT);
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static boolean retrieveDebug(Bid bidObject) {
        try {
            return bidObject.getExt().getDebug() == 1;
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static List<String> retrieveMeasurePartners(Bid bidObject) {
        try {
            return bidObject.getExt().getMeasurePartners();
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static String retrieveOrientation(Bid bidObject) {
        try {
            String orientation = bidObject.getExt().getOrientation();
            if (orientation.equalsIgnoreCase(DEFAULT)) {
                return PORTRAIT;
            } else {
                return orientation;
            }
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return PORTRAIT;
    }

    private static boolean retrieveV360(Bid bidObject) {
        try {
            return (int) bidObject.getExt().getV360() == 1;
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static String retrieveAdm(Bid bidObject) {
        try {
            return bidObject.getAdm();
        } catch (IllegalStateException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static boolean getVideo360(JSONObject settings) {
        int video360Value = getInt(settings, V360);
        return video360Value == 1;
    }

    private static void checkFormat(String format) {
        if (!ValidationHelper.isValidFormat(format)) {
            LoopMeTracker.post("Broken response [wrong format parameter: " + format + "]", Constants.ErrorType.SERVER);
        }
    }

    private static String parseCreativeTypeFromModel(ResponseJsonModel responseModel) {
        try {
            return responseModel.getSeatbid().get(FIRST_ELEMENT).getBid().get(FIRST_ELEMENT).getExt().getCrtype();
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return AdType.HTML.name();
    }

    private static String parseCreativeTypeFromBid(Bid bid) {
        try {
            return bid.getExt().getCrtype();
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return AdType.HTML.name();
    }

    public static boolean isVastVpaidAd(ResponseJsonModel responseModel) {
        String creativeType = parseCreativeTypeFromModel(responseModel);
        return isVastAd(creativeType) || isVpaidAd(creativeType);
    }

    private static boolean isVpaidAd(String creativeType) {
        return !TextUtils.isEmpty(creativeType) && creativeType.equalsIgnoreCase(AdType.VPAID.name());
    }

    private static boolean isVastAd(String creativeType) {
        return !TextUtils.isEmpty(creativeType) && creativeType.equalsIgnoreCase(AdType.VAST.name());
    }

    public static boolean isVastAd(ResponseJsonModel responseJsonModel) {
        String type = parseCreativeTypeFromModel(responseJsonModel);
        return isVastAd(type);
    }

    private static boolean isHtmlAd(String creativeType) {
        return !TextUtils.isEmpty(creativeType) && creativeType.equalsIgnoreCase(AdType.HTML.name());
    }


    public static AdType parseCreativeType(ResponseJsonModel body) {
        String type = parseCreativeTypeFromModel(body);
        return AdType.fromString(type);
    }

    public static boolean isMraidAd(String creativeType) {
        return !TextUtils.isEmpty(creativeType) && creativeType.equalsIgnoreCase(AdType.MRAID.name());
    }
}
