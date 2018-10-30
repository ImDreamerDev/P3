package ds304e18.models;

import dk.aau.ds304e18.models.ProjectState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectStateTest {

    @Test
    void TestProjectStateGetValue01() {
        assertEquals(0,ProjectState.ONGOING.getValue());
    }

    @Test
    void TestProjectStateGetValue03() {
        assertEquals(2,ProjectState.ARCHIVED.getValue());
    }

}
