package launcher;

import launcher.NetworkState;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by Sandeep on 10-Jul-17.
 */
public class SimulatorParameters {



    private static double simulationTime=90000;// Simulation time= 25 hours
    private static double interval;
   // private static int numberOfTotalRequests=10000; // Max number of total request events before to stop the simulation

   // private static double[] offeredLoad = {10,20,30}; // Offered Load for all runs

    private static double[] subcarrierBoundaries= {0, 0, 0}; // subcarrier capacity allowed ratio per classes; dummy value

    private static double[] bandwidthDemands={1024.0, 10240.0, 0.25}; // bandwidth demands (in Mb/s)

    private static double[] meanHoldingTimes={600.0, 600.0, 1.0};

    private static String htDistribution="EXP" ;// "EXP" or "LogN"

    private static double[] cov = {1.0,1.0,1.0}; // if lognormal then set other than 1

    private static int onus=16; // Number Of ONUs

    private static int numberOfSubcarriers=20; // number Of Subcarriers Per ONU

    private static double capacityPerSubcarriers=25000;// capacity per subcarriers

    private static double[] trafficScaling ={1.7, 2, 1};
    private static int policy= 0;
    private static boolean dynamicBoundary = true; //false;true
    private static boolean delayAllowed = true;
    //private static int numberOfRuns=offeredLoad.length;

    private static int numberOfTrafficClasses = bandwidthDemands.length;
    private static int numberOfCores;
    private static double lambdaPerSource=0.0;

    private static List<byte[]> listOfSeeds;

    private static int seedCounter;
    private static List<TrafficGenerator> listOfGenerators;
    private static List<List<Double>> listOfTrafficLoads;
    private static List<List<Double>> listOfTrafficPredictions;

    private static List<TrafficClass> listOfTrafficClasses;

    private static List<Queue> listOfQueues;
    public static int getNumberOfRuns() {
        return listOfTrafficLoads.get(0).size();
    }

    /**
     * Function to read the config file for the simulator
     */
    public static void readSeedFile(String pathFile) throws IOException {
        listOfSeeds = new ArrayList<>();
        new ReadFile(pathFile);
        String line = ReadFile.readLine();
        int lineCounter = 0;
        while (line != null) {
            line = line.replaceAll("\\s+", "");
            byte[] seed = new BigInteger(line, 2).toByteArray();
            if (seed.length == 17) {
                byte[] seedCopy = new byte[16];
                for (int i = 0; i < seed.length - 1; i++)
                    seedCopy[i] = seed[i + 1];
                listOfSeeds.add(seedCopy);
            } else
                listOfSeeds.add(seed);
            line = ReadFile.readLine();
            lineCounter++;
        }
        listOfTrafficLoads= new ArrayList<>();
        listOfTrafficPredictions = new ArrayList<>();
    }

    public static void readTrafficFile(String pathFile) throws IOException {
        listOfTrafficLoads.add(new ArrayList<>());
        new ReadFile(pathFile);
        String line = ReadFile.readLine();
        int lineCounter = 0;
        while (line != null) {
           // String[] trafficString = line.split("");
            //double[] traffic = Arrays.stream(trafficString).mapToDouble(Double::valueOf).toArray();
            Double traffic = Double.parseDouble(line);
            listOfTrafficLoads.get(listOfTrafficLoads.size()-1).add(traffic*trafficScaling[listOfTrafficLoads.size()-1]);
            line = ReadFile.readLine();
            lineCounter++;
        }
    }

    public static void readTrafficPredictFile(String pathFile) throws IOException {
        listOfTrafficPredictions.add(new ArrayList<>());
        new ReadFile(pathFile);
        String line = ReadFile.readLine();
        int lineCounter = 0;
        while (line != null) {
           // String[] trafficString = line.split(",");
            //double[] traffic = Arrays.stream(trafficString).mapToDouble(Double::valueOf).toArray();
            Double traffic = Double.parseDouble(line);
            listOfTrafficPredictions.get(listOfTrafficPredictions.size()-1).add(traffic*trafficScaling[listOfTrafficPredictions.size()-1]);
            line = ReadFile.readLine();
            lineCounter++;
        }
    }




