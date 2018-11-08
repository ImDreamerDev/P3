package dk.aau.ds304e18;


import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;

class Main {
    public static void main(String[] args) {
        //DatabaseManager.distributeModels();
        ProjectManager projectManager = new ProjectManager("Milton", "hardcore");
        Employee rasmus = new Employee("Rasmus Smit Lindholt");
        Project testProj = new Project("Dank communication things to communicate with things", projectManager);


        Task task = new Task("Test task", 100, 1, testProj);
        task.addEmployee(rasmus);
    }
}
