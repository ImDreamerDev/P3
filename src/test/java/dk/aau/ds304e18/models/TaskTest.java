package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    /**
     * Tests that the task constructor initialises the task correctly
     */
    @Test
    void TestTaskConstructor01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals("Test Task", newTask.getName());
    }

    /**
     * Tests that the task constructor initialises the task correctly
     */
    @Test
    void TestTaskConstructor02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(1, newTask.getEstimatedTime());
    }

    /**
     * Tests that the task constructor initialises the task correctly
     */
    @Test
    void TestTaskConstructor03() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(1, newTask.getPriority());
    }

    /**
     * Tests that the task constructor initialises the task correctly
     */
    @Test
    void TestTaskConstructor04() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(newProject, newTask.getProject());
    }

    /**
     * Tests the task getter for id
     */
    @Test
    void TestTaskGetId01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        newTask.setId(5);

        assertEquals(5, newTask.getId());
    }

    /**
     * Tests the task getter for name
     */
    @Test
    void TestTaskGetName01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 2, newProject);

        assertEquals("Test Task", newTask.getName());
    }

    /**
     * Test the task getter for estimate time
     */
    @Test
    void TestTaskGetEstimatedTime01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 2, newProject);

        assertEquals(1, newTask.getEstimatedTime());
    }

    /**
     * Tests the task getter for priority
     */
    @Test
    void TestTaskGetPriority01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 2, newProject);

        assertEquals(2, newTask.getPriority());
    }

    /**
     * Tests the task getter for employee
     */
    @Test
    void TestTaskGetEmployee01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Employee newEmployee01 = new Employee(0, "Person01");
        Employee newEmployee02 = new Employee(1, "Person02");

        newTask.addEmployee(newEmployee01, newEmployee02);

        ArrayList<Employee> testList = new ArrayList<>();
        testList.add(newEmployee01);
        testList.add(newEmployee02);

        assertEquals(testList, newTask.getEmployees());
    }

    /**
     * Tests the task getter for dependencies
     */
    @Test
    void TestTaskGetDependencies01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependency01 = new Task("Dependency01", 1, 1, newProject);
        Task dependency02 = new Task("Dependency02", 1, 1, newProject);

        newTask.addDependency(Arrays.asList(dependency01, dependency02));

        List<Task> testList = new ArrayList<>();
        testList.add(dependency01);
        testList.add(dependency02);

        assertEquals(testList, newTask.getDependencies());
    }


    /**
     * Tests the task getter for project
     */
    @Test
    void TestTaskGetProject01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 4, newProject);

        assertEquals(newProject, newTask.getProject());
    }


    /**
     * Tests the task setter for priority
     */
    @Test
    void TestTaskSetPriority01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        newTask.setPriority(5);

        assertEquals(5, newTask.getPriority());
    }

    /**
     * Tests adding employees to the task
     */
    @Test
    void TestTaskAddEmployees01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        Employee newEmployee01 = new Employee(0, "Person01");
        Employee newEmployee02 = new Employee(1, "Person02");
        Employee newEmployee03 = new Employee(2, "Person03");

        newTask.addEmployee(newEmployee01, newEmployee02, newEmployee03);

        List<Employee> testList = new ArrayList<>();
        testList.add(newEmployee01);
        testList.add(newEmployee02);
        testList.add(newEmployee03);

        assertEquals(testList, newTask.getEmployees());
    }

    /**
     * Tests adding dependencies to the task
     */
    @Test
    void TestTaskAddDependencies01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependency01 = new Task("Dependency01", 1, 1, newProject);
        Task dependency02 = new Task("Dependency02", 1, 1, newProject);
        Task dependency03 = new Task("Dependency03", 1, 1, newProject);

        newTask.addDependency(Arrays.asList(dependency01, dependency02, dependency03));

        List<Task> testList = new ArrayList<>();
        testList.add(dependency01);
        testList.add(dependency02);
        testList.add(dependency03);

        assertEquals(testList, newTask.getDependencies());
    }

    /**
     * Tests the task setter for project
     */
    @Test
    void TestTaskSetProject() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Project", projectManager);
        Task newTask = new Task("Test", 2, 10, project);
        Project newProject = new Project("Test Project", projectManager);

        newTask.setProject(newProject);

        assertEquals(newProject, newTask.getProject());
    }

    /**
     * Tests the task setter for estimated time
     */
    @Test
    void TestTaskSetEstimatedTime() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project = new Project("Test Project", projectManager);
        Task newTask = new Task("Test", 2, 10, project);

        newTask.setEstimatedTime(9.1);

        assertEquals(9.1, newTask.getEstimatedTime());
    }

    /**
     * Tests adding dependencies to the task
     */
    @Test
    void TestTaskDistributeAddDependency01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependency01 = new Task("Dependencie01", 1, 1, newProject);

        newTask.distributeAddDependency(dependency01);

        assertEquals(dependency01, newTask.getDependencies().get(0));
    }

    /**
     * Tests equals for the task
     */
    @Test
    void TestTaskEquals01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task2", 2, 2, newProject);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    /**
     * Tests equals for the task
     */
    @Test
    void TestTaskEquals02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task1", 1, 1, newProject);

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2);
    }

    /**
     * Tests hashcode for task
     */
    @Test
    void TestTaskHashcode01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task2", 2, 2, newProject);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode());
    }

    /**
     * Tests hashcode for task
     */
    @Test
    void TestTaskHashcode02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task2", 1, 1, newProject);

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    /**
     * Tests the task getter for employee
     */
    @Test
    void TestTaskGetEmployeeIds01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Employee newEmployee = new Employee(0, "Employee");

        newEmployee.setId(10);

        List<Integer> dependencyIds = new ArrayList<>();
        List<Integer> employeeIds = new ArrayList<>();
        List<Probabilities> probabilities = new ArrayList<>();

        employeeIds.add(newEmployee.getId());

        Task newTask = new Task(1, "Task", 1, 2, dependencyIds, employeeIds, newProject.getId(), probabilities, 1);

        assertEquals(10, newTask.getEmployeeIds().get(0).intValue());
    }

    /**
     * Tests the task getter for dependencies
     */
    @Test
    void TestTaskGetDependencyIds01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task dependency = new Task("Dependency", 1, 1, newProject);

        dependency.setId(1);

        List<Integer> dependencyIds = new ArrayList<>();
        List<Integer> employeeIds = new ArrayList<>();
        List<Probabilities> probabilities = new ArrayList<>();

        dependencyIds.add(dependency.getId());

        Task newTask = new Task(1, "Task", 1, 2, dependencyIds, employeeIds, newProject.getId(), probabilities, -1);

        assertEquals(1, newTask.getDependencyIds().get(0).intValue());
    }


    /**
     * Tests the tasks toString returns the name correct.
     */
    @Test
    void TestTaskToString01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals("Test Task", newTask.toString());
    }

    @Test
    void TestTaskSetStartTime() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);
        newTask.setStartTime(50);

        assertEquals(newTask.getStartTime(), 50);
    }

    @Test
    void TestTaskGetProjectId() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(newProject.getId(), newTask.getProjectId());
    }

    @Test
    void TestTaskGetInvG() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 1, 1, newProject);
        assertEquals(newTask.getInvG().getLambda(), 0);
    }
}