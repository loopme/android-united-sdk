
package com.loopme.tester.testfairy.model;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.loopme.tester.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "builds"
})
public class TestFairyResponse {

    @JsonProperty("status")
    private String status;
    @JsonProperty("builds")
    private List<Build> builds = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("builds")
    public List<Build> getBuilds() {
        return builds;
    }

    @JsonProperty("builds")
    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public boolean isNeedUpdate() {
        if (builds == null) {
            return false;
        }
        leaveOnlyReleases();

        for (Build build : builds) {
            if (build.isOlderThan(BuildConfig.VERSION_NAME)) {
                return true;
            }
        }
        return false;
    }

    private void leaveOnlyReleases() {
        List<Build> notReleasedList = new ArrayList<>();
        for (Build build : builds) {
            if (TextUtils.isEmpty(build.getUploadedVia())) {
                notReleasedList.add(build);
            }
        }
        builds.removeAll(notReleasedList);
    }
}
