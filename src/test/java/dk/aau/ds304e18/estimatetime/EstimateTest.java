package dk.aau.ds304e18.estimatetime;

import dk.aau.ds304e18.database.DatabaseManager;
import dk.aau.ds304e18.models.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EstimateTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }


    @Test
    void estimateTest() {
        Estimate estimate = new Estimate(Arrays.asList(2.0, 5.2, 1.5, 0.5, 2.5), 20);
        assertNotNull(estimate);
    }

    @Test
    void getDurationTest() {
        Estimate estimate = new Estimate(null, 20);
        assertEquals(estimate.getDuration(), 20);
    }

    @Test
    void setDurationTest() {
        Estimate estimate = new Estimate(null, 20);
        assertEquals(estimate.getDuration(), 20);
        estimate.setDuration(30);
        assertEquals(estimate.getDuration(), 30);
    }

    @Test
    void getChancesTest() {
        Estimate estimate = new Estimate(Arrays.asList(2.0, 5.2, 1.5, 0.5, 2.5), 20);
        assertEquals(estimate.getChances(), Arrays.asList(2.0, 5.2, 1.5, 0.5, 2.5));
    }
}
