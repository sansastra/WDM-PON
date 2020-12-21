package rng;

/**
 * Abstract class for probability distributions
 *
 * @author Fran Carpio
 */
public abstract class Distribution {

    public Distribution(double rate, byte[] seed) {}

    public Distribution(double min, double max, byte[] seed) {}

    public abstract Double execute();

    public  abstract String getDistributionName();

}
