package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;
import java.util.List;

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
     * The list of tasks that are a part of the project.
     */
    public List<Task> tasks = new ArrayList<>();

    /**
     * The list of ids of the employees assigned to tasks in the project.
     */
    public List<Integer> employeeIds = new ArrayList<>();
}
