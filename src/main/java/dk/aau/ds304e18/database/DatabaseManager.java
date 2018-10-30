package dk.aau.ds304e18.database;

import dk.aau.ds304e18.LocalObjStorage;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DatabaseManager {

    private static Connection dbConnection;

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
            System.err.println(e.getMessage());
            e.printStackTrace();
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
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO employees (name, currenttasks," +
                    " previoustasks, projectid) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, emp.getName());
            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    emp.getCurrentTask().stream().map(Task::getId).toArray()
            ));
            statement.setArray(3, dbConnection.createArrayOf("INTEGER",
                    emp.getPreviousTask().stream().map(Task::getId).toArray()
            ));

            if (emp.getProject() != null) statement.setInt(4, emp.getProject().getId());
            else statement.setInt(4, 0);

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
     * Adds a project to the database and fetches the unique project ID and adds to project.
     *
     * @param project the project to add.
     * @return bool to indicate whether the operation was successful.
     */
    public static Boolean addProject(Project project) {
        if (dbConnection == null) connect();
        try {
            PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO projects " +
                    "(name, state, tasks, employees) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, project.getName());
            statement.setInt(2, project.getState().getValue());
            statement.setArray(3, dbConnection.createArrayOf("INTEGER",
                    project.getTasks().stream().map(Task::getId).toArray()));
            statement.setArray(4, dbConnection.createArrayOf("INTEGER",
                    project.getEmployees().stream().map(Employee::getId).toArray()));

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
     * Gets all DatabaseEmployees from database.
     *
     * @return list of all DatabaseEmployees.
     */
    private static List<DatabaseEmployee> getAllEmployees() {
        if (dbConnection == null) connect();
        List<DatabaseEmployee> empList = new ArrayList<>();
        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM employees");
            while (rs.next()) {
                DatabaseEmployee emp = new DatabaseEmployee();
                emp.name = rs.getString(2);
                emp.id = (rs.getInt(1));
                emp.currentTaskId = Arrays.asList((Integer[]) rs.getArray(3).getArray());
                emp.preTaskId = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                emp.projectId = rs.getInt(5);
                empList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empList;
    }


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
     * Return the databaseProjects from the database.
     *
     * @return list of all databaseProjects.
     */
    private static List<DatabaseProject> getAllProjects() {
        if (dbConnection == null) connect();
        List<DatabaseProject> databaseProjects = new ArrayList<>();
        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM projects");
            while (rs.next()) {
                DatabaseProject project = new DatabaseProject();
                project.name = rs.getString(2);
                project.id = (rs.getInt(1));
                project.state = ProjectState.values()[rs.getInt(3)];
                project.tasks = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                project.employeeIds = Arrays.asList((Integer[]) rs.getArray(5).getArray());
                databaseProjects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return databaseProjects;
    }

    /**
     * Get all databaseTasks.
     *
     * @return list of all database tasks.
     */
    private static List<DatabaseTask> getAllTasks() {
        if (dbConnection == null) connect();

        List<DatabaseTask> databaseTasks = new ArrayList<>();

        try {
            ResultSet rs = dbConnection.createStatement().executeQuery("SELECT * FROM tasks");
            while (rs.next()) {
                DatabaseTask task = new DatabaseTask();
                task.id = rs.getInt(1);
                task.name = rs.getString(2);
                task.estimatedTime = rs.getDouble(3);
                if (rs.getArray(4) != null)
                    task.employeeIds = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                task.dependenceIds = Arrays.asList((Integer[]) rs.getArray(5).getArray());
                task.priority = rs.getInt(6);
                task.projectId = rs.getInt(7);
                task.startTime = rs.getDouble(8);
                task.endTime = rs.getDouble(9);
                databaseTasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return databaseTasks;
    }

    public static void distributeModels() {
        List<DatabaseEmployee> dbEmpList = getAllEmployees();
        List<DatabaseTask> dbTaskList = getAllTasks();
        List<DatabaseProject> dbProjectList = getAllProjects();

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
        }
    }


    public static void updateEmployee(Employee employee) {
        try {
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE employees SET currenttasks = ?" +
                    ", previoustasks = ?, projectid = ? WHERE id = ?");
            statement.setArray(1, dbConnection.createArrayOf("INTEGER",
                    employee.getCurrentTask().stream().map(Task::getId).toArray()
            ));
            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    employee.getPreviousTask().stream().map(Task::getId).toArray()
            ));
            //TODO: Check if this is correct
            if (employee.getProject() != null)
                statement.setInt(3, employee.getProject().getId());
            else
                statement.setInt(3, 0);
            statement.setInt(4, employee.getId());
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
            PreparedStatement statement = dbConnection.prepareStatement("UPDATE projects SET state = ?," +
                    " tasks = ?, employees = ? WHERE id = ?");
            statement.setInt(1, project.getState().getValue());
            statement.setArray(2, dbConnection.createArrayOf("INTEGER",
                    project.getTasks().stream().map(Task::getId).toArray()));
            statement.setArray(3, dbConnection.createArrayOf("INTEGER",
                    project.getEmployees().stream().map(Employee::getId).toArray()));

            statement.setInt(4, project.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
