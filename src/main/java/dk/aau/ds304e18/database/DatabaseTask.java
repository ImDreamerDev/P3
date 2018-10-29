package dk.aau.ds304e18.database;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTask {

    /**
     * The id of the task.
     */
    public int id;

    /**
     * The name of the task.
     */
    public String name;

    /**
     * The estimated completion of the task.
     */
    public double estimatedTime;

    /**
     * The list of ids of employees assigned to the task.
     */
    public List<Integer> employeeIds = new ArrayList<>();

    /**
     * The list of ids of dependencies the task has.
     */
    public  List<Integer> dependenceIds = new ArrayList<>();

    /**
     * The starting date of the task.
     */
    public double startTime;

    /**
     * The estimated completion date of the task.
     */
    public double endTime;

    /**
     * The priority of the task.
     */
    public int priority;

    /**
     * The id of the project the task is a part of.
     */
    public int projectId;
}
