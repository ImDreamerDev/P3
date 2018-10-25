package dk.aau.ds304e18;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;

public class LocalObjStorage {
    private static ArrayList<Employee> employeeList;
    private static ArrayList<Project> projectList;
    private static ArrayList<Task> taskList;

    public static ArrayList<Employee> getEmployeeList() {
        return employeeList;
    }

    public static void addEmployee(Employee emp) {
        LocalObjStorage.employeeList.add(emp);
    }

    /**
     * Find an employee with ID in employeeList.
     * @param id the employee ID to find
     * @return the employee with ID or else null.
     */
    public static Employee getEmployeeById(int id) {
        return LocalObjStorage.employeeList.stream().filter(emp -> emp.getId() == id)
                .findFirst().orElse(null);
    }

    public static void setEmployeeList(ArrayList<Employee> employeeList) {
        LocalObjStorage.employeeList = employeeList;
    }

    public static ArrayList<Project> getProjectList() {
        return projectList;
    }

    public static void addProject(Project project) {
        LocalObjStorage.projectList.add(project);
    }

    public static Project getProjectById(int id) {
        return LocalObjStorage.projectList.stream().filter(project -> project.getId() == id)
                .findFirst().orElse(null);
    }

    public static void setProjectList(ArrayList<Project> projectList) {
        LocalObjStorage.projectList = projectList;
    }

    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    public static void addTask(Task task) {
        LocalObjStorage.taskList.add(task);
    }

    public static Task getTaskById(int id) {
        return LocalObjStorage.taskList.stream().filter(task -> task.getId() == id)
                .findFirst().orElse(null);
    }

    public static void setTaskList(ArrayList<Task> taskList) {
        LocalObjStorage.taskList = taskList;
    }
}
