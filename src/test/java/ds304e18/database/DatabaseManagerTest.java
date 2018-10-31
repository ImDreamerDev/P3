package ds304e18.database;

import dk.aau.ds304e18.database.DatabaseManager;
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
        Employee testEmp = new Employee("Ur mum");

        ResultSet rs = DatabaseManager.query("SELECT * FROM employees WHERE id = " + testEmp.getId());
        assertNotNull(rs);
        try {
            rs.next();
            assertEquals(testEmp.getId(), rs.getInt(1));
            assertEquals(testEmp.getName(), rs.getString(2));

            DatabaseManager.query("DELETE FROM employees WHERE id = " + testEmp.getId());
        } catch (SQLException ignored) {

        }
    }

    @Test
    void testAddProject() throws SQLException {
        Project testProj = new Project("TestProj");
        ResultSet rs = DatabaseManager.query("SELECT * FROM projects WHERE id = " + testProj.getId());
        assertNotNull(rs);
        rs.next();
        assertEquals(testProj.getId(), rs.getInt(1));
        assertEquals(testProj.getName(), rs.getString(2));
        DatabaseManager.query("DELETE FROM projects WHERE id = " + testProj.getId());
    }

    @Test
    void testAddTask() throws SQLException {
        Project testProj = new Project("TestProj");
        Task testTask = new Task("TestTask", 10, 1, testProj);
        ResultSet rs = DatabaseManager.query("SELECT * FROM tasks WHERE id = " + testTask.getId());
        assertNotNull(rs);
        rs.next();
        assertEquals(testTask.getId(), rs.getInt(1));
        assertEquals(testTask.getName(), rs.getString(2));
        assertEquals(testTask.getEstimatedTime(), rs.getDouble(3), 0.001);
        assertEquals(testTask.getProject().getId(), testProj.getId());
        DatabaseManager.query("DELETE FROM tasks WHERE id = " + testTask.getId());
        DatabaseManager.query("DELETE FROM projects WHERE id = " + testProj.getId());
    }
}
