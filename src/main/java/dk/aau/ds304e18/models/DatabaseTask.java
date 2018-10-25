package dk.aau.ds304e18.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTask {
    public int id;
    public String name;
    public int estimatedTime;
    public List<Integer> employeeIds = new ArrayList<>();
    public List<Integer> dependencieIds = new ArrayList<>();
    public LocalDate startDate;
    public LocalDate endDate;
    public int priority;
    public int projectId;
}
