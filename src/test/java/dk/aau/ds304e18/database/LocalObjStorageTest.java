package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocalObjStorageTest {
    ProjectManager projectManager = new ProjectManager(1, "Jens", null, null);
    Employee employee = new Employee("Jens", null);
    Project project = new Project(1, "Proejct", ProjectState.ONGOING, null, 20d,
            null, 1, null);

    Task task = new Task(1, "task", 20, 5,
            null, null, 1, null, 1);


    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void testGetEmployees() {
        employee = new Employee("Jens");
        LocalObjStorage.addEmployee(employee);

        assertTrue(LocalObjStorage.getEmployeeList().contains(employee));
    }

    @Test
    void testGetEmployeeById() {
        employee = new Employee("Jens");
        LocalObjStorage.addEmployee(employee);

        assertEquals(LocalObjStorage.getEmployeeById(employee.getId()), employee);
    }

    @Test
    void testGetProjectById() {
        assertNotNull(project);
        LocalObjStorage.addProject(project);

        assertEquals(LocalObjStorage.getProjectById(project.getId()), project);
    }

    @Test
    void testGetProjectManagerById() {
        assertNotNull(projectManager);
        LocalObjStorage.addProjectManager(projectManager);

        assertEquals(LocalObjStorage.getProjectManagerById(projectManager.getId()), projectManager);
    }

    @Test
    void testGetTaskById() {
        assertNotNull(task);
        LocalObjStorage.addTask(task);

        assertEquals(LocalObjStorage.getTaskById(task.getId()), task);
    }
}
