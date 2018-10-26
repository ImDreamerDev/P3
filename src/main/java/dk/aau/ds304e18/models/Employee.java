package dk.aau.ds304e18.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing the Employee
 */
public class Employee {

    /**
     * The id given by the database.
     */
    private int id;

    /**
     * The name of the Employee
     */
    private String name;

    /**
     * The project he is currently assigned.
     */
    private Project project;

    /**
     * The task he is currently assigned.
     */
    private List<Task> currentTask = new ArrayList<>();

    /**
     * The tasks that the employee has previously worked on.
     */
    private List<Task> previousTask = new ArrayList<>();

    /**
     * The constructor for the employee class
     * @param name - The name of the employee.
     */
    public Employee(String name) {
        this.name = name;
    }

    /**
     * A function to add a new task to the currentTask list.
     * @param task - The specific Task object which is to be added to the list.
     */
    public void addNewTask(Task task) {
        currentTask.add(task);
    }

    public void setCurrentTask(List<Task> currentTask) {
        this.currentTask = currentTask;
    }

    /**
     * The getter for the id
     * @return id - The id of the employee.
     */
    public int getId() {
        return id;
    }

    /**
     * The getter for the name.
     *
     * @return name - The name of the Employee
     */
    public String getName() {
        return name;
    }

    /**
     * The getter for the project
     * @return project - The project that the employee is assigned.
     */
    public Project getProject() {
        return project;
    }

    /**
     * the getter for the currentTask
     * @return currenTask - a list of the currentTasks that the employee is assigned to
     */
    public List<Task> getCurrentTask() {
        return currentTask;
    }

    /**
     * The getter for prevousTask.
     * @return previousTask - a list of the previous tasks that the employee is no longer working on.
     */
    public List<Task> getPreviousTask() {
        return previousTask;
    }

    public void addPreviousTask(Task task) {
        this.previousTask.add(task);
    }

    /**
     * The setter for the id
     * @param id - The unique id for the employee.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The setter for the project
     * @param project - The project object that the employee will work on.
     */
    public void setProject(Project project) {
        this.project = project;
    }

}
