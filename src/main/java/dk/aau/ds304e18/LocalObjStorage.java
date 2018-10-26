package dk.aau.ds304e18;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.ArrayList;

/**
 * Contains all the local lists of employees, projects and tasks.
 */
public class LocalObjStorage {

    /**
     * The list of employees.
     */
    private static ArrayList<Employee> employeeList = new ArrayList<>();


    /**
     * The list of projects.
     */
    private static ArrayList<Project> projectList = new ArrayList<>();


    /**
     * The list of tasks.
     */
    private static ArrayList<Task> taskList = new ArrayList<>();

    /**
     * Gets list of employees.
     * @return the list of employees.
     */
    public static ArrayList<Employee> getEmployeeList() {
        return employeeList;
    }

    /**
     * Adds specific employee to employee list.
     * @param emp employee to add.
     */
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

    /**
     * Sets the employeeList to the new employeeList.
     * @param employeeList the new list of employees to set.
     */
    public static void setEmployeeList(ArrayList<Employee> employeeList) {
        LocalObjStorage.employeeList = employeeList;
    }

    /**
     * Gets list of projects.
     * @return the list of projects.
     */
    public static ArrayList<Project> getProjectList() {
        return projectList;
    }

    /**
     * Adds a project to projectList.
     * @param project the project to add.
     */
    public static void addProject(Project project) {
        LocalObjStorage.projectList.add(project);
    }

    /**
     * Gets a project by id.
     * @param id the project id to find.
     * @return the project with id or null if not found.
     */
    public static Project getProjectById(int id) {
        return LocalObjStorage.projectList.stream().filter(project -> project.getId() == id)
                .findFirst().orElse(null);
    }

    /**
     * Sets the current projectList to the projectList passed as an argument.
     * @param projectList the new projectlist to set.
     */
    public static void setProjectList(ArrayList<Project> projectList) {
        LocalObjStorage.projectList = projectList;
    }

    /**
     * Gets the list of tasks.
     * @return the list of tasks.
     */
    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    /**
     * Adds a task to the taskList.
     * @param task the task to add.
     */
    public static void addTask(Task task) {
        LocalObjStorage.taskList.add(task);
    }

    /**
     * Returns the project with id, or null if not found.
     * @param id the task id to find.
     * @return a task with id or null if not found.
     */
    public static Task getTaskById(int id) {
        return LocalObjStorage.taskList.stream().filter(task -> task.getId() == id)
                .findFirst().orElse(null);
    }

    /**
     * Sets the current taskList to the passed taskList.
     * @param taskList the new task list to set.
     */
    public static void setTaskList(ArrayList<Task> taskList) {
        LocalObjStorage.taskList = taskList;
    }
}
