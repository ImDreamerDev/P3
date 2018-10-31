package ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.sequence.Sequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class SequenceTest {

    @BeforeAll
    static void init(){
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

        List<Task> sequencedTasks = Sequence.sequenceTasks(tasks);
        List<Task> assertedSequencedTasks = new ArrayList<>();
        assertedSequencedTasks.add(task1);
        assertedSequencedTasks.add(task3);
        assertedSequencedTasks.add(task2);
        assertedSequencedTasks.add(task4);

        if(assertedSequencedTasks.size() != sequencedTasks.size()) fail("The length of the sequencedTasks is not the length of the assertedSequencedTasks");

        for(int i = 0; i < assertedSequencedTasks.size(); i++){

            if(!sequencedTasks.get(i).equals(assertedSequencedTasks.get(i))){
                fail(sequencedTasks.get(i).getName() + " is not equal to " + assertedSequencedTasks.get(i).getName());
            }

        }

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

        List<Task> sequencedTasks = Sequence.sequenceTasks(tasks);
        List<Task> assertedSequencedTasks = new ArrayList<>();
        assertedSequencedTasks.add(task4);
        assertedSequencedTasks.add(task3);
        assertedSequencedTasks.add(task2);
        assertedSequencedTasks.add(task1);

        if(assertedSequencedTasks.size() != sequencedTasks.size()) fail("The length of the sequencedTasks is not the length of the assertedSequencedTasks");

        for(int i = 0; i < assertedSequencedTasks.size(); i++){

            if(!sequencedTasks.get(i).equals(assertedSequencedTasks.get(i))){
                fail(sequencedTasks.get(i).getName() + " is not equal to " + assertedSequencedTasks.get(i).getName());
            }

        }

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

        List<Task> sequencedTasks = Sequence.sequenceTasks(tasks);
        List<Task> assertedSequencedTasks = new ArrayList<>();
        assertedSequencedTasks.add(task4);
        assertedSequencedTasks.add(task2);
        assertedSequencedTasks.add(task1);
        assertedSequencedTasks.add(task3);

        if(assertedSequencedTasks.size() != sequencedTasks.size()) fail("The length of the sequencedTasks is not the length of the assertedSequencedTasks");

        for(int i = 0; i < assertedSequencedTasks.size(); i++){

            if(!sequencedTasks.get(i).equals(assertedSequencedTasks.get(i))){
                fail(sequencedTasks.get(i).getName() + " is not equal to " + assertedSequencedTasks.get(i).getName());
            }

        }

    }

}
