package dk.aau.ds304e18.math;

public class Calc {

    public static int amountMax(int amount) {
        if (amount > 12)
            return Integer.MAX_VALUE;
        if (amount != 1)
            return amount * amountMax(amount - 1);
        return 1;
    }
}
