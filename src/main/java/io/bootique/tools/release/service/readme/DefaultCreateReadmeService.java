package io.bootique.tools.release.service.readme;

import io.bootique.tools.release.model.persistent.Issue;
import io.bootique.tools.release.model.persistent.Milestone;
import io.bootique.tools.release.model.persistent.Repository;
import io.bootique.tools.release.service.github.GitHubApi;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

public class DefaultCreateReadmeService implements CreateReadmeService {

    @Inject
    @Named("updateCache")
    private GitHubApi gitHubApi;

    @Override
    public StringBuilder createReadme(List<Repository> repositories, String milestoneTitle) {
        StringBuilder readme = new StringBuilder();
        for (Repository repository : repositories) {
            for (Milestone milestone : repository.getMilestones()) {
                if (milestone.getTitle().equals(milestoneTitle)) {
                    if (milestone.getIssues() != null && !milestone.getIssues().isEmpty()) {
                        readme.append(repository.getName()).append("\n").append("\n");
                        for (Issue issue : milestone.getIssues()) {
                            String title = issue.getTitle().replaceAll("^\\s+", "");
                            readme.append("* #").append(issue.getNumber()).append(" ").append(title).append("\n");
                        }
                        readme.append("\n");
                    }
                }
            }
        }
        return readme;
    }
}
