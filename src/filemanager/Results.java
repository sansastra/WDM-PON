package filemanager;

import launcher.Launcher;
import launcher.SimulatorParameters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulator.Scheduler;

/**
 * Created by Sandeep on 13-Jul-17.
 */
public class Results {
    private static WriteFile blockingWriteFile;
    private static WriteFile overallBlockingFile;
    private static int totalRequests;
    private static double total_BW_requests;
    private static int totalBlockedRequests;
    private static double totalBlocked_BW_requests;
    private static double delay;
    private static int[] requestPerSrcClass;
    private static int[] blockedPerSrcClass;
    private static double[] request_BW_PerSrcClass;
    private static double[] blocked_BW_PerSrcClass;
    private static double[][] blockingPerClass;
    private static double[] avgDelayVector;
    private static int[] max_request;
    private static final Logger log = LoggerFactory.getLogger(Results.class);

    public Results() {

        try {
            SimpleDateFormat MY_FORMAT = new SimpleDateFormat(
                    "dd-MM-yyyy HH-mm-ss", Locale.getDefault());
            Date date = new Date();
            blockingWriteFile = new WriteFile("BlockingProb-run-"
                    + Launcher.get_runNumber(), false);
            blockingWriteFile.write(MY_FORMAT.format(date) + "\n");
            blockingWriteFile.write("Run  Type  Req	   Blocked	    Blocked(%) \n");
            //blockingWriteFile.write("S	D	T	Req	Blocked	SimTime  \n");
            /**overallBlockingFile = new WriteFile("OverallBlockingProb-run-"
             + Launcher.get_runNumber(), false);
             //overallBlockingFile.write("Run  Type  Req	   Blocked	    Blocked(%) \n");*/
            blockingPerClass= new double[SimulatorParameters.getNumberOfClasses()][SimulatorParameters.getNumberOfRuns()];
            avgDelayVector = new double[SimulatorParameters.getNumberOfRuns()];
            max_request=new int[SimulatorParameters.getNumberOfClasses()];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void increaseBlockedRequests(int classType, double bw) {
        blockedPerSrcClass[classType] += 1;
        blocked_BW_PerSrcClass[classType] += bw;
    }

    public static void increaseTotalRequests(int classType, double bw) {
        totalRequests++;
        requestPerSrcClass[classType] += 1;
        request_BW_PerSrcClass[classType] += bw;
        if (Scheduler.currentTime() >= (Launcher.get_runNumber()+1)*SimulatorParameters.getInterval()) {
            log.info("Completed Run- " + Launcher.get_runNumber() + "\n");
            writeBlockingResults();
            Launcher.runSimulation();
        }
    }



    public static void writeBlockingResults(){
        for (int i = 0; i < SimulatorParameters.getNumberOfClasses(); i++) {
            blockingWriteFile.write(Launcher.get_runNumber()
                    + " "
                    +i
                    +" "
                    +requestPerSrcClass[i]
                    +" "
                    +blockedPerSrcClass[i]
                    +" "
                    +100.0*blockedPerSrcClass[i]/requestPerSrcClass[i]+"\n");

                blockingPerClass[i][Launcher.get_runNumber()]=/**100.0*/(double) blockedPerSrcClass[i];///requestPerSrcClass[i];
                if(max_request[i]<requestPerSrcClass[i])
                    max_request[i]=requestPerSrcClass[i];
        }
        avgDelayVector[Launcher.get_runNumber()]=delay/requestPerSrcClass[2]; // for class-3
    }


    public static double[][] getBlockingPerClass() {

        for (int i = 0; i <SimulatorParameters.getNumberOfRuns() ; i++) {
            for (int j = 0; j < SimulatorParameters.getNumberOfClasses(); j++) {
                blockingPerClass[j][i]=blockingPerClass[j][i]/max_request[j];
            }
        }
        return blockingPerClass;
    }

    public static void increaseDelay(double d){

        delay += d;
    }

    public static double getDelay(){return delay;}

    public static double[] getAvgDelayVector() {
        return avgDelayVector;
    }

    public static void initializeCounters(){
        delay=0;
        totalRequests=0;
        totalBlockedRequests=0;
        total_BW_requests=0.0;
        totalBlocked_BW_requests=0.0;
        requestPerSrcClass=new int[SimulatorParameters.getNumberOfClasses()];
        blockedPerSrcClass=new int[SimulatorParameters.getNumberOfClasses()];
        request_BW_PerSrcClass = new double[SimulatorParameters.getNumberOfClasses()];
        blocked_BW_PerSrcClass = new double[SimulatorParameters.getNumberOfClasses()];

    }
}
