package ds304e18.models;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.ProjectState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectStateTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    /**
     * Tests that the ProjectState ongoing have the value of 0
     */
    @Test
    void TestProjectStateGetValue01() {
        assertEquals(0, ProjectState.ONGOING.getValue());
    }

    /**
     * Tests that the ProjectState archived have the value of 1
     */
    @Test
    void TestProjectStateGetValue02() {
        assertEquals(1, ProjectState.ARCHIVED.getValue());
    }

}
