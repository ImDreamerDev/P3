package dk.aau.ds304e18.models;

import java.util.ArrayList;
import java.util.List;

public class DatabaseProject {
    public int id;
    public String name;
    public ProjectState state;
    public List<Task> tasks = new ArrayList<>();
    public List<Integer> employeeIds = new ArrayList<>();
}
