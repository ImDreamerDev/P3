package ds304e18.database;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.math.Probabilities;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseManagerTest {

    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }


    /**
     * Asserts that addEmployee function in DatabaseManager works.
     **/
    @Test
    void testAddEmployee() {
        Employee testEmp = new Employee("Søren");

        Employee testGetEmp = DatabaseManager.getEmployee(testEmp.getId());
        assertNotNull(testGetEmp);
        assertEquals(testEmp.getId(), testGetEmp.getId());
        assertEquals(testEmp.getName(), testGetEmp.getName());

        DatabaseManager.removeEmployee(testEmp.getId());
    }

    @Test
    void testAddProject() throws SQLException {
        Project testProj = new Project("TestProj");
        Project testGetProj = DatabaseManager.getProject(testProj.getId());
        assertNotNull(testGetProj);
        assertEquals(testProj.getId(), testGetProj.getId());
        assertEquals(testProj.getName(), testGetProj.getName());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + testProj.getId());
    }

    @Test
    void testAddTask() throws SQLException {
        Project testProj = new Project("TestProj");
        Task testTask = new Task("TestTask", 10, 1, testProj);
        Task testGetTask = DatabaseManager.getTask(testTask.getId());
        assertNotNull(testGetTask);
        assertEquals(testTask.getId(), testGetTask.getId());
        assertEquals(testTask.getName(), testGetTask.getName());
        assertEquals(testTask.getEstimatedTime(), testGetTask.getEstimatedTime(), 0.001);
        assertEquals(testTask.getProject().getId(), testProj.getId());
        DatabaseManager.removeTask(testTask.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + testProj.getId());
    }

    @Test
    void testUpdateTask() throws SQLException {
        Project testProj = new Project("TestProj");
        Task testTask = new Task("TestTask", 10, 1, testProj);
        testTask.getProbabilities().add(new Probabilities(32133, 4));
        testTask.getProbabilities().add(new Probabilities(432, 32));
        DatabaseManager.updateTask(testTask);

        ResultSet rs = DatabaseManager.query("SELECT probabilities FROM tasks WHERE id =" + testTask.getId());
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
        DatabaseManager.query("DELETE FROM projects WHERE id = " + testProj.getId());

    }


}
