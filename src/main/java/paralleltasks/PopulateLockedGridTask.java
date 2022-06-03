package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.locks.Lock;

/*
   1) This class is used in version 5 to create the initial grid holding the total population for each grid cell
        - You should not be using the ForkJoin framework but instead should make use of threads and locks
        - Note: the resulting grid after all threads have finished running should be the same as the final grid from
          PopulateGridTask.java
 */

public class PopulateLockedGridTask extends Thread {
    CensusGroup[] censusGroups;
    int lo, hi, numRows, numColumns;
    MapCorners corners;
    int[][] populationGrid;
    Lock[][] lockGrid;

0
    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners, int[][] populationGrid, Lock[][] lockGrid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = corners;
        this.populationGrid = populationGrid;
        this.lockGrid = lockGrid;
    }

    @Override
    public void run() {
        for(int i = lo; i < hi; i ++){
            int row = getRow(censusGroups[i], corners, numRows);
            int col = getColumn(censusGroups[i], corners, numColumns);
            lockGrid[col][row].lock();
            populationGrid[col][row] += censusGroups[i].population;
            lockGrid[col][row].unlock();
        }
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



