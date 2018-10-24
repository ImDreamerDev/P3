package dk.aau.ds304e18.models;

import java.util.ArrayList;
import java.util.List;

public class Project {

    private int id;
    private String name;
    private ProjectState state;
    private List<Task> tasks = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();

    public Project(String name) {
        this.name = name;
        this.state = ProjectState.ONGOING;
    }

    private void addNewTask(Task task) {
        tasks.add(task);
    }

    private void removeTask(Task task) {
        tasks.remove(task);
    }

    private void addNewEmployee(Employee employee) {
        employees.add(employee);
    }

    private void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectState getState() {
        return state;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setState(ProjectState state) {
        this.state = state;
    }
}
