package dk.aau.ds304e18.math;

public class Main {
    public static void main(String[] args) {

        InverseGaussian invG = new InverseGaussian(5.85
                ,54.81852
        );

        System.out.println(invG.getProbability(10));
    }
}
