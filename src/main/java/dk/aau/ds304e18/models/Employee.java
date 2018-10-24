package dk.aau.ds304e18.models;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    private int id;
    private String name;
    private Project project;
    private List<Task> currentTask = new ArrayList<>();
    private List<Task> previousTask = new ArrayList<>();

    public Employee(String name) {
        this.name = name;
    }

    private void addNewTask(Task task) {
        currentTask.add(task);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Project getProject() {
        return project;
    }

    public List<Task> getCurrentTask() {
        return currentTask;
    }

    public List<Task> getPreviousTask() {
        return previousTask;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setCurrentTask(List<Task> currentTask) {
        this.currentTask = currentTask;
    }

    public void setPreviousTask(List<Task> previousTask) {
        this.previousTask = previousTask;
    }
}
