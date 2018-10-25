package dk.aau.ds304e18.database;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing the employee in the database.
 */
public class DatabaseEmployee {
    /**
     * the Unique id of the employee.
     */
    public int id;

    /**
     * The name of the employee.
     */
    public String name;
    public List<Integer> currentTaskId = new ArrayList<>();

    /**
     * The list of ids of tasks.
     */
    public List<Integer> taskId = new ArrayList<>();

    /**
     * The list of ids of previous tasks.
     */
    public List<Integer> preTaskId = new ArrayList<>();

    /**
     * The id of the project
     */
    public int projectId;

    public DatabaseEmployee() {
    }
}
