package ds304e18.math;

import dk.aau.ds304e18.math.Maths;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathsTest {

    @Test
    void calcAmountMaxTest01() {
        int amount = Maths.amountMax(4);
        assertEquals(24, amount);
    }

    @Test
    void clapMaxIntTest01() {
        assertEquals(Maths.clamp(55, 0, 1).intValue(), 1);
    }

    @Test
    void clapMaxDoubleTest02() {
        assertEquals(Maths.clamp(55.7, 0.0, 25.5).doubleValue(), 25.5);
    }

    @Test
    void clapMinIntTest03() {
        assertEquals(Maths.clamp(-18, 7, 25).floatValue(), 7);
    }

    @Test
    void clapMinDoubleTest04() {
        assertEquals(Maths.clamp(-55.5, 7.0, 25.5).floatValue(), 7.0);
    }

    @Test
    void clapMinIntTest05() {
        assertEquals(Maths.clamp(23, 7, 25).floatValue(), 23);
    }

    @Test
    void clapMinDoubleTest06() {
        assertEquals(Maths.clamp(20.5, 7.0, 25.5).floatValue(), 20.5);
    }

}
