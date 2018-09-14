package ds304e18.models;
/*
 * Author: Lasse Stig Emil Rasmussen
 * Email: lser17@student.aau.dk
 * Class: Software 2nd semester
 */

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import dk.aau.ds304e18.models.Worker;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestProject {

    @Test
    public void TestAddTask01() {
        Project project = new Project();
        assertNotNull(project.AddTask("Task1", Duration.ofHours(6)));
    }

    @Test
    public void TestAddTask02() {
        Project project = new Project();
        project.AddTask("Task1", Duration.ofHours(6));
        assertNull(project.AddTask("Task1", Duration.ofHours(6)));
    }

    @Test
    public void TestGetTask01() {
        Project project = new Project();
        Task t = project.AddTask("Task1", Duration.ofHours(6));
        assertEquals(t, project.GetTask("Task1"));
    }

    @Test
    public void TestGetTask02() {
        Project project = new Project();
        assertNull(project.GetTask("Task1"));
    }

    @Test
    public void AddWorker01() {
        Project project = new Project();
        assertNotNull(project.AddWorker("Task1", 9));
    }

    @Test
    public void AddWorker02() {
        Project project = new Project();
        project.AddWorker("Task1", 9);
        assertNotNull(project.AddWorker("Task1", 9));
    }

    @Test
    public void GetWorker01() {
        Project project = new Project();
        Worker worker = project.AddWorker("Task1", 9);
        assertEquals(worker, project.GetWorker("Task1"));
    }

    @Test
    public void GetWorker02() {
        Project project = new Project();
        assertNull(project.GetWorker("Task1"));
    }
}
