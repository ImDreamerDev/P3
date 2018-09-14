package dk.aau.ds304e18.models;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Task {
    private String Name;
    private LocalDate DueDate;
    private java.time.Duration Duration;
    private LocalDate StartDate;
    private int Priority;
    private boolean Delayed;
    private boolean Complete;
    private Worker Assignee;


    private ArrayList<Task> Dependencies;

    Task(String name, Duration duration, Task... dependencies) {
        Name = name;
        Duration = duration;
        DueDate = LocalDate.now().plusDays(Duration.toDays());
        Dependencies = new ArrayList<>();
        Dependencies.addAll(Arrays.stream(dependencies).collect(Collectors.toList()));
    }


    public void Assign(Worker worker, int priority) {
        if (!DependenciesComplete()) {
            System.out.println("Dependencies not complete");
            return;
        }

        Assignee = worker;
        Priority = priority;
        worker.CurrentTask = this;
        StartDate = LocalDate.now();
    }


    public void Assign(Worker worker) {
        if (!DependenciesComplete()) {
            System.out.print("Dependencies not complete: ");
            Dependencies.forEach(task -> System.out.print(task.Name + " "));
            return;
        }
        Assignee = worker;
        worker.CurrentTask = this;
        StartDate = LocalDate.now();
    }

    public boolean DependenciesComplete() {
        if (Dependencies.size() == 0)
            return true;
        return Dependencies.stream().allMatch(task -> task.Complete);
    }

    //region Getter and Setter
    public Worker getAssignee() {
        return Assignee;
    }

    public LocalDate getStartDate() {
        return StartDate;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public LocalDate getDueDate() {
        return DueDate;
    }

    public void setComplete(boolean complete) {
        Complete = complete;
    }

    public boolean isDelayed() {
        return Delayed;
    }

    public void setDelayed(boolean delayed) {
        Delayed = delayed;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<Task> getDependencies() {
        return Dependencies;
    }

    public Duration getDuration() {
        return Duration;
    }

    //endregion
}
