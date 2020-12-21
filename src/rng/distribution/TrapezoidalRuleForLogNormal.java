package rng.distribution;
import org.apache.commons.math3.*;
/*************************************************************************
 * Compilation: javac TrapezoidalRule.java Execution: java TrapezoidalRule a b
 * 
 * Numerically integrate the function in the interval [a, b].
 *
 * % java TrapezoidalRule -3 3 0.9973002031388447 // true answer =
 * 0.9973002040...
 *
 * Observation: this says that 99.7% of time a standard normal random variable
 * is within 3 standard deviation of its mean.
 *
 * % java TrapezoidalRule 0 100000 1.9949108930964732 // true answer = 1/2
 *
 * Caveat: this is not the best way to integrate the normal density function.
 * See what happens if you make b very big.
 *
 *************************************************************************/

public class TrapezoidalRuleForLogNormal {


    private static double sigma;
    private static double mu;
    private static double m;

    public TrapezoidalRuleForLogNormal(double _sigma, double _mu){
        sigma = _sigma;
        mu = _mu;
        m = Math.exp(mu+sigma*sigma/2);
    }




	/**********************************************************************
	 * Integrate f from a to b using the trapezoidal rule. Increase N for more
	 * precision.
	 **********************************************************************/

    public static double integrate(double x) {
        double alpha;
        double erf1;
        double erf2;
        alpha = (Math.log(x)-(mu + sigma*sigma))/(sigma*Math.sqrt(2.0));
        erf1 = org.apache.commons.math3.special.Erf.erf(alpha);

        alpha = (Math.log(x)-mu)/(sigma*Math.sqrt(2.0));
        erf2 = org.apache.commons.math3.special.Erf.erf(alpha);
        return m*((1.0 - erf1)/(1-erf2))- x;
    }




}