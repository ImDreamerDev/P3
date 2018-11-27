package dk.aau.ds304e18.math;

import dk.aau.ds304e18.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateLambdaTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

    @Test
    void CalculateLambda01() {
        Probabilities probability1 = new Probabilities(1, 10);
        Probabilities probability2 = new Probabilities(2, 30);
        Probabilities probability3 = new Probabilities(3, 60);

        List<Probabilities> probabilities = new ArrayList<>();
        probabilities.add(probability1);
        probabilities.add(probability2);
        probabilities.add(probability3);

        assertEquals(CalculateLambda.calculateLambda(4, probabilities).get(0), 3.2, 0.01);
    }

    @Test
    void CalculateLambda02() {
        assertEquals(CalculateLambda.calculateLambda(4, new ArrayList<>()).get(0), 4.4, 0.01);
    }

    @Test
    void CalculateLambda03() {
        Probabilities probability1 = new Probabilities(1, 10);
        Probabilities probability2 = new Probabilities(2, 30);
        Probabilities probability3 = new Probabilities(3, 60);

        List<Probabilities> probabilities = new ArrayList<>();
        probabilities.add(probability1);
        probabilities.add(probability2);
        probabilities.add(probability3);

        assertEquals(CalculateLambda.calculateLambda(4, probabilities).get(1), 5.7, 0.01);
    }

    @Test
    void CalculateLambda04() {
        assertEquals(CalculateLambda.calculateLambda(4, new ArrayList<>()).get(1), 21.7, 0.01);
    }

}
