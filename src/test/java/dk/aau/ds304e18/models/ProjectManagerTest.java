package dk.aau.ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
