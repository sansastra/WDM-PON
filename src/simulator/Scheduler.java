package simulator;

import jsim.event.Event;
import jsim.queue.TQ_Node;
import jsim.queue.TemporalQueue;

public class Scheduler {
    private static final int MID_PRIORITY = 100;
    private static double currentTime;
    private static TemporalQueue eventList;

    public Scheduler() {
        eventList = new TemporalQueue();
        currentTime = 0.0D;
    }

    public static double currentTime() {
        return currentTime;
    }

    public static void schedule(Event event, double timeDelay) {
        eventList.enqueue(event, currentTime + timeDelay, 100);
    }

    public static void schedule(Event event, double timeDelay, int priority) {
        eventList.enqueue(event, currentTime + timeDelay, priority);
    }

    public static void startSim() {
        while(!eventList.empty()) {
            TQ_Node nextNode = (TQ_Node)eventList.dequeue();
            Event nextEvent = (Event)nextNode.getItem();
            currentTime = nextNode.getTime();
            nextEvent.occur();
        }

    }
}

