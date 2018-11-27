package dk.aau.ds304e18.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendedEmployeesTest {
    @Test
    void getAmountEmployeesTest() {
        RecommendedEmployees rex = new RecommendedEmployees();
        rex.add(2, 43);
        assertEquals(rex.getAmountEmployees().get(0).intValue(), 2);
    }

    @Test
    void getEstimatedTimeTest() {
        RecommendedEmployees rex = new RecommendedEmployees();
        rex.add(2, 43);
        assertEquals(rex.getEstimatedTime().get(0).doubleValue(), 43);
    }

    @Test
    void addTest() {
        RecommendedEmployees rex = new RecommendedEmployees();
        rex.add(2, 43.54);
        assertEquals(rex.getEstimatedTime().get(0).doubleValue(), 43.54);
        assertEquals(rex.getAmountEmployees().get(0).intValue(), 2);
    }
}
