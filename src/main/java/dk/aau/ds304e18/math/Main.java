package dk.aau.ds304e18.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Main {
    public static void main(String[] args) {

        //Guessed mu value
        double muValue = 15;

        //Create a list with probabilities
        List<Probabilities> probabilities = new ArrayList<>();
        probabilities.add(new Probabilities(7d,5d));
        probabilities.add(new Probabilities(12d,40d));
        probabilities.add(new Probabilities(20d,75d));
        probabilities.add(new Probabilities(30d, 90d));

        //Get the best mu and lambda values
        List<Double> muLambda = CalculateLambda.calculateLambda(muValue, probabilities);
        double mu = muLambda.get(0);
        double lambda = muLambda.get(1);

        //Create the inverse gaussian so we can check if it's alright
        InverseGaussian invG = new InverseGaussian(mu,lambda);

        //Check if they're close to correct
        System.out.println(mu);
        System.out.println(lambda);
        for(Probabilities probability : probabilities){
            System.out.println(invG.getProbability(probability.getValue()));
        }
    }
}
