package dk.aau.ds304e18;


import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

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
        DatabaseManager.distributeModels();
        Employee rasmus = new Employee("Rasmus Smit Lindholt");
        Project testProj = new Project("Dank communication things to communicate with things");


        Task task = new Task("Test task", 100, 1, testProj);
        task.addEmployee(rasmus);

       // DatabaseManager.addTask(task);
       
    }
}
