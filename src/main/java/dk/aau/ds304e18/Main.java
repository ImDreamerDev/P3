package dk.aau.ds304e18;


import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
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
        ProjectManager projectManager = new ProjectManager("Milton", "hardcore");
        Employee rasmus = new Employee("Rasmus Smit Lindholt");
        Project testProj = new Project("Dank communication things to communicate with things", projectManager);


        Task task = new Task("Test task", 100, 1, testProj);
        task.addEmployee(rasmus);
        /* Test performance
        Instant start = java.time.Instant.now();
        Thread.sleep(1000);
        Instant end = java.time.Instant.now();
        Duration between = java.time.Duration.between(start, end);
        System.out.println( between ); // PT1.001S
        System.out.format("%dD, %02d:%02d:%02d.%04d \n", between.toDays(),
        between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001 
         */
    }
}