    public static void initializeNetworkState(){

        defineBoundaries();
        new NetworkState(onus, numberOfSubcarriers,capacityPerSubcarriers,subcarrierBoundaries);
    }

    public static void initializeTrafficForONUs(){
        seedCounter=0;
        double[] trafficClassProb= new double[numberOfTrafficClasses];
        listOfTrafficClasses = new ArrayList<>();
        lambdaPerSource=0;
        for (int i = 0; i <numberOfTrafficClasses ; i++) { // only one destination per onus
            trafficClassProb[i]=listOfTrafficLoads.get(i).get(0)/(onus*meanHoldingTimes[i]);
            lambdaPerSource += trafficClassProb[i];
            // initialize traffic classes for generating holding time distribution
            listOfTrafficClasses.add(new TrafficClass(i, bandwidthDemands[i], htDistribution, meanHoldingTimes[i], cov[i]));
        }
        for (int i = 0; i <numberOfTrafficClasses ; i++)
            trafficClassProb[i]=trafficClassProb[i]/lambdaPerSource;

        listOfGenerators = new ArrayList<>();
        listOfQueues = new ArrayList<>();
        for (int i = 0; i < onus; i++) {
            listOfGenerators.add(new TrafficGenerator(i, lambdaPerSource, trafficClassProb));
            listOfQueues.add(new Queue(i)) ;
        }
        listOfGenerators.forEach(TrafficGenerator::initialize);
        interval = simulationTime/listOfTrafficLoads.get(0).size();
    }

    public static void renewTrafficForONUs(){
        seedCounter=0;
        double[] trafficClassProb= new double[numberOfTrafficClasses];

        lambdaPerSource=0;
        for (int i = 0; i <numberOfTrafficClasses ; i++) { // only one destination per onus
            trafficClassProb[i]=listOfTrafficLoads.get(i).get(Launcher.get_runNumber())/(onus*meanHoldingTimes[i]);
            lambdaPerSource += trafficClassProb[i];
            }
        for (int i = 0; i <numberOfTrafficClasses ; i++)
            trafficClassProb[i]=trafficClassProb[i]/lambdaPerSource;



        for (int i = 0; i < onus; i++) {
            listOfGenerators.get(i).renewParameters(lambdaPerSource, trafficClassProb);

        }


    }


    public static void defineBoundaries(){
        if(dynamicBoundary) {
            // dynamic boundaries
            double tempSubcarriers=numberOfSubcarriers;
            double boundary = 0.0;
            for (int i = 0; i < listOfTrafficPredictions.size(); i++)
                boundary += listOfTrafficPredictions.get(i).get(Launcher.get_runNumber())*bandwidthDemands[i]/meanHoldingTimes[i];//*bandwidthDemands[i];
            for (int i = 0; i < listOfTrafficPredictions.size() - 1; i++)
                subcarrierBoundaries[i] = tempSubcarriers * listOfTrafficPredictions.get(i).get(Launcher.get_runNumber())*bandwidthDemands[i] / (meanHoldingTimes[i]*boundary);

//            subcarrierBoundaries[0] = Math.floor(tempSubcarriers * listOfTrafficPredictions.get(0).get(Launcher.get_runNumber())*bandwidthDemands[0] / (meanHoldingTimes[0]*boundary));
//            subcarrierBoundaries[1] = Math.ceil(numberOfSubcarriers * listOfTrafficPredictions.get(1).get(Launcher.get_runNumber())*bandwidthDemands[1] / (meanHoldingTimes[1]*boundary));

        }
        else{
            double[] boundary= new double[listOfTrafficPredictions.size()];
            double total=0.0;
            for (int i = 0; i < listOfTrafficPredictions.size(); i++){
                for (int j = 0; j <listOfTrafficPredictions.get(i).size() ; j++)
                    boundary[i] += listOfTrafficPredictions.get(i).get(j)*bandwidthDemands[i]/meanHoldingTimes[i];
                total += boundary[i];
            }
            for (int i = 0; i < listOfTrafficPredictions.size()-1; i++)
                subcarrierBoundaries[i] = numberOfSubcarriers *boundary[i]/total;
              //  subcarrierBoundaries[i] = Math.floor(numberOfSubcarriers *boundary[i]/total);
        }
        subcarrierBoundaries[listOfTrafficPredictions.size() - 1] = numberOfSubcarriers - subcarrierBoundaries[0] - subcarrierBoundaries[1];
    }

