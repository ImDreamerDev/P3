package dk.aau.ds304e18.math;


import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

class ProbabilitiesTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }


    @Test
    void probabilitiesTest() {
        Probabilities probabilities = new Probabilities(10, 34);
        assertEquals(probabilities.getProbability(), 34);
        assertEquals(probabilities.getDuration(), 10);
    }


    @Test
    void toStringTest() {
        Probabilities probabilities = new Probabilities(22, 42);
        assertEquals(probabilities.toString(), "22.0, 42.0");
    }
}
