package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.ProjectState;

import java.util.ArrayList;
import java.util.List;

/**
 * The DatabaseProject is the representation of the project in the database.
 */
public class DatabaseProject {

    /**
     * The unique id of the project.
     */
    public int id;

    /**
     * The name of the project.
     */
    public String name;

    /**
     * The state of the project.
     */
    public ProjectState state;

    /**
     * The list of ids of the tasks involved in project.
     */
    public List<Integer> tasks = new ArrayList<>();

    /**
     * The list of ids of the employees assigned to tasks in the project.
     */
    public List<Integer> employeeIds = new ArrayList<>();
}
