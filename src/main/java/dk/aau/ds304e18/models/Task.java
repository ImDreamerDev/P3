package dk.aau.ds304e18.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {

    private int id;
    private String name;
    private int estimatedTime;
    private List<Employee> employees = new ArrayList<>();
    private List<Task> dependencies = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private int priority;
    private Project project;

    public Task(String name, int estimatedTime, int priority, Project project) {
        this.name = name;
        this.estimatedTime = estimatedTime;
        this.priority = priority;
        this.project = project;
        this.startDate = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Task> getDependencies() {
        return dependencies;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getPriority() {
        return priority;
    }

    public Project getProject() {
        return project;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
