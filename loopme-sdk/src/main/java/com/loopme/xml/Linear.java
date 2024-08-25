package com.loopme.xml;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.loopme.Constants;
import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Tag;
import com.loopme.utils.Utils;
import com.loopme.xml.vast4.Icons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private static List<MediaFile> filterAndSortSupportedMediaFiles(List<MediaFile> mediaFileList) {
        if (mediaFileList == null) return new ArrayList<>();
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

    public List<String> getVideoFiles() {
        if (mediaFiles == null) return new ArrayList<>();
        List<MediaFile> mediaFileList = mediaFiles.getMediaFileList();
        if (mediaFileList == null) return new ArrayList<>();
        List<String> videoFileUrlsList = new ArrayList<>();
        for (MediaFile mediaFile : filterAndSortSupportedMediaFiles(mediaFileList))
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
