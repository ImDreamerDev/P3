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
        String url = "jdbc:postgresql://molae.duckdns.org/P3";
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
                //TODO get tasks and projectID
                empList.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empList;
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
     * @return lsit of all database tasks.
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
                task.estimatedTime = rs.getInt(3);
                task.employeeIds = Arrays.asList((Integer[]) rs.getArray(4).getArray());
                task.startDate = rs.getDate(5).toLocalDate();
                task.endDate = rs.getDate(6).toLocalDate();
                task.priority = rs.getInt(7);
                task.projectId = rs.getInt(8);
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

        dbEmpList.forEach(dbEmp -> LocalObjStorage.addEmployee((convertEmployee(dbEmp))));
        dbTaskList.forEach(dbTask -> LocalObjStorage.addTask(convertTask(dbTask)));
        dbProjectList.forEach(dbProj -> LocalObjStorage.addProject((convertProject(dbProj))));

        //Distribute employees
        for (int i = 0; i < LocalObjStorage.getEmployeeList().size(); i++) {
            LocalObjStorage.getEmployeeList().get(i).setProject(
                    LocalObjStorage.getProjectById(dbEmpList.get(i).projectId));

            int finalI = i;
            dbEmpList.get(i).currentTaskIds.forEach(taskId -> LocalObjStorage.getEmployeeList().get(finalI)
                    .addNewTask(LocalObjStorage.getTaskById(taskId)));

            dbEmpList.get(i).preTaskId.forEach(taskId -> LocalObjStorage.getEmployeeList().get(finalI)
                    .addPreviousTask(LocalObjStorage.getTaskById(taskId)));
        }

        //Distribute tasks
        for (int i = 0; i < LocalObjStorage.getTaskList().size(); i++) {

            //Set project
            LocalObjStorage.getTaskList().get(i).setProject(
                    LocalObjStorage.getProjectById(dbTaskList.get(i).projectId));

            int finalI = i;
            //Set employees
            dbTaskList.get(i).employeeIds.forEach(empId -> LocalObjStorage.getTaskList().get(finalI).addEmployee(LocalObjStorage.getEmployeeById(empId)));

            //Set Dependencies
            dbTaskList.get(i).dependencieIds.forEach(taskId -> LocalObjStorage.getTaskList().get(finalI)
                    .addDependency(LocalObjStorage.getTaskById(taskId)));
        }

        //Distribute projects
        for (int i = 0; i < LocalObjStorage.getProjectList().size(); i++) {
            int finalI = i;
            dbProjectList.get(i).employeeIds.forEach(empId -> LocalObjStorage.getProjectList().get(finalI)
                    .addNewEmployee(LocalObjStorage.getEmployeeById(empId)));

            dbProjectList.get(i).tasks.forEach(taskId -> LocalObjStorage.getProjectList().get(finalI)
                    .addNewTask(LocalObjStorage.getTaskById(taskId)));
        }
        System.out.println("Done");
    }


    private static Project convertProject(DatabaseProject dbProj) {
        Project project = new Project(dbProj.name);
        project.setId(dbProj.id);
        project.setState(dbProj.state);
        //TODO
        return project;
    }

    private static Task convertTask(DatabaseTask dbTask) {
        return new Task(dbTask.id, dbTask.name, dbTask.estimatedTime, dbTask.startDate, dbTask.endDate, dbTask.priority);
    }

    private static Employee convertEmployee(DatabaseEmployee dbEmp) {
        Employee emp = new Employee(dbEmp.name);
        emp.setId(dbEmp.id);
        return emp;
    }
}
