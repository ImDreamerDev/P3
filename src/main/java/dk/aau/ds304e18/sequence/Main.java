package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;

public class Main {
    public static void main(String[] args) {
        ProjectManager projectManager = new ProjectManager("Rasmus Smit Lindholt", "hardcore");
        Project project = new Project("Druktur", projectManager);
        Task task1 = new Task("Drik øl", 20, 1, project);
        Task task2 = new Task("Fyr shots", 5, 1, project);
        Task task3 = new Task("Drik stor øl", 35, 1, project);
        Task task4 = new Task("Stå i kø for at købe øl", 5, 1, project);
        Task task5 = new Task("Drik 2 øl", 50, 1, project);
        Task task6 = new Task("Drik rom & cola", 15, 1, project);
        Task task7 = new Task("Stå i kø igen", 15, 1, project);
        Task task8 = new Task("Drik meget", 70, 1, project);

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

        Sequence.sequenceTasks(project);

        MonteCarlo.estimateTime(project);
        System.out.println("Duration of the project: " + project.getDuration());

    }
}