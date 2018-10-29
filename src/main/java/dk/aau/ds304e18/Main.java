package dk.aau.ds304e18;


import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.time.LocalDate;
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


        Task task = new Task("Test task", 100, 1, LocalObjStorage.getProjectById(10));
        task.addEmployee(rasmus);

        DatabaseManager.addTask(task);
        DatabaseManager.distributeModels();
    }
}
