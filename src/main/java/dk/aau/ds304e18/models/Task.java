package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;

import javax.xml.stream.events.StartDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The class representing a task
 */
public class Task {

    /**
     * The id of the task.
     */
    private int id;

    private int projectId;

    /**
     * The name of the task.
     */
    private final String name;

    /**
     * The estimated completion time of the task - This is used as mu in the inverse gaussian
     */
    private double estimatedTime;

    /**
     * Start time of the task
     */
    private double startTime;

    /**
     * The estimated lambda value for the inverse gaussian
     */
    private double lambda = -1;

    /**
     * The list of the employees assigned to the task
     */
    private final List<Employee> employees = new ArrayList<>();

    private final List<Integer> employeeIds = new ArrayList<>();

    /**
     * The list of tasks which the task has dependencies upon.
     */
    private final List<Task> dependencies = new ArrayList<>();

    private final List<Integer> dependencyIds = new ArrayList<>();

    private final List<Probabilities> probabilities = new ArrayList<>();

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
        DatabaseManager.addTask(this);
        this.project.addNewTask(this);
    }


    public Task(int id, String name, double estimatedTime, int priority,
                List<Integer> dependencyIds, List<Integer> employeeIds, int projectId,
                List<Probabilities> probabilities, double startTime) {
        this.name = name;
        this.id = id;
        this.estimatedTime = estimatedTime;
        this.priority = priority;
        this.dependencyIds.addAll(dependencyIds);
        this.employeeIds.addAll(employeeIds);
        this.projectId = projectId;
        this.probabilities.addAll(probabilities);
        this.startTime = startTime;
    }

    /**
     * The getter for the id.
     *
     * @return id - The unique id of the task
     */
    public int getId() {
        return id;
    }

    public int getProjectId() {
        return projectId;
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

    /**
     * The getter for the dependencies list.
     *
     * @return dependencies - The list of dependencies
     */
    public List<Task> getDependencies() {
        return dependencies;
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
    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    /**
     * The setter for the priority of the task.
     *
     * @param priority - The priority of the task.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Assign employees to the Task.
     *
     * @param employee - The employees to add to the Task.
     */
    public void addEmployee(Employee... employee) {
        for (Employee emp : employee) {
            if (!employees.contains(emp))
                employees.add(emp);
            if (emp.getProject() == null || !emp.getProject().equals(project)) emp.setProject(project);
            if (!emp.getCurrentTask().contains(this)) emp.addNewTask(this);
        }
        DatabaseManager.updateTask(this);
        if (!project.getTasks().contains(this)) project.addNewTask(this);
    }

    /**
     * Assign Dependencies to the Task
     *
     * @param tasks - The tasks to add to dependencies
     */
    public void addDependency(List<Task> tasks) {
        for (Task tsk : tasks) {
            if (tsk != this && !dependencies.contains(tsk))
                dependencies.add(tsk);
        }
        if (!project.getTasks().contains(this)) project.addNewTask(this);
        DatabaseManager.updateTask(this);
    }

    public void distributeAddDependency(Task task) {
        this.dependencies.add(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    /**
     * The hashcode for the task
     *
     * @return Hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * The getter for the list of employee ids
     *
     * @return employeeIds - A list of employee ids
     */
    public List<Integer> getEmployeeIds() {
        return employeeIds;
    }

    /**
     * The getter for the ids of the dependencies.
     *
     * @return dependencyIds - a list of ids of the tasks dependencies.
     */
    public List<Integer> getDependencyIds() {
        return dependencyIds;
    }
    
    /**
     * the getter for the list of probabilities.
     *
     * @return probabilities - a list of the probabilities.
     */
    public List<Probabilities> getProbabilities() {
        return probabilities;
    }

    /**
     * The getter for the lambda value.
     *
     * @return lambda.
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * The setter for lambda.
     *
     * @param lambda - the shape parameter.
     */
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * The getter for startTime
     * @return startTime
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * The setter for startTime
     * @param startTime - the time where a task starts
     */
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
}
