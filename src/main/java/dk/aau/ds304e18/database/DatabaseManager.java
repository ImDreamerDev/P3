package dk.aau.ds304e18.database;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

    private static Connection dbConnection;

    /**
     * Sends a query to the DB and returns the result.
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
}
