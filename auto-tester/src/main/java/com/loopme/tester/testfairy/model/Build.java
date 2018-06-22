
package com.loopme.tester.testfairy.model;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;
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
        "originalFilename",
        "fileSize",
        "uploadDate",
        "comment",
        "version",
        "sessions",
        "crashes",
        "activities"
})
public class Build {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("self")
    private String self;
    @JsonProperty("appName")
    private String appName;
    @JsonProperty("originalFilename")
    private String originalFilename;
    @JsonProperty("fileSize")
    private Integer fileSize;
    @JsonProperty("uploadDate")
    private String uploadDate;
    @JsonProperty("comment")
    private Object comment;
    @JsonProperty("version")
    private String version;
    @JsonProperty("sessions")
    private Integer sessions;
    @JsonProperty("crashes")
    private Integer crashes;
    @JsonProperty("activities")
    private List<String> activities = null;
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

    @JsonProperty("originalFilename")
    public String getOriginalFilename() {
        return originalFilename;
    }

    @JsonProperty("originalFilename")
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @JsonProperty("fileSize")
    public Integer getFileSize() {
        return fileSize;
    }

    @JsonProperty("fileSize")
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    @JsonProperty("uploadDate")
    public String getUploadDate() {
        return uploadDate;
    }

    @JsonProperty("uploadDate")
    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @JsonProperty("comment")
    public Object getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(Object comment) {
        this.comment = comment;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
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

    @JsonProperty("activities")
    public List<String> getActivities() {
        return activities;
    }

    @JsonProperty("activities")
    public void setActivities(List<String> activities) {
        this.activities = activities;
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
        return !TextUtils.isEmpty(getVersion()) && getVersion().compareTo(currentVersionName) > 0;
    }
}
