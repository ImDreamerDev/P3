package dk.aau.ds304e18.models;

import java.util.ArrayList;
import java.util.List;

/** This is the class representing the Project Manager.
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
     * The password the project manager to use while logging in.
     */
    private String password;

    /**
     * The current project that the project manager is working on
     */
    private Project currentProject;

    private int projectId;

    /**
     *  A list of projects that the project manager has previously worked on.
     */
    private List<Project> oldProjects = new ArrayList<>();

    public ProjectManager(String name, String password) {
        this.name = name;
        this.password = password;
    }

    /**
     * The getter for the id.
     * @return id - the unique id of the program manager.
     */
    public int getId() {
        return id;
    }

    /**
     * The getter for the name of the project manager.
     * @return name - the name of the project manager.
     */
    public String getName() {
        return name;
    }

    /**
     * The getter for the current project
     * @return
     */
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * The getter for the old projects list.
     * @return oldProjects - The list of the old projects that the program manager has worked on previously.
     */
    public List<Project> getOldProjects() {
        return oldProjects;
    }

    /**
     * This function adds a project to the old projects list.
     * @param project - the project to be added to the list.
     */
    public void addOldProject(Project project){
        if(project != null)oldProjects.add(project);

    }

    /**
     * The setter for the current project.
     * @param currentProject - the project which the program manager is working on.
     */
    public void setCurrentProject(Project currentProject) {
        if (currentProject != null)
        this.currentProject = currentProject;
    }


}
