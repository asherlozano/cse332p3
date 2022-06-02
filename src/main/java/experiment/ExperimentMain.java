package experiment;

import cse332.types.CensusGroup;
import main.PopulationQuery;
import queryresponders.ComplexSequential;
import queryresponders.SimpleSequential;

public class ExperimentMain {
    private static CensusGroup[] data;
    final static int NUM_TRIALS = 10;
    final static int NUM_WARMUPS = 4;
    final static int NUM_QUERIES = 1000;
    public static void main(String[] args) {
        data = PopulationQuery.parse("CenPop2010.txt");
        ComplexSequential complexGrid = new ComplexSequential(data, 500, 100);
        SimpleSequential sequentialGrid = new SimpleSequential(data, 500, 100);
        double sequentialTotalTime = 0;
        for (int i = 0; i < NUM_TRIALS; i++) {
            long startTime = System.nanoTime();
            for (int j = 0; j < NUM_QUERIES; j++) {
                sequentialGrid.getPopulation(51, 1, 500, 100);
            }
            long endTime = System.nanoTime();
            if (NUM_WARMUPS <= i) {
                sequentialTotalTime += (endTime-startTime);
            }
        }
        double sequentialAverageRuntime = sequentialTotalTime/ (NUM_TRIALS - NUM_WARMUPS);
        System.out.println("Simple Sequential average run time: " + sequentialAverageRuntime);

        double complexTotalTime = 0;
        for (int i = 0; i < NUM_TRIALS; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < NUM_QUERIES; j++) {
                complexGrid.getPopulation(51, 1, 500, 100);

            }
            long end = System.nanoTime();
            if (NUM_WARMUPS <= i) {
                complexTotalTime += (end-start);
            }
        }
        double complexAverageRuntime = complexTotalTime/ (NUM_TRIALS - NUM_WARMUPS);
        System.out.println("Complex average run time: " + complexAverageRuntime);
    }
}
