package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Project project = new Project("Lol");
        Task task1 = new Task("Test1", 20, 1, project);
        Task task2 = new Task("Test2", 15, 1, project);
        Task task3 = new Task("Test3", 40, 1, project);
        project.addNewTask(task1);
        project.addNewTask(task2);
        project.addNewTask(task3);

        task2.addDependency(task3);

        task1.getProbabilities().add(new Probabilities(10,10));
        task1.getProbabilities().add(new Probabilities(20,45));
        task2.getProbabilities().add(new Probabilities(15,50));
        task3.getProbabilities().add(new Probabilities(25, 20));
        task3.getProbabilities().add(new Probabilities(45,50));

        Sequence.sequenceTasks(project);

        MonteCarlo.estimateTime(project);
        System.out.println("Duration of the project: " + project.getDuration());

    }
}
