package dk.aau.ds304e18.math;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Main {
    public static void main(String[] args) {
        Instant start = java.time.Instant.now();
        //Guessed mu value
        double muValue = 15;

        //Create a list with probabilities
        List<Probabilities> probabilities = new ArrayList<>();
        probabilities.add(new Probabilities(7d, 5d));
        probabilities.add(new Probabilities(12d, 40d));
        probabilities.add(new Probabilities(20d, 75d));
        probabilities.add(new Probabilities(30d, 90d));

        //Get the best mu and lambda values
        List<Double> muLambda = CalculateLambda.calculateLambda(muValue, probabilities);
        double mu = muLambda.get(0);
        double lambda = muLambda.get(1);

        //Create the inverse gaussian so we can check if it's alright
        InverseGaussian invG = new InverseGaussian(mu, lambda);
        Instant end = java.time.Instant.now();
        

        //Check if they're close to correct
        System.out.println(mu);
        System.out.println(lambda);
        for (Probabilities probability : probabilities) {
            System.out.println(invG.getProbability(probability.getValue()));
        }

        Duration between = java.time.Duration.between(start, end);
        System.out.format((char) 27 + "[31mNote: total in that unit!\n" + (char) 27 + "[39mHours: %02d Minutes: %02d Seconds: %02d Milliseconds: %04d \n",
                between.toHours(), between.toMinutes(), between.getSeconds(), between.toMillis()); // 0D, 00:00:01.1001

    }
}
