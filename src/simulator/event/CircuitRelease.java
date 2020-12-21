package simulator.event;

import filemanager.Results;
import jsim.event.Entity;
import jsim.event.Event;
import launcher.NetworkState;
import launcher.Queue;
import launcher.SimulatorParameters;
import launcher.TrafficClass;
import simulator.Scheduler;


/**
 * Created by Sandeep on 12-Jul-17.
 */
public class CircuitRelease extends Event{
    private int sourceONU;
    private double bw;
    private int classType;
    TrafficClass trafficClass;

    public CircuitRelease(Entity entity,int sourceONU,double bw,int classType){
        super(entity);
        this.sourceONU=sourceONU;
        this.bw=bw;
        this.classType=classType;
    }
    public void occur() {
        NetworkState.releaseCapacity(sourceONU,classType,bw);

        // schedule the waiting requests

        Queue queue =SimulatorParameters.getAQueue(sourceONU);
//            if(queue.getListOfDelayedRequests().size()>1)
//                log.info("more than 1 delayed requests");
        if(queue.getListOfDelayedRequests().size()>0) {
            trafficClass=queue.getListOfDelayedRequests().get(0);
            double holdingTime = trafficClass.getHoldingTimeDistribution().execute();

            double bw = trafficClass.getBw();

            if(NetworkState.reserveCapacity(sourceONU,trafficClass.getType(),bw)) {
                Event event = new CircuitRelease(new Entity(holdingTime), sourceONU, bw, trafficClass.getType());
                Scheduler.schedule(event,holdingTime);
                Results.increaseTotalRequests(trafficClass.getType(), bw);
                Results.increaseDelay(Scheduler.currentTime()-queue.getTimeStamp());
                queue.removeFirstElement();
            }
        }

    }
}
