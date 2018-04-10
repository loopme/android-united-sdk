package com.loopme;

/**
 * Created by katerina on 10/30/17.
 */

public class Verifications {

    private static final String LOG_TAG = Verifications.class.getSimpleName();

    public static boolean checkNotNull(Object object) {
        return checkNotNullInternal(object, "Objects should not be null", false);
    }

    public static boolean checkNotNull(Object object, boolean allowThrow) {
        return checkNotNullInternal(object, "Objects should not be null", allowThrow);
    }

    private static boolean checkNotNullInternal(Object object, String errorMessage, boolean allowThrow) {
        if (object != null) {
            return true;
        }
        if (allowThrow) {
            throw new NullPointerException("Internal exception: " + errorMessage);
        }
        Logging.out(LOG_TAG, errorMessage);
        return false;
    }
}
