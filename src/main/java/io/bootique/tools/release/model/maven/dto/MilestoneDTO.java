package io.bootique.tools.release.model.maven.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.bootique.tools.release.model.persistent.Milestone;
import io.bootique.tools.release.model.persistent.OpenIssue;

import java.util.ArrayList;
import java.util.List;

public class MilestoneDTO {

    @JsonProperty("url")
    private String url;

    @JsonProperty("title")
    private String title;

    @JsonProperty("issues")
    private IssueDTO issues;

    @JsonProperty("state")
    private String state;

    public MilestoneDTO() { }

    private void init(Milestone milestone) {
        this.url = milestone.getUrl();
        this.title = milestone.getTitle();
        this.issues = new IssueDTO(milestone.getOpenIssues().size());
        this.state = milestone.getState();
    }

    private void convertFromDTO(Milestone milestone) {
        milestone.setUrl(this.url);
        milestone.setTitle(this.title);
        List<OpenIssue> issueList = new ArrayList<>();
        for (int i = 0; i < issues.getLength(); i++) {
            issueList.add(new OpenIssue());
        }
        milestone.writePropertyDirectly(Milestone.OPEN_ISSUES.getName(), issueList);
        milestone.setState(this.state);
    }

    public static MilestoneDTO fromModel(Milestone milestone){
        MilestoneDTO milestoneDTO = new MilestoneDTO();
        milestoneDTO.init(milestone);
        return milestoneDTO;
    }

    public static Milestone toModel(MilestoneDTO milestoneDTO) {
        Milestone milestone = new Milestone();
        milestoneDTO.convertFromDTO(milestone);
        return milestone;
    }
}
