package ds304e18.database;

import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {


    /**
     * Tests that the result from the employees table is not null.
     */
    @Test
    void assertQuery() {
        DatabaseManager.isTests = true;
        ResultSet rs = DatabaseManager.query("SELECT * FROM employees");
        assertNotNull(rs);
    }
}
