package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.*;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.aau.ds304e18.database.DatabaseManager.connect;
import static dk.aau.ds304e18.database.DatabaseManager.getDbConnection;

public class DatabaseDistributor {

    /**
     * Retrieves everything from the database and converts it to objects, and at distributes it to the correct lists in the localObjStorgae.
     *
     * @param projectManager - the projectManager that is logged in.
     * @return null if the distributeModels is cancelled, or if the ProjectManager is null. (new javafx.concurrent.task)
     */
    public static javafx.concurrent.Task<Void> distributeModels(ProjectManager projectManager) {
        return new javafx.concurrent.Task<>() {
            @Override
            public Void call() {

                if (isCancelled()) {
                    return null;
                }

                int progressBarParts = 5;
                updateProgress(0, progressBarParts);
                // The localObjStorage is cleared to prepare for new objects.
                LocalObjStorage.getEmployeeList().clear();
                LocalObjStorage.getProjectList().clear();
                LocalObjStorage.getTaskList().clear();

                List<Project> projectManagerProjects = getPMProjects(projectManager);

                //We need all employees to add them to new projects and tasks so we get them no matter what.
                List<Employee> employees = getAvailableEmployees(projectManager);
                if (employees == null) return null;
                employees.forEach(LocalObjStorage::addEmployee);

                updateProgress(1, progressBarParts);

                // Adds all projects to the localObjectStorage.
                if (projectManagerProjects != null) {
                    projectManagerProjects.forEach(LocalObjStorage::addProject);
                } else return null;

                updateProgress(2, progressBarParts);

                // Gets all currentProjectIds and for each id creates a project object.
                if (projectManager.getCurrentProjectIds().size() != 0) {
                    projectManager.getCurrentProjectIds().forEach(projId -> {
                        Project project = LocalObjStorage.getProjectById(projId);
                        project.setCreator(projectManager);
                        if (project.getState() != ProjectState.ONGOING)
                            project.setState(ProjectState.ONGOING);
                    });

                }
                // A list of the oldProjects.
                List<Integer> projectIds = new ArrayList<>(projectManager.getOldProjectsId());

                // Gets all oldProjectIds and creates oldProject objects, and sets the creator as the projectManager.
                for (Integer projectId : projectIds) {
                    Project oldProject = LocalObjStorage.getProjectById(projectId);
                    oldProject.setCreator(projectManager);
                    if (oldProject.getState() != ProjectState.ARCHIVED)
                        oldProject.setState(ProjectState.ARCHIVED);

                }
                // Adds the projectManager to the localobjectstorage.
                LocalObjStorage.addProjectManager(projectManager);
                updateProgress(3, progressBarParts);

                // For each employee in the localobjectstorage assigns the employee to the project.
                for (Employee emp : LocalObjStorage.getEmployeeList()) {
                    if (emp.getProjectId() != 0) {
                        emp.setProject(LocalObjStorage.getProjectById(emp.getProjectId()));
                        LocalObjStorage.getProjectById(emp.getProjectId()).addNewEmployee(emp);
                    }
                }

                updateProgress(4, progressBarParts);

                if (getTasksForProjectManager(projectManager) != null)
                    LocalObjStorage.getTaskList().addAll(Objects.requireNonNull(getTasksForProjectManager(projectManager)));

                // For each Task in the localObjStorage get the project with the same id and if it exists add the task to the project.
                for (Task task : LocalObjStorage.getTaskList()) {
                    Project project = LocalObjStorage.getProjectById(task.getProjectId());

                    if (project != null)
                        project.addNewTask(task);

                    // For each employeeId add the employee to the corresponding task which has their employeeId.
                    for (Integer employeeId : task.getEmployeeIds()) {
                        Employee emp = LocalObjStorage.getEmployeeById(employeeId);

                        if (emp != null) {
                            task.addEmployee(emp);
                            emp.distributeAddTask(task);
                        } else {
                            //TODO We don't get employees from previous projects from DB
                            task.addEmployee(new Employee(0, "John Doe"));
                        }
                    }
                    // Adds all the dependencies to the tasks that has the same id as the dependency.
                    for (Integer dependencyId : task.getDependencyIds()) {
                        task.distributeAddDependency(LocalObjStorage.getTaskById(dependencyId));
                    }
                }
                //  Adds all the currentProjects to the projectManager with the corresponding id.
                for (Integer currentProjectId : projectManager.getCurrentProjectIds()) {
                    projectManager.distributeAddCurrentProject(LocalObjStorage.getProjectById(currentProjectId));
                }
                // Adds all the oldProjects to the projectManager wit the corresponding id.
                for (Integer projectId : projectManager.getOldProjectsId()) {
                    projectManager.addOldProject(LocalObjStorage.getProjectById(projectId));
                }
                updateProgress(5, progressBarParts);
                return null;
            }
        };
    }

    private static List<Task> getTasksForProjectManager(ProjectManager projectManager) {
        List<Integer> queryArray = new ArrayList<>();
        queryArray.addAll(projectManager.getCurrentProjectIds());
        queryArray.addAll(projectManager.getOldProjectsId());

        try {
            if (getDbConnection() == null || getDbConnection().isClosed()) connect();
            PreparedStatement statement = getDbConnection().prepareStatement("SELECT * FROM tasks " +
                    "WHERE projectid = ANY (?)");
            statement.setArray(1, getDbConnection().createArrayOf("INTEGER", queryArray.toArray()));
            ResultSet rs = statement.executeQuery();
            return DatabaseParser.parseTasksFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the employees with no current project or those part of his current project.
     *
     * @param projectManager the project manager to get available employees of
     * @return list of employees with no current project or are part of project managers current project.
     */
    private static List<Employee> getAvailableEmployees(ProjectManager projectManager) {
        List<Integer> employeeIdsToQuery = new ArrayList<>();

        //We want unassigned employees
        employeeIdsToQuery.add(0);
        //We want employees from the current project
        employeeIdsToQuery.addAll(projectManager.getCurrentProjectIds());

        try {
            if (getDbConnection() == null || getDbConnection().isClosed()) connect();
            PreparedStatement statement = getDbConnection().prepareStatement("SELECT * FROM employees WHERE projectid IS NULL OR projectid = ANY (?) ");
            statement.setArray(1, getDbConnection().createArrayOf("INTEGER", employeeIdsToQuery.toArray()));
            ResultSet rs = statement.executeQuery();
            return DatabaseParser.parseEmployeesFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Project> getPMProjects(ProjectManager projectManager) {
        try {
            if (getDbConnection() == null || getDbConnection().isClosed()) connect();
            PreparedStatement statement = getDbConnection().prepareStatement("SELECT * FROM projects WHERE id = ANY(?)");
            List<Integer> queryList = new ArrayList<>();
            if (projectManager.getCurrentProjectIds().size() != 0) {
                queryList.addAll(projectManager.getCurrentProjectIds());
            }

            if (projectManager.getOldProjectsId() != null) {
                queryList.addAll(projectManager.getOldProjectsId());
            }
            Array queryArray = getDbConnection().createArrayOf("INTEGER", queryList.toArray());
            statement.setArray(1, queryArray);
            ResultSet rs = statement.executeQuery();
            if (rs == null) return null;
            return DatabaseParser.parseProjectsFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
