package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
/*
   1) This class is used in version 4 to create the initial grid holding the total population for each grid cell
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) Note that merging the grids from the left and right subtasks should NOT be done in this class.
      You will need to implement the merging in parallel using a separate parallel class (MergeGridTask.java)
 */

public class PopulateGridTask extends RecursiveTask<int[][]> {
    final static int SEQUENTIAL_CUTOFF = 10000;
    private static final ForkJoinPool POOL = new ForkJoinPool();
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    double cellWidth, cellHeight;

    public PopulateGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = corners;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
    }

    @Override
    protected int[][] compute() {
        if (hi - lo <= SEQUENTIAL_CUTOFF){
            return sequentialPopulateGrid(censusGroups, lo, hi, numRows,numColumns, corners, cellWidth, cellHeight);
        }
        int mid = lo + (hi - lo) / 2;
        PopulateGridTask left = new PopulateGridTask(censusGroups, lo, mid, numRows,numColumns, corners, cellWidth, cellHeight);
        PopulateGridTask right = new PopulateGridTask(censusGroups, mid, hi, numRows, numColumns, corners, cellWidth, cellHeight);
        left.fork();
        int[][] rightRes = right.compute();
        int[][] leftRes = left.join();
        POOL.invoke(new MergeGridTask(leftRes,rightRes,0, leftRes[0].length, 0, leftRes.length));
        return leftRes;
    }

    private int[][] sequentialPopulateGrid(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, double cellWidth, double cellHeight) {
        int[][] grid = new int[numRows +1][numColumns+1];
        int low = lo;
        while(low < hi){
            int xR = (int)((censusGroups[low].latitude-corners.south)/cellHeight)+1;
            int yC = (int)((censusGroups[low].longitude-corners.west)/ cellWidth)+1;
            if (xR >= numRows){
                xR = grid.length-1;
            }
            if(yC >= numColumns){
                yC = grid[0].length-1;
            }
            grid[xR][yC]+= censusGroups[low].population;
            low++;
        }
        return grid;
    }
}

