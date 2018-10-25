package dk.aau.ds304e18.models;

import java.util.ArrayList;
import java.util.List;

public class DatabaseEmployee {
    public int id;
    public String name;
    public List<Integer> taskId = new ArrayList<>();
    public List<Integer> preTaskId = new ArrayList<>();
    public int projectId;

}
