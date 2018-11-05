package ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectTest {

    /**
     * Tests that the Project constructor initialises the project correctly.
     */
    @Test
    void TestProjectConstructor01() {
        ProjectManager projectManager = new ProjectManager("Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        assertEquals("Test Project", newProject.getName());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

   /* TODO: Fix tests
    @Test
    void TestProjectConstructor02() {
        DatabaseProject newDatabaseProject = new DatabaseProject();
        newDatabaseProject.id = 1;
        Project newProject = new Project(newDatabaseProject);

        assertEquals(1, newProject.getId());
    }

    @Test
    void TestProjectConstructor03() {
        DatabaseProject newDatabaseProject = new DatabaseProject();
        newDatabaseProject.name = "Test Project";
        Project newProject = new Project(newDatabaseProject);

        assertEquals("Test Project", newProject.getName());
    }

    @Test
    void TestProjectConstructor04() {
        DatabaseProject newDatabaseProject = new DatabaseProject();
        newDatabaseProject.state = ProjectState.ONGOING;
        Project newProject = new Project(newDatabaseProject);

        assertEquals(ProjectState.ONGOING, newProject.getState());
    }*/

    /**
     * Tests adding a new task to the project.
     */
    @Test
    void TestProjectAddNewTask01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 2, 1, newProject);

        newProject.addNewTask(newTask);
        assertEquals(newTask, newProject.getTasks().get(0));

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests removing a task from the project.
     */
    @Test
    void TestProjectRemoveTask01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask = new Task("Test Task", 2, 1, newProject);

        newProject.addNewTask(newTask);
        newProject.removeTask(newTask);

        assertTrue(newProject.getTasks().isEmpty());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests adding a new employee to the project.
     */
    @Test
    void TestProjectAddNewEmployee01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Employee newEmployee = new Employee("Test Person");

        newProject.addNewEmployee(newEmployee);

        assertEquals(newEmployee, newProject.getEmployees().get(0));

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests removing a employee from the project.
     */
    @Test
    void TestProjectRemoveEmployee01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Employee newEmployee = new Employee("Test Person");

        newProject.addNewEmployee(newEmployee);
        newProject.removeEmployee(newEmployee);

        assertTrue(newProject.getEmployees().isEmpty());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeEmployee(newEmployee.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests getting the id of the project.
     */
    @Test
    void TestProjectGetId01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Slim Shady Project", projectManager);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());

        newProject.setId(5);

        assertEquals(5, newProject.getId());
    }

    /**
     * Tests getting the name of the project.
     */
    @Test
    void TestProjectGetName01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        assertEquals("Test Project", newProject.getName());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests getting the project state of the project.
     */
    @Test
    void TestProjectGetState01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        assertEquals(ProjectState.ONGOING, newProject.getState());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests getting the project's tasks.
     */
    @Test
    void TestProjectGetTasks01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Task newTask01 = new Task("Test Task01", 2, 5, newProject);
        Task newTask02 = new Task("Test Task02", 1, 2, newProject);

        newProject.addNewTask(newTask01, newTask02);

        List<Task> testList = new ArrayList<>();
        testList.add(newTask01);
        testList.add(newTask02);

        assertEquals(testList, newProject.getTasks());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeTask(newTask01.getId());
        DatabaseManager.removeTask(newTask02.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests getting the employees of the project.
     */
    @Test
    void TestProjectGetEmployees01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);
        Employee newEmployee01 = new Employee("Person01");
        Employee newEmployee02 = new Employee("Person02");

        newProject.addNewEmployee(newEmployee01, newEmployee02);

        List<Employee> testList = new ArrayList<>();
        testList.add(newEmployee01);
        testList.add(newEmployee02);

        assertEquals(testList, newProject.getEmployees());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeEmployee(newEmployee01.getId());
        DatabaseManager.removeEmployee(newEmployee02.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    /**
     * Tests setting the id of the project.
     */
    @Test
    void TestProjectSetId01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Slim Project", projectManager);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());

        newProject.setId(3);

        assertEquals(3, newProject.getId());
    }

    @Test
    void TestProjectSetState01 () {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        newProject.setState(ProjectState.ONGOING);

        assertEquals(ProjectState.ONGOING, newProject.getState());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    @Test
    void TestProjectSetState02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        newProject.setState(ProjectState.ARCHIVED);

        assertEquals(ProjectState.ARCHIVED,newProject.getState());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    @Test
    void TestProjectEquals01(){
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        ProjectManager projectManager2 = new ProjectManager("Project Manager", "Password");
        Project project1 = new Project("Project1", projectManager);
        Project project2 = new Project("Project2", projectManager2);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + project1.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project2.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
        DatabaseManager.removeProjectManager(projectManager2.getId());

        project1.setId(1);
        project2.setId(1);

        assertEquals(project1,project2);
    }

    @Test
    void TestProjectEquals02(){
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        ProjectManager projectManager2 = new ProjectManager("Project Manager", "Password");
        Project project1 = new Project("Project1", projectManager);
        Project project2 = new Project("Project2", projectManager2);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + project1.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project2.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
        DatabaseManager.removeProjectManager(projectManager2.getId());

        project1.setId(1);
        project2.setId(2);

        assertNotEquals(project1,project2);
    }

    @Test
    void TestProjectHashcode01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        ProjectManager projectManager2 = new ProjectManager("Project Manager", "Password");
        Project project1 = new Project("Project1", projectManager);
        Project project2 = new Project("Project2", projectManager2);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + project1.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project2.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
        DatabaseManager.removeProjectManager(projectManager2.getId());

        project1.setId(1);
        project2.setId(1);

        assertEquals(project1.hashCode(), project2.hashCode());
    }

    @Test
    void TestProjectHashcode02() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project project1 = new Project("Project1", projectManager);
        Project project2 = new Project("Project2", projectManager);

        DatabaseManager.query("DELETE FROM projects WHERE id = " + project1.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + project2.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());

        project1.setId(1);
        project2.setId(2);

        assertNotEquals(project1.hashCode(), project2.hashCode());
    }

    @Test
    void TestProjectGetDuration01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        newProject.setDuration(10.5);

        assertEquals(10.5, newProject.getDuration());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }

    @Test
    void TestProjectSetDuration01() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project newProject = new Project("Test Project", projectManager);

        newProject.setDuration(1.5);

        assertEquals(1.5, newProject.getDuration());

        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
        DatabaseManager.removeProjectManager(projectManager.getId());
    }
}
