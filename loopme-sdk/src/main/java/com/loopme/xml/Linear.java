package com.loopme.xml;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.xml.vast4.Icons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Linear {

    @Attribute
    private String skipoffset;

    @Tag
    private Duration duration;
    @Tag
    private TrackingEvents trackingEvents;
    @Tag
    private VideoClicks videoClicks;
    @Tag
    private MediaFiles mediaFiles;
    @Tag
    private AdParameters adParameters;
    @Tag
    private Icons icons;

    private static final int MAX_RESOLUTION = 1920 * 1080;
    private static final int MAX_BITRATE = 2000;

    public Duration getDuration() { return duration; }
    public VideoClicks getVideoClicks() { return videoClicks; }

    @NonNull
    public List<Tracking> getTrackingEvents() {
        return trackingEvents != null ? trackingEvents.getTrackingList() : new ArrayList<>();
    }

    @NonNull
    public String getClickThroughUrl() {
        if (videoClicks == null) return "";
        ClickThrough clickThrough = videoClicks.getClickThrough();
        return clickThrough != null ? clickThrough.getText() : "";
    }

    @NonNull
    public List<String> getVideoClickEvents() {
        if (videoClicks == null || videoClicks.getClickTrackingList() == null) return new ArrayList<>();
        List<String> clickEvents = new ArrayList<>();
        for (ClickTracking tracking : videoClicks.getClickTrackingList()) clickEvents.add(tracking.getText());
        return clickEvents;
    }

    public MediaFiles getMediaFiles() { return mediaFiles; }

    private static List<MediaFile> getSupportedMediaFiles(List<MediaFile> mediaFileList) {
        if (mediaFileList == null) return new ArrayList<>();
        List<MediaFile> supportedMediaFilesList = new ArrayList<>();
        for (MediaFile mediaFile : mediaFileList) {
            if (mediaFile == null) continue;
            String text = mediaFile.getText();
            boolean isSupportedFormat =
                text.contains(Constants.MP4_FORMAT_EXT) || text.contains(Constants.WEBM_FORMAT_EXT);
            int resolution = mediaFile.getHeight() * mediaFile.getWidth();
            boolean isPreferredResolutionAndBitrate =
                resolution <= MAX_RESOLUTION && mediaFile.getBitrate() < MAX_BITRATE;
            if (isSupportedFormat && isPreferredResolutionAndBitrate) {
                supportedMediaFilesList.add(mediaFile);
            }
        }
        Collections.sort(supportedMediaFilesList, (mediaFile1, mediaFile2) -> {
            int square1 = mediaFile1.getHeight() * mediaFile1.getWidth();
            int square2 = mediaFile2.getHeight() * mediaFile2.getWidth();
            return Integer.compare(square2, square1);
        });
        return supportedMediaFilesList;
    }

    public List<String> getVideoFiles() {
        if (mediaFiles == null) return new ArrayList<>();
        List<MediaFile> mediaFileList = mediaFiles.getMediaFileList();
        if (mediaFileList == null) return new ArrayList<>();
        List<String> videoFileUrlsList = new ArrayList<>();
        for (MediaFile mediaFile : getSupportedMediaFiles(mediaFileList))
            videoFileUrlsList.add(mediaFile.getText().trim());
        return(videoFileUrlsList);
    }

    private static String getVpaidJsUrl(@NonNull List<MediaFile> mediaFileList) {
        for (MediaFile mediaFile : mediaFileList) {
            boolean isMediaFileValid = mediaFile != null &&
                Constants.TYPE_APPLICATION_JAVASCRIPT.equalsIgnoreCase(mediaFile.getType()) &&
                (Constants.TYPE_VPAID.equalsIgnoreCase(mediaFile.getApiFramework()) ||
                Constants.TYPE_VAST.equalsIgnoreCase(mediaFile.getApiFramework()));
            if (isMediaFileValid) return mediaFile.getText().trim();
        }
        return "";
    }

    @NonNull
    public String getVpaidUrl () {
        if (mediaFiles == null) return "";
        List<MediaFile> mediaFileList = mediaFiles.getMediaFileList();
        String vpaidJsUrl = mediaFileList == null ? "" : getVpaidJsUrl(mediaFileList);
        return !TextUtils.isEmpty(vpaidJsUrl) ? vpaidJsUrl : "";
    }

    public String getSkipoffset() { return skipoffset; }

    @NonNull
    public String getAdParameters() {
        return adParameters == null ? "" : adParameters.getText().trim();
    }

    @NonNull
    public List<Tracking> getTrackingList() {
        return trackingEvents == null ? new ArrayList<>() : trackingEvents.getTrackingList();
    }

    @NonNull
    public ArrayList<String> getVideoClicksList() {
        return videoClicks == null ? new ArrayList<>() : videoClicks.getClicksList();
    }

    @NonNull
    public String getOrientation() {
        MediaFile mediafile = getMediaFiles().getMediaFileList().get(0);
        return mediafile.getHeight() > mediafile.getWidth() ? "portrait" : "landscape";
    }
}
