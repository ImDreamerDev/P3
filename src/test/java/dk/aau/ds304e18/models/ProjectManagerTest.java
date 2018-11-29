package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The tests for the ProjectManager class.
 */
class ProjectManagerTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    /**
     * Test for the Constructor.
     */
    @Test
    void TestProjectManagerConstructor() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        assertEquals("Adam", newProjectManager.getName());
    }

    /**
     * Test for constructor2
     */
    @Test
    void TestProjectManagerConstructor2() {
        ProjectManager newProjectManager = new ProjectManager(1, "Peter", null, null);
        assertEquals("Peter", newProjectManager.getName());
    }

    /**
     * Tests for the 3rd type of constructor.
     * This uses id, name, currentProjectId, and a list of old projects.
     */
    @Test
    void TestProjectManagerConstructor3() {
        ProjectManager newProjectManager = new ProjectManager(1, "Peter", null, Arrays.asList(1, 3, 4));
        assertEquals("Peter", newProjectManager.getName());
    }

    /**
     * Test for the toString method.
     */
    @Test
    void TestProjectManagerToString() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        assertEquals("Adam", newProjectManager.toString());
    }

    /**
     * Test for the setter and getter of the ID.
     */
    @Test
    void TestProjectManagerGetId() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        newProjectManager.setId(1);
        assertEquals(1, newProjectManager.getId());
    }

    /**
     * Test fot the getter for the name.
     */
    @Test
    void TestProjectManagerGetName() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        assertEquals("Adam", newProjectManager.getName());
    }

    /**
     * Test for the getter of the currentProject.
     */
    @Test
    void TestProjectManagerGetCurrentProject() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0, "", 1, null);
        newProjectManager.addCurrentProject(newProject);
        assertTrue(newProjectManager.getCurrentProjects().contains(newProject));
    }

    /**
     * Test for the getter of the currentProjectIds.
     */
    @Test
    void TestProjectManagerGetCurrentProjectId() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0, "", 1, null);
        newProjectManager.getCurrentProjectIds().add(newProject.getId());
        assertTrue(newProjectManager.getCurrentProjectIds().contains(1));
    }

    @Test
    void TestProjectDistributeAddCurrentProject() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0, "", 1, null);
        newProjectManager.distributeAddCurrentProject(newProject);
        assertNotNull(newProjectManager.getCurrentProjects().get(0));
    }

    @Test
    void TestProjectManagerArchiveProject(){
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0, "", 1, null);
        newProjectManager.archiveProject(newProject);
        assertEquals(ProjectState.ARCHIVED, newProject.getState());
    }
    @Test
    void TestProjectManagerArchiveProject2(){
        Project newProject = new Project(-1, "TestProject", ProjectState.ONGOING, "", 0, "", 1, null);
        Employee newEmployee = new Employee("Tim",newProject);
        Task newTask = new Task(-1,"Dish",1,1, new ArrayList<>(),new ArrayList<>(),-1,new ArrayList<>(),10);

        newProject.addNewEmployee(newEmployee);
        newEmployee.addNewTask(newTask);

        ProjectManager newProjectManager = new ProjectManager(-1,"Adam", new ArrayList<>(), new ArrayList<>());
        newProjectManager.archiveProject(newProject);
        assertEquals(null,newEmployee.getProject());

    }
    @Test
    void TestProjectManagerArchiveProject3(){
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0, "", 1, null);
        newProjectManager.getOldProjectsId().add(newProject.getId());
        newProjectManager.archiveProject(newProject);
        assertEquals(ProjectState.ARCHIVED, newProject.getState());
    }

}
