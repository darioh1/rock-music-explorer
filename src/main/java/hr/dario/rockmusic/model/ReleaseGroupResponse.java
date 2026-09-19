package hr.dario.rockmusic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReleaseGroupResponse {
    private int count;

    @JsonProperty("release-groups")
    private List<ReleaseGroup> releaseGroups;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ReleaseGroup> getReleaseGroups() {
        return releaseGroups;
    }

    public void setReleaseGroups(List<ReleaseGroup> releaseGroups) {
        this.releaseGroups = releaseGroups;
    }
}
