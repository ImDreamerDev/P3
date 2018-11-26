package dk.aau.ds304e18.math;

import java.util.ArrayList;
import java.util.List;

public class RecommendedEmployees {

    private final List<Integer> amountEmployees = new ArrayList<>();
    private final List<Double> estimatedTime = new ArrayList<>();

    public List<Integer> getAmountEmployees() {
        return amountEmployees;
    }

    public List<Double> getEstimatedTime() {
        return estimatedTime;
    }

    public void add(int emp, double est) {
        amountEmployees.add(emp);
        estimatedTime.add(est);
    }
}
