package com.loopme.gdpr;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by katerina on 4/27/18.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "need_consent"
})
public class GdprResponse {
    @JsonProperty("need_consent")
    private int needConsent;

    @JsonProperty("user_consent")
    private int userConsent;

    @JsonProperty("consent_url")
    private String consentUrl;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("need_consent")
    public int getNeedConsent() {
        return needConsent;
    }

    @JsonProperty("need_consent")
    public void setNeedConsent(int needConsent) {
        this.needConsent = needConsent;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean needShowDialog() {
        return needConsent == 1;
    }

    @JsonProperty("consent_url")
    public String getConsentUrl() {
        return consentUrl;
    }

    @JsonProperty("consent_url")
    public void setConsentUrl(String consentUrl) {
        this.consentUrl = consentUrl;
    }

    @JsonProperty("user_consent")
    public int getUserConsent() {
        return userConsent;
    }

    @JsonProperty("user_consent")
    public void setUserConsent(int userConsent) {
        this.userConsent = userConsent;
    }
}
