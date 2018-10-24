package dk.aau.ds304e18.models;

import dk.aau.ds304e18.models.database.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ResultSet rs = DatabaseManager.query("SELECT * FROM employees");

        try {
            if (rs == null) return;
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
