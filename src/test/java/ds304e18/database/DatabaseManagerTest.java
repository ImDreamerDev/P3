package ds304e18.database;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

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
     *
     * @throws SQLException
     */
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

}
