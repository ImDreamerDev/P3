package ds304e18.models;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    @Test
    public void TestTaskConstructor01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        assertEquals("Test Task", newTask.getName());
    }

    @Test
    public void TestTaskConstructor02() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        assertEquals(1, newTask.getEstimatedTime());
    }

    @Test
    public void TestTaskConstructor03() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        assertEquals(1, newTask.getPriority());
    }

    @Test
    public void TestTaskConstructor04() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        assertEquals(newProject, newTask.getProject());
    }

    @Test
    public void TestTaskGetId01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        newTask.setId(5);
        assertEquals(5, newTask.getId());
    }

    @Test
    public void TestTaskGetName01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        assertEquals("Test Task", newTask.getName());
    }

    @Test
    public void TestTaskGetEstimatedTime01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 2, newProject);
        assertEquals(1, newTask.getEstimatedTime());
    }

    @Test
    public void TestTaskGetPriority01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 2, newProject);
        assertEquals(2, newTask.getPriority());
    }

    @Test
    public void TestTaskGetEmployee01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Employee newEmployee01 = new Employee("Person01");
        Employee newEmployee02 = new Employee("Person02");
        newTask.addEmployee(newEmployee01, newEmployee02);

        ArrayList<Employee> testList = new ArrayList<>();
        testList.add(newEmployee01);
        testList.add(newEmployee02);

        assertEquals(testList, newTask.getEmployees());
    }

    @Test
    public void TestTaskGetDependencies01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependencie01 = new Task("Dependencie01", 1, 1, newProject);
        Task dependencie02 = new Task("Dependencie02", 1, 1, newProject);
        newTask.addDependency(dependencie01, dependencie02);

        List<Task> testList = new ArrayList<>();
        testList.add(dependencie01);
        testList.add(dependencie02);

        assertEquals(testList, newTask.getDependencies());
    }

    @Test
    public void TestTaskGetStartDate01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(0, newTask.getStartTime());
    }

    @Test
    public void TestTaskGetEndDate01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        newTask.setEndTime(1010);

        assertEquals(1010, newTask.getEndTime());
    }

    @Test
    public void TestTaskGetProject01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);

        assertEquals(newProject, newTask.getProject());
    }

    @Test
    public void TestTaskSetEndDate01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        newTask.setEndTime(12.4);

        assertEquals(12.4, newTask.getEndTime());
    }

    @Test
    public void TestTaskSetPriority01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        newTask.setPriority(5);

        assertEquals(5, newTask.getPriority());
    }

    @Test
    public void TestTaskAddEmployees01() {
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
    }

    @Test
    public void TestTaskAddDependencies01() {
        Project newProject = new Project("Test Project");
        Task newTask = new Task("Test Task", 1, 1, newProject);
        Task dependencie01 = new Task("Dependencie01", 1, 1, newProject);
        Task dependencie02 = new Task("Dependencie02", 1, 1, newProject);
        Task dependencie03 = new Task("Dependencie03", 1, 1, newProject);
        newTask.addDependency(dependencie01, dependencie02, dependencie03);

        List<Task> testList = new ArrayList<>();
        testList.add(dependencie01);
        testList.add(dependencie02);
        testList.add(dependencie03);

        assertEquals(testList, newTask.getDependencies());
    }

    @Test
    public void TestTaskSetProject() {
        Project project = new Project("Reeer");
        Task newTask = new Task("Test", 2, 10, project);
        Project newProject = new Project("Test Project");
        newTask.setProject(newProject);

        assertEquals(newProject, newTask.getProject());
    }

    @Test
    public void TestSetEstimatedTime() {
        Project project = new Project("Reeer");
        Task newTask = new Task("Test", 2, 10, project);
        newTask.setEstimatedTime(9);

        assertEquals(9, newTask.getEstimatedTime());
    }
}
