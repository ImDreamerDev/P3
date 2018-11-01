package ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.Sequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceTest {

    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void testSequenceTasks01() {
        Project project = new Project("Project");
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task("Test1", 1.0, 5, project);
        Task task2 = new Task("Test2", 2.0, 5, project);
        Task task3 = new Task("Test3", 5.0, 5, project);
        Task task4 = new Task("Test4", 2.0, 5, project);
        task2.addDependency(task1);
        task2.addDependency(task3);
        task4.addDependency(task2);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        String sequencedTasks = Sequence.sequenceTasks(tasks);
        String assertedSequencedTasks = String.valueOf(task1.getId()) + "," +
                task3.getId() + "|" +
                task2.getId() + "(" + task1.getId() + "," + task3.getId() + ")" + "|" +
                task4.getId() + "(" + task2.getId() + ")";

        /*System.out.println("First test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        // assertEquals(sequencedTasks, assertedSequencedTasks);
//TODO: fix test
    }

    @Test
    void testSequenceTasks02() {
        Project project = new Project("Project");
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        String sequencedTasks = Sequence.sequenceTasks(tasks);
        String assertedSequencedTasks = String.valueOf(task4.getId()) + "," +
                task3.getId() + "," +
                task2.getId() + "," +
                task1.getId();

        /*System.out.println("Second test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        // assertEquals(sequencedTasks, assertedSequencedTasks);
        //TODO: fix test

    }

    @Test
    void testSequenceTasks03() {
        Project project = new Project("Project");
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        task3.addDependency(task1);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        String sequencedTasks = Sequence.sequenceTasks(tasks);
        String assertedSequencedTasks = String.valueOf(task4.getId()) + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        /*System.out.println("Third test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        //assertEquals(sequencedTasks, assertedSequencedTasks);
    //TODO: fix test
    }

}
