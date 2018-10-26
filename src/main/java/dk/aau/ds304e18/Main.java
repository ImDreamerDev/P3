package dk.aau.ds304e18;


import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;

import java.util.List;

class Main {
    public static void main(String[] args) {

/*        ResultSet rs = DatabaseManager.query("SELECT * FROM employees");
        try {
            if (rs == null) return;
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */

        Employee rasmus = new Employee("Rasmus Smit Lindholt");
        Project testProj = new Project("Dank communication things to communicate with things");
        DatabaseManager.distributeModels();

        DatabaseManager.addEmployees(rasmus);
        DatabaseManager.addProject(testProj);

        LocalObjStorage.getProjectById(testProj.getId())
                .addNewEmployee(LocalObjStorage.getEmployeeById(rasmus.getId()));

    }
}
