package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    public int NUM_THREADS = 4;
    int[][] grid;

    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {
        CornerFindingResult res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        this.totalPopulation = res.getTotalPopulation();
        grid = new int[numColumns + 1][numRows + 1];
        Lock[][] popLocks = new Lock[numColumns + 1][numRows + 1];
        for(int i = 0; i < numColumns + 1; i++){
            for(int j = 0; j < numRows + 1; j++){
                popLocks[i][j] = new ReentrantLock();
            }
        }

        PopulateLockedGridTask[] tasks = new PopulateLockedGridTask[NUM_THREADS - 1];
        int size = censusData.length / NUM_THREADS;
        for(int i = 0; i < NUM_THREADS - 1; i++){
            tasks[i] = new PopulateLockedGridTask(censusData, size * i, size * (i + 1), numRows, numColumns,
                    res.getMapCorners(),grid, popLocks);
        }
        // Run NUM_THREAD in current thread
        PopulateLockedGridTask threadTask = new PopulateLockedGridTask(censusData, size * (NUM_THREADS - 1), censusData.length,
                numRows, numColumns, res.getMapCorners(),grid, popLocks);

        for (PopulateLockedGridTask t : tasks) {
            t.start();
        }
        threadTask.run();

        for (PopulateLockedGridTask t : tasks) {
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Step 2
        for (int i = 1; i <= numColumns; i++) {
            for (int j = 1; j <= numRows; j++) {
                grid[i][j] = (grid[i][j] + grid[i - 1][j] + grid[i][j - 1]) - grid[i - 1][j - 1];
            }
        }

    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return grid[east][north] - grid[west - 1][north] - grid[east][south - 1] + grid[west - 1][south - 1];
    }
}


