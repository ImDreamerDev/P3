package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.models.Task;

import java.util.HashMap;
import java.util.List;

public class Estimate {

    private final List<Double> chances;
    private final HashMap<Task, Double> startTimes;
    private double duration;

    public Estimate(List<Double> chances, HashMap<Task, Double> startTimes, double duration) {
        this.chances = chances;
        this.startTimes = startTimes;
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

    public HashMap<Task, Double> getStartTimes() {
        return startTimes;
    }
}
