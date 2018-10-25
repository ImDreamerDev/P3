package dk.aau.ds304e18;


import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        /*ResultSet rs = DatabaseManager.query("SELECT * FROM employees");
        try {
            if (rs == null) return;
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

/*        Employee rasmus = new Employee("Rasmus Smit Lindholt");
        Project testProj = new Project("Dank communication things to communicate with things");

        DatabaseManager.addEmployees(rasmus);
        testProj.addNewEmployee(rasmus);
        DatabaseManager.addProject(testProj);

        System.out.println(rasmus.getId());
        System.out.println(testProj.getId());*/

        List<Employee> empList = DatabaseManager.getAllEmployees();
        empList.forEach(emp-> System.out.println(emp.getId() + " " + emp.getName()));

    }
}
