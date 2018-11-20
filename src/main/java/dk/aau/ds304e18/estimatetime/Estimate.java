package dk.aau.ds304e18.estimatetime;

import dk.aau.ds304e18.models.Task;

import java.util.HashMap;
import java.util.List;


public class Estimate {

    private List<Double> chances;
    private HashMap<Task, Double> startTimes;
    private HashMap<Task, Double> endTimes;
    private double duration;

    public Estimate(List<Double> chances, HashMap<Task, Double> startTimes, HashMap<Task, Double> endTimes, double duration) {
        this.chances = chances;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
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

    public HashMap<Task, Double> getEndTimes() {
        return endTimes;
    }
}
