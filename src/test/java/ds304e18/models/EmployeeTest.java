package ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmployeeTest {

    /**
     * Tests that the Employee constructor initialises the employee correctly.
     */
    @Test
    void TestEmployeeConstructor01() {
        Employee newEmployee = new Employee("Abraham");

        assertEquals("Abraham", newEmployee.getName());

        DatabaseManager.removeEmployee(newEmployee.getId());
    }


    @Test
    void TestEmployeeConstructor02() {
        List<Integer> previousTasks = new ArrayList<>();
        Employee newEmployee = new Employee(1,"Test Person",previousTasks);

        assertEquals(1, newEmployee.getId());
    }


    @Test
    void TestEmployeeConstructor03() {
        List<Integer> previousTasks = new ArrayList<>();
        Employee newEmployee = new Employee(1,"Kasper",previousTasks);

        assertEquals("Kasper", newEmployee.getName());
    }

    @Test
    void TestEmployeeConstructor04() {
        List<Integer> previousTasks = new ArrayList<>();
        Employee newEmployee = new Employee(1,"Kasper BTW", previousTasks);

        assertEquals(previousTasks, newEmployee.getPreviousTaskIds());
    }

    /**
     * Tests that adding a task to the employee works.
     */
    @Test
    void TestEmployeeAddNewTask01() {
        Employee newEmployee = new Employee("Slim Shady");
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 5, 1, newProject);

        newEmployee.addNewTask(newTask);

        assertEquals(newTask, newEmployee.getCurrentTask().get(0));

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    /**
     * Testing getting the id of the employee.
     */
    @Test
    void TestEmployeeGetId01() {
        Employee newEmployee = new Employee("The Real Slim Shady");
        DatabaseManager.removeEmployee(newEmployee.getId());

        newEmployee.setId(1000);

        assertEquals(1000, newEmployee.getId());
    }

    /**
     * Tests getting the name of the employee.
     */
    @Test
    void TestEmployeeGetName01() {
        Employee newEmployee = new Employee("Employee");
        assertEquals("Employee", newEmployee.getName());

        DatabaseManager.removeEmployee(newEmployee.getId());
    }

    /**
     * Tests getting the employee's assigned project.
     */
    @Test
    void TestEmployeeGetProject01() {
        Employee newEmployee = new Employee("Employee");
        Project newProject = new Project("Test Project");

        newEmployee.setProject(newProject);

        assertEquals(newProject, newEmployee.getProject());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeEmployee(newEmployee.getId());

    }

    /**
     * Tests getting the employee's current tasks.
     */
    @Test
    void TestEmployeeGetCurrentTask01() {
        Employee newEmployee = new Employee("Test Employee");
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 5, 1, newProject);

        newEmployee.addNewTask(newTask);

        assertEquals(newTask, newEmployee.getCurrentTask().get(0));

        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    /**
     * Tests getting the previous tasks of the employee.
     */
    @Test
    void TestEmployeeGetPreviousTask01() {
        Employee newEmployee = new Employee("Test Employee");
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 5, 1, newProject);
        newEmployee.addPreviousTask(newTask);

        assertEquals(newTask, newEmployee.getPreviousTask().get(0));

        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }


    /**
     * Tests setting the employee's id.
     */
    @Test
    void TestEmployeeSetId01() {
        Employee newEmployee = new Employee("Employee");
        newEmployee.setId(5);

        assertEquals(5, newEmployee.getId());

        DatabaseManager.removeEmployee(newEmployee.getId());
    }

    /**
     * Tests setting the employee's project.
     */
    @Test
    void TestEmployeeSetProject01() {
        Employee newEmployee = new Employee("Employee");
        Project newProject = new Project("Test Project");

        newEmployee.setProject(newProject);

        assertEquals(newProject, newEmployee.getProject());

        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

    @Test
    void TestEmployeeEquals01() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");

        employee1.setId(1);
        employee2.setId(1);

        assertEquals(employee1, employee2);

        DatabaseManager.removeEmployee(employee1.getId());
    }

    @Test
    void TestEmployeeEquals02() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");

        employee1.setId(1);
        employee2.setId(2);

        assertNotEquals(employee1, employee2);

        DatabaseManager.removeEmployee(employee1.getId());
        DatabaseManager.removeEmployee(employee2.getId());
    }

    @Test
    void TestEmployeeHashcode01() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");

        DatabaseManager.removeEmployee(employee1.getId());

        employee1.setId(1);
        employee2.setId(1);

        assertEquals(employee1.hashCode(), employee2.hashCode());
    }

    @Test
    void TestEmployeeHashcode02() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");

        DatabaseManager.removeEmployee(employee1.getId());
        DatabaseManager.removeEmployee(employee2.getId());

        employee1.setId(1);
        employee2.setId(2);

        assertNotEquals(employee1.hashCode(), employee2.hashCode());
    }

    @Test
    void TestEmployeeDistributeAddTask01 () {
        Employee newEmployee = new Employee("Lars");
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Task", 1 , 1, newProject);

        newEmployee.distributeAddTask(newTask);

        assertEquals(newTask, newEmployee.getCurrentTask().get(0));

        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }
}
