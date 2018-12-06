package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceTest {

    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void testSequenceTasks01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 5, project);
        Task task2 = new Task("Test2", 2.0, 5, project);
        Task task3 = new Task("Test3", 5.0, 5, project);
        Task task4 = new Task("Test4", 2.0, 5, project);
        task2.addDependency(Arrays.asList(task1, task3));
        task4.addDependency(Collections.singletonList(task2));

        project.setNumberOfWorkGroups(1);

        Sequence.sequenceAndCalculateProject(project, false, 10000);
        String assertedSequencedTasks = task1.getId() + "," +
                task3.getId() + "|" +
                task2.getId() + "(" + task1.getId() + "," + task3.getId() + ")" + "|" +
                task4.getId() + "(" + task2.getId() + ")";

        assertEquals(project.getSequence(), assertedSequencedTasks);
    }

    @Test
    void testSequenceTasks02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        project.setNumberOfWorkGroups(5);

        Sequence.sequenceAndCalculateProject(project, true, 10000);
        String assertedSequencedTasks = task4.getId() + "," +
                task3.getId() + "," +
                task2.getId() + "," +
                task1.getId();

        assertEquals(project.getSequence(), assertedSequencedTasks);
    }

    @Test
    void testSequenceTasks03() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        task3.addDependency(Collections.singletonList(task1));

        project.setNumberOfWorkGroups(2);

        Sequence.sequenceAndCalculateProject(project, true, 10000);
        String assertedSequencedTasks = task4.getId() + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        assertEquals(project.getSequence(), assertedSequencedTasks);
    }

    @Test
    void testSequenceTasks04() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        task3.addDependency(Collections.singletonList(task1));

        project.setNumberOfWorkGroups(1);

        Sequence.sequenceAndCalculateProject(project, true, 10000);
        String assertedSequencedTasks = task4.getId() + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        assertEquals(project.getSequence(), assertedSequencedTasks);
    }

    @Test
    void testSequenceTasks05() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        project.setNumberOfWorkGroups(2);

        task3.addDependency(Collections.singletonList(task1));

        Sequence.sequenceAndCalculateProject(project, false, 10000);
        String assertedSequencedTasks = task4.getId() + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        assertEquals(project.getSequence(), assertedSequencedTasks);
    }

}
