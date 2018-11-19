package dk.aau.ds304e18.math;

public class Maths {

    public static int amountMax(int amount) {
        if (amount > 12)
            return Integer.MAX_VALUE;
        if (amount != 1)
            return amount * amountMax(amount - 1);
        return 1;
    }

    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        else if (val.compareTo(max) > 0) return max;
        else return val;
    }
}
