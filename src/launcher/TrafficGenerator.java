package launcher;



import jsim.event.Entity;
import jsim.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rng.Distribution;
import rng.distribution.ContinuousUniformDistribution;
import rng.distribution.ExponentialDistribution;
import simulator.Scheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandeep on 10-Jul-17.
 */
public class TrafficGenerator {
    /**
     * Set of Flows for the generator
     */

    private int source;
    /**
     * Distribution for the generation of requests
     */
    private Distribution requestDistribution;


    /**
     * Distribution for port class
     */
    private Distribution portDistribution;


    /**
     * Traffic class probabilities
     */
    private double[] trafficClassProb;




    //  private static final Logger log = LoggerFactory.getLogger(Generator.class);

    public TrafficGenerator(int source, double lambda, double[] trafficClassProb) {
        this.source = source;
        this.trafficClassProb = trafficClassProb;
        portDistribution = new ContinuousUniformDistribution(0.0, 1.0, SimulatorParameters.getSeed());
        requestDistribution = new ExponentialDistribution(lambda, SimulatorParameters.getSeed());

    }

    /**
     * Function to initialize the generator and generate the first event
     */
    public void initialize() {


        double interArrivalTime = requestDistribution.execute();
        Event event = new simulator.event.CircuitRequest(new Entity(interArrivalTime), this, getRandomTrafficClass());
        Scheduler.schedule(event, interArrivalTime);
    }

    public TrafficClass getRandomTrafficClass() {
        TrafficClass trafficClass = null;

        double decisionValue = portDistribution.execute();
        double threshold = 0;
        for (int i = 0; i < SimulatorParameters.getNumberOfClasses(); i++) {
            threshold += trafficClassProb[i];
            if (decisionValue <= threshold) {
                trafficClass = SimulatorParameters.getListOfTrafficClasses().get(i);
                break;

            }
        }
        return trafficClass;

    }

    public int getSource() {
        return source;
    }

    public void renewParameters(double lambdaPerSource, double[] trafficClassProb){
        this.trafficClassProb=trafficClassProb;
        this.requestDistribution=new ExponentialDistribution(lambdaPerSource, SimulatorParameters.getSeed());
    }

    public Distribution getRequestDistribution() {
        return requestDistribution;
    }
}

