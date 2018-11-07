package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The class representing of a project
 */
public class Project {

    /**
     * The unique project ID.
     */
    private int id;

    /**
     * The name of the project.
     */
    private String name;

    private String sequence;

    /**
     * The current state of the project.
     */
    private ProjectState state;

    private ProjectManager Creator;

    /**
     * The duration of the project
     */
    private double duration;

    /**
     * The list of tasks.
     */
    private final List<Task> tasks = new ArrayList<>();

    /**
     * The list of employees.
     */
    private final List<Employee> employees = new ArrayList<>();

    /**
     * The calculated recommended path for the tasks in the project
     */
    private String recommendedPath;

    /**
     * Constructor for project.
     *
     * @param name - The name of the project.
     */
    public Project(String name, ProjectManager creator) {
        this.name = name;
        this.state = ProjectState.ONGOING;
        this.Creator = creator;
        if (creator.getCurrentProject() != null) {
            creator.addOldProject(creator.getCurrentProject());
        }
        DatabaseManager.addProject(this);
        creator.setCurrentProject(this);
    }

    public Project(int id, String name, ProjectState projectState, String sequence, double duration, String recommendedPath) {
        this.id = id;
        this.state = projectState;
        this.name = name;
        this.sequence = sequence;
        this.duration = duration;
        this.recommendedPath = recommendedPath;
    }

    /**
     * Adds new tasks to the project.
     *
     * @param tasks - The tasks to add.
     */
    public void addNewTask(Task... tasks) {

        for (Task firstTask : tasks) {
            if (!this.tasks.contains(firstTask)) this.tasks.add(firstTask);

            if (firstTask.getProject() == null || !firstTask.getProject().equals(this)) firstTask.setProject(this);
        }
    }

    /**
     * Removes task from tasks.
     *
     * @param task - Task to remove.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Assign a new employee to the project.
     *
     * @param employee - The employee to add to the project.
     */
    public void addNewEmployee(Employee... employee) {
        employees.addAll(Arrays.asList(employee));
        for (Employee emp : employee) {
            if (emp.getProjectId() != this.id)
                emp.setProject(this);
        }
    }

    /**
     * Removes employee from project.
     *
     * @param employee - The employee to remove.
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        if (employee.getProject().equals(this)) employee.setProject(null);
    }

    /**
     * Returns project unique ID.
     *
     * @return id - Project ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the project name.
     *
     * @return name - String project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current state og the project.
     *
     * @return state - The current state of the project.
     */
    public ProjectState getState() {
        return state;
    }

    /**
     * Returns the list of all tasks in the project.
     *
     * @return tasks - List of all tasks.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns all the employees assigned to the project.
     *
     * @return employees - a list of all the employees assigned to the project.
     */
    public List<Employee> getEmployees() {
        return employees;
    }

    /**
     * Sets the unique ID.
     *
     * @param id - the unique ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the current state of the project.
     *
     * @param state - the new state to set.
     */
    public void setState(ProjectState state) {
        if (this.state != state) {
            this.state = state;
            DatabaseManager.updateProject(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
        DatabaseManager.updateProject(this);
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
        DatabaseManager.updateProject(this);
    }

    public ProjectManager getCreator() {
        return Creator;
    }

    public void setCreator(ProjectManager creator) {
        Creator = creator;
    }

    public String getRecommendedPath() {
        return recommendedPath;
    }

    public void setRecommendedPath(String recommendedPath) {
        this.recommendedPath = recommendedPath;
    }
}
