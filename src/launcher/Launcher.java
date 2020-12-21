package launcher;



import filemanager.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulator.Scheduler;
import java.text.DecimalFormat;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Sandeep on 10-Jul-17.
 */
public class Launcher {

    private static final Logger log = LoggerFactory.getLogger(Launcher.class);
    private static Date date;
    private static int _runNumber = 0;
    private static int seedCounter;
    private static int numberOfRuns;
    private static DecimalFormat df4 = new DecimalFormat("0.####");
    public static void main(String[] args) throws IOException {

        date = new Date();
        SimulatorParameters.readSeedFile("seeds.txt");
        SimulatorParameters.readTrafficFile("traffic-1.txt");
        SimulatorParameters.readTrafficFile("traffic-2.txt");
        SimulatorParameters.readTrafficFile("traffic-3.txt");

        SimulatorParameters.readTrafficPredictFile("predict-1.txt");
        SimulatorParameters.readTrafficPredictFile("predict-2.txt");
        SimulatorParameters.readTrafficPredictFile("predict-3.txt");
        numberOfRuns=SimulatorParameters.getNumberOfRuns();
        new Results();
        startSimulation();
    }

    public static Date getDate() {
        return date;
    }
    public static void startSimulation() {      /** Create new result files*/
        /** Initialize the scheduler*/
        new Scheduler();
        SimulatorParameters.initializeTrafficForONUs();
        SimulatorParameters.initializeNetworkState();
        Results.initializeCounters();

        Scheduler.startSim();
    }

    public static void runSimulation() {
        seedCounter = -1;

        if (_runNumber == numberOfRuns - 1) {
            printResults();
            System.exit(0);
        }
        else {
            _runNumber++;
        }
        SimulatorParameters.renewTrafficForONUs();

         SimulatorParameters.defineBoundaries();
         NetworkState.setCapcity(SimulatorParameters.getSubcarrierBoundaries());

        Results.initializeCounters();


    }

    public static void printResults(){
        double[][] blockingPerClass= Results.getBlockingPerClass();
        System.out.print("Blocking Results per class: \n");
        for (int i = 0; i < SimulatorParameters.getNumberOfClasses(); i++) {
            for (int j = 1; j < numberOfRuns; j++) {
                System.out.print(df4.format(blockingPerClass[i][j])+",");
            }
            System.out.print("\n");
        }
        double[] avgDelay= Results.getAvgDelayVector();
        System.out.print("Avg. delay for class-3: \n");
        for (int i = 1; i < numberOfRuns; i++) {
            System.out.print((float) avgDelay[i]+",");
        }
    }

    public static int get_runNumber() {
        return _runNumber;
    }
}
