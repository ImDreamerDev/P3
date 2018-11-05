package dk.aau.ds304e18.database;

import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.*;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

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
        if (dbConnection == null) connect();
        try {
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
        try {
            dbConnection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
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
     * @return bool to indicate whether the operation was successful.
     */
    public static boolean addEmployees(Employee emp) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO employees (name," +
                    " previoustasks, projectid) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, emp.getName());

            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    emp.getPreviousTask().stream().map(Task::getId).toArray()
            ));

            if (emp.getProject() != null) statement.setInt(3, emp.getProject().getId());
            else statement.setInt(3, 0);

            if (statement.execute()) return false;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) emp.setId(rs.getInt(1));
            LocalObjStorage.addEmployee(emp);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean addProjectManager(ProjectManager pm, String password) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO projectmanagers (" +
                    "username,password,currentproject,oldprojects ) values (?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pm.getName());
            statement.setString(2, password);
            if (pm.getCurrentProject() != null)
                statement.setInt(3, pm.getCurrentProject().getId());

            if (pm.getOldProjects() != null)
                statement.setArray(4, dbConnection.createArrayOf("INTEGER",
                        pm.getOldProjects().stream().map(Project::getId).toArray()));
            else statement.setInt(4, 0);
            if (statement.execute()) return false;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) pm.setId(rs.getInt(1));
            LocalObjStorage.addProjectManager(pm);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
        DatabaseManager.query("DELETE FROM tasks WHERE id = " + id);
    }

    /**
     * Adds a project to the database and fetches the unique project ID and adds to project.
     *
     * @param project the project to add.
     * @return bool to indicate whether the operation was successful.
     */
    public static Boolean addProject(Project project) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO projects " +
                    "(name, state, sequence) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, project.getName());
            statement.setInt(2, project.getState().getValue());
            statement.setString(3, "");

            if (statement.execute()) return false;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) project.setId(rs.getInt(1));
            LocalObjStorage.addProject(project);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Adds a single task to the db.
     *
     * @param task the task to add.
     * @return Whether the operation was successful or not
     */
    public static boolean addTask(Task task) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO tasks (name, estimatedtime," +
                    " employees, dependencies, startdate, enddate, priority, projectid) values (?, ?, ?, ?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
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
            statement.setDouble(5, (task.getStartTime()));
            statement.setDouble(6, (task.getEndTime()));
            statement.setInt(7, task.getPriority());

            if (task.getProject() != null) statement.setInt(8, task.getProject().getId());
            else statement.setInt(8, 0);

            if (statement.execute()) return false;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) task.setId(rs.getInt(1));
            LocalObjStorage.addTask(task);


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static List<ProjectManager> parseProjectManagerFromResultSet(ResultSet rs) {
        List<ProjectManager> projectManagers = new ArrayList<>();
        try {
            if (rs == null) return null;
            while (rs.next()) {
                ProjectManager projectManager;
                if (rs.getArray(5) != null)
                    projectManager = new ProjectManager(rs.getInt(1), rs.getString(2),
                            rs.getInt(4), Arrays.asList((Integer[]) rs.getArray(5).getArray()));
                else {
                    projectManager = new ProjectManager(rs.getInt(1), rs.getString(2),
                            rs.getInt(4), null);
                }
                projectManagers.add(projectManager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectManagers;
    }


    /**
     * Parses a ResultSet to Task(s).
     *
     * @param rs the ResultSet to parse.
     * @return a list of Task that got parsed.
     */
    private static List<Task> parseTasksFromResultSet(ResultSet rs) {

        List<Task> tasks = new ArrayList<>();

        try {
            if (rs == null) return null;

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                double estimatedTime = rs.getDouble(3);
                int priority = rs.getInt(5);
                int projectId = rs.getInt(6);
                double startTime = rs.getDouble(7);
                double endTime = rs.getDouble(8);

                List<Integer> dependenceIds = new ArrayList<>();
                List<Integer> employeeIds = new ArrayList<>();
                List<Probabilities> probabilities = new ArrayList<>();
                if (rs.getArray(4) != null) {
                    dependenceIds = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                }

                if (rs.getArray(9) != null) {
                    employeeIds = Arrays.asList((Integer[]) rs.getArray(9).getArray());
                }

                if (rs.getArray(10) != null) {
                    ResultSet rsw = rs.getArray(10).getResultSet();

                    while (rsw.next()) {
                        String[] probValues = rsw.getString(2).replaceAll("[/(/)]", "").split(",");
                        probabilities.add(new Probabilities(Double.parseDouble(probValues[0]),
                                Double.parseDouble(probValues[1])));
                    }
                }

                Task task = new Task(id, name, estimatedTime, startTime, endTime, priority, dependenceIds, employeeIds,
                        projectId, probabilities);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;

    }

    /**
     * Parses a ResultSet to a list of Employees.
     *
     * @param rs the ResultSet to parse.
     * @return list of Employees that got parsed or null.
     */
    private static List<Employee> parseEmployeesFromResultSet(ResultSet rs) {
        List<Employee> empList = new ArrayList<>();
        try {
            if (rs == null) return null;
            while (rs.next()) {
                Employee emp = new Employee(rs.getInt(1), rs.getString(2),
                        Arrays.asList((Integer[]) rs.getArray(3).getArray()));
                emp.setProjectId(rs.getInt(4));
                empList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empList;
    }

    /**
     * Parses a ResultSet to Projects.
     *
     * @param rs the ResultSet to parse.
     * @return a list of Projects that got passed or null.
     */
    private static List<Project> parseProjectsFromResultSet(ResultSet rs) {
        List<Project> projects = new ArrayList<>();
        try {
            if (rs == null) return null;
            while (rs.next()) {
                Project project = new Project(rs.getInt(1), rs.getString(2),
                        ProjectState.values()[rs.getInt(3)], rs.getString(4), rs.getDouble(5));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return projects;
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
            return Objects.requireNonNull(DatabaseManager.parseTasksFromResultSet(rs)).get(0);
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
            return Objects.requireNonNull(parseEmployeesFromResultSet(rs)).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a project with projId from db.
     *
     * @param projId the id of the project to get.
     * @return the project or null.
     */
    public static Project getProject(int projId) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projects WHERE id = ?");
            statement.setInt(1, projId);
            ResultSet rs = statement.executeQuery();
            return Objects.requireNonNull(parseProjectsFromResultSet(rs)).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ProjectManager getPM(int id) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projectmanagers WHERE id = ?");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            List<ProjectManager> projectManagers = parseProjectManagerFromResultSet(rs);
            if (projectManagers.size() != 0)
                return projectManagers.get(0);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return the projects from db which are ongoing.
     *
     * @return list of all ongoing projects.
     */
    public static List<Project> getAllOngoingProjects() {
        if (dbConnection == null) connect();
        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM projects WHERE state = 0");
            if (rs == null) return null;
            return parseProjectsFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return the projects from db which are ongoing.
     *
     * @return list of all ongoing projects.
     */
    public static List<Project> getAllProjects() {
        if (dbConnection == null) connect();
        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM projects");
            if (rs == null) return null;
            return parseProjectsFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets all DatabaseEmployees from database. Assumes projects are loaded.
     *
     * @return list of all DatabaseEmployees.
     */
    public static List<Employee> getAllEmployees() {
        if (dbConnection == null) connect();
        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM employees");
            return parseEmployeesFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all databaseTasks.
     *
     * @return list of all database tasks.
     */
    public static List<Task> getAllTasks() {
        if (dbConnection == null) connect();

        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM tasks");
            return DatabaseManager.parseTasksFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Task> getAllTasksForProject(Project project) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM tasks WHERE projectid = ?");
            statement.setInt(1, project.getId());
            ResultSet rs = statement.executeQuery();
            return parseTasksFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static List<ProjectManager> getAllProjectManagers() {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projectmanagers");
            ResultSet rs = statement.executeQuery();
            List<ProjectManager> projectManagers = parseProjectManagerFromResultSet(rs);
            if (projectManagers != null && projectManagers.size() != 0)
                return projectManagers;
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void distributeModels() {
        List<Employee> employees = getAllEmployees();
        if (employees == null) return;
        employees.forEach(LocalObjStorage::addEmployee);

        List<Project> ongoingProjects = getAllProjects();
        if (ongoingProjects != null) {
            ongoingProjects.forEach(LocalObjStorage::addProject);
            /*for (Project proj : ongoingProjects) {
                Objects.requireNonNull(getAllTasksForProject(proj)).forEach(task -> {
                    LocalObjStorage.addTask(task);
                    proj.addNewTask(task);
                });
            }*/
        }

        List<ProjectManager> projectManagers = getAllProjectManagers();
        if (projectManagers != null) {
            projectManagers.forEach(projectManager -> {
                if (projectManager.getCurrentProjectId() != 0) {
                    projectManager.setCurrentProject(LocalObjStorage.getProjectById(projectManager.getCurrentProjectId()));
                    LocalObjStorage.getProjectById(projectManager.getCurrentProjectId()).setCreator(projectManager);
                }
                List<Integer> projectIds = new ArrayList<>(projectManager.getOldProjectsId());
                for (Integer projectId : projectIds) {
                    LocalObjStorage.getProjectById(projectId).setCreator(projectManager);
                    projectManager.addOldProject(LocalObjStorage.getProjectById(projectId));
                }
                LocalObjStorage.addProjectManager(projectManager);
            });

        }


        for (Employee emp : LocalObjStorage.getEmployeeList()) {
            emp.setProject(LocalObjStorage.getProjectById(emp.getProjectId()));
            if (emp.getProjectId() != 0)
                LocalObjStorage.getProjectById(emp.getProjectId()).addNewEmployee(emp);
        }
        LocalObjStorage.getTaskList().addAll(getAllTasks());
        List<Integer> employeesToRemove = new ArrayList<>();
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
                    employeesToRemove.add(employeeId);
                }

            }
            task.getEmployeeIds().removeAll(employeesToRemove);

            // DatabaseManager.updateTask(task);

            for (Integer dependencyId : task.getDependencyIds()) {
                task.distributeAddDependency(LocalObjStorage.getTaskById(dependencyId));
            }
        }

    }


    public static void updateEmployee(Employee employee) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE employees SET " +
                    " previoustasks = ?, projectid = ? WHERE id = ?");

            statement.setArray(1, dbConnection.createArrayOf("INTEGER",
                    employee.getPreviousTask().stream().map(Task::getId).toArray()
            ));
            //TODO: Check if this is correct
            if (employee.getProject() != null)
                statement.setInt(2, employee.getProject().getId());
            else
                statement.setInt(2, 0);
            statement.setInt(3, employee.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTask(Task task) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE tasks SET employees = ?" +
                    ", dependencies = ?, projectid = ?, estimatedtime = ?, priority = ?, startdate = ?, enddate = ?," +
                    " probabilities =" + task.parseProbabilitiesForDatabase() + "   WHERE id = ?");
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
            statement.setDouble(7, task.getEndTime());
            statement.setInt(8, task.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateProject(Project project) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projects SET state = ?, sequence = ?" +
                    ", duration = ? WHERE id = ?");
            statement.setInt(1, project.getState().getValue());
            statement.setString(2, project.getSequence());
            statement.setDouble(3, project.getDuration());
            statement.setInt(4, project.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ProjectManager logIn(String username, String password) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM projectmanagers WHERE username=? AND password=?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            List<ProjectManager> projectManagers = parseProjectManagerFromResultSet(rs);
            if (projectManagers != null && projectManagers.size() != 0)
                return projectManagers.get(0);
            else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateProjectManager(ProjectManager manager) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projectmanagers SET currentproject " +
                    "= ?, oldprojects = ? WHERE id = ? ");
            if (manager.getCurrentProject() != null)
                statement.setInt(1, manager.getCurrentProject().getId());
            else
                statement.setInt(1, 0);
            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    manager.getOldProjectsId().toArray()));
            statement.setInt(3, manager.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /* Parse probs
     //UPDATE tasks SET prob[1] = (534.1,3123.2) WHERE id = 47;
        ResultSet rs = DatabaseManager.query("SELECT probabilities FROM tasks WHERE id =" + 47);
        rs.next();

        ResultSet rsw = rs.getArray(1).getResultSet();
        while (rsw.next()) {
            String string = rsw.getString(2).replaceAll("[/(/)]", "");
            Probabilities probabilities = new Probabilities(Double.parseDouble(string.split(",")[0]),
                    Double.parseDouble(string.split(",")[1]));
            System.out.println(probabilities.getDuration() + " " + probabilities.getProbability());
        }
     */
}
