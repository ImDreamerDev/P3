package ds304e18.models;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectState;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectTest {

    /**
     * Tests that the Project constructor initialises the project correctly.
     */
    @Test
    public void TestProjectConstructor01() {
        Project newProject = new Project("Test Project");
        assertEquals("Test Project", newProject.getName());
    }

    /**
     * Tests adding a new task to the project.
     */
    @Test
    public void TestAddNewTask01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 2, 1, newProject);
        newProject.addNewTask(newTask);
        assertEquals(newTask, newProject.getTasks().get(0));
    }

    /**
     * Tests removing a task from the project.
     */
    @Test
    public void TestRemoveTask01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 2, 1, newProject);
        newProject.addNewTask(newTask);
        newProject.removeTask(newTask);
        assertTrue(newProject.getTasks().isEmpty());
    }

    /**
     * Tests adding a new employee to the project.
     */
    @Test
    public void TestAddNewEmployee01() {
        Project newProject = new Project("Test Project");
        Employee newEmployee = new Employee("Test Person");
        newProject.addNewEmployee(newEmployee);
        assertEquals(newEmployee, newProject.getEmployees().get(0));
    }

    /**
     * Tests removing a employee from the project.
     */
    @Test
    public void TestRemoveEmployee01() {
        Project newProject = new Project("Test Project");
        Employee newEmployee = new Employee("Test Person");
        newProject.addNewEmployee(newEmployee);
        newProject.removeEmployee(newEmployee);
        assertTrue(newProject.getEmployees().isEmpty());
    }

    /**
     * Tests getting the id of the project.
     */
    @Test
    public void TestGetId01() {
        Project newProject = new Project("Test Project");
        newProject.setId(5);
        assertEquals(5, newProject.getId());
    }

    /**
     * Tests getting the name of the project.
     */
    @Test
    public void TestGetName01() {
        Project newProject = new Project("Test Project");
        assertEquals("Test Project", newProject.getName());
    }

    /**
     * Tests getting the project state of the project.
     */
    @Test
    public void TestGetState01() {
        Project newProject = new Project("Test Project");
        assertEquals(ProjectState.ONGOING, newProject.getState());
    }

    /**
     * Tests getting the project's tasks.
     */
    @Test
    public void TestGetTasks01() {
        Project newProject = new Project("Test Project");
        Task newTask01 = new Task("Test Task01", 2, 5, newProject);
        Task newTask02 = new Task("Test Task02", 1, 2, newProject);
        newProject.addNewTask(newTask01, newTask02);

        List testList = new ArrayList();
        testList.add(newTask01);
        testList.add(newTask02);

        assertEquals(testList, newProject.getTasks());
    }

    /**
     * Tests getting the employees of the project.
     */
    @Test
    public void TestGetEmployees01() {
        Project newProject = new Project("Test Project");
        Employee newEmployee01 = new Employee("Person01");
        Employee newEmployee02 = new Employee("Person02");
        newProject.addNewEmployee(newEmployee01, newEmployee02);

        List testList = new ArrayList();
        testList.add(newEmployee01);
        testList.add(newEmployee02);

        assertEquals(testList, newProject.getEmployees());
    }

    /**
     * Tests setting the id of the project.
     */
    @Test
    public void TestSetId01() {
        Project newProject = new Project("Test Project");
        newProject.setId(3);
        assertEquals(3, newProject.getId());
    }

    /**
     * Tests setting the state of the project.
     */
    @Test
    public void TestSetState01() {
        Project newProject = new Project("Test Project");
        newProject.setState(ProjectState.COMPLETED);
        assertEquals(ProjectState.COMPLETED, newProject.getState());
    }
}