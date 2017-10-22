package com.loopme;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdTargetingData implements Serializable {

    private static final String LOG_TAG = AdTargetingData.class.getSimpleName();

    private String mKeywords;
    private int mYearOfBirth;
    private String mGender;

    private List<CustomRequestParameter> mCustomParams = new ArrayList<CustomRequestParameter>();

    public void clear() {
        mKeywords = null;
        mYearOfBirth = 0;
        mGender = null;

        mCustomParams.clear();
    }

    public void setKeywords(String keywords) {
        mKeywords = keywords;
    }

    public String getKeywords() {
        return mKeywords;
    }

    public void setYob(int yob) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (yob >= 1900 && yob <= currentYear) {
            mYearOfBirth = yob;
        }
    }

    public int getYob() {
        return mYearOfBirth;
    }

    public List<CustomRequestParameter> getCustomParameters() {
        return mCustomParams;
    }

    public void setCustomParameters(String paramName, String paramValue) {
        if (!TextUtils.isEmpty(paramValue) && !TextUtils.isEmpty(paramName)) {
            mCustomParams.add(new CustomRequestParameter(paramName, paramValue));
        }
    }

    public void setGender(String gender) {
        if (gender == null) {
            return;
        }
        if (gender.equalsIgnoreCase("f") || gender.equalsIgnoreCase("m") ||
                gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("male")) {
            mGender = gender;
        } else {
            Logging.out(LOG_TAG, "Wrong gender value");
        }
    }

    public String getGender() {
        return mGender;
    }
}
