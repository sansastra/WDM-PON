package launcher;

import rng.Distribution;
import rng.distribution.ExponentialDistribution;
import rng.distribution.LogNormalDistribution;
/**
 * Created by Sandeep on 10-Jul-17.
 */
public class TrafficClass {

    private int type;
    private double bw;
    private Distribution holdingTimeDistribution;
    private double meanHoldingTime;
    private double cov;


    /**
     * Constructor class
     *
     * @param type              type value for the class
     * @param bw                bandwidth value
     * @param holdingTimeDist   distribution for the holding time
     * @param meanHoldingTime   mean value of the holding time
     * @param cov               coefficient of variation for the holding time distribution
     */
    public TrafficClass(int type, double bw, String holdingTimeDist, double meanHoldingTime, double cov) {

        this.type = type;
        this.bw = bw;
        this.meanHoldingTime = meanHoldingTime;
        this.cov = cov;
        /**Initialize distribution for holding times*/
        switch (holdingTimeDist) {
            case "EXP":
                holdingTimeDistribution = new ExponentialDistribution(1 / meanHoldingTime, SimulatorParameters.getSeed());
                break;
            case "LOGN":
                holdingTimeDistribution = new LogNormalDistribution(meanHoldingTime, cov * meanHoldingTime, SimulatorParameters.getSeed());
                break;
        }
    }

    public int getType() {
        return type;
    }

    /**
     * Get bandwidth of the traffic class
     *
     * @return bandwidth value
     */
    public double getBw() {
        return bw;
    }

    public Distribution getHoldingTimeDistribution() {
        return holdingTimeDistribution;
    }
}
