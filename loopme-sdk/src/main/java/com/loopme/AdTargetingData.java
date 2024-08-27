package com.loopme;

import java.io.Serializable;
import java.util.Calendar;

public class AdTargetingData implements Serializable {

    private static final String LOG_TAG = AdTargetingData.class.getSimpleName();

    private String mKeywords;
    public String getKeywords() { return mKeywords; }
    public void setKeywords(String keywords) { mKeywords = keywords; }

    private int mYob;
    public int getYob() { return mYob; }
    public void setYob(int yob) {
        if (yob >= 1900 && yob <= Calendar.getInstance().get(Calendar.YEAR)) mYob = yob;
        else Logging.out(LOG_TAG, "Year of birth should be between 1900 and " + Calendar.getInstance().get(Calendar.YEAR));
    }

    private String mGender;
    public String getGender() { return mGender; }
    public void setGender(String gender) {
        if (gender != null && gender.matches("(?i)^(f|m|female|male)$")) mGender = gender;
        else Logging.out(LOG_TAG, "Gender should be 'f', 'm', 'female', 'male'");
    }

    public void clear() {
        mKeywords = null;
        mGender = null;
        mYob = 0;
    }
}
