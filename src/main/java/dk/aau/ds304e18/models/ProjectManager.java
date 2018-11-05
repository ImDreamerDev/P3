package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;

import javax.xml.crypto.Data;
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
    private String name;

    /**
     * The current project that the project manager is working on
     */
    private Project currentProject;

    /**
     * The id of the project that the manager is assigned to
     */
    private int currentProjectId;

    /**
     * A list of projects that the project manager has previously worked on.
     */
    private List<Project> oldProjects = new ArrayList<>();


    private List<Integer> oldProjectsId = new ArrayList<>();

    /**
     * The constructor of the ProjectManager class.
     *
     * @param name - The name of the manager.
     */
    public ProjectManager(String name, String password) {
        this.name = name;
        DatabaseManager.addProjectManager(this, password);
    }


    public ProjectManager(int id, String name, int currentProject, List<Integer> oldProjects) {
        this.id = id;
        this.name = name;
        currentProjectId = currentProject;
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
     * @return
     */
    public Project getCurrentProject() {
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
        if (project != null) {
            if (oldProjectsId.contains(project.getId()))
                return;
            oldProjects.add(project);
            oldProjectsId.add(project.getId());
            project.setState(ProjectState.ARCHIVED);
            if (currentProject == project)
                currentProject = null;
            DatabaseManager.updateProjectManager(this);
        }
    }

    /**
     * The setter for the current project.
     *
     * @param currentProject - the project which the program manager is working on.
     */
    public void setCurrentProject(Project currentProject) {
        if (currentProject != null)
            this.currentProject = currentProject;
        DatabaseManager.updateProjectManager(this);
    }

    /**
     * The setter for the unique id.
     *
     * @param id - the unique id of the project manager.
     */
    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getOldProjectsId() {
        return oldProjectsId;
    }

    public int getCurrentProjectId() {
        return currentProjectId;
    }

    @Override
    public String toString() {
        return name;
    }
}
