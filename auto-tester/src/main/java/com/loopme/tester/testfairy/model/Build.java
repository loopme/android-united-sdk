
package com.loopme.tester.testfairy.model;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "self",
        "appName",
        "appVersion",
        "appVersionCode",
        "appDisplayName",
        "sessions",
        "crashes",
        "testers",
        "feedbacks",
        "uploadedAt",
        "uploadedVia",
        "hasTestFairySdk",
        "insightsEnabled",
        "videoEnabled"
})
public class Build {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("self")
    private String self;
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("appVersion")
    private String appVersion;
    @JsonProperty("appVersionCode")
    private String appVersionCode;
    @JsonProperty("appDisplayName")
    private String appDisplayName;
    @JsonProperty("sessions")
    private Integer sessions;
    @JsonProperty("crashes")
    private Integer crashes;
    @JsonProperty("testers")
    private Integer testers;
    @JsonProperty("feedbacks")
    private Integer feedbacks;
    @JsonProperty("uploadedAt")
    private String uploadedAt;
    @JsonProperty("uploadedVia")
    private String uploadedVia;
    @JsonProperty("hasTestFairySdk")
    private Boolean hasTestFairySdk;
    @JsonProperty("insightsEnabled")
    private Boolean insightsEnabled;
    @JsonProperty("videoEnabled")
    private Boolean videoEnabled;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("self")
    public String getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(String self) {
        this.self = self;
    }

    @JsonProperty("appName")
    public String getAppName() {
        return appName;
    }

    @JsonProperty("appName")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @JsonProperty("appVersion")
    public String getAppVersion() {
        return appVersion;
    }

    @JsonProperty("appVersion")
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @JsonProperty("appVersionCode")
    public String getAppVersionCode() {
        return appVersionCode;
    }

    @JsonProperty("appVersionCode")
    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    @JsonProperty("appDisplayName")
    public String getAppDisplayName() {
        return appDisplayName;
    }

    @JsonProperty("appDisplayName")
    public void setAppDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
    }

    @JsonProperty("sessions")
    public Integer getSessions() {
        return sessions;
    }

    @JsonProperty("sessions")
    public void setSessions(Integer sessions) {
        this.sessions = sessions;
    }

    @JsonProperty("crashes")
    public Integer getCrashes() {
        return crashes;
    }

    @JsonProperty("crashes")
    public void setCrashes(Integer crashes) {
        this.crashes = crashes;
    }

    @JsonProperty("testers")
    public Integer getTesters() {
        return testers;
    }

    @JsonProperty("testers")
    public void setTesters(Integer testers) {
        this.testers = testers;
    }

    @JsonProperty("feedbacks")
    public Integer getFeedbacks() {
        return feedbacks;
    }

    @JsonProperty("feedbacks")
    public void setFeedbacks(Integer feedbacks) {
        this.feedbacks = feedbacks;
    }

    @JsonProperty("uploadedAt")
    public String getUploadedAt() {
        return uploadedAt;
    }

    @JsonProperty("uploadedAt")
    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @JsonProperty("uploadedVia")
    public String getUploadedVia() {
        return uploadedVia;
    }

    @JsonProperty("uploadedVia")
    public void setUploadedVia(String uploadedVia) {
        this.uploadedVia = uploadedVia;
    }

    @JsonProperty("hasTestFairySdk")
    public Boolean getHasTestFairySdk() {
        return hasTestFairySdk;
    }

    @JsonProperty("hasTestFairySdk")
    public void setHasTestFairySdk(Boolean hasTestFairySdk) {
        this.hasTestFairySdk = hasTestFairySdk;
    }

    @JsonProperty("insightsEnabled")
    public Boolean getInsightsEnabled() {
        return insightsEnabled;
    }

    @JsonProperty("insightsEnabled")
    public void setInsightsEnabled(Boolean insightsEnabled) {
        this.insightsEnabled = insightsEnabled;
    }

    @JsonProperty("videoEnabled")
    public Boolean getVideoEnabled() {
        return videoEnabled;
    }

    @JsonProperty("videoEnabled")
    public void setVideoEnabled(Boolean videoEnabled) {
        this.videoEnabled = videoEnabled;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isOlderThan(String currentVersionName) {
        return TextUtils.isEmpty(getAppVersion()) || getAppVersion().compareTo(currentVersionName) > 0;
    }
}
