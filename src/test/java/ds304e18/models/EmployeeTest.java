package ds304e18.models;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    /**
     * Tests that the Employee constructor initialises the employee correctly.
     */
    @Test
    public void TestEmployeeConstructor01() {
        Employee newEmployee = new Employee("Abraham");
        assertEquals("Abraham", newEmployee.getName());
    }

    /**
     * Tests that adding a task to the employee works.
     */
    @Test
    public void TestEmployeeAddNewTask01() {
        Employee newEmployee = new Employee("Slim Shady");
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 5, 1, newProject);
        newEmployee.addNewTask(newTask);
        assertEquals(newTask, newEmployee.getCurrentTask().get(0));
    }

    /**
     * Testing getting the id of the employee.
     */
    @Test
    public void TestEmployeeGetId01() {
        Employee newEmployee = new Employee("The Real Slim Shady");
        newEmployee.setId(1);
        assertEquals(1, newEmployee.getId());
    }

    /**
     * Tests getting the name of the employee.
     */
    @Test
    public void TestEmployeeGetName01() {
        Employee newEmployee = new Employee("Employee");
        assertEquals("Employee", newEmployee.getName());
    }

    /**
     * Tests getting the employee's assigned project.
     */
    @Test
    public void TestEmployeeGetProject01() {
        Employee newEmployee = new Employee("Employee");
        Project newProject = new Project("Test Project");
        newEmployee.setProject(newProject);
        assertEquals(newProject, newEmployee.getProject());
    }

    /**
     * Tests getting the employee's current tasks.
     */
    @Test
    public void TestEmployeeGetCurrentTask01() {
        Employee newEmployee = new Employee("Test Employee");
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 5, 1, newProject);
        newEmployee.addNewTask(newTask);
        assertEquals(newTask, newEmployee.getCurrentTask().get(0));
    }
/*
    @Test
    public void TestEmployeeGetPreviousTask01 () {
        Mangler en funktion som kan tilføje tasks til PreviousTask listen....
    }
*/

    /**
     * Tests setting the employee's id.
     */
    @Test
    public void TestEmployeeSetId01() {
        Employee newEmployee = new Employee("Employee");
        newEmployee.setId(5);
        assertEquals(5, newEmployee.getId());
    }

    /**
     * Tests setting the employee's project.
     */
    @Test
    public void TestEmployeeSetProject01() {
        Employee newEmployee = new Employee("Employee");
        Project newProject = new Project("Test Project");
        newEmployee.setProject(newProject);
        assertEquals(newProject, newEmployee.getProject());
    }

}
