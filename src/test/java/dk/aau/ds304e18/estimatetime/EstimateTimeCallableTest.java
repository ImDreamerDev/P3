package dk.aau.ds304e18.estimatetime;

import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;

class EstimateTimeCallableTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }


}
