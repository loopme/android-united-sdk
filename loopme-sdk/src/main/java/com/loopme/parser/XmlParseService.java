package com.loopme.parser;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopme.Constants;
import com.loopme.ad.AdParams;
import com.loopme.models.Errors;
import com.loopme.network.response.Bid;
import com.loopme.network.response.BidResponse;
import com.loopme.parser.xml.XmlParser;
import com.loopme.utils.Utils;
import com.loopme.xml.ClickThrough;
import com.loopme.xml.ClickTracking;
import com.loopme.xml.Companion;
import com.loopme.xml.CompanionClickThrough;
import com.loopme.xml.CompanionClickTracking;
import com.loopme.xml.Creative;
import com.loopme.xml.Error;
import com.loopme.xml.Impression;
import com.loopme.xml.InLine;
import com.loopme.xml.Linear;
import com.loopme.xml.MediaFile;
import com.loopme.xml.Tracking;
import com.loopme.xml.Vast;
import com.loopme.xml.vast4.AdVerifications;
import com.loopme.xml.vast4.VastInfo;
import com.loopme.xml.vast4.Verification;
import com.loopme.xml.vast4.ViewableImpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class XmlParseService {

    private static final String LOG_TAG = XmlParseService.class.getSimpleName();
    private static AdParams sAdParams = new AdParams();

    @NonNull
    public static AdParams parse(@NonNull AdParams adParams, @NonNull Bid bidObject) {
        sAdParams = adParams;
        AdParams newAdParams = parseResponse(bidObject.getAdm());
        return newAdParams == null ? sAdParams : newAdParams;
    }

    public static AdParams parse(String vastString) {
        sAdParams = new AdParams();
        return parseResponse(vastString);
    }

    public static String parseOrientation(BidResponse responseModel) {
        Bid bidObject = retrieveBidObject(responseModel);
        return (bidObject == null || bidObject.getExt() == null) ?
            "" : bidObject.getExt().getOrientation();
    }

    @Nullable
    private static Bid retrieveBidObject(BidResponse responseModel) {
        try {
            return responseModel.getSeatbid().get(0).getBid().get(0);
        } catch (IllegalArgumentException | NullPointerException ex) {
            return null;
        }
    }

    @Nullable
    private static AdParams parseResponse(String response) {
        Vast vast = parseVast(response);
        return vast == null ? null : createAdParams(vast);
    }

    private static List<String> getImpressionsList(InLine inLine) {
        List<String> impressions = new ArrayList<>();
        for (Impression impression : inLine.getImpressionList()) {
            if (!TextUtils.isEmpty(impression.getText())) {
                impressions.add(impression.getText());
            }
        }
        return impressions;
    }

    private static String getErrorUrl(InLine inLine) {
        Error error = inLine.getError();
        return (error != null && !TextUtils.isEmpty(error.getText())) ? error.getText() : "";
    }

    private static AdParams createAdParams(@NonNull Vast vast) {
        if (vast.getAd() == null) {
            throw new IllegalArgumentException("Vast or vast.getAd() shouldn't be null");
        }
        InLine inLine = vast.getAd().getInLine();
        Linear linear = getParamLinear(inLine);
        if (linear == null) {
            throw new IllegalArgumentException("NonLinear AD is not supported");
        }
        sAdParams.setExpiredDate(Constants.DEFAULT_EXPIRED_TIME);
        sAdParams.setId(vast.getAd().getId());
        sAdParams.setImpressionsList(getImpressionsList(inLine));
        sAdParams.addErrorUrl(getErrorUrl(inLine));
        ViewableImpression viewableImpression = inLine.getViewableImpression();
        if (viewableImpression != null) {
            sAdParams.setViewableImpressionMap(viewableImpression.getViewableImpressionMap());
        }
        setAdVerificationJavaScriptUrl(inLine.getAdVerifications());
        sAdParams.setSkipTime(linear.getSkipoffset());
        if (linear.getTrackingEvents() != null) {
            sAdParams.setTrackingEventsList(linear.getTrackingEvents().getTrackingList());
        }
        sAdParams.setDuration(Utils.parseDuration(linear.getDuration().getText()));
        sAdParams.setAdParams(parseAdParameters(linear));
        if (linear.getVideoClicks() != null) {
            ClickThrough clickThrough = linear.getVideoClicks().getClickThrough();
            if (clickThrough != null) {
                sAdParams.setVideoRedirectUrl(clickThrough.getText());
            }
        }
        if (linear.getVideoClicks() != null) {
            List<ClickTracking> trackingList = linear.getVideoClicks().getClickTrackingList();
            List<String> clickEvents = new ArrayList<>();
            if (trackingList != null) {
                for (ClickTracking tracking : trackingList) {
                    clickEvents.add(tracking.getText());
                }
            }
            sAdParams.setVideoClicks(clickEvents);
        }
        if (linear.getMediaFiles() != null) {
            List<MediaFile> mediaFileList = linear.getMediaFiles().getMediaFileList();
            String vpaidJsUrl = getVpaidJsUrl(mediaFileList);
            if (!TextUtils.isEmpty(vpaidJsUrl)) {
                sAdParams.setVpaid();
                sAdParams.setVpaidJsUrl(vpaidJsUrl);
            } else {
                if (mediaFileList != null) {
                    List<String> videoFileUrlsList = new ArrayList<>();
                    for (MediaFile mediaFile : filterAndSortSupportedMediaFiles(mediaFileList))
                        videoFileUrlsList.add(mediaFile.getText().trim());
                    sAdParams.setVideoFileUrlsList(videoFileUrlsList);
                }
            }
        }
        sAdParams.setOrientation(linear.getOrientation());
        List<Companion> companionList = getCompanionList(getCreativeList(inLine));
        if (companionList == null || companionList.isEmpty()) {
            return sAdParams;
        }
        List<String> endCardUrlList = new ArrayList<>();
        for (Companion companion : companionList) {
            endCardUrlList.add(companion.getStaticResource().getText().trim());
        }
        sAdParams.setEndCardUrlList(endCardUrlList);
        Companion companion = companionList.get(0);
        if (companion == null) {
            return sAdParams;
        }
        CompanionClickThrough clickThrough = companion.getCompanionClickThrough();
        if (clickThrough != null && clickThrough.getText() != null) {
            String redirectUrl = clickThrough.getText().trim();
            sAdParams.setEndCardRedirectUrl(redirectUrl);
        }
        if (companion.getCompanionClickTracking() != null) {
            List<String> clickEvents = new ArrayList<>();
            for (CompanionClickTracking tracking : companion.getCompanionClickTracking()) {
                clickEvents.add(tracking.getText());
            }
            sAdParams.setEndCardClicks(clickEvents);
        }
        if (companion.getTrackingEvents() != null) {
            List<String> events = new ArrayList<>();
            for (Tracking tracking : companion.getTrackingEvents().getTrackingList()) {
                events.add(tracking.getText());
            }
            sAdParams.setCompanionCreativeViewEvents(events);
        }
        return sAdParams;
    }

    private static void setAdVerificationJavaScriptUrl(AdVerifications adVerifications) {
        if (adVerifications == null)
            return;

        List<Verification> verifications = adVerifications.getVerificationList();
        if (verifications == null)
            return;

        sAdParams.setVerificationList(verifications);
    }

    private static Linear getParamLinear(InLine inLine) {
        if (inLine == null || inLine.getCreatives() == null) {
            return null;
        }
        List<Creative> creativeList = getCreativeList(inLine);
        for (Creative creative : creativeList) {
            if (creative.getLinear() != null) {
                return creative.getLinear();
            }
        }
        return null;
    }

    private static List<Creative> getCreativeList(InLine inLine) {
        return inLine == null ? new ArrayList<>() : inLine.getCreatives().getCreativeList();
    }

    private static String getVpaidJsUrl(List<MediaFile> mediaFileList) {
        if (mediaFileList == null) {
            return "";
        }
        for (MediaFile mediaFile : mediaFileList) {
            boolean isMediaFileValid = mediaFile != null &&
                mediaFile.getApiFramework() != null &&
                (mediaFile.getApiFramework().equalsIgnoreCase(Constants.TYPE_VPAID) ||
                mediaFile.getApiFramework().equalsIgnoreCase(Constants.TYPE_VAST)) &&
                mediaFile.getType().equalsIgnoreCase(Constants.TYPE_APPLICATION_JAVASCRIPT);
            if (isMediaFileValid) {
                return mediaFile.getText().trim();
            }
        }
        return "";
    }

    private static String parseAdParameters(Linear linear) {
        try {
            return linear.getAdParameters() != null ? linear.getAdParameters().getText().trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static List<Companion> getCompanionList(List<Creative> creativeList) {
        if (creativeList != null) {
            for (Creative creative : creativeList) {
                boolean isCompanionAdsNull = creative == null ||
                    creative.getCompanionAds() == null ||
                    creative.getCompanionAds().getCompanionList() == null;
                if (!isCompanionAdsNull) {
                    return creative.getCompanionAds().getCompanionList();
                }
            }
        }
        return new ArrayList<>();
    }

    private static List<MediaFile> filterAndSortSupportedMediaFiles(List<MediaFile> mediaFileList) {
        if (mediaFileList == null) {
            return new ArrayList<>();
        }
        List<MediaFile> supportedMediaFilesList = new ArrayList<>();
        for (MediaFile mediaFile : mediaFileList) {
            String text = mediaFile == null ? "" : mediaFile.getText();
            boolean isSupportedFormat =
                text.contains(Constants.MP4_FORMAT_EXT) || text.contains(Constants.WEBM_FORMAT_EXT);
            if (isSupportedFormat) supportedMediaFilesList.add(mediaFile);
        }
        Comparator<MediaFile> mediaSizeComparator =
            createMediaSizeComparator(Utils.getScreenWidth() * Utils.getScreenHeight());
        Collections.sort(supportedMediaFilesList, mediaSizeComparator);
        return supportedMediaFilesList;
    }

    private static Comparator<MediaFile> createMediaSizeComparator(final int screenSquare) {
        return new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile mediaFile1, MediaFile mediaFile2) {
                // TODO. Use resolution comparisons instead of squares?
                int square1 = calculateSquare(mediaFile1);
                int square2 = calculateSquare(mediaFile2);

                // Higher bitrate first.
                if (square1 == square2)
                    return Integer.compare(mediaFile2.getBitrate(), mediaFile1.getBitrate());

                // Equal screen resolutions are top priority.
                if (screenSquare == square1)
                    return -1;

                // Equal screen resolutions are top priority.
                if (screenSquare == square2)
                    return 1;

                // Lower-than-screen resolutions go first.
                if (square2 > screenSquare && screenSquare > square1)
                    return -1;

                // Lower-than-screen resolutions go first.
                if (square1 > screenSquare && screenSquare > square2)
                    return 1;

                // The lowest of the higher-than-screen resolutions go first
                // when there's no lower-than-screen resolution.
                return square1 > screenSquare
                    ? Integer.compare(square1, square2)
                    : Integer.compare(square2, square1);
            }

            private int calculateSquare(MediaFile mediaFile) {
                return mediaFile.getHeight() * mediaFile.getWidth();
            }
        };
    }

    @Nullable
    private static Vast parseVast(String xml) {
        try {
            return XmlParser.parse(xml, Vast.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getVastString(BidResponse mResponseModel) {
        Bid bidObject = retrieveBidObject(mResponseModel);
        return bidObject == null ? "" : bidObject.getAdm();
    }

    public static boolean isValidXml(BidResponse body) {
        Bid bid = retrieveBidObject(body);
        String xml = bid == null ? "" : bid.getAdm();
        if (TextUtils.isEmpty(xml)) {
            return false;
        }
        Vast vast = parseVast(xml);
        return vast != null && vast.getAd() != null;
    }

    public static VastInfo getVastInfo(String vastString) {
        try {
            Vast mVast = XmlParser.parse(vastString, Vast.class);
            VastInfo mVastInfo = new VastInfo();
            mVastInfo.setWrapper(mVast.getAd() == null ? null : mVast.getAd().getWrapper());
            return mVastInfo;
        } catch (Exception e) {
            VastInfo mVastInfo = new VastInfo();
            mVastInfo.setError(Errors.SYNTAX_ERROR_IN_XML);
            return mVastInfo;
        }
    }
}
