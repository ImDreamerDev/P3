package dk.aau.ds304e18.math;

public class Probabilities {

    private double value;
    private double probability;

    public Probabilities(double value, double probability){
        this.value = value;
        this.probability = probability;
    }
    
    public double getValue(){
        return this.value;
    }

    public double getProbability(){
        return this.probability;
    }

}
