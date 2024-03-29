package dk.aau.ds304e18.math;

public class InverseGaussian {

    //Large part of the code in here is taken from:
    //https://www.iro.umontreal.ca/~simardr/ssj/doc/html/overview-summary.html - Last accessed on 05-Dec-2018
    //Indicated by a citation in the comments above the specific methods

    private double mu;
    private double lambda;

    //Taken from: https://www.iro.umontreal.ca/~simardr/ssj/doc/html/overview-summary.html - Last accessed on 05-Dec-2018
    private static final double[] NORMAL2_A = new double[]
            {0.6101430819232004D, -0.4348412727125775D, 0.1763511936436055D, -0.06071079560924941D,
                    0.017712068995694115D, -0.004321119385567294D, 8.542166768870987E-4D, -1.2715509060916275E-4D,
                    1.1248167243671189E-5D, 3.1306388542182096E-7D, -2.70988068537762E-7D, 3.073762270140769E-8D,
                    2.515620384817623E-9D, -1.0289299213203192E-9D, 2.994405211994994E-11D, 2.6051789687266936E-11D,
                    -2.6348399241719693E-12D, -6.434045098906365E-13D, 1.1245740180166345E-13D, 1.7281533389986097E-14D,
                    -4.264101694942375E-15D, -5.45371977880191E-16D, 1.58697607761671E-16D, 2.0899837844334E-17D,
                    -5.900526869409E-18D, -9.41893387554E-19D};

    //Empty constructor
    public InverseGaussian() {
    }

    //Sets the parameters for the Inverse Gaussian Distribution
    public InverseGaussian(double mu, double lambda) {
        this.setParams(mu, lambda);
    }

    /**
     * Taken from: https://www.iro.umontreal.ca/~simardr/ssj/doc/html/overview-summary.html - Last accessed on 05-Dec-2018
     * Calculate the y-value given an x value, i.e. the probability of a task completing at a certain time (y : probability, x : time) using the CDF
     *
     * @param mu     - the mean.
     * @param lambda - shape parameter.
     * @param x      - completion time of a given task.
     * @return The probability of the task being completed at x time.
     */
    private static double cdf(double mu, double lambda, double x) {
        if (x <= 0.0D) {
            return 0.0D;
        } else {
            double temp = Math.sqrt(lambda / x);
            double z = temp * (x / mu - 1.0D);
            double w = temp * (x / mu + 1.0D);
            return cdf01(z) + Math.exp(2.0D * lambda / mu) * cdf01(-w);
        }
    }

    /**
     * Taken from: https://www.iro.umontreal.ca/~simardr/ssj/doc/html/overview-summary.html - Last accessed on 05-Dec-2018
     * The CDF for the Gaussian Distribution aka normal distribution (Used to calculate the CDF of the Inverse Gaussian Distribution)
     *
     * @param x - It's the x value of the normal distribution.
     * @return r - The result of the function
     */
    private static double cdf01(double x) {
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
                r = 1.0D - 0.5D * Math.exp(-x * x) * evalCheby(t);
            } else {
                t = (x - 3.75D) / (x + 3.75D);
                r = 0.5D * Math.exp(-x * x) * evalCheby(t);
            }

            return r;
        }
    }

    /**
     * Taken from: https://www.iro.umontreal.ca/~simardr/ssj/doc/html/overview-summary.html - Last accessed on 05-Dec-2018
     * Used to calculate the CDF of the normal distribution
     *
     * @param x -the parameter of the Tj functions.
     * @return the value of a series of Chebyshev polynomials Tj..
     */
    private static double evalCheby(double x) {
        if (Math.abs(x) > 1.0D) {
            System.err.println("Chebychev polynomial evaluated at x outside [-1, 1]");
        }

        double xx = 2.0D * x;
        double b0 = 0.0D;
        double b1 = 0.0D;
        double b2 = 0.0D;

        for (int j = 24; j >= 0; --j) {
            b2 = b1;
            b1 = b0;
            b0 = xx * b0 - b2 + InverseGaussian.NORMAL2_A[j];
        }

        return (b0 - b2) / 2.0D;
    }

    /**
     * Taken from: https://www.iro.umontreal.ca/~simardr/ssj/doc/html/overview-summary.html - Last accessed on 05-Dec-2018
     * Sets the parameters for the Inverse Gaussian Distribution, if mu or lambda is less than or equal to 0, throw exception
     *
     * @param mu     - the mean.
     * @param lambda - the shape parameter.
     */
    public void setParams(double mu, double lambda) {
        if (mu <= 0.0D) {
            throw new IllegalArgumentException("mu <= 0");
        } else if (lambda <= 0.0D) {
            throw new IllegalArgumentException("lambda <= 0");
        } else {
            this.mu = mu;
            this.lambda = lambda;
        }
    }

    /**
     * The getter for lambda.
     *
     * @return lambda.
     */
    public double getLambda() {
        return this.lambda;
    }

    /**
     * The getter for the probability
     *
     * @param x - the x value from the cdf (the task estimated completion time)
     * @return cdf(probability).
     */
    public double getProbability(double x) {
        return cdf(this.mu, this.lambda, x) * 100;
    }

    /**
     * The getter for the duration
     *
     * @param y - The probability of the task succeeding at that duration.
     * @return x - Duration of task.
     */
    public double getDuration(double y) {
        double x = 20;
        double startX = -1;
        double endX = -1;
        double probX = getProbability(x);

        //While we're not within a margin from the probability
        while (probX < (y - 1) || probX > (y + 1)) {

            //Check if we're lower
            if (probX < y) {
                //Set startX so we know where to look
                startX = x;

                //If we have not found a higher value before
                if (endX == -1)
                    x *= 2;
                    //If we have
                else
                    x += (endX - startX) / 2;
            } else {
                //Set endX so we know where to look
                endX = x;

                //If we have not found a lower value before
                if (startX == -1)
                    x /= 2;
                    //If we have
                else
                    x -= (endX - startX) / 2;
            }
            probX = getProbability(x);
        }

        //Return the x value
        return x;
    }

}
