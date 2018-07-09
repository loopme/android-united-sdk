package com.loopme.gdpr;

/**
 * Created by katerina on 4/27/18.
 */

public class GdprResponse {
    private int needConsent;

    private int userConsent;

    private String consentUrl;


    public int getNeedConsent() {
        return needConsent;
    }

    public void setNeedConsent(int needConsent) {
        this.needConsent = needConsent;
    }

    public boolean needShowDialog() {
        return needConsent == 1;
    }

    public String getConsentUrl() {
        return consentUrl;
    }

    public void setConsentUrl(String consentUrl) {
        this.consentUrl = consentUrl;
    }

    public int getUserConsent() {
        return userConsent;
    }

    public void setUserConsent(int userConsent) {
        this.userConsent = userConsent;
    }
}
