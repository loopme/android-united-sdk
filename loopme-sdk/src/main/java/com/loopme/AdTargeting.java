package com.loopme;

/**
 * Represents extra information to be passed as part of ad request
 * to get more relevant advertisement
 */
public interface AdTargeting {

    /**
     * Sets targeting gender.
     *
     * @param gender - acceptable values "f"/"m". All other values will be ignored.
     *               "f" - for female
     *               "m" - for male
     */
    void setGender(String gender);

    /**
     * Sets targeting user's year of birth
     *
     * @param year - like "1985"
     */
    void setYearOfBirth(int year);

    /**
     * A string representing a set of keywords
     * Keywords are typically used to target ad campaigns at specific user segments.
     * Keywords should be formatted as comma-separated value (e.g. "keyword1,keyword2").
     * @param keywords - keywords
     */
    void setKeywords(String keywords);

    /**
     * Allows to add custom parameters to ad request.
     *
     * @param param      - parameter name
     * @param paramValue - parameter value
     */
    void addCustomParameter(String param, String paramValue);
}
