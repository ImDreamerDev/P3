package dk.aau.ds304e18.estimatetime;

import java.util.List;


public class Estimate {

    private final List<Double> chances;
    private final double duration;

    public Estimate(List<Double> chances, double duration) {
        this.chances = chances;
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }
    
    public List<Double> getChances() {
        return chances;
    }
}
