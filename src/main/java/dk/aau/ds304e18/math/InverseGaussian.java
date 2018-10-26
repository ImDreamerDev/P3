package dk.aau.ds304e18.math;

public class InverseGaussian {

    private double mu;
    private double lambda;
    private static final double[] NORMAL2_A = new double[]{0.6101430819232004D, -0.4348412727125775D, 0.1763511936436055D, -0.06071079560924941D, 0.017712068995694115D, -0.004321119385567294D, 8.542166768870987E-4D, -1.2715509060916275E-4D, 1.1248167243671189E-5D, 3.1306388542182096E-7D, -2.70988068537762E-7D, 3.073762270140769E-8D, 2.515620384817623E-9D, -1.0289299213203192E-9D, 2.994405211994994E-11D, 2.6051789687266936E-11D, -2.6348399241719693E-12D, -6.434045098906365E-13D, 1.1245740180166345E-13D, 1.7281533389986097E-14D, -4.264101694942375E-15D, -5.45371977880191E-16D, 1.58697607761671E-16D, 2.0899837844334E-17D, -5.900526869409E-18D, -9.41893387554E-19D};

    InverseGaussian(double mu, double lambda) {
        this.setParams(mu, lambda);
    }

    private static double density(double mu, double lambda, double x) {
        if (mu <= 0.0D) {
            throw new IllegalArgumentException("mu <= 0");
        } else if (lambda <= 0.0D) {
            throw new IllegalArgumentException("lambda <= 0");
        } else if (x <= 0.0D) {
            return 0.0D;
        } else {
            double sqrtX = Math.sqrt(x);
            return Math.sqrt(lambda / 6.283185307179586D) / (sqrtX * sqrtX * sqrtX) * Math.exp(-lambda * (x - 2.0D * mu + mu * mu / x) / (2.0D * mu * mu));
        }
    }

    public static double cdf(double mu, double lambda, double x) {
        if (mu <= 0.0D) {
            throw new IllegalArgumentException("mu <= 0");
        } else if (lambda <= 0.0D) {
            throw new IllegalArgumentException("lambda <= 0");
        } else if (x <= 0.0D) {
            return 0.0D;
        } else {
            double temp = Math.sqrt(lambda / x);
            double z = temp * (x / mu - 1.0D);
            double w = temp * (x / mu + 1.0D);
            return cdf01(z) + Math.exp(2.0D * lambda / mu) * cdf01(-w);
        }
    }
    
    public static double cdf01(double x) {
        if (x <= -100.0D) {
            return 0.0D;
        } else if (x >= 100.0D) {
            return 1.0D;
        } else {
            x = -x / 1.4142135623730951D;
            double t;
            double r;
            if (x < 0.0D) {
                x = -x;
                t = (x - 3.75D) / (x + 3.75D);
                r = 1.0D - 0.5D * Math.exp(-x * x) * evalCheby(NORMAL2_A, 24, t);
            } else {
                t = (x - 3.75D) / (x + 3.75D);
                r = 0.5D * Math.exp(-x * x) * evalCheby(NORMAL2_A, 24, t);
            }

            return r;
        }
    }

    public static double evalCheby(double[] a, int n, double x) {
        if (Math.abs(x) > 1.0D) {
            System.err.println("Chebychev polynomial evaluated at x outside [-1, 1]");
        }

        double xx = 2.0D * x;
        double b0 = 0.0D;
        double b1 = 0.0D;
        double b2 = 0.0D;

        for(int j = n; j >= 0; --j) {
            b2 = b1;
            b1 = b0;
            b0 = xx * b0 - b2 + a[j];
        }

        return (b0 - b2) / 2.0D;
    }

    private void setParams(double mu, double lambda) {
        if (mu <= 0.0D) {
            throw new IllegalArgumentException("mu <= 0");
        } else if (lambda <= 0.0D) {
            throw new IllegalArgumentException("lambda <= 0");
        } else {
            this.mu = mu;
            this.lambda = lambda;
        }
    }

    double getLambda() {
        return this.lambda;
    }

    double getMu() {
        return this.mu;
    }

    public double getDensity(int x) {
        return density(this.mu, this.lambda, x);
    }

    public double getProbability(int x) {
        return cdf(this.mu, this.lambda, x)*100;
    }

}