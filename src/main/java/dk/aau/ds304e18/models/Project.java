package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.database.DatabaseProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /**
     * The current state of the project.
     */
    private ProjectState state;

    /**
     * The list of tasks.
     */
    private final List<Task> tasks = new ArrayList<>();

    /**
     * The list of employees.
     */
    private final List<Employee> employees = new ArrayList<>();

    /**
     * Constructor for project.
     *
     * @param name - The name of the project.
     */
    public Project(String name) {
        this.name = name;
        this.state = ProjectState.ONGOING;
        DatabaseManager.addProject(this);
    }

    /**
     * Constructor for a project using data from database.
     * @param databaseProject - Project object from database.
     */
    public Project(DatabaseProject databaseProject) {
        id = databaseProject.id;
        state = databaseProject.state;
        name = databaseProject.name;
    }

    /**
     * Adds a new task to the project.
     *
     * @param task - The task to add.
     */
    public void addNewTask(Task... task) {
        tasks.addAll(Arrays.asList(task));
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
        for (Employee emp : employee) {
            emp.setProject(this);
        }
        employees.addAll(Arrays.asList(employee));
    }

    /**
     * Removes employee from project.
     *
     * @param employee - The employee to remove.
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
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
        this.state = state;
    }

}
