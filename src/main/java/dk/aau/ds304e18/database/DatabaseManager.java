package dk.aau.ds304e18.database;

import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.*;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     * Sends a query to the DB and returns the result.
     *
     * @param query The search query.
     * @return The result from the DB.
     */
    public static ResultSet query(String query) {
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            Statement st = dbConnection.createStatement();
            return st.executeQuery(query);
        } catch (PSQLException e) {
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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
        props.setProperty("password", loadPassword());
        props.setProperty("tcpKeepAlive", "true");

        try {
            dbConnection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Loads the password from a file on disk.
     *
     * @return Returns the password for the database.
     */
    private static String loadPassword() {
        try {
            return Files.readString(Paths.get("pass.txt"));
        } catch (IOException e) {
            System.err.println("Pass file not found");
            return "";
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
                    " previoustasks, projectid) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, emp.getName());

            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    emp.getPreviousTask().stream().map(Task::getId).toArray()
            ));

            if (emp.getProject() != null) statement.setInt(3, emp.getProject().getId());
            else statement.setInt(3, 0);

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
     * Removes an employee with id from the database.
     *
     * @param id id of the Employee to remove.
     */
    public static void removeEmployee(int id) {
        DatabaseManager.query("DELETE FROM employees WHERE id = " + id);
    }

    public static void removeProjectManager(int id) {
        DatabaseManager.query("DELETE FROM projectmanagers WHERE id = " + id);
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
        DatabaseManager.query("DELETE FROM tasks WHERE id = " + id);
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
     * @return Whether the operation was successful or not
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

    /**
     * Gets a task with taskId from the database or null.
     *
     * @param taskId the id of the task to get from db.
     * @return the Task with taskId or null.
     */
    public static Task getTask(int taskId) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM tasks WHERE id = ?");
            statement.setInt(1, taskId);

            ResultSet rs = statement.executeQuery();
            if (rs == null) return null;
            return Objects.requireNonNull(DatabaseParser.parseTasksFromResultSet(rs)).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a single employee with empId from the db.
     *
     * @param empId the id of the employee to get.
     * @return the employee or null.
     */
    public static Employee getEmployee(int empId) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM employees WHERE id = ?");
            statement.setInt(1, empId);
            ResultSet rs = statement.executeQuery();
            return Objects.requireNonNull(DatabaseParser.parseEmployeesFromResultSet(rs)).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a project with project Id from db.
     *
     * @param projectId the id of the project to get.
     * @return the project or null.
     */
    public static Project getProject(int projectId) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projects WHERE id = ?");
            statement.setInt(1, projectId);
            ResultSet rs = statement.executeQuery();
            return Objects.requireNonNull(DatabaseParser.parseProjectsFromResultSet(rs)).get(0);
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
            if (projectManager.getCurrentProjectId() != 0) {
                queryList.add(projectManager.getCurrentProjectId());
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
        employeeIdsToQuery.add(projectManager.getCurrentProjectId());

        try {
            if (dbConnection == null || dbConnection.isClosed() || dbConnection.isClosed()) connect();
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
        queryArray.add(projectManager.getCurrentProjectId());
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
     * Retrieves everything from the database and converts it to objects.
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
                LocalObjStorage.getEmployeeList().clear();
                LocalObjStorage.getProjectList().clear();
                LocalObjStorage.getTaskList().clear();

                List<Project> projectManagerProjects = getPMProjects(projectManager);

                //We need the employees to add them to new projects and tasks so we get them no matter what
                List<Employee> employees = getAvailableEmployees(projectManager);
                if (employees == null) return null;
                employees.forEach(LocalObjStorage::addEmployee);

                updateProgress(1, progressBarParts);

                if (projectManagerProjects != null) {
                    projectManagerProjects.forEach(LocalObjStorage::addProject);
                } else return null;

                updateProgress(2, progressBarParts);

                if (projectManager.getCurrentProjectId() != 0) {
                    Project project = LocalObjStorage.getProjectById(projectManager.getCurrentProjectId());
                    project.setCreator(projectManager);
                    if (project.getState() != ProjectState.ONGOING)
                        project.setState(ProjectState.ONGOING);
                }

                List<Integer> projectIds = new ArrayList<>(projectManager.getOldProjectsId());

                for (Integer projectId : projectIds) {
                    Project oldProject = LocalObjStorage.getProjectById(projectId);
                    oldProject.setCreator(projectManager);
                    if (oldProject.getState() != ProjectState.ARCHIVED)
                        oldProject.setState(ProjectState.ARCHIVED);

                }
                LocalObjStorage.addProjectManager(projectManager);
                updateProgress(3, progressBarParts);

                for (Employee emp : LocalObjStorage.getEmployeeList()) {
                    if (emp.getProjectId() != 0)
                        emp.setProject(LocalObjStorage.getProjectById(emp.getProjectId()));
                    if (emp.getProjectId() != 0)
                        LocalObjStorage.getProjectById(emp.getProjectId()).addNewEmployee(emp);
                }

                updateProgress(4, progressBarParts);

                if (getTasksForProjectManager(projectManager) != null)
                    LocalObjStorage.getTaskList().addAll(Objects.requireNonNull(getTasksForProjectManager(projectManager)));

                for (Task task : LocalObjStorage.getTaskList()) {
                    Project project = LocalObjStorage.getProjectById(task.getProjectId());

                    if (project != null)
                        project.addNewTask(task);

                    for (Integer employeeId : task.getEmployeeIds()) {
                        Employee emp = LocalObjStorage.getEmployeeById(employeeId);

                        if (emp != null) {
                            task.addEmployee(emp);
                            emp.distributeAddTask(task);
                        } else {
                            //TODO We don't get employees from previous projects from DB
                            task.addEmployee(new Employee(0, "John Doe", new ArrayList<>()));
                        }
                    }
                    for (Integer dependencyId : task.getDependencyIds()) {
                        task.distributeAddDependency(LocalObjStorage.getTaskById(dependencyId));
                    }
                }

                projectManager.setCurrentProject(LocalObjStorage.getProjectById(projectManager.getCurrentProjectId()));

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
                    " previoustasks = ?, projectid = ? WHERE id = ?");

            statement.setArray(1, dbConnection.createArrayOf("INTEGER",
                    employee.getPreviousTask().stream().map(Task::getId).toArray()
            ));

            if (employee.getProject() != null)
                statement.setInt(2, employee.getProject().getId());
            else
                statement.setNull(2, java.sql.Types.INTEGER);
            statement.setInt(3, employee.getId());
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
        try {
            if (dbConnection == null || dbConnection.isClosed()) connect();
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projectmanagers SET currentproject " +
                    "= ?, oldprojects = ? WHERE id = ? ");
            if (manager.getCurrentProject() != null)
                statement.setInt(1, manager.getCurrentProject().getId());
            else
                statement.setNull(1, Types.INTEGER);
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
                return new ProjectManager(-1, "Connection error", -1, null);
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
        LocalObjStorage.getProjectManager().clear();

    }
}