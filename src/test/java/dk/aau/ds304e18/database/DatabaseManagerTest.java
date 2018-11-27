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


    /**
     * Asserts that addEmployee function in DatabaseManager works.
     **/
   /* @Test
    void testAddEmployee() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project testProj = new Project("TestProj", projectManager);
        Employee testEmp = new Employee("Søren",testProj);
        Task testTask = new Task("TestTask", 10, 1, testProj);


        Employee testGetEmp = DatabaseManager.getEmployee(testEmp.getId());
        assertNotNull(testGetEmp);
        assertEquals(testEmp.getId(), testGetEmp.getId());
        assertEquals(testEmp.getName(), testGetEmp.getName());
        assertTrue(testGetEmp.getPreviousTaskIds().contains(testTask.getId()));
        assertEquals(testGetEmp.getProjectId(), testProj.getId());

        DatabaseManager.removeTask(testTask.getId());
    }*/

   /*TODO: @Test
    void testAddProject() {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project testProj = new Project("TestProj", projectManager);
        String testSequence = "{123,35,234,324,5,6,57,65,7,567,243,235,45,634,346,456,45,67}";
        testProj.setSequence(testSequence);
        Project testGetProj = DatabaseManager.getProject(testProj.getId());
        assertNotNull(testGetProj);
        assertEquals(testProj.getId(), testGetProj.getId());
        assertEquals(testProj.getName(), testGetProj.getName());
        assertEquals(testProj.getState(), testGetProj.getState());
        assertEquals(testProj.getSequence(), testGetProj.getSequence());
        assertEquals(testProj.getDuration(), testGetProj.getDuration());
    }*/

   /* //TODO:@Test
    void testUpdateTask() throws SQLException {
        ProjectManager projectManager = new ProjectManager("Project Manager", "Password");
        Project testProj = new Project("TestProj", projectManager);
        Task testTask = new Task("TestTask", 10, 1, testProj);

        testTask.getProbabilities().add(new Probabilities(32133, 4));
        testTask.getProbabilities().add(new Probabilities(432, 32));
        DatabaseManager.updateTask(testTask);

        ResultSet rs = DatabaseManager.query("SELECT probabilities FROM tasks WHERE id =" + testTask.getId());

        assertNotNull(rs);
        rs.next();

        ResultSet rsw = rs.getArray(1).getResultSet();
        int i = 0;
        while (rsw.next()) {
            String[] probValues = rsw.getString(2).replaceAll("[/(/)]", "").split(",");

            Probabilities probabilities = new Probabilities(Double.parseDouble(probValues[0]),
                    Double.parseDouble(probValues[1]));

            assertEquals(probabilities.getDuration(), testTask.getProbabilities().get(i).getDuration());
            assertEquals(probabilities.getProbability(), testTask.getProbabilities().get(i).getProbability());


            i++;
        }
        DatabaseManager.removeTask(testTask.getId());
    }*/
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
