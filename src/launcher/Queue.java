package launcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sandeep on 14-Jul-17.
 */
public class Queue {
    private int id;


    private  List<TrafficClass> listOfDelayedRequests;
    private  List<Double> listArrivalTimes;



    public  Queue(int id){
        this.id=id;
        listOfDelayedRequests=new ArrayList<>();
        listArrivalTimes = new ArrayList<>();
    }

    public void addEvent(TrafficClass trafficClass, double time){
        listOfDelayedRequests.add(trafficClass);
        listArrivalTimes.add(time);
    }

    public void removeFirstElement(){
        listOfDelayedRequests.remove(0);
        listArrivalTimes.remove(0);
    }

    public  TrafficClass getFirstElement(){
        if(listOfDelayedRequests.size()== 0)
            return null;
        return listOfDelayedRequests.get(0);
    }

    public  double getTimeStamp(){
        return listArrivalTimes.get(0);
    }

    public List<Double> getListArrivalTimes() {
        return listArrivalTimes;
    }

    public List<TrafficClass> getListOfDelayedRequests() {
        return listOfDelayedRequests;
    }
}

