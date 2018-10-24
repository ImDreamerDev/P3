package ds304e18.models.DatabaseManager;

import dk.aau.ds304e18.models.database.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    @Test
    void assertQuery() {
        ResultSet rs = DatabaseManager.query("SELECT * FROM employees");
        assertNotNull(rs);
    }
}
