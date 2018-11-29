package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }
    
    @Test
    void testLogOut() {
        ProjectManager projectManager = DatabaseManager.logIn("Project Manager", "Password");

        if (projectManager == null) {
            new ProjectManager("Project Manager", "Password");
            projectManager = DatabaseManager.logIn("Project Manager", "Password");
        }
        LocalObjStorage.addProjectManager(projectManager);

        assertNotNull(projectManager);
        Project testProj = new Project("TestProj", projectManager);
        Task testTask = new Task("TestTask", 10, 1, testProj);

        assertTrue(LocalObjStorage.getTaskList().size() > 0);
        assertTrue(LocalObjStorage.getProjectList().size() > 0);
        assertTrue(LocalObjStorage.getProjectManagerList().size() > 0);

        DatabaseManager.logOut();

        assertEquals(LocalObjStorage.getTaskList().size(), 0);
        assertEquals(LocalObjStorage.getProjectList().size(), 0);
        assertEquals(LocalObjStorage.getProjectManagerList().size(), 0);
        DatabaseManager.removeTask(testTask.getId());
    }
}
