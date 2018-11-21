package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class representing the Project Manager.
 * The project manager has more control than employees.
 */
public class ProjectManager {

    /**
     * The specific id that is assigned to one specific project manager only
     */
    private int id;

    /**
     * The name of the project manager
     */
    private final String name;

    /**
     * The current project that the project manager is working on
     */
    private List<Project> currentProject;

    /**
     * The id of the project that the manager is assigned to
     */
    private List<Integer> currentProjectIds;

    /**
     * A list of projects that the project manager has previously worked on.
     */
    private final List<Project> oldProjects = new ArrayList<>();

    /**
     * A list of the oldProjectId's.
     */
    private final List<Integer> oldProjectsId = new ArrayList<>();

    /**
     * The constructor of the ProjectManager class.
     *
     * @param name     - The name of the manager.
     * @param password - the password of the manager.
     */
    public ProjectManager(String name, String password) {
        this.name = name;
        DatabaseManager.addProjectManager(this, password);
    }

    /**
     * 2nd Constructor for the ProjectManager class.
     *
     * @param id                - the unique id of the project manager.
     * @param name              - the name of the project manager.
     * @param currentProjectIds - the id of the project the project manager is currently working on.
     * @param oldProjects       - A list of projects that the project manager has previously worked on.
     */
    public ProjectManager(int id, String name, List<Integer> currentProjectIds, List<Integer> oldProjects) {
        this.id = id;
        this.name = name;
        this.currentProjectIds = currentProjectIds;
        if (oldProjects != null)
            oldProjectsId.addAll(oldProjects);
    }

    /**
     * The getter for the id.
     *
     * @return id - the unique id of the program manager.
     */
    public int getId() {
        return id;
    }

    /**
     * The getter for the name of the project manager.
     *
     * @return name - the name of the project manager.
     */
    public String getName() {
        return name;
    }

    /**
     * The getter for the current project
     *
     * @return currentProject - the project that the program manager is currently working on.
     */
    public List<Project> getCurrentProjects() {
        return currentProject;
    }

    /**
     * The getter for the old projects list.
     *
     * @return oldProjects - The list of the old projects that the program manager has worked on previously.
     */
    public List<Project> getOldProjects() {
        return oldProjects;
    }

    /**
     * This function adds a project to the old projects list.
     *
     * @param project - the project to be added to the list.
     */
    public void addOldProject(Project project) {
        boolean isLoadingFromDB = false;
        if (oldProjects.contains(project)) return;

        if (project != null) {
            if (oldProjectsId.contains(project.getId())) {
                isLoadingFromDB = true;
            }


            oldProjects.add(project);

            if (!isLoadingFromDB) oldProjectsId.add(project.getId());
            project.setState(ProjectState.ARCHIVED);

            if (currentProject == project)
                currentProject = null;

            if (!isLoadingFromDB) DatabaseManager.updateProjectManager(this);
        }
    }

    /**
     * The setter for the current project.
     *
     * @param currentProject - the project which the program manager is working on.
     */
    public void addCurrentProject(Project currentProject) {
        if (currentProject != null) {
            this.currentProject.add(currentProject);
            DatabaseManager.updateProjectManager(this);
        }
    }

    public void distributeAddCurrentProject(Project project) {
        if (currentProject != null) {
            this.currentProject.add(project);
        }
    }

    /**
     * The setter for the unique id.
     *
     * @param id - the unique id of the project manager.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getter for the oldProjects id.
     *
     * @return oldProjectsId - This returns a List of the project id's.
     */
    public List<Integer> getOldProjectsId() {
        return oldProjectsId;
    }

    /**
     * The getter for the currentProjectIds.
     *
     * @return currentProjectIds - the id of the project that is currently being worked on by the manager.
     */
    public List<Integer> getCurrentProjectIds() {
        return currentProjectIds;
    }


    /**
     * To string method.
     *
     * @return name - the name of the project manager.
     */
    @Override
    public String toString() {
        return name;
    }
}
