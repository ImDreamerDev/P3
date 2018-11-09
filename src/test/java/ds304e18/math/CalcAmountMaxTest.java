package ds304e18.math;

import dk.aau.ds304e18.math.Calc;
import dk.aau.ds304e18.math.InverseGaussian;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalcAmountMaxTest {

    @Test
    void calcAmountMaxTest001(){
        int amount = Calc.amountMax(4);

        assertEquals(24, amount);
    }

}
