package ds304e18.models;

import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.ProjectManager;
import dk.aau.ds304e18.models.ProjectState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectManagerTest {

    @Test
    void TestProjectManagerConstructor(){
        ProjectManager newProjectManager = new ProjectManager("Adam");

        assertEquals("Adam",newProjectManager.getName());

    }

    @Test
    void TestProjectManagerGetID(){
        ProjectManager newProjectManager = new ProjectManager("Adam");

    }

    @Test
    void TestProjectManagerGetName(){
        ProjectManager newProjectManager = new ProjectManager("Adam");
        assertEquals("Adam",newProjectManager.getName());
    }
    @Test
    void TestProjectManagerGetCurrentProject(){
        ProjectManager newProjectManager = new ProjectManager("Adam");
        Project newProject = new Project(1,"TestProject",ProjectState.ONGOING,"");
        newProjectManager.setCurrentProject(newProject);
        assertEquals(newProject, newProjectManager.getCurrentProject());
    }
    @Test
    void TestProjectManagerGetOldProjects(){
        ProjectManager newProjectManager = new ProjectManager("Adam");
        Project newProject = new Project(1,"TestProject",ProjectState.ONGOING,"");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(newProjectManager.getOldProjects().size(),1);
    }
    @Test
    void TestProjectManagerGetOldProjects2() {
        ProjectManager newProjectManager = new ProjectManager("Adam");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(newProjectManager.getCurrentProject(),null);
    }
    @Test
    void TestProjectManagerGetOldProjects3() {
        ProjectManager newProjectManager = new ProjectManager("Adam");
        Project newProject = new Project(1, "TestProject", ProjectState.ONGOING, "");
        newProjectManager.setCurrentProject(newProject);
        newProjectManager.addOldProject(newProject);
        assertEquals(newProjectManager.getOldProjects().get(0).getState(),ProjectState.ARCHIVED);
    }
}
