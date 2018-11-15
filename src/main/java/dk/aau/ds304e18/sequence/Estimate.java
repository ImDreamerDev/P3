package dk.aau.ds304e18.sequence;

import java.util.List;

public class Estimate {

    private List<Double> chances;
    private double duration;

    public Estimate(List<Double> chances, double duration) {
        this.chances = chances;
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<Double> getChances() {
        return chances;
    }

    public void setChances(List<Double> chances) {
        this.chances = chances;
    }
}
