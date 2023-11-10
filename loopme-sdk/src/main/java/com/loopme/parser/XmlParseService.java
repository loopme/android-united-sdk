package com.loopme.parser;

import android.text.TextUtils;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.ad.AdParams;
import com.loopme.models.Errors;
import com.loopme.models.response.Bid;
import com.loopme.models.response.ResponseJsonModel;
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
import com.loopme.xml.vast4.Wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class XmlParseService {

    private static final String LOG_TAG = XmlParseService.class.getSimpleName();
    private static final String MEDIA_FILE_TYPE_MP4 = "video/mp4";
    private static final String MEDIA_FILE_TYPE_WEBM = "video/webm";
    private static final int FIRST_ELEMENT = 0;
    private static AdParams sAdParams = new AdParams();


    public static AdParams parse(AdParams adParams, ResponseJsonModel responseModel) {
        Bid bidObject = retrieveBidObject(responseModel);
        if (bidObject == null) {
            return null;
        }
        sAdParams = adParams;
        String xml = bidObject.getAdm();
        Logging.out(LOG_TAG, xml);
        return parseResponse(xml);
    }

    public static AdParams parse(String vastString) {
        sAdParams = new AdParams();
        Logging.out(LOG_TAG, vastString);
        return parseResponse(vastString);
    }

    private static void setExpiredDate() {
        sAdParams.setExpiredDate(Constants.DEFAULT_EXPIRED_TIME);
    }

    public static String parseOrientation(ResponseJsonModel responseModel) {
        Bid bidObject = retrieveBidObject(responseModel);
        if (bidObject != null && bidObject.getExt() != null) {
            return bidObject.getExt().getOrientation();
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

    private static String retrieveXml(ResponseJsonModel responseModel) {
        Bid bid = retrieveBidObject(responseModel);
        if (bid != null) {
            return bid.getAdm();
        }
        return "";
    }

    private static AdParams parseResponse(String response) {
        Vast vast = parseVast(response);
        if (vast != null) {
            return createAdParams(vast);
        }
        return null;
    }

    private static AdParams createAdParams(Vast vast) {
        if (vast == null || vast.getAd() == null) {
            throw new IllegalArgumentException("Vast or vast.getAd() shouldn't be null");
        }
        InLine inLine = vast.getAd().getInLine();
        Linear linear = getParamLinear(inLine);

        if (linear == null) {
            throw new IllegalArgumentException("NonLinear AD is not supported");
        }
        setExpiredDate();
        setParamId(vast);
        setParamImpressions(inLine);
        setErrorUrl(inLine.getError());
        setViewableImpression(inLine.getViewableImpression());
        setAdVerificationJavaScriptUrl(inLine.getAdVerifications());
        setParamSkipTime(linear);
        setParamTrackingEvents(linear);
        setParamDuration(linear);
        setAdParameters(linear);
        setParamVideoRedirectUrl(linear);
        setParamVideoClicks(linear);
        setVastVpaid(linear);

        List<Companion> companionList = getCompanionList(getCreativeList(inLine));

        if (companionList != null && companionList.size() > 0) {
            setParamEndCardUrlList(inLine, companionList);
            Companion companion = companionList.get(0);
            if (companion != null) {
                setParamEndCardRedirectUrl(companion);
                setParamEndCardClicks(companion);
                setParamCompanionCreativeViewEvents(companion);
            }
        }
        return sAdParams;
    }

    private static void setErrorUrl(Error error) {
        if (error != null) {
            String errorUrl = error.getText();
            if (!TextUtils.isEmpty(errorUrl)) {
                sAdParams.addErrorUrl(errorUrl);
            }
        }
    }

    private static void setViewableImpression(ViewableImpression viewableImpression) {
        if (viewableImpression != null) {
            Map<String, List<String>> viewableImpressionMap = viewableImpression.getViewableImpressionMap();
            sAdParams.setViewableImpressionMap(viewableImpressionMap);
        }
    }

    private static void setAdVerificationJavaScriptUrl(AdVerifications adVerifications) {
        if (adVerifications == null)
            return;

        List<Verification> verifications = adVerifications.getVerificationList();
        if (verifications == null)
            return;

        sAdParams.setVerificationList(verifications);
    }

    private static void setParamCompanionCreativeViewEvents(Companion companion) {
        if (companion.getTrackingEvents() != null) {
            List<String> events = new ArrayList<>();
            for (Tracking tracking : companion.getTrackingEvents().getTrackingList()) {
                events.add(tracking.getText());
            }
            sAdParams.setCompanionCreativeViewEvents(events);
        }
    }

    private static void setParamEndCardClicks(Companion companion) {
        if (companion.getCompanionClickTracking() != null) {
            List<String> clickEvents = new ArrayList<>();
            for (CompanionClickTracking tracking : companion.getCompanionClickTracking()) {
                clickEvents.add(tracking.getText());
            }
            sAdParams.setEndCardClicks(clickEvents);
        }
    }

    private static void setParamEndCardRedirectUrl(Companion companion) {
        CompanionClickThrough clickThrough = companion.getCompanionClickThrough();
        if (clickThrough != null && clickThrough.getText() != null) {
            String redirectUrl = clickThrough.getText().trim();
            sAdParams.setEndCardRedirectUrl(redirectUrl);
        }

    }

    private static void setParamEndCardUrlList(InLine inLine, List<Companion> companionList) {
        if (inLine != null && companionList != null) {
            List<String> endCardUrlList = new ArrayList<>();
            for (Companion companion : companionList) {
                endCardUrlList.add(companion.getStaticResource().getText().trim());
            }
            sAdParams.setEndCardUrlList(endCardUrlList);
        }
    }

    private static void setVastVpaid(Linear linear) {
        if (linear.getMediaFiles() != null) {
            List<MediaFile> mediaFileList = linear.getMediaFiles().getMediaFileList();
            String vpaidJsUrl = getVpaidJsUrl(mediaFileList);

            if (!TextUtils.isEmpty(vpaidJsUrl)) {
                setVpaidJsUrl(vpaidJsUrl);
            } else {
                setParamVastVideoFileUrlList(mediaFileList);
            }
        }
    }

    private static void setVpaidJsUrl(String vpaidJsUrl) {
        sAdParams.setVpaid();
        sAdParams.setVpaidJsUrl(vpaidJsUrl);
    }

    private static void setParamVastVideoFileUrlList(List<MediaFile> mediaFileList) {
        if (mediaFileList == null)
            return;

        List<String> videoFileUrlsList = new ArrayList<>();

        for (MediaFile mediaFile : filterAndSortSupportedMediaFiles(mediaFileList))
            videoFileUrlsList.add(mediaFile.getText().trim());

        sAdParams.setVideoFileUrlsList(videoFileUrlsList);
    }

    private static void setParamVideoClicks(Linear linear) {
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
    }

    private static void setParamVideoRedirectUrl(Linear linear) {
        if (linear.getVideoClicks() != null) {
            ClickThrough clickThrough = linear.getVideoClicks().getClickThrough();
            if (clickThrough != null) {
                sAdParams.setVideoRedirectUrl(clickThrough.getText());
            }
        }
    }

    private static void setAdParameters(Linear linear) {
        String adParameters = parseAdParameters(linear);
        sAdParams.setAdParams(adParameters);
    }

    private static void setParamDuration(Linear linear) {
        int duration = Utils.parseDuration(linear.getDuration().getText());
        sAdParams.setDuration(duration);
    }

    private static void setParamTrackingEvents(Linear linear) {
        if (linear.getTrackingEvents() != null) {
            sAdParams.setTrackingEventsList(linear.getTrackingEvents().getTrackingList());
        }
    }

    private static void setParamSkipTime(Linear linear) {
        sAdParams.setSkipTime(linear.getSkipoffset());
    }

    private static Linear getParamLinear(InLine inLine) {
        if (inLine != null && inLine.getCreatives() != null) {
            List<Creative> creativeList = getCreativeList(inLine);

            for (Creative creative : creativeList) {
                if (creative.getLinear() != null) {
                    return creative.getLinear();
                }
            }
        }
        return null;
    }

    private static List<Creative> getCreativeList(InLine inLine) {
        if (inLine != null) {
            return inLine.getCreatives().getCreativeList();
        } else {
            return new ArrayList<Creative>();
        }
    }

    private static void setParamImpressions(InLine inLine) {
        if (inLine == null || inLine.getImpressionList() == null) {
            return;
        }
        List<String> impressions = new ArrayList<>();
        for (Impression impression : inLine.getImpressionList()) {
            if (!TextUtils.isEmpty(impression.getText())) {
                impressions.add(impression.getText());
            }
        }
        sAdParams.setImpressionsList(impressions);
    }


    private static void setParamId(Vast vast) {
        if (vast != null) {
            sAdParams.setId(vast.getAd().getId());
        }
    }

    private static String getVpaidJsUrl(List<MediaFile> mediaFileList) {
        if (mediaFileList != null) {
            for (MediaFile mediaFile : mediaFileList) {
                if (isMediaFileValid(mediaFile)) {
                    return mediaFile.getText().trim();
                }
            }
        }
        return "";
    }

    private static boolean isMediaFileValid(MediaFile mediaFile) {
        return mediaFile != null
                && mediaFile.getApiFramework() != null
                && (mediaFile.getApiFramework().equalsIgnoreCase(Constants.TYPE_VPAID) ||
                mediaFile.getApiFramework().equalsIgnoreCase(Constants.TYPE_VAST))
                && mediaFile.getType().equalsIgnoreCase(Constants.TYPE_APPLICATION_JAVASCRIPT);
    }

    private static String parseAdParameters(Linear linear) {
        try {
            if (linear.getAdParameters() != null) {
                return linear.getAdParameters().getText().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static List<Companion> getCompanionList(List<Creative> creativeList) {
        if (creativeList != null) {
            for (Creative creative : creativeList) {
                if (!isCompanionAdsNull(creative)) {
                    return creative.getCompanionAds().getCompanionList();
                }
            }
        }
        return new ArrayList<>();
    }

    private static boolean isCompanionAdsNull(Creative creative) {
        return creative == null
                || creative.getCompanionAds() == null
                || creative.getCompanionAds().getCompanionList() == null;
    }

    private static List<MediaFile> filterAndSortSupportedMediaFiles(List<MediaFile> mediaFileList) {
        if (mediaFileList == null)
            return new ArrayList<>();

        List<MediaFile> supportedMediaFilesList = new ArrayList<>();
        for (MediaFile mediaFile : mediaFileList)
            if (isSupportedFormat(mediaFile))
                supportedMediaFilesList.add(mediaFile);

        Collections.sort(
                supportedMediaFilesList,
                createMediaSizeComparator(Utils.getScreenWidth() * Utils.getScreenHeight()));

        return supportedMediaFilesList;
    }

    private static boolean isSupportedFormat(MediaFile mediaFile) {
        String text = mediaFile == null ? "" : mediaFile.getText();
        return text.contains(Constants.MP4_FORMAT_EXT)
                || text.contains(Constants.WEBM_FORMAT_EXT);
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

    private static Vast parseVast(String xml) {
        try {
            return XmlParser.parse(xml, Vast.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static VastInfo getVastInfo(String vastString) {
        VastInfoParser vastInfoParser = new VastInfoParser(vastString);
        return vastInfoParser.getVastInfo();
    }

    public static String getVastString(ResponseJsonModel mResponseModel) {
        Bid bidObject = retrieveBidObject(mResponseModel);
        if (bidObject != null) {
            return bidObject.getAdm();
        }
        return "";
    }

    public static boolean isValidXml(ResponseJsonModel body) {
        String xml = retrieveXml(body);
        if (!TextUtils.isEmpty(xml)) {
            Vast vast = parseVast(xml);
            return vast != null && vast.getAd() != null;
        } else {
            return false;
        }
    }

    private static class VastInfoParser {
        private boolean mHasWrapper;
        private Vast mVast;
        private String mVastTagUrl;
        private String mVastString;
        private Wrapper mWrapper;
        private VastInfo mVastInfo;

        VastInfoParser(String vastString) {
            mVastString = vastString;
            if (isValidXml()) {
                setWrapper();
                detectWrapper();
                setVastTagUri();
                createVastInfo();
            }
        }

        private boolean isValidXml() {
            try {
                mVast = XmlParser.parse(mVastString, Vast.class);
                return true;
            } catch (Exception e) {
                mVastInfo = new VastInfo();
                mVastInfo.setError(Errors.SYNTAX_ERROR_IN_XML);
                return false;
            }
        }

        private void setWrapper() {
            if (mVast != null && mVast.getAd() != null) {
                mWrapper = mVast.getAd().getWrapper();
            }
        }

        private void detectWrapper() {
            mHasWrapper = mWrapper != null;
        }

        private void setVastTagUri() {
            if (mHasWrapper) {
                mVastTagUrl = mWrapper.getVastTagUrl();
            }
        }

        private void createVastInfo() {
            if (mVast != null) {
                mVastInfo = new VastInfo();
                mVastInfo.setVastTagUrl(mVastTagUrl);
                mVastInfo.setHasWrapper(mHasWrapper);
                mVastInfo.setWrapper(mWrapper);
            }
        }

        private VastInfo getVastInfo() {
            return mVastInfo;
        }
    }
}
