package dk.aau.ds304e18.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class InverseGaussianTest {

    @Test
    void InverseGaussian01() {
        InverseGaussian invG = new InverseGaussian();
        assertEquals(0, invG.getLambda());
        assertEquals(0, invG.getMu());
    }

    @Test
    void InverseGaussian02() {
        try {
            new InverseGaussian(0, 10);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    void InverseGaussian03() {
        try {
            new InverseGaussian(10, 0);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    @Test
    void InverseGaussian04() {
        InverseGaussian invG = new InverseGaussian(10, 10);
        assertEquals(0, invG.getDensity(0));
    }

    @Test
    void InverseGaussian06() {
        InverseGaussian invG = new InverseGaussian(10, 10);
        assertEquals(2.34375, invG.getDuration(10));
    }

    @Test
    void InverseGaussian07() {
        InverseGaussian invG = new InverseGaussian(10, 10);
        assertEquals(0, invG.getProbability(0));
    }

    @Test
    void InverseGaussian08() {
        InverseGaussian invG = new InverseGaussian(100, 350);
        assertEquals(87.5, invG.getDuration(50));
    }

    @Test
    void InverseGaussian05() {
        InverseGaussian invG = new InverseGaussian(10, 10);
        assertEquals(66.81020012231707, invG.getProbability(10));
    }

    @Test
    void InverseGaussianDensityTest001() {
        InverseGaussian invG = new InverseGaussian(1, 1);
        assertEquals(invG.getDensity(1), 0.4, 0.01);
    }

    @Test
    void InverseGaussianGetMuTest001() {
        InverseGaussian invG = new InverseGaussian(1, 1);
        assertEquals(invG.getMu(), 1);
    }

    @Test
    void InverseGaussianGetLambdaTest001() {
        InverseGaussian invG = new InverseGaussian(1, 1);
        assertEquals(invG.getLambda(), 1);
    }
}
