package ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    void TestTaskConstructor01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals("Test Task", newTask.getName());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskConstructor02() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(1, newTask.getEstimatedTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskConstructor03() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(1, newTask.getPriority());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskConstructor04() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(newProject, newTask.getProject());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

  /* TODO: Fix tests
    
    @Test
    void TestTaskConstructor05() {
        DatabaseTask newDatabaseTask = new DatabaseTask();
        newDatabaseTask.id = 1;
        Task newTask = new Task(newDatabaseTask);

        assertEquals(1,newTask.getId());
    }

    @Test
    void TestTaskConstructor06() {
        DatabaseTask newDatabaseTask = new DatabaseTask();
        newDatabaseTask.name = "Test Task";
        Task newTask = new Task(newDatabaseTask);

        assertEquals("Test Task",newTask.getName());
    }

    @Test
    void TestTaskConstructor07() {
        DatabaseTask newDatabaseTask = new DatabaseTask();
        newDatabaseTask.priority = 2;
        Task newTask = new Task(newDatabaseTask);

        assertEquals(2,newTask.getPriority());
    }

    @Test
    void TestTaskConstructor08() {
        DatabaseTask newDatabaseTask = new DatabaseTask();
        newDatabaseTask.estimatedTime = 10;
        Task newTask = new Task(newDatabaseTask);

        assertEquals(10,newTask.getEstimatedTime());
    }

    @Test
    void TestTaskConstructor09() {
        DatabaseTask newDatabaseTask = new DatabaseTask();
        newDatabaseTask.startTime = 3;
        Task newTask = new Task(newDatabaseTask);

        assertEquals(3,newTask.getStartTime());
    }

    @Test
    void TestTaskConstructor10() {
        DatabaseTask newDatabaseTask = new DatabaseTask();
        newDatabaseTask.endTime = 6;
        Task newTask = new Task(newDatabaseTask);

        assertEquals(6,newTask.getEndTime());
    }*/

    @Test
    void TestTaskGetId01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());

        newTask.setId(5);

        assertEquals(5, newTask.getId());
    }

    @Test
    void TestTaskGetName01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 2, newProject);

        assertEquals("Test Task", newTask.getName());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetEstimatedTime01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 2, newProject);

        assertEquals(1, newTask.getEstimatedTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetPriority01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 2, newProject);

        assertEquals(2, newTask.getPriority());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetEmployee01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Employee newEmployee01 = new Employee("Person01");
        Employee newEmployee02 = new Employee("Person02");

        newTask.addEmployee(newEmployee01, newEmployee02);

        ArrayList<Employee> testList = new ArrayList<>();
        testList.add(newEmployee01);
        testList.add(newEmployee02);

        assertEquals(testList, newTask.getEmployees());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeEmployee(newEmployee01.getId());
        DatabaseManager.removeEmployee(newEmployee02.getId());
    }

    @Test
    void TestTaskGetDependencies01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependency01 = new Task("Dependency01", 1, 1, newProject);
        Task dependency02 = new Task("Dependency02", 1, 1, newProject);

        newTask.addDependency(dependency01, dependency02);

        List<Task> testList = new ArrayList<>();
        testList.add(dependency01);
        testList.add(dependency02);

        assertEquals(testList, newTask.getDependencies());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeTask(dependency01.getId());
        DatabaseManager.removeTask(dependency02.getId());
    }

    @Test
    void TestTaskGetStartTime01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(0, newTask.getStartTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetEndTime01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        newTask.setEndTime(1010);

        assertEquals(1010, newTask.getEndTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetProject01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 4, newProject);

        assertEquals(newProject, newTask.getProject());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskSetEndDate01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        newTask.setEndTime(12.4);

        assertEquals(12.4, newTask.getEndTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskSetPriority01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        newTask.setPriority(5);

        assertEquals(5, newTask.getPriority());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskAddEmployees01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        Employee newEmployee01 = new Employee("Person01");
        Employee newEmployee02 = new Employee("Person02");
        Employee newEmployee03 = new Employee("Person03");

        newTask.addEmployee(newEmployee01, newEmployee02, newEmployee03);

        List<Employee> testList = new ArrayList<>();
        testList.add(newEmployee01);
        testList.add(newEmployee02);
        testList.add(newEmployee03);

        assertEquals(testList, newTask.getEmployees());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeEmployee(newEmployee01.getId());
        DatabaseManager.removeEmployee(newEmployee02.getId());
        DatabaseManager.removeEmployee(newEmployee03.getId());
    }

    @Test
    void TestTaskAddDependencies01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependency01 = new Task("Dependency01", 1, 1, newProject);
        Task dependency02 = new Task("Dependency02", 1, 1, newProject);
        Task dependency03 = new Task("Dependency03", 1, 1, newProject);

        newTask.addDependency(dependency01, dependency02, dependency03);

        List<Task> testList = new ArrayList<>();
        testList.add(dependency01);
        testList.add(dependency02);
        testList.add(dependency03);

        assertEquals(testList, newTask.getDependencies());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeTask(dependency01.getId());
        DatabaseManager.removeTask(dependency01.getId());
        DatabaseManager.removeTask(dependency01.getId());
    }

    @Test
    void TestTaskSetProject() {
        Project project = new Project("Project");
        Task newTask = new Task("Test", 2, 10, project);
        Project newProject = new Project("Test Project");

        newTask.setProject(newProject);

        assertEquals(newProject, newTask.getProject());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskSetEstimatedTime() {
        Project project = new Project("Test Project");
        Task newTask = new Task("Test", 2, 10, project);

        newTask.setEstimatedTime(9.1);

        assertEquals(9.1, newTask.getEstimatedTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + project.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskDistributeAddDependency01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependency01 = new Task("Dependencie01", 1, 1, newProject);

        newTask.distributeAddDependency(dependency01);

        assertEquals(dependency01, newTask.getDependencies().get(0));

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeTask(dependency01.getId());
    }

    @Test
    void TestTaskEquals01() {
        Project newProject = new Project("Test Project");
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task2", 2,2, newProject);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(task1.getId());
        DatabaseManager.removeTask(task2.getId());

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1,task2);
    }

    @Test
    void TestTaskEquals02() {
        Project newProject = new Project("Test Project");
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task1", 1,1, newProject);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(task1.getId());
        DatabaseManager.removeTask(task2.getId());

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1,task2);
    }

    @Test
    void TestTaskHashcode01() {
        Project newProject = new Project("Test Project");
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task2", 2,2, newProject);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(task1.getId());
        DatabaseManager.removeTask(task2.getId());

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1.hashCode(),task2.hashCode());
    }

    @Test
    void TestTaskHashcode02() {
        Project newProject = new Project("Test Project");
        Task task1 = new Task("Task1", 1, 1, newProject);
        Task task2 = new Task("Task2", 1,1, newProject);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(task1.getId());
        DatabaseManager.removeTask(task2.getId());

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1.hashCode(),task2.hashCode());
    }

    @Test
    void TestTaskGetEmployeeIds01() {
        Project newProject = new Project("Test Project");
        Employee newEmployee = new Employee("Employee");

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeEmployee(newEmployee.getId());

        newEmployee.setId(10);

        List<Integer> dependencyIds = new ArrayList<>();
        List<Integer> employeeIds = new ArrayList<>();
        List<Probabilities> probabilities = new ArrayList<>();

        employeeIds.add(newEmployee.getId());

        Task newTask = new Task(1,"Task", 1, 2, 3, 2, dependencyIds, employeeIds, newProject.getId(), probabilities);

        assertEquals(10,newTask.getEmployeeIds().get(0).intValue());
    }

    @Test
    void TestTaskGetDependencyIds01() {
        Project newProject = new Project("Test Project");
        Task dependency = new Task("Dependency", 1,1,newProject);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(dependency.getId());

        dependency.setId(1);

        List<Integer> dependencyIds = new ArrayList<>();
        List<Integer> employeeIds = new ArrayList<>();
        List<Probabilities> probabilities = new ArrayList<>();

        dependencyIds.add(dependency.getId());

        Task newTask = new Task(1, "Task", 1, 2, 3, 2, dependencyIds, employeeIds, newProject.getId(),probabilities);

        assertEquals(1, newTask.getDependencyIds().get(0).intValue());
    }

    @Test
    void TestTaskSetStartTime01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Task", 1,1,newProject);

        newTask.setStartTime(10);

        assertEquals(10, newTask.getStartTime());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetLambda01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Task", 1,1,newProject);

        newTask.setLambda(10);

        assertEquals(10, newTask.getLambda());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

    @Test
    void TestTaskGetLambda02() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("New Task", 1,1,newProject);

        newTask.setLambda(1);

        assertEquals(1, newTask.getLambda());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
    }

}