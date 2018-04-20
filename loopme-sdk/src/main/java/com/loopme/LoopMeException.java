package com.loopme;

import com.loopme.common.LoopMeError;
import com.loopme.tracker.partners.LoopMeTracker;

/**
 * Created by katerina on 10/30/17.
 */

public class LoopMeException extends Exception {

    public LoopMeException() {
        super();
    }

    public LoopMeException(String errorMessage) {
        super(errorMessage);
        onError(errorMessage);
    }

    public LoopMeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoopMeException(Throwable cause) {
        super(cause);
    }


    private void onError(final LoopMeError error) {
        LoopMeTracker.post(error.getMessage(), error.getErrorType());
    }

    private void onError(final String errorMessage) {
        LoopMeTracker.post(errorMessage);
    }
}
