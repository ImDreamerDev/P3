package dk.aau.ds304e18.database;

import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;
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

    /**
     * Removes an employee with id from the database.
     *
     * @param id id of the Employee to remove.
     */
    public static void removeEmployee(int id) {
        DatabaseManager.query("DELETE FROM employees WHERE id = " + id);
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
                if (rs.getArray(4) != null) {
                    dependenceIds = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                }

                if (rs.getArray(9) != null) {
                    employeeIds = Arrays.asList((Integer[]) rs.getArray(9).getArray());
                }

                Task task = new Task(id, name, estimatedTime, startTime, endTime, priority, dependenceIds, employeeIds, projectId);
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
                LocalObjStorage.getProjectById(rs.getInt(4));
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
                        ProjectState.values()[rs.getInt(3)], rs.getString(4));
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

    /**
     * Return the projects from db which are ongoing.
     *
     * @return list of all ongoing projects.
     */
    private static List<Project> getAllOngoingProjects() {
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
     * Gets all DatabaseEmployees from database. Assumes projects are loaded.
     *
     * @return list of all DatabaseEmployees.
     */
    private static List<Employee> getAllEmployees() {
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
    private static List<Task> getAllTasks() {
        if (dbConnection == null) connect();

        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM tasks");
            return DatabaseManager.parseTasksFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void distributeModels() {
     /*   List<DatabaseEmployee> dbEmpList = getAllEmployees();
        List<DatabaseTask> dbTaskList = getAllTasks();
        List<DatabaseProject> dbProjectList = getAllOngoingProjects();

        LocalObjStorage.getEmployeeList().clear();
        LocalObjStorage.getTaskList().clear();
        LocalObjStorage.getProjectList().clear();

        dbEmpList.forEach(dbEmp -> {
            if (LocalObjStorage.getEmployeeList().stream().anyMatch(emp -> emp.getId() == dbEmp.id))
                return;
            LocalObjStorage.addEmployee((Converter.convertEmployee(dbEmp)));
        });
        dbTaskList.forEach(dbTask -> {
            if (LocalObjStorage.getTaskList().stream().anyMatch(task -> task.getId() == dbTask.id))
                return;
            LocalObjStorage.addTask(Converter.convertTask(dbTask));
        });
        dbProjectList.forEach(dbProj -> {
            if (LocalObjStorage.getProjectList().stream().anyMatch(project -> project.getId() == dbProj.id))
                return;
            LocalObjStorage.addProject((Converter.convertProject(dbProj)));
        });

        //Distribute employees
        for (Employee emp : LocalObjStorage.getEmployeeList()) {
            DatabaseEmployee dbEmp = dbEmpList.stream().filter(databaseEmployee -> databaseEmployee.id == emp.getId()).findFirst().orElse(null);
            if (dbEmp == null) continue;
            emp.setProject(LocalObjStorage.getProjectById(dbEmp.projectId));
            dbEmp.currentTaskId.forEach(taskId -> emp.addNewTask(LocalObjStorage.getTaskById(taskId)));
            dbEmp.preTaskId.forEach(taskId -> emp.addPreviousTask(LocalObjStorage.getTaskById(taskId)));
        }

        //Distribute tasks
        for (Task task : LocalObjStorage.getTaskList()) {
            DatabaseTask dbTask = dbTaskList.stream().filter(databaseTask -> databaseTask.id == task.getId()).findFirst().orElse(null);
            if (dbTask == null) continue;
            task.setProject(LocalObjStorage.getProjectById(dbTask.projectId));
            dbTask.employeeIds.forEach(empId -> task.addEmployee(LocalObjStorage.getEmployeeById(empId)));
            dbTask.dependenceIds.forEach(taskId -> task.addDependency(LocalObjStorage.getTaskById(taskId)));
        }

        //Distribute projects
        for (Project proj : LocalObjStorage.getProjectList()) {
            if (LocalObjStorage.getProjectList().stream().anyMatch(project -> project.getId() == proj.getId()))
                continue;
            DatabaseProject dbProject = dbProjectList.stream().filter(databaseProject -> databaseProject.id == proj.getId()).findFirst().orElse(null);
            if (dbProject == null) continue;
            dbProject.employeeIds.forEach(empId -> proj.addNewEmployee(LocalObjStorage.getEmployeeById(empId)));
            dbProject.tasks.forEach(taskId -> proj.addNewTask(LocalObjStorage.getTaskById(taskId)));
        }*/
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
                    ", dependencies = ?, projectid = ?, estimatedtime = ?, priority = ?, startdate = ?, enddate = ? WHERE id = ?");
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
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projects SET state = ?, sequence = ?" +
                    "WHERE id = ?");
            statement.setInt(1, project.getState().getValue());
            statement.setString(2, project.getSequence());
            statement.setInt(3, project.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
