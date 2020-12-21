package simulator.event;

import filemanager.Results;
import launcher.*;
import simulator.Scheduler;
import jsim.event.Entity;
import jsim.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;

/**
 * Created by Sandeep on 12-Jul-17.
 */
public class CircuitRequest extends Event {

    /**
     * Generator responsible of the event
     */
    private TrafficGenerator generator;
    /**
     * TrafficClass that generates the request
     */
    TrafficClass trafficClass;

    private static final Logger log = LoggerFactory.getLogger(CircuitRequest.class);

    /**
     * Constructor class
     */
    public CircuitRequest(Entity entity, TrafficGenerator generator,
                               TrafficClass trafficClass) {
        super(entity);
        this.generator = generator;
        this.trafficClass = trafficClass;

    }

    @Override
    public void occur() {
        int sourceONU = generator.getSource();
        double holdingTime=0.0;
        if(SimulatorParameters.isDelayAllowed()&& trafficClass.getType()==2){
            Queue queue =SimulatorParameters.getAQueue(sourceONU);
//            if(queue.getListOfDelayedRequests().size()>1)
//                log.info("more than 1 delayed requests");
                queue.addEvent(trafficClass,Scheduler.currentTime());
                for (int i=0;i<queue.getListOfDelayedRequests().size();i++) {
                    trafficClass=queue.getListOfDelayedRequests().get(i);
                    holdingTime = SimulatorParameters.getMeanHoldingTimes(2);//trafficClass.getHoldingTimeDistribution().execute();

                    double bw = trafficClass.getBw();

                    if(NetworkState.reserveCapacity(sourceONU,trafficClass.getType(),bw)) {
                        Event event = new CircuitRelease(new Entity(holdingTime), sourceONU, bw, trafficClass.getType());
                        Scheduler.schedule(event,holdingTime);
                        Results.increaseTotalRequests(trafficClass.getType(), bw);
                        Results.increaseDelay(Scheduler.currentTime()-queue.getTimeStamp());
                        queue.removeFirstElement();
                    }
                    else
                        break;
                }

        }
        else {
            if(trafficClass.getType()==2)
                holdingTime =SimulatorParameters.getMeanHoldingTimes(2);
            else
                holdingTime = trafficClass.getHoldingTimeDistribution().execute();

            double bw = trafficClass.getBw();

            if (NetworkState.reserveCapacity(sourceONU, trafficClass.getType(), bw)) {
                Event event = new CircuitRelease(new Entity(holdingTime), sourceONU, bw, trafficClass.getType());
                Scheduler.schedule(event, holdingTime);
            } else {
                // increase blocking
                Results.increaseBlockedRequests(trafficClass.getType(), bw);
            }

            Results.increaseTotalRequests(trafficClass.getType(), bw);

        }

        /** Add a new request event */
        TrafficClass nextTrafficClass = generator.getRandomTrafficClass();
        double nextInterArrivalTime = generator.getRequestDistribution().execute();
        Event event = new CircuitRequest(new Entity(nextInterArrivalTime), generator, nextTrafficClass);
        Scheduler.schedule(event, nextInterArrivalTime);
        log.debug("Added request event: " + generator.getSource());
    }
}