package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    MapCorners corners;
    int lo, hi, numRows, numColumns;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners) {
        this.censusGroups = censusGroups;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.hi = hi;
        this.lo = lo;
        this.corners = corners;
    }

    @Override
    protected int[][] compute() {
        if((hi - lo) <= SEQUENTIAL_CUTOFF){
            return this.sequentialMergeGird();
        }
        int mid = (hi - lo)/ 2 + lo;
        PopulateGridTask task1 = new PopulateGridTask(censusGroups, lo, mid, numRows, numColumns, corners);
        PopulateGridTask task2= new PopulateGridTask(censusGroups, mid, hi, numRows, numColumns, corners);
        task1.fork();
        int[][] hiRes = task2.compute();
        int[][] loRes = task1.join();
        POOL.invoke(new MergeGridTask(loRes, hiRes, 0, loRes[0].length, 0, loRes.length));
        return loRes;
    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private int[][] sequentialMergeGird() {
        int[][] pop = new int[numColumns + 1][numRows + 1];
        for(int i = lo; i < hi; i++){
            int row = getRow(censusGroups[i], this.corners, this.numRows);
            int col = getColumn(censusGroups[i], this.corners, this.numColumns);
            pop[col][row] += censusGroups[i].population;
        }
        return pop;
    }

    private int getRow(CensusGroup group, MapCorners corners, int rows){
        int row = (int) (rows * ((group.latitude - corners.south)/ (corners.north - corners.south))) + 1;
        if (row > rows)
            row = rows;
        return row;
    }

    private int getColumn(CensusGroup group, MapCorners corners, int columns){
        int col = (int) (columns * ((group.longitude - corners.west)/ (corners.east - corners.west))) + 1;
        if (col > columns)
            col = columns;
        return col;
    }
}




