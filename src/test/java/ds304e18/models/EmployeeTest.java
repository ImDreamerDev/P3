package ds304e18.models;

import dk.aau.ds304e18.database.DatabaseEmployee;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmployeeTest {

    /**
     * Tests that the Employee constructor initialises the employee correctly.
     */
    @Test
    void TestEmployeeConstructor01() {
        Employee newEmployee = new Employee("Abraham");
        assertEquals("Abraham", newEmployee.getName());
    }

    @Test
    void TestEmployeeConstructor02() {
        DatabaseEmployee newDatabaseEmployee = new DatabaseEmployee();
        newDatabaseEmployee.id = 1;
        Employee newEmployee = new Employee(newDatabaseEmployee);

        assertEquals(1,newEmployee.getId());
    }

    @Test
    void TestEmployeeConstructor03() {
        DatabaseEmployee newDatabaseEmployee = new DatabaseEmployee();
        newDatabaseEmployee.name = "Person";
        Employee newEmployee = new Employee(newDatabaseEmployee);

        assertEquals("Person",newEmployee.getName());
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
    }

    /**
     * Testing getting the id of the employee.
     */
    @Test
    void TestEmployeeGetId01() {
        Employee newEmployee = new Employee("The Real Slim Shady");
        newEmployee.setId(1);
        assertEquals(1, newEmployee.getId());
    }

    /**
     * Tests getting the name of the employee.
     */
    @Test
    void TestEmployeeGetName01() {
        Employee newEmployee = new Employee("Employee");
        assertEquals("Employee", newEmployee.getName());
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
    }


    /**
     * Tests setting the employee's id.
     */
    @Test
    void TestEmployeeSetId01() {
        Employee newEmployee = new Employee("Employee");
        newEmployee.setId(5);
        assertEquals(5, newEmployee.getId());
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
    }

    @Test
    void TestEmployeeEquals01() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");
        employee1.setId(1);
        employee2.setId(1);

        assertEquals(employee1, employee2);
    }

    @Test
    void TestEmployeeEquals02() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");
        employee1.setId(1);
        employee2.setId(2);

        assertNotEquals(employee1, employee2);
    }

    @Test
    void TestEmployeeHashcode01() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");
        employee1.setId(1);
        employee2.setId(1);

        assertEquals(employee1.hashCode(), employee2.hashCode());
    }

    @Test
    void TestEmployeeHashcode02() {
        Employee employee1 = new Employee("Test Person1");
        Employee employee2 = new Employee("Test Person2");
        employee1.setId(1);
        employee2.setId(2);

        assertNotEquals(employee1.hashCode(), employee2.hashCode());
    }
}
