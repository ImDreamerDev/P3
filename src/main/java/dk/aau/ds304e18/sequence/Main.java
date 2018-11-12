package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        Instant start = java.time.Instant.now();

        ProjectManager projectManager = new ProjectManager("rsl", "1");
        Project project = new Project("Druktur", projectManager);
        Task task1 = new Task("Drik øl", 20, 1, project);
        Task task2 = new Task("Fyr shots", 5, 1, project);
        Task task3 = new Task("Drik stor øl", 35, 1, project);
        Task task4 = new Task("Stå i kø for at købe øl", 5, 1, project);
        Task task5 = new Task("Drik 2 øl", 50, 1, project);
        Task task6 = new Task("Drik rom & cola", 15, 1, project);
        Task task7 = new Task("Stå i kø igen", 15, 1, project);
        Task task8 = new Task("Drik meget", 70, 1, project);

        project.setNumberOfEmployees(2);

        /*project.addNewEmployee(new Employee("Milton"));
        project.addNewEmployee(new Employee("Rasmus"));
        project.addNewEmployee(new Employee("Kasper"));
        project.addNewEmployee(new Employee("Emil"));
        /*project.addNewEmployee(new Employee("Test2"));
        project.addNewEmployee(new Employee("Test2"));
        project.addNewEmployee(new Employee("Test2"));
        project.addNewEmployee(new Employee("Test2"));*/

        task5.addDependency(task4);
        task6.addDependency(task4);
        task7.addDependency(task5);
        task7.addDependency(task6);
        task8.addDependency(task7);

        task1.getProbabilities().add(new Probabilities(10, 25));
        task1.getProbabilities().add(new Probabilities(20, 55));
        task1.getProbabilities().add(new Probabilities(30, 85));
        task1.getProbabilities().add(new Probabilities(45, 95));
        DatabaseManager.updateTask(task1);

        task2.getProbabilities().add(new Probabilities(1, 20));
        task2.getProbabilities().add(new Probabilities(2, 30));
        task2.getProbabilities().add(new Probabilities(5, 65));
        task2.getProbabilities().add(new Probabilities(10, 95));
        DatabaseManager.updateTask(task2);

        task3.getProbabilities().add(new Probabilities(20, 20));
        task3.getProbabilities().add(new Probabilities(35, 45));
        task3.getProbabilities().add(new Probabilities(55, 70));
        task3.getProbabilities().add(new Probabilities(70, 95));
        DatabaseManager.updateTask(task3);

        task4.getProbabilities().add(new Probabilities(1, 20));
        task4.getProbabilities().add(new Probabilities(5, 65));
        task4.getProbabilities().add(new Probabilities(15, 95));
        DatabaseManager.updateTask(task4);

        task5.getProbabilities().add(new Probabilities(15, 10));
        task5.getProbabilities().add(new Probabilities(25, 20));
        task5.getProbabilities().add(new Probabilities(40, 45));
        task5.getProbabilities().add(new Probabilities(50, 60));
        task5.getProbabilities().add(new Probabilities(80, 80));
        task5.getProbabilities().add(new Probabilities(100, 95));
        DatabaseManager.updateTask(task5);

        task6.getProbabilities().add(new Probabilities(5, 25));
        task6.getProbabilities().add(new Probabilities(10, 40));
        task6.getProbabilities().add(new Probabilities(15, 55));
        task6.getProbabilities().add(new Probabilities(25, 85));
        DatabaseManager.updateTask(task6);

        task7.getProbabilities().add(new Probabilities(1, 15));
        task7.getProbabilities().add(new Probabilities(5, 35));
        task7.getProbabilities().add(new Probabilities(15, 55));
        task7.getProbabilities().add(new Probabilities(25, 90));
        DatabaseManager.updateTask(task7);

        task8.getProbabilities().add(new Probabilities(50, 20));
        task8.getProbabilities().add(new Probabilities(70, 45));
        task8.getProbabilities().add(new Probabilities(100, 75));
        task8.getProbabilities().add(new Probabilities(140, 95));
        task8.getProbabilities().add(new Probabilities(200, 99));
        DatabaseManager.updateTask(task8);

        Sequence.sequenceTasks(project, true);
        Instant end = java.time.Instant.now();

        System.out.println("Duration of the project: " + project.getDuration());
        System.out.println("With sequence: " + project.getSequence());
        System.out.println("Rec path: " + project.getRecommendedPath());
        System.out.println(project.getPossibleCompletions());

        //Instant start2 = java.time.Instant.now();
        //MonteCarlo.estimateTime(project, true, 1000000);
        //Instant end2 = java.time.Instant.now();

        Duration between = java.time.Duration.between(start, end);
        System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 + "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001

        //Duration between2 = java.time.Duration.between(start2, end2);
        //System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 + "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                //between2.toHours(), between2.toMinutes(), between2.getSeconds(), between2.toMillis()); // 0D, 00:00:01.1001

    }
}
