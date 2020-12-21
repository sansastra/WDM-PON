package launcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;


/**
 * Created by Sandeep on 12-Jul-17.
 */
public class NetworkState {
    private static int onus;
    private static int scsPerONU;
    private static double capacityPerSC; // sc: subcarrier
    private static double[] capacityForClasses;
    private static double[][] onuChannelStatus;

    public NetworkState(int onus, int scsPerONU, double capacityPerSC,double[] scBoundaries){
        this.onus=onus;
        this.scsPerONU=scsPerONU;
        this.capacityPerSC=capacityPerSC;
        this.onuChannelStatus= new double[onus][SimulatorParameters.getNumberOfClasses()];
        this.capacityForClasses= new double[SimulatorParameters.getNumberOfClasses()];
        setCapcity(scBoundaries);

    }

    public static boolean reserveCapacity(int source,int requestType, double bw){

        if (onuChannelStatus[source][requestType] + bw > capacityForClasses[requestType])
            return false;
        else
            onuChannelStatus[source][requestType] +=  bw;
        return true;
    }

    public static void releaseCapacity(int source,int requestType, double bw){
        if (onuChannelStatus[source][requestType]-bw<-0.00001)
            System.exit(2);
        else
            onuChannelStatus[source][requestType]-=bw;
    }

    public static void setCapcity(double[] scBoundaries){
        for (int i = 0; i < SimulatorParameters.getNumberOfClasses(); i++)
            capacityForClasses[i]=scsPerONU*capacityPerSC*(scBoundaries[i]/DoubleStream.of(scBoundaries).sum());

    }
}
