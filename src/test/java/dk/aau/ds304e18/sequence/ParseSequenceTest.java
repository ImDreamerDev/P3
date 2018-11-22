package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParseSequenceTest {

    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void parseSequenceTest01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 1, project);
        Task task3 = new Task("Test3", 5.0, 1, project);
        Task task4 = new Task("Test4", 2.0, 1, project);

        task3.addDependency(Arrays.asList(task1));

        Sequence.sequenceTasks(project);

        List<List<Task>> assertedList = new ArrayList<>();
        List<Task> list1 = new ArrayList<>();
        List<Task> list2 = new ArrayList<>();

        list1.add(task1);
        list1.add(task2);
        list1.add(task4);
        list2.add(task3);

        assertedList.add(list1);
        assertedList.add(list2);

        assertEquals(assertedList, ParseSequence.parseToMultipleLists(project));
        for (Task task : project.getTasks()) {
            DatabaseManager.removeTask(task.getId());
        }
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project.getId());
        DatabaseManager.query("DELETE FROM projectmanagers WHERE id = " + projectManager.getId());
    }

    @Test
    void TestParseToSingleList() {
        ProjectManager tom = new ProjectManager("Tom", "Hello");
        Project project = new Project("Ree", tom);
        Task task1 = new Task("Test1", 1.0, 1, project);
        Task task2 = new Task("Test2", 2.0, 1, project);
        Task task3 = new Task("Test3", 5.0, 1, project);
        Task task4 = new Task("Test4", 2.0, 1, project);

        task3.addDependency(Arrays.asList(task1));

        Sequence.sequenceTasks(project);
        assertNotNull(ParseSequence.parseToSingleList(project, false));
        for (Task task : project.getTasks()) {
            DatabaseManager.removeTask(task.getId());
        }
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project.getId());
        DatabaseManager.query("DELETE FROM projectmanagers WHERE id = " + tom.getId());
    }

}
