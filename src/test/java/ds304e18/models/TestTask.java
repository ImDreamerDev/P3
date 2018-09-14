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

public class TestTask {


    @Test
    public void TestAssign01() {
        Project project = new Project();
        Task task = project.AddTask("Task1", Duration.ofHours(3));
        Worker worker = project.AddWorker("Worker", 2);
        task.Assign(worker);
        assertEquals(task.getAssignee(), worker);
    }

    @Test
    public void TestAssign02() {
        Project project = new Project();
        Task task2 = project.AddTask("Task2", Duration.ofHours(3));
        Task task = project.AddTask("Task1", Duration.ofHours(3), task2);
        Worker worker = project.AddWorker("Worker", 2);
        task.Assign(worker);
        assertNull(worker.CurrentTask);
    }

    @Test
    public void TestAssign03() {
        Project project = new Project();
        Task task = project.AddTask("Task1", Duration.ofHours(3));
        Worker worker = project.AddWorker("Worker", 3);
        task.Assign(worker, 5);
        assertEquals(task.getPriority(), 5);
    }

    @Test
    public void TestAssign04() {
        Project project = new Project();
        Task task2 = project.AddTask("Task2", Duration.ofHours(3));
        Task task = project.AddTask("Task1", Duration.ofHours(3), task2);
        Worker worker = project.AddWorker("Worker", 2);
        task.Assign(worker, 5);
        assertNull(worker.CurrentTask);
    }


    @Test
    public void TestDependenciesComplete01() {
        Project project = new Project();
        Task task = project.AddTask("Task1", Duration.ofHours(3));
        Task task2 = project.AddTask("Task2", Duration.ofHours(3), task);
        assertFalse(task2.DependenciesComplete());
    }

    @Test
    public void TestDependenciesComplete02() {
        Project project = new Project();
        Task task = project.AddTask("Task1", Duration.ofHours(3));
        Task task2 = project.AddTask("Task2", Duration.ofHours(3), task);
        task.setComplete(true);
        assertTrue(task2.DependenciesComplete());
    }
}
