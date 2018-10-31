package dk.aau.ds304e18.math;

public class Probabilities {

    private double duration;
    private double probability;

    public Probabilities(double duration, double probability){
        this.duration = duration;
        this.probability = probability;
    }
    
    public double getDuration(){
        return this.duration;
    }

    public double getProbability(){
        return this.probability;
    }

}
