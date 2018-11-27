package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EmployeeTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    /**
     * Tests that the Employee constructor initialises the employee correctly.
     */
    @Test
    void TestEmployeeConstructor01() {
        Employee newEmployee = new Employee(0, "Abraham");

        assertEquals("Abraham", newEmployee.getName());
    }


    @Test
    void TestEmployeeConstructor02() {
        Employee newEmployee = new Employee(1, "Test Person");

        assertEquals(1, newEmployee.getId());
    }


    @Test
    void TestEmployeeConstructor03() {
        Employee newEmployee = new Employee(1, "Kasper");

        assertEquals("Kasper", newEmployee.getName());
    }


    @Test
    void TestEmployeeConstructor05() {
        Project project = new Project(-1, "Test Project",
                ProjectState.ONGOING, "", 55.0, "", 5, new ArrayList<>());
        Employee newEmployee = new Employee("Tom", project);

        assertEquals(project, newEmployee.getProject());
    }


    /**
     * Tests that adding a task to the employee works.
     */
    @Test
    void TestEmployeeAddNewTask01() {
        Employee newEmployee = new Employee(0, "Slim Shady");
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 5, 1, newProject);

        newEmployee.addNewTask(newTask);

        assertEquals(newTask, newEmployee.getCurrentTask().get(0));
    }

    /**
     * Testing getting the id of the employee.
     */
    @Test
    void TestEmployeeGetId01() {
        Employee newEmployee = new Employee(0, "The Real Slim Shady");

        newEmployee.setId(1000);

        assertEquals(1000, newEmployee.getId());
    }

    /**
     * Tests getting the name of the employee.
     */
    @Test
    void TestEmployeeGetName01() {
        Employee newEmployee = new Employee(0, "Employee");
        assertEquals("Employee", newEmployee.getName());
    }

    /**
     * Tests getting the employee's assigned project.
     */
    @Test
    void TestEmployeeGetProject01() {
        Employee newEmployee = new Employee(0, "Employee");
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        newEmployee.setProject(newProject);

        assertEquals(newProject, newEmployee.getProject());
    }

    /**
     * Tests getting the employee's current tasks.
     */
    @Test
    void TestEmployeeGetCurrentTask01() {
        Employee newEmployee = new Employee(0, "Test Employee");
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 5, 1, newProject);

        newEmployee.addNewTask(newTask);

        assertEquals(newTask, newEmployee.getCurrentTask().get(0));
    }


    /**
     * Tests setting the employee's id.
     */
    @Test
    void TestEmployeeSetId01() {
        Employee newEmployee = new Employee(0, "Employee");
        newEmployee.setId(5);

        assertEquals(5, newEmployee.getId());
    }

    /**
     * Tests setting the employee's project.
     */
    @Test
    void TestEmployeeSetProject01() {
        Employee newEmployee = new Employee(0, "Employee");
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        newEmployee.setProject(newProject);

        assertEquals(newProject, newEmployee.getProject());
    }

    /**
     * Tests the employee equals
     */
    @Test
    void TestEmployeeEquals01() {
        Employee employee1 = new Employee(0, "Test Person1");
        Employee employee2 = new Employee(1, "Test Person2");

        employee1.setId(1);
        employee2.setId(1);

        assertEquals(employee1, employee2);
    }

    /**
     * Tests the employee equals
     */
    @Test
    void TestEmployeeEquals02() {
        Employee employee1 = new Employee(0, "Test Person1");
        Employee employee2 = new Employee(1, "Test Person2");

        employee1.setId(1);
        employee2.setId(2);

        assertNotEquals(employee1, employee2);
    }

    /**
     * Tests the employee hashcode
     */
    @Test
    void TestEmployeeHashcode01() {
        Employee employee1 = new Employee(0, "Test Person1");
        Employee employee2 = new Employee(1, "Test Person2");


        employee1.setId(1);
        employee2.setId(1);

        assertEquals(employee1.hashCode(), employee2.hashCode());
    }

    /**
     * Tests the employee hashcode
     */
    @Test
    void TestEmployeeHashcode02() {
        Employee employee1 = new Employee(0, "Test Person1");
        Employee employee2 = new Employee(1, "Test Person2");

        employee1.setId(1);
        employee2.setId(2);

        assertNotEquals(employee1.hashCode(), employee2.hashCode());
    }

    /**
     * Tests adding a a task to employee
     */
    @Test
    void TestEmployeeDistributeAddTask01() {
        Employee newEmployee = new Employee(0, "Lars");
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Task", 1, 1, newProject);

        newEmployee.distributeAddTask(newTask);

        assertEquals(newTask, newEmployee.getCurrentTask().get(0));
    }

    @Test
    void TestToString() {
        assertEquals(new Employee(0, "Lars").toString(), "Lars");
    }

    @Test
    void TestSetProjectId() {
        Employee lars = new Employee(0, "Lars");
        lars.setProjectId(1);
        assertEquals(lars.getProjectId(), 1);
    }
}
