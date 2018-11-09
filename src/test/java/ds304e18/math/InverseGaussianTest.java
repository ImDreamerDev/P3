package ds304e18.math;

import dk.aau.ds304e18.math.InverseGaussian;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InverseGaussianTest {

    @Test
    void InverseGaussianDensityTest001(){
        InverseGaussian invG = new InverseGaussian(1,1);

        assertEquals(invG.getDensity(1), 0.4, 0.01);
    }

    @Test
    void InverseGaussianGetMuTest001(){
        InverseGaussian invG = new InverseGaussian(1,1);

        assertEquals(invG.getMu(), 1);
    }

    @Test
    void InverseGaussianGetLambdaTest001(){
        InverseGaussian invG = new InverseGaussian(1,1);

        assertEquals(invG.getLambda(), 1);
    }
}
