package io.bootique.tools.release.controller;

import io.agrest.DataResponse;
import io.bootique.tools.release.model.maven.persistent.Project;
import io.bootique.tools.release.model.release.ReleaseDescriptor;
import io.bootique.tools.release.model.release.ReleaseStage;
import io.bootique.tools.release.service.logger.LoggerService;
import io.bootique.tools.release.service.release.ReleaseService;
import io.bootique.tools.release.view.ReleaseContinueView;
import io.bootique.tools.release.view.ReleaseView;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/release")
public class ReleaseController extends BaseController {

    @Inject
    private LoggerService loggerService;

    @Inject
    private ReleaseService releaseService;

    @GET
    public Response home() {
        if (releaseService.hasCurrentActiveRelease()) {
            loggerService.prepareLogger(releaseService.getReleaseDescriptor());
            if (releaseService.getReleaseDescriptor().isAutoReleaseMode()) {
                releaseService.createThreadForRelease();
            }
            return Response.seeOther(URI.create("release/continue-release")).build();
        }

        return Response.seeOther(URI.create("release/start-release")).build();
    }

    @GET
    @Path("/continue-release")
    public ReleaseContinueView continueRelease() {
        ReleaseDescriptor releaseDescriptor = releaseService.getReleaseDescriptor();
        String lastSuccessStage = releaseDescriptor.getCurrentReleaseStage() != ReleaseStage.NO_RELEASE ?
                releaseDescriptor.getLastSuccessReleaseStage().getText() :
                releaseDescriptor.getCurrentRollbackStage().getText();

        return new ReleaseContinueView(
                getCurrentUser(),
                getCurrentOrganization(),
                releaseDescriptor.getReleaseVersion(),
                lastSuccessStage,
                releaseDescriptor.getProjectList()
        );
    }

    @GET
    @Path("/start-release")
    public ReleaseView startRelease() {
        return new ReleaseView(getCurrentUser(), getCurrentOrganization());
    }

    @GET
    @Path("/show-all")
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<Project> showAll() {
        return fetchProjects("[\"repository\",\"rootModule\",\"dependencies.repository\"]");
    }

    @GET
    @Path("show-projects")
    @Produces(MediaType.APPLICATION_JSON)
    public DataResponse<Project> showProjects(@QueryParam("version") String version) {
        DataResponse<Project> projectDataResponse = fetchProjects("[\"repository\",\"rootModule\"]");

        projectDataResponse.getObjects().forEach(project -> {
            project.setDisable(true);
            if (project.getVersion().equals(version)) {
                project.setDisable(false);
                checkDependencies(project);
            }
        });

        return projectDataResponse;
    }

    @GET
    @Path("select-projects")
    @Consumes(MediaType.APPLICATION_JSON)
    public DataResponse<Project> selectProjects(@QueryParam("version") String version, @QueryParam("projects") final String selected,
                                                @QueryParam("selectedProject") String selectedProject,
                                                @QueryParam("state") boolean state) throws IOException {
        @SuppressWarnings("unchecked")
        List<String> selectedProjects = objectMapper.readValue(selected, List.class);
        DataResponse<Project> allProjects = fetchProjects("[\"repository\",\"rootModule\",\"rootModule.dependencies\"]");
        List<Project> selectedProjectsResp = allProjects.getObjects().stream()
                .filter(project -> selectedProjects.contains(project.getRepository().getName()))
                .collect(Collectors.toList());
        Optional<Project> haveProject = allProjects.getObjects().stream()
                .filter(p -> selectedProject.equals(p.getRepository().getName()))
                .findFirst();
        haveProject.ifPresent(project -> buildOrder(selectedProjectsResp, state, project, allProjects.getObjects()));

        filter(allProjects, selectedProjectsResp);
        return allProjects;
    }

    private void buildOrder(List<Project> selectedProjectsResp, boolean state, Project currentProject, List<Project> allProjects) {
        allProjects.forEach(project -> {
            if (state && !selectedProjectsResp.contains(project)) {
                currentProject.getDependencies().forEach(dependency -> {
                    if (dependency.getRootModule().equals(project.getRootModule())
                            && dependency.getVersion().equals(currentProject.getVersion())) {
                        selectedProjectsResp.add(project);
                        buildOrder(selectedProjectsResp, true, project, allProjects);
                    }
                });
            } else if (!state && selectedProjectsResp.contains(project)) {
                project.getDependencies().forEach(dependency -> {
                    if (dependency.getRootModule().equals(currentProject.getRootModule())
                            && dependency.getVersion().equals(project.getVersion())) {
                        selectedProjectsResp.remove(project);
                        buildOrder(selectedProjectsResp, false, project, allProjects);
                    }
                });
            }
        });
    }

    private void filter(DataResponse<Project> allProjects, List<Project> selectedProjectsResp) {
        DataResponse<Project> dataResponse = fetchProjects();

        int flag = 0;
        for (Project selectedProjectResp : dataResponse.getObjects()) {
            for (Project project : selectedProjectsResp) {
                if (selectedProjectResp.compareTo(project) == 0) {
                    flag++;
                }
            }
            if (flag == 0) {
                allProjects.getObjects().remove(selectedProjectResp);
            }
            flag = 0;
        }
    }

    private void checkDependencies(Project project) {
        project.getDependencies().forEach(projectDependency -> {
            if (projectDependency.isDisable()) {
                checkDependencies(projectDependency);
                projectDependency.setDisable(false);
            }
        });
    }
}
