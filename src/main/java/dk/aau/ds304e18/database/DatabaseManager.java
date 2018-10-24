package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Task;

import java.sql.*;
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
        //TODO: Load from file or other, something better than just having it as plain text
        props.setProperty("password", "Ng^PjafXoj94zNAQECYA&484NRIG%9!p");
        try {
            dbConnection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an employee to the database and fetches the unique employee ID. and adds to employee.
     * @param emp Employee to add.
     * @return bool to indicate whether the operation was successful.
     */
    public static Boolean addEmployees(Employee emp) {
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

            if (!statement.execute()) return false;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) emp.setId(rs.getInt(1));


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
