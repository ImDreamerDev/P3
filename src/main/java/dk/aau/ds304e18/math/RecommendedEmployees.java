package dk.aau.ds304e18.math;

import java.util.ArrayList;
import java.util.List;

public class RecommendedEmployees {

    private List<Integer> amountEmps;
    private List<Double> estTime;

    public RecommendedEmployees() {
        this.amountEmps = new ArrayList<>();
        this.estTime = new ArrayList<>();
    }

    public List<Integer> getAmountEmps() {
        return amountEmps;
    }

    public List<Double> getEstTime() {
        return estTime;
    }

    public void add(int emp, double est) {
        amountEmps.add(emp);
        estTime.add(est);
    }
}
