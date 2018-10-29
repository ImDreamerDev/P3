package dk.aau.ds304e18.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The class representing a task
 */
public class Task {

    /**
     * The id of the task.
     */
    private int id;

    /**
     * The name of the task.
     */
    private final String name;

    /**
     * The estimated completion time of the task
     */
    private double estimatedTime;

    /**
     * The list of the employees assigned to the task
     */
    private final List<Employee> employees = new ArrayList<>();

    /**
     * The list of tasks which the task has dependencies upon.
     */
    private final List<Task> dependencies = new ArrayList<>();

    /**
     * The date that the task starts.
     */
    private double startTime;

    /**
     * The date the task should be completed.
     */
    private LocalDate endDate;

    /**
     * The level of priority that the task has compared to other tasks.
     */
    private int priority;

    /**
     * The project which the task is a part of.
     */
    private Project project;

    /**
     * The Constructor of the task.
     *
     * @param name          The name of the task.
     * @param estimatedTime The estimated completion time of the task.
     * @param priority      The priority value of the task.
     * @param project       The project that that the task is a part of.
     */
    public Task(String name, double estimatedTime, int priority, Project project) {
        this.name = name;
        this.estimatedTime = estimatedTime;
        this.priority = priority;
        this.project = project;
    }

    public Task(int id, String name, double estimatedTime, double startTime, LocalDate endDate, int priority) {
        this.id = id;
        this.name = name;
        this.estimatedTime = estimatedTime;
        this.startTime = startTime;
        this.endDate = endDate;
        this.priority = priority;
    }

    /**
     * The getter for the id.
     *
     * @return id - The unique id of the task
     */
    public int getId() {
        return id;
    }

    /**
     * The getter for the name.
     *
     * @return name - The name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * The getter for the estimated completion time
     *
     * @return estimatedTime - The estimated completion time.
     */
    public double getEstimatedTime() {
        return estimatedTime;
    }

    /**
     * The getter for the Employees list.
     *
     * @return employees - The list of employees.
     */
    public List<Employee> getEmployees() {
        return employees;
    }

    public void addEmployee(Employee emp) {
        this.employees.add(emp);
    }

    /**
     * The getter for the depencies list.
     *
     * @return dependencies - The list of dependencies
     */
    public List<Task> getDependencies() {
        return dependencies;
    }

    public void addDependency(Task task) {
        this.dependencies.add(task);
    }

    /**
     * The getter for the start date
     *
     * @return startTime - The date the task was started.
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * The getter for the end date of the task.
     *
     * @return endDate - The date at which the task should be completed.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * The getter for the priority
     *
     * @return priority - The priority of the task
     */
    public int getPriority() {
        return priority;
    }

    /**
     * The getter for the project.
     *
     * @return project - The project which the task is assigned to.
     */
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * The setter for the id
     *
     * @param id - The unique id for the task
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * the setter for the Estimated time
     *
     * @param estimatedTime - The amount of time that it takes for the task to be completed.
     */
    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    /**
     * The setter for the end date.
     *
     * @param endDate - The date at which the task should be completed
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * The setter for the priority of the task.
     *
     * @param priority - The priority of the task.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
