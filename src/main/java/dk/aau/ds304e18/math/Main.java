package dk.aau.ds304e18.math;

public class Main {
    public static void main(String[] args) {

        InverseGaussian invG = new InverseGaussian(11.03,66.661341);

        System.out.println(invG.getProbability(5));
    }
}
