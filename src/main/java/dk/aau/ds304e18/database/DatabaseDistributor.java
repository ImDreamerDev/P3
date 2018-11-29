package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

                List<Project> projectManagerProjects = DatabaseManager.getPMProjects(projectManager);

                //We need all employees to add them to new projects and tasks so we get them no matter what.
                List<Employee> employees = DatabaseManager.getAvailableEmployees(projectManager);
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

                if (DatabaseManager.getTasksForProjectManager(projectManager) != null)
                    LocalObjStorage.getTaskList().addAll(Objects.requireNonNull(DatabaseManager.getTasksForProjectManager(projectManager)));

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
                            //Note: Old projects can't have employees.
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
                    projectManager.archiveProject(LocalObjStorage.getProjectById(projectId));
                }
                updateProgress(5, progressBarParts);
                return null;
            }
        };
    }
}
