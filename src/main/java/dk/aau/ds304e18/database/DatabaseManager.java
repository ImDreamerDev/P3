package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.*;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


public class DatabaseManager {

    /**
     * The connection to the database.
     */
    private static Connection dbConnection;

    /**
     * A variable that defines if we are running tests.
     */
    public static boolean isTests = false;

    /**
     * Connects to the database P3.
     */
    private static void connect() {
        String url;
        if (isTests)
            url = "jdbc:postgresql://molae.duckdns.org/TestP3";
        else
            url = "jdbc:postgresql://molae.duckdns.org/P3";
        Properties props = new Properties();
        props.setProperty("user", "projectplanner");
        props.setProperty("password", "");
        props.setProperty("tcpKeepAlive", "true");

        try {
            dbConnection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Adds an employee to the database and fetches the unique employee ID. and adds to employee.
     *
     * @param emp Employee to add.
     */
    public static void addEmployees(Employee emp) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO employees (name," +
                    "  projectid) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, emp.getName());

            if (emp.getProject() != null) statement.setInt(2, emp.getProject().getId());
            else statement.setInt(2, 0);

            if (statement.execute()) return;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) emp.setId(rs.getInt(1));
            LocalObjStorage.addEmployee(emp);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new project manager to the DB, if the username does not already exist in the DB.
     *
     * @param pm       the project manager to be added.
     * @param password clear text of password to add.
     */
    public static void addProjectManager(ProjectManager pm, String password) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement checkName = dbConnection.prepareStatement("SELECT id FROM projectmanagers WHERE username = ?");
            checkName.setString(1, pm.getName());
            ResultSet checkNameRs = checkName.executeQuery();
            if (checkNameRs.next()) return;


            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO projectmanagers (" +
                    "username, password, salt ) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pm.getName());
            byte[] salt = Password.getNextSalt();
            statement.setBytes(2, Password.hash(password.toCharArray(), salt));
            statement.setBytes(3, salt);

            if (statement.execute()) return;

            ResultSet rs = statement.getGeneratedKeys();

            if (rs.next()) pm.setId(rs.getInt(1));
            LocalObjStorage.addProjectManager(pm);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a task with id from the database.
     *
     * @param id id of the task to remove.
     */
    public static void removeTask(int id) {
        Task tempTask = new Task(id, "", 0d, 0,
                new ArrayList<>(), new ArrayList<>(), 0, new ArrayList<>(), -1);

        //Removes the task as dependency from other tasks
        LocalObjStorage.getTaskList().forEach(task -> {
            if (task.getDependencies().contains(tempTask)) {
                task.getDependencies().remove(tempTask);
                DatabaseManager.updateTask(task);
            }
        });
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();

            PreparedStatement statement = dbConnection.prepareStatement("DELETE FROM tasks where id = ?");
            statement.setInt(1, id);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a project to the database and fetches the unique project ID and adds to project.
     *
     * @param project the project to add.
     */
    public static void addProject(Project project) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO projects " +
                    "(name, state) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, project.getName());
            statement.setInt(2, project.getState().getValue());

            if (statement.execute()) return;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) project.setId(rs.getInt(1));
            LocalObjStorage.addProject(project);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a single task to the db.
     *
     * @param task the task to add.
     */
    public static void addTask(Task task) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO tasks (name, estimatedtime," +
                    " employees, dependencies, priority, projectid)" +
                    " values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, task.getName());
            statement.setDouble(2, task.getEstimatedTime());
            if (task.getEmployees().size() == 0)
                statement.setArray(3, null);
            else
                statement.setArray(3, dbConnection.createArrayOf("INTEGER",
                        task.getEmployees().stream().map(Employee::getId).toArray()
                ));

            statement.setArray(4, dbConnection.createArrayOf("INTEGER",
                    task.getDependencies().stream().map(Task::getId).toArray()
            ));
            statement.setInt(5, task.getPriority());


            if (task.getProject() != null)
                statement.setInt(6, task.getProject().getId());
            else
                statement.setInt(6, 0);

            if (statement.execute()) return;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) task.setId(rs.getInt(1));
            LocalObjStorage.addTask(task);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The getter for the list of project managers
     *
     * @return ProjectManagers - an list of the project managers.
     */
    public static List<ProjectManager> getAllProjectManagers() {
        try {
            if (dbConnection == null || dbConnection.isClosed())
                connect();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projectmanagers");
            ResultSet rs = statement.executeQuery();
            List<ProjectManager> projectManagers = DatabaseParser.parseProjectManagersFromResultSet(rs);
            if (projectManagers != null && projectManagers.size() != 0)
                return projectManagers;
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static List<Project> getPMProjects(ProjectManager projectManager) {

        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projects WHERE id = ANY(?)");
            List<Integer> queryList = new ArrayList<>();
            if (projectManager.getCurrentProjectIds().size() != 0) {
                queryList.addAll(projectManager.getCurrentProjectIds());
            }

            if (projectManager.getOldProjectsId() != null) {
                queryList.addAll(projectManager.getOldProjectsId());
            }
            Array queryArray = dbConnection.createArrayOf("INTEGER", queryList.toArray());
            statement.setArray(1, queryArray);
            ResultSet rs = statement.executeQuery();
            if (rs == null) return null;
            return DatabaseParser.parseProjectsFromResultSet(rs);
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
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM employees WHERE projectid IS NULL OR projectid = ANY (?) ");
            statement.setArray(1, dbConnection.createArrayOf("INTEGER", employeeIdsToQuery.toArray()));
            ResultSet rs = statement.executeQuery();
            return DatabaseParser.parseEmployeesFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Task> getTasksForProjectManager(ProjectManager projectManager) {
        List<Integer> queryArray = new ArrayList<>();
        queryArray.addAll(projectManager.getCurrentProjectIds());
        queryArray.addAll(projectManager.getOldProjectsId());

        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM tasks " +
                    "WHERE projectid = ANY (?)");
            statement.setArray(1, dbConnection.createArrayOf("INTEGER", queryArray.toArray()));
            ResultSet rs = statement.executeQuery();
            return DatabaseParser.parseTasksFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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

                // adds all projects to the localObjectStorage.
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
                // a list of the oldProjects.
                List<Integer> projectIds = new ArrayList<>(projectManager.getOldProjectsId());

                // Gets all oldProjectIds and creates oldProject objects, and sets the creator as the projectManager.
                for (Integer projectId : projectIds) {
                    Project oldProject = LocalObjStorage.getProjectById(projectId);
                    oldProject.setCreator(projectManager);
                    if (oldProject.getState() != ProjectState.ARCHIVED)
                        oldProject.setState(ProjectState.ARCHIVED);

                }
                // adds the projectManager to the localobjectstorage.
                LocalObjStorage.addProjectManager(projectManager);
                updateProgress(3, progressBarParts);

                // for each employee in the localobjectstorage assigns the employee to the project.
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

                    // for each employeeId add the employee to the corresponding who has their employeeId.
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

    /**
     * The update method for the employee.
     * Updates the information stored in the database.
     *
     * @param employee - the employee to update.
     */
    public static void updateEmployee(Employee employee) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE employees SET " +
                    " projectid = ? WHERE id = ?");
            if (employee.getProject() != null)
                statement.setInt(1, employee.getProject().getId());
            else
                statement.setNull(1, java.sql.Types.INTEGER);
            statement.setInt(2, employee.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The update method for the Task.
     * Updates the information stored in the database.
     *
     * @param task - the task to update.
     */
    public static void updateTask(Task task) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE tasks SET employees = ?" +
                    ", dependencies = ?, projectid = ?, estimatedtime = ?, priority = ?, starttime = ?," +
                    " probabilities =" + DatabaseParser.parseProbabilities(task) + " WHERE id = ?");
            statement.setArray(1, dbConnection.createArrayOf("INTEGER",
                    task.getEmployees().stream().map(Employee::getId).toArray()
            ));
            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    task.getDependencies().stream().map(Task::getId).toArray()
            ));
            statement.setInt(3, task.getProject().getId());
            statement.setDouble(4, task.getEstimatedTime());
            statement.setInt(5, task.getPriority());
            statement.setDouble(6, task.getStartTime());
            statement.setInt(7, task.getId());

            int maxRetry = 5;
            int i = 0;
            sendStatementMaxRetryTimes(statement, maxRetry, i);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO THIS SHOULD BE FIXED AND NOT EXIST. REALLY BAD FIX TO SOCKET TIMEOUT
    private static void sendStatementMaxRetryTimes(PreparedStatement statement, int maxRetry, int i) throws SQLException {
        while (i < maxRetry) {

            try {
                statement.execute();
                i = maxRetry;
            } catch (PSQLException e) {
                i++;
                connect();
            }
        }
    }

    /**
     * The method for updating the Project.
     * Updates the information stored in the database.
     *
     * @param project - the project to update.
     */
    public static void updateProject(Project project) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projects SET state = ?, sequence = ?" +
                    ", duration = ?, recommendedpath = ?, numberofemployees = ?, possiblecompletions = ? WHERE id = ?");
            statement.setInt(1, project.getState().getValue());
            statement.setString(2, project.getSequence());
            statement.setDouble(3, project.getDuration());
            statement.setString(4, project.getRecommendedPath());
            statement.setDouble(5, project.getNumberOfEmployees());
            if (project.getPossibleCompletions() != null)
                statement.setArray(6,
                        dbConnection.createArrayOf("FLOAT", project.getPossibleCompletions().toArray()));
            statement.setInt(7, project.getId());

            int maxRetry = 5;
            int i = 0;
            sendStatementMaxRetryTimes(statement, maxRetry, i);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method to update the ProjectManager.
     * Updates the information stored in the database.
     *
     * @param manager - the ProjectManager to update.
     */
    public static void updateProjectManager(ProjectManager manager) {
        List<Integer> currentProjArray = new ArrayList<>();

        manager.getCurrentProjects().forEach(project -> currentProjArray.add(project.getId()));
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projectmanagers SET currentproject " +
                    "= ?, oldprojects = ? WHERE id = ? ");
            if (manager.getCurrentProjects() != null && manager.getCurrentProjects().size() != 0)
                statement.setArray(1, dbConnection.createArrayOf("INTEGER", currentProjArray.toArray()));
            else
                statement.setNull(1, Types.ARRAY);
            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    manager.getOldProjectsId().toArray()));
            statement.setInt(3, manager.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a ProjectManger object if a user with username and password clearTextPassword is found in db, else null.
     *
     * @param username          username of user to find in DB.
     * @param clearTextPassword clear text version of the users password to find.
     * @return the ProjectManager found, else null.
     */
    public static ProjectManager logIn(String username, String clearTextPassword) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            if (dbConnection == null)
                return new ProjectManager(-1, "Connection error", null, null);
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projectmanagers WHERE LOWER(username) = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, username.toLowerCase());
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) return null;

            byte[] password = rs.getBytes(3);
            byte[] salt = rs.getBytes(6);

            if (Password.isExpectedPassword(clearTextPassword.toCharArray(), salt, password)) {
                rs.previous();
                return Objects.requireNonNull(DatabaseParser.parseProjectManagersFromResultSet(rs)).get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * Used to log out. Closes connection to database and clears local storage.
     */
    public static void logOut() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
                dbConnection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        LocalObjStorage.getTaskList().clear();
        LocalObjStorage.getProjectList().clear();
        LocalObjStorage.getTaskList().clear();
        LocalObjStorage.getProjectManagerList().clear();

    }
}