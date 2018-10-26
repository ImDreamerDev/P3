package dk.aau.ds304e18.math;

import java.util.ArrayList;
import java.util.List;

public class CalculateLambda {

    /*
    https://www.mathsisfun.com/data/random-variables-mean-variance.html
    https://www.mathsisfun.com/data/frequency-grouped-mean-median-mode.html
    https://ocw.mit.edu/courses/mathematics/18-05-introduction-to-probability-and-statistics-spring-2014/readings/MIT18_05S14_Reading6a.pdf

    Find expected value
    EV = Mean, however we do not have full information, so we have to guesstimate
    Square value of x and multiply it by probability and sum then up
    Subtract the square of the expected value
    You now have variance

    Take mu^3 and divide it with the variance

    You can find mu by taking the values given, times them by their probability sum them together and divide by the summed up probability
    Because of the nature of the inverse gaussian it's not necessarily right that if you give the value 90% we're done after 10 means in the cdf that 10x = 0,9y
     */

    private List<Double> days = new ArrayList<>();

}
