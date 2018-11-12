package dk.aau.ds304e18.math;

public class Calc {

    //TODO: Optimize this, right now it's O(n), it can be done better I think
    public static int amountMax(int amount) {
        if (amount > 12)
            return Integer.MAX_VALUE;
        if (amount != 1)
            return amount * amountMax(amount - 1);
        return 1;
    }
}
