package com.loopme.om;

import com.iab.omid.library.loopme.adsession.AdEvents;
import com.iab.omid.library.loopme.adsession.media.InteractionType;
import com.iab.omid.library.loopme.adsession.media.MediaEvents;
import com.iab.omid.library.loopme.adsession.media.Position;
import com.iab.omid.library.loopme.adsession.media.VastProperties;
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

    private final Set<String> sentProgressEvents = new HashSet<>();

    private final AdEvents adEvents;
    private final MediaEvents mediaEvents;

    public OmidEventTrackerWrapper(AdEvents adEvents, MediaEvents mediaEvents) {
        this.adEvents = adEvents;
        this.mediaEvents = mediaEvents;
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
        if (startSent || mediaEvents == null)
            return;

        try {
            mediaEvents.start(duration, videoMuted ? 0 : 1);
            startSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    // TODO. Refactor. Ugly af.
    public void sendOneTimeProgressEvent(float progress, float duration) {
        if (mediaEvents == null)
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
                        mediaEvents.firstQuartile();
                        break;
                    case EventConstants.MIDPOINT:
                        mediaEvents.midpoint();
                        break;
                    case EventConstants.THIRD_QUARTILE:
                        mediaEvents.thirdQuartile();
                        break;
                }

                sentProgressEvents.add(progressPointKey);

            } catch (Exception e) {
                Logging.out(LOG_TAG, e.toString());
            }
        }
    }

    public void sendOneTimeCompleteEvent() {
        if (completeSent || mediaEvents == null)
            return;

        try {
            mediaEvents.complete();
            completeSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendOneTimeSkipEvent() {
        if (skipSent || mediaEvents == null)
            return;

        try {
            mediaEvents.skipped();
            skipSent = true;
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendVolume(boolean videoMuted) {
        if (mediaEvents == null)
            return;

        try {
            mediaEvents.volumeChange(videoMuted ? 0 : 1);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendPause() {
        if (mediaEvents == null)
            return;

        try {
            mediaEvents.pause();
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendResume() {
        if (mediaEvents == null)
            return;

        try {
            mediaEvents.resume();
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendLoaded() {
        if (adEvents == null)
            return;

        try {
            adEvents.loaded();
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendLoaded(String skipTime, int duration) {
        if (adEvents == null)
            return;

        try {
            Position position = Position.STANDALONE;
            boolean autoPlay = true;

            int vastSkipTimeMillis = TimeUtils.retrieveSkipTime(skipTime, duration);

            float vastSkipTimeSeconds = vastSkipTimeMillis > 0
                    ? vastSkipTimeMillis / 1000f
                    : 0;

            adEvents.loaded(vastSkipTimeSeconds == 0
                    ? VastProperties.createVastPropertiesForNonSkippableMedia(
                    autoPlay,
                    position)
                    : VastProperties.createVastPropertiesForSkippableMedia(
                    vastSkipTimeSeconds,
                    autoPlay,
                    position));

        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }

    public void sendClicked() {
        if (mediaEvents == null)
            return;

        try {
            mediaEvents.adUserInteraction(InteractionType.CLICK);
        } catch (Exception e) {
            Logging.out(LOG_TAG, e.toString());
        }
    }
}
