package dk.aau.ds304e18.math;

import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;

class CalculateLambdaTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

}