   /* public static void defineBoundaries(){
        if(dynamicBoundary) {
            // dynamic boundaries
            double boundary = 0.0;
            for (int i = 0; i < listOfTrafficPredictions.size(); i++)
                boundary += listOfTrafficPredictions.get(i).get(Launcher.get_runNumber())*bandwidthDemands[i];//;
            // give more bandwidth to higher traffic
            if (listOfTrafficPredictions.get(0).get(Launcher.get_runNumber()) < listOfTrafficPredictions.get(0).get(Launcher.get_runNumber())) {
                subcarrierBoundaries[0] = Math.max(1, Math.floor(numberOfSubcarriers * listOfTrafficPredictions.get(0).get(Launcher.get_runNumber()) / boundary));
                subcarrierBoundaries[1] = numberOfSubcarriers - 1 - subcarrierBoundaries[0];
            } else {
                subcarrierBoundaries[1] = Math.max(1, Math.floor(numberOfSubcarriers * listOfTrafficPredictions.get(1).get(Launcher.get_runNumber()) / boundary));
                subcarrierBoundaries[0] = numberOfSubcarriers - 1 - subcarrierBoundaries[1];
            }
        }
        else{
            double[] boundary= new double[listOfTrafficPredictions.size()];
            double total=0.0;
            for (int i = 0; i < listOfTrafficPredictions.size(); i++){
                for (int j = 0; j <listOfTrafficPredictions.get(i).size() ; j++)
                    boundary[i] += listOfTrafficPredictions.get(i).get(j)*bandwidthDemands[i];
                total += boundary[i];
            }

            subcarrierBoundaries[0] = Math.floor(numberOfSubcarriers *boundary[0]/total);
            subcarrierBoundaries[1] = numberOfSubcarriers - 1 - subcarrierBoundaries[0];
        }
        subcarrierBoundaries[listOfTrafficPredictions.size() - 1] = numberOfSubcarriers - subcarrierBoundaries[0] - subcarrierBoundaries[1];
    }*/

    public static double[] getSubcarrierBoundaries() {
        return subcarrierBoundaries;
    }

    public static byte[] getSeed() {
        seedCounter++;
        return listOfSeeds.get(seedCounter);
    }

    public static int getNumberOfClasses() {
        return numberOfTrafficClasses;
    }

    public static int getNumberOfONUs(){
        return onus;
    }
    public static List<TrafficClass> getListOfTrafficClasses() {
        return listOfTrafficClasses;
    }

    public static double getMaxSimTime() {
        return simulationTime;
    }

    public static boolean isDelayAllowed() {
        return delayAllowed;
    }

    public static Queue getAQueue(int source) {
        return listOfQueues.get(source);
    }

    public static double getInterval() { return interval; }
    public static double getMeanHoldingTimes(int i){
        return meanHoldingTimes[i];
    }

//    public static void initializeTrafficPerONUsUsingOverallLoad(){
//        double[] scalingFactor= new double[numberOfTrafficClasses];
//        double factor= 0;//
//
//        for (int i = 0; i <numberOfTrafficClasses ; i++)
//            factor += arrivalRatio[i]/arrivalRatio[0];
//
//        for (int i = 0; i <numberOfTrafficClasses ; i++) { // only one destination per onus
//            scalingFactor[i] = (offeredLoad[Launcher.get_runNumber()] / onus) * (1 / factor) * (arrivalRatio[i] / arrivalRatio[0]);
//            lambdaPerSource += scalingFactor[i]/meanHoldingTimes[i];
//        }

//      }
}
