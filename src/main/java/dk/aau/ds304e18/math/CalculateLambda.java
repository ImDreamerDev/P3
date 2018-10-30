package dk.aau.ds304e18.math;

import java.util.ArrayList;
import java.util.List;

public class CalculateLambda {

    //Optimizes mu and calculates lambda from the given probabilities
    public static List<Double> calculateLambda(double mu, List<Probabilities> probabilities){
        List<Double> returnVariables = new ArrayList<>();

        //Lowest mu value to check
        double minMu = mu/2;

        //Largest mu value to check
        double maxMu = mu+mu/2;

        //Current best mu value (Closest to wanted values)
        double bestMu = mu;

        //Current mu being checked
        double currentMu = minMu;

        //Lowest lambda to check
        double startLambda = 0.1;

        //Largest lambda to check
        double endLambda = 1000;

        //Current best mu value (Closest to wanted values)
        double bestLambda = -1;

        //Current lambda being checked
        double currentLambda;

        //bestValue should be as low as possible, as that means that all the probabilities are combined closest to the wanted values
        double bestValue = -1;

        //Check through all mu values
        while(currentMu <= maxMu){

            //(Re-)set currentLambda
            currentLambda = startLambda;

            //Go through all lambda values
            while(currentLambda <= endLambda){

                //Create an Inverse Gaussian distribution with the mu and lambda values so we can test
                InverseGaussian invG = new InverseGaussian(currentMu, currentLambda);

                //Temp value is to be checked against the best value
                double tempValue = 0;

                //For each probability
                for (Probabilities probability : probabilities) {

                    //Get the values of the probability
                    var realProb = invG.getProbability(probability.getValue());
                    var expectedProb = probability.getProbability();

                    //Check which probability is largest to get the difference
                    if (realProb > expectedProb) {
                        tempValue += realProb - expectedProb;
                    } else {
                        tempValue += expectedProb - realProb;
                    }

                }

                //If tempValue is lower than bestValue (and therefore better) or bestValue is -1 change the different bestValues so we save the current best values
                if(tempValue < bestValue || bestValue == -1){
                    bestValue = tempValue;
                    bestLambda = currentLambda;
                    bestMu = currentMu;
                }

                //TODO: Consider changing this amount
                currentLambda += 0.1;

            }

            //TODO: Consider changing this amount
            currentMu += mu/10;
        }

        //Add the best values to the variables to return
        returnVariables.add(bestMu);
        returnVariables.add(bestLambda);

        //Return the best mu value and the best lambda value
        return returnVariables;
    }

}