package dk.aau.ds304e18.models;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

import java.time.Duration;
import java.util.ArrayList;

public class Project {
    public ArrayList<Task> Tasks = new ArrayList<>();
    public ArrayList<Worker> Workers = new ArrayList<>();

    public Task AddTask(String name, Duration duration, Task... dependencies) {
        if (Tasks.stream().anyMatch(task -> task.getName().equalsIgnoreCase(name)))
            return null;
        Task task = new Task(name, duration, dependencies);
        Tasks.add(task);
        return task;
    }

    public Task GetTask(String name) {
        for (Task task : Tasks) {
            if (task.getName().equalsIgnoreCase(name)) {
                return task;
            }
        }
        System.out.println("dk.aau.ds304e18.models.Task: " + name + " not found!");
        return null;
    }

    public Worker AddWorker(String name, int workingHours) {
        Worker worker = new Worker(name, workingHours);
        Workers.add(worker);
        return worker;
    }

    public Worker GetWorker(String name) {
        for (Worker worker : Workers) {
            if (worker.getName().equalsIgnoreCase(name)) {
                return worker;
            }
        }
        System.out.println("dk.aau.ds304e18.models.Worker: " + name + " not found!");
        return null;
    }
}
