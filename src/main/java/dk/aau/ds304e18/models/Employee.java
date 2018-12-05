package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private int projectId;

    /**
     * The task he is currently assigned.
     */
    private final List<Task> currentTask = new ArrayList<>();


    public Employee(String name, Project project) {
        this.name = name;
        this.project = project;
        if (project != null && !project.getEmployees().contains(this)) project.addNewEmployee(this);
        DatabaseManager.addEmployees(this);
    }

    public Employee(int id, String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * A function to add a new task to the currentTask list.
     *
     * @param task - The specific Task object which is to be added to the list.
     */
    public void addNewTask(Task task) {
        if (!this.currentTask.contains(task)) currentTask.add(task);
        DatabaseManager.updateEmployee(this);
    }

    /**
     * This method is used in the distributeModels method to add tasks locally and not interfere with the database.
     *
     * @param task - the task to add.
     */
    public void distributeAddTask(Task task) {
        if (!this.currentTask.contains(task)) currentTask.add(task);
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
     * @return currentTask - a list of the currentTasks that the employee is assigned to
     */
    public List<Task> getCurrentTask() {
        return currentTask;
    }

    /**
     * The setter for the id
     *
     * @param id - The unique id for the employee.
     */
    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * The setter for the project
     *
     * @param project - The project object that the employee will work on.
     */
    public void setProject(Project project) {

        this.project = project;
        DatabaseManager.updateEmployee(this);
        if (project != null && !project.getEmployees().contains(this)) project.addNewEmployee(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
