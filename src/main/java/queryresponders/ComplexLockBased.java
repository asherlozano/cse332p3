package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateLockedGridTask;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;

public class ComplexLockBased extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool(); // only to invoke CornerFindingTask
    public int NUM_THREADS = 4;
    private CensusGroup[] censusData;
    private int numColumns, numRows;
    CornerFindingResult res;
    MapCorners corners;
    int[][] grid;
    double cellH, cellW;
    Lock[][] lockGrid;

    public ComplexLockBased(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        this.totalPopulation = res.getTotalPopulation();
        this.corners = res.getMapCorners();
        this.cellH = (this.corners.north - this.corners.south)/numRows;
        this.cellW = (this.corners.east - this.corners.west)/numColumns;
        this.grid = new int[numRows +1][numColumns +1];
        PopulateLockedGridTask[] ts = new PopulateLockedGridTask[NUM_THREADS];
        for(int i = 0; i < NUM_THREADS-1; i++){
            ts[i] = new PopulateLockedGridTask(censusData, i*(censusData.length/NUM_THREADS),
                    (i+1)*(censusData.length/NUM_THREADS), numRows, numColumns, corners, cellW, cellH, grid, lockGrid);
        }
        // Run NUM_THREAD in current thread
        ts[NUM_THREADS-1] = new PopulateLockedGridTask(censusData,
                (NUM_THREADS-1)*(censusData.length/NUM_THREADS), censusData.length,
                numRows, numColumns, corners, cellW, cellH, grid, lockGrid);

        for (int i = 0; i < NUM_THREADS-1; i++) {
            ts[i].run();
        }

        ts[NUM_THREADS - 1].run();

        for (int i = 0; i < NUM_THREADS-1; i++) {
            try {
                ts[i].join();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
                System.exit(1);
            }
        }

        // Step 2
        for (int i = 1; i <= numRows; i++) {
            for (int j = 1; j <= numColumns; j++) {
                grid[i][j] += (grid[i - 1][j] + grid[i][j - 1]) - grid[i - 1][j - 1];
            }
        }

    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        assert west >= 1 && west <= this.numColumns && south <= this.numRows && east <= this.numColumns
                && north >= south && north <= this.numRows;
        int nE = grid[north][east];
        int sE = grid[south-1][east];
        int sW = grid[south-1][west-1];
        int nW = grid[north][west-1];
        return (nE-sE-nW)+sW;
    }
}

