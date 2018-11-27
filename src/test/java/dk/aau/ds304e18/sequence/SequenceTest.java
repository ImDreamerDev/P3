package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        project.setNumberOfEmployees(1);

        Sequence.sequenceTasks(project, false);
        String assertedSequencedTasks = task1.getId() + "," +
                task3.getId() + "|" +
                task2.getId() + "(" + task1.getId() + "," + task3.getId() + ")" + "|" +
                task4.getId() + "(" + task2.getId() + ")";

        /*System.out.println("First test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        assertEquals(project.getSequence(), assertedSequencedTasks);

        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    @Test
    void testSequenceTasks02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        project.setNumberOfEmployees(1);

        Sequence.sequenceTasks(project, false);
        String assertedSequencedTasks = task4.getId() + "," +
                task3.getId() + "," +
                task2.getId() + "," +
                task1.getId();

        /*System.out.println("Second test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        assertEquals(project.getSequence(), assertedSequencedTasks);

        DatabaseManager.removeProjectManager(projectManager.getId());
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

        project.setNumberOfEmployees(1);

        Sequence.sequenceTasks(project, false);
        String assertedSequencedTasks = task4.getId() + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        /*System.out.println("Third test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        assertEquals(project.getSequence(), assertedSequencedTasks);

        DatabaseManager.removeProjectManager(projectManager.getId());
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

        project.setNumberOfEmployees(1);

        Sequence.sequenceTasks(project, false);
        String assertedSequencedTasks = task4.getId() + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        /*System.out.println("Third test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        assertEquals(project.getSequence(), assertedSequencedTasks);

        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    @Test
    void testSequenceTasks05() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 2, project);
        Task task3 = new Task("Test3", 5.0, 3, project);
        Task task4 = new Task("Test4", 2.0, 4, project);

        project.setNumberOfEmployees(2);

        task3.addDependency(Collections.singletonList(task1));

        Sequence.sequenceTasks(project, false);
        String assertedSequencedTasks = task4.getId() + "," +
                task2.getId() + "," +
                task1.getId() + "|" +
                task3.getId() + "(" + task1.getId() + ")";

        /*System.out.println("Third test:");
        System.out.println("SequencedTasks: " + sequencedTasks);
        System.out.println("AssertedSequencedTasks: " + assertedSequencedTasks.toString());*/
        assertEquals(project.getSequence(), assertedSequencedTasks);

        DatabaseManager.removeProjectManager(projectManager.getId());
    }

}
