package dk.aau.ds304e18;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;

public class HitlerSSListManager {
    private static ArrayList<Employee> employeeList;
    private static ArrayList<Project> projectList;
    private static ArrayList<Task> taskList;

    public static ArrayList<Employee> getEmployeeList() {
        return employeeList;
    }

    public static void addEmployee(Employee emp) {
        HitlerSSListManager.employeeList.add(emp);
    }

    public static void setEmployeeList(ArrayList<Employee> employeeList) {
        HitlerSSListManager.employeeList = employeeList;
    }

    public static ArrayList<Project> getProjectList() {
        return projectList;
    }

    public static void addProject(Project project) {
        HitlerSSListManager.projectList.add(project);
    }

    public static void setProjectList(ArrayList<Project> projectList) {
        HitlerSSListManager.projectList = projectList;
    }

    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    public static void addTask(Task task) {
        HitlerSSListManager.taskList.add(task);
    }

    public static void setTaskList(ArrayList<Task> taskList) {
        HitlerSSListManager.taskList = taskList;
    }
}
