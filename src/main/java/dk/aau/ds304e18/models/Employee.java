package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseEmployee;
import dk.aau.ds304e18.database.DatabaseManager;

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
    private final String name;

    /**
     * The project he is currently assigned.
     */
    private Project project;

    /**
     * The task he is currently assigned.
     */
    private final List<Task> currentTask = new ArrayList<>();

    /**
     * The tasks that the employee has previously worked on.
     */
    private final List<Task> previousTask = new ArrayList<>();

    /**
     * The constructor for the employee class
     *
     * @param name - The name of the employee.
     */
    public Employee(String name) {
        this.name = name;
        DatabaseManager.addEmployees(this);
    }

    public Employee(DatabaseEmployee databaseEmployee) {
        name = databaseEmployee.name;
        id = databaseEmployee.id;
    }

    /**
     * A function to add a new task to the currentTask list.
     *
     * @param task - The specific Task object which is to be added to the list.
     */
    public void addNewTask(Task task) {
        currentTask.add(task);
        DatabaseManager.updateEmployee(this);
    }

    /**
     * The getter for the id
     *
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
     *
     * @return project - The project that the employee is assigned.
     */
    public Project getProject() {
        return project;
    }

    /**
     * the getter for the currentTask
     *
     * @return currenTask - a list of the currentTasks that the employee is assigned to
     */
    public List<Task> getCurrentTask() {
        return currentTask;
    }

    /**
     * The getter for prevousTask.
     *
     * @return previousTask - a list of the previous tasks that the employee is no longer working on.
     */
    public List<Task> getPreviousTask() {
        return previousTask;
    }

    public void addPreviousTask(Task task) {
        this.previousTask.add(task);
        DatabaseManager.updateEmployee(this);
    }

    /**
     * The setter for the id
     *
     * @param id - The unique id for the employee.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The setter for the project
     *
     * @param project - The project object that the employee will work on.
     */
    public void setProject(Project project) {
        if (project != null) {
            this.project = project;
            DatabaseManager.updateEmployee(this);
        }
    }

}
