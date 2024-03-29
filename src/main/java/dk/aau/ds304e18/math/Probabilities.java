package dk.aau.ds304e18.math;

public class Probabilities {

    /**
     * Duration is in the time the user wants it in (They need to keep it consistent throughout the project)
     */
    private final double duration;

    /**
     * Probability is in percent, i.e. out of 100
     */
    private final double probability;

    /**
     * @param duration    - duration in the time the user wants.
     * @param probability - probability in percent.
     */
    public Probabilities(double duration, double probability) {
        this.duration = duration;
        this.probability = probability;
    }

    /**
     * The getter for the Duration.
     *
     * @return duration.
     */
    public double getDuration() {
        return this.duration;
    }

    /**
     * The getter for the probability
     *
     * @return probability.
     */
    public double getProbability() {
        return this.probability;
    }

    @Override
    public String toString() {
        return duration + ", " + probability;
    }
}
