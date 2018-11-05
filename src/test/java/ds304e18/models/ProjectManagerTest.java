package ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.ProjectState;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The tests for the ProjectManager class.
 */
public class ProjectManagerTest {

    /**
     * Test for the Constructor.
     */
    @Test
    void TestProjectManagerConstructor() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        assertEquals("Adam", newProjectManager.getName());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
    }

    @Test
    void TestProjectManagerConstructor2() {
        ProjectManager newProjectManager = new ProjectManager(1, "Peter", 1, null);
        assertEquals("Peter", newProjectManager.getName());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
    }
    @Test
    void TestProjectManagerConstructor3() {
        ProjectManager newProjectManager = new ProjectManager(1, "Peter", 1, Arrays.asList(1,3,4));
        assertEquals("Peter", newProjectManager.getName());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
    }
    @Test
    void TestProjectManagerToString() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        assertEquals("Adam", newProjectManager.toString());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
    }

    /**
     * Test for the setter and getter of the ID.
     */
    @Test
    void TestProjectManagerGetID() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        DatabaseManager.removeProjectManager(newProjectManager.getId());
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
        DatabaseManager.removeProjectManager(newProjectManager.getId());
    }

    /**
     * Test for the getter of the currentProject.
     */
    @Test
    void TestProjectManagerGetCurrentProject() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        assertEquals(newProject, newProjectManager.getCurrentProject());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

    @Test
    void TestProjectManagerGetCurrentProjectID() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        assertEquals(0, newProjectManager.getCurrentProjectId());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

    /**
     * Test for the getter of the oldProjects list.
     * Tests to see if a project is succesfully added to the list.
     */
    @Test
    void TestProjectManagerGetOldProjects() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(newProjectManager.getOldProjects().size(), 1);
        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

    /**
     * 2nd Test for the getter of the oldProjects list.
     * Tests if the currentProject is moved to the oldProjects list, the currentProject is set to null.
     */
    @Test
    void TestProjectManagerGetOldProjects2() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(newProjectManager.getCurrentProject(), null);
        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

    /**
     * 3rd Test for the getter of the oldProjects list.
     * Tests if an ongoing project is moved to oldProjects, the state changes to archived.
     */
    @Test
    void TestProjectManagerGetOldProjects3() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(newProjectManager.getOldProjects().get(0).getState(), ProjectState.ARCHIVED);
        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

    @Test
    void TestProjectManagerGetOldProjects4() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(1, newProjectManager.getOldProjectsId().get(0).intValue());
        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }
    @Test
    void TestProjectManagerAddOldProject() {
        ProjectManager newProjectManager = new ProjectManager("Adam", "test");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "", 0,"");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(1,newProjectManager.getOldProjects().size());

        DatabaseManager.removeProjectManager(newProjectManager.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + newProject.getId());
    }

}
