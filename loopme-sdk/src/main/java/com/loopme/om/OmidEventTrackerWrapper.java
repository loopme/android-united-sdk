package com.loopme.om;

import com.iab.omid.library.loopme.adsession.AdEvents;
import com.iab.omid.library.loopme.adsession.video.InteractionType;
import com.iab.omid.library.loopme.adsession.video.Position;
import com.iab.omid.library.loopme.adsession.video.VastProperties;
import com.iab.omid.library.loopme.adsession.video.VideoEvents;
import com.loopme.Logging;
import com.loopme.time.TimeUtils;
import com.loopme.tracker.constants.EventConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO. Check one-time events (skip, complete, start, impression etc?).
public final class OmidEventTrackerWrapper {
    private static final String LOG_TAG = OmidEventTrackerWrapper.class.getSimpleName();

    private boolean impressionSent;
    private boolean startSent;
    private boolean completeSent;
    private boolean skipSent;

    private Set<String> sentProgressEvents = new HashSet<>();

    private AdEvents adEvents;
    private VideoEvents videoEvents;

    public OmidEventTrackerWrapper(AdEvents adEvents, VideoEvents videoEvents) {
        this.adEvents = adEvents;
        this.videoEvents = videoEvents;
    }

    public void sendOneTimeImpression() {
        if (impressionSent || adEvents == null)
            return;

        try {
            adEvents.impressionOccurred();
            impressionSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendOneTimeStartEvent(float duration, boolean videoMuted) {
        if (startSent || videoEvents == null)
            return;

        try {
            videoEvents.start(duration, videoMuted ? 0 : 1);
            startSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    // TODO. Refactor. Ugly af.
    public void sendOneTimeProgressEvent(float progress, float duration) {
        if (videoEvents == null)
            return;

        float progressPercent = progress / duration * 100;
        // TODO. VAST compliance: is sending of "late" events okay?
        List<String> progressPointKeys = new ArrayList<>();
        if (progressPercent >= 25)
            progressPointKeys.add(EventConstants.FIRST_QUARTILE);
        if (progressPercent >= 50)
            progressPointKeys.add(EventConstants.MIDPOINT);
        if (progressPercent >= 75)
            progressPointKeys.add(EventConstants.THIRD_QUARTILE);

        for (String progressPointKey : progressPointKeys) {
            if (sentProgressEvents.contains(progressPointKey))
                continue;

            try {
                switch (progressPointKey) {
                    case EventConstants.FIRST_QUARTILE:
                        videoEvents.firstQuartile();
                        break;
                    case EventConstants.MIDPOINT:
                        videoEvents.midpoint();
                        break;
                    case EventConstants.THIRD_QUARTILE:
                        videoEvents.thirdQuartile();
                        break;
                }

                sentProgressEvents.add(progressPointKey);

            } catch (Exception e) {
                Logging.out(LOG_TAG, e.toString());
            }
        }
    }

    public void sendOneTimeCompleteEvent() {
        if (completeSent || videoEvents == null)
            return;

        try {
            videoEvents.complete();
            completeSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendOneTimeSkipEvent() {
        if (skipSent || videoEvents == null)
            return;

        try {
            videoEvents.skipped();
            skipSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendVolume(boolean videoMuted) {
        if (videoEvents == null)
            return;

        try {
            videoEvents.volumeChange(videoMuted ? 0 : 1);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendPause() {
        if (videoEvents == null)
            return;

        try {
            videoEvents.pause();
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendResume() {
        if (videoEvents == null)
            return;

        try {
            videoEvents.resume();
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendLoaded(String skipTime, int duration) {
        if (videoEvents == null)
            return;

        try {
            Position position = Position.STANDALONE;
            boolean autoPlay = true;

            int vastSkipTimeMillis = TimeUtils.retrieveSkipTime(skipTime, duration);

            float vastSkipTimeSeconds = vastSkipTimeMillis > 0
                    ? vastSkipTimeMillis / 1000f
                    : 0;

            videoEvents.loaded(vastSkipTimeSeconds == 0
                    ? VastProperties.createVastPropertiesForNonSkippableVideo(
                    autoPlay,
                    position)
                    : VastProperties.createVastPropertiesForSkippableVideo(
                    vastSkipTimeSeconds,
                    autoPlay,
                    position));

        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendClicked() {
        if (videoEvents == null)
            return;

        try {
            videoEvents.adUserInteraction(InteractionType.CLICK);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }
}
