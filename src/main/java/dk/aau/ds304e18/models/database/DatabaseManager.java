package dk.aau.ds304e18.models.database;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {

    private static Connection dbConnection;

    public static ResultSet query(String query) {
        if(dbConnection == null)connect();
        try {
            Statement st = dbConnection.createStatement();
             return st.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void connect() {
        String url = "jdbc:postgresql://molae.duckdns.org/P3";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","5$m4!x1D3$3sh&KxhcP60t8dzCz2Zkla9F0zPSajN#*6MshiiHbbWvKwKuaBeQmP");
        try {
            dbConnection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
