package dk.aau.ds304e18.models;

import java.util.ArrayList;
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
    private List<Task> tasks = new ArrayList<>();

    /**
     * The list of employees.
     */
    private List<Employee> employees = new ArrayList<>();

    /**
     * Constructor for project.
     * @param name the name of the project.
     */
    public Project(String name) {
        this.name = name;
        this.state = ProjectState.ONGOING;
    }

    /**
     * Adds a new task to the project.
     * @param task the task to add.
     */
    private void addNewTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes task from tasks.
     * @param task Task to remove.
     */
    private void removeTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Assign a new employee to the project.
     * @param employee the employee to add to the project.
     */
    public void addNewEmployee(Employee employee) {
        employees.add(employee);
    }

    /**
     * Removes employee from project.
     * @param employee the employee to remove.
     */
    private void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    /**
     * Returns project unique ID.
     * @return Project ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the project name.
     * @return String project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current state og the project.
     * @return the current state of the project.
     */
    public ProjectState getState() {
        return state;
    }

    /**
     * Returns the list of all tasks in the project.
     * @return list of all tasks.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns allt he employees assigned to the project.
     * @return all employees assigned to the project.
     */
    public List<Employee> getEmployees() {
        return employees;
    }

    /**
     * Sets the unique ID.
     * @param id the unique ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the current state of the project.
     * @param state the new state to set.
     */
    public void setState(ProjectState state) {
        this.state = state;
    }
}
