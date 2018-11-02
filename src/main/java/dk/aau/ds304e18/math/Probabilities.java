package dk.aau.ds304e18.math;

public class Probabilities {

    /**
     * Duration is in days
     */
    private double duration;

    /**
     * Probability is in percent, i.e. out of 100
     */
    private double probability;

    /**
     *
     * @param duration - duration in days.
     * @param probability - probability in percent.
     */
    public Probabilities(double duration, double probability){
        this.duration = duration;
        this.probability = probability;
    }

    /**
     * The getter for the Duration.
     * @return duration.
     */
    public double getDuration(){
        return this.duration;
    }

    /**
     * The getter for the probability
     * @return probability.
     */
    public double getProbability(){
        return this.probability;
    }

}
