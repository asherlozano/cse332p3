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
    double cellWidth, cellHeight;
    int[][] populationGrid;
    Lock[][] lockGrid;


    public PopulateLockedGridTask(CensusGroup[] censusGroups, int lo, int hi, int numRows, int numColumns, MapCorners corners,
                                  double cellWidth, double cellHeight, int[][] popGrid, Lock[][] lockGrid) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.corners = corners;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.populationGrid = popGrid;
        this.lockGrid = lockGrid;
    }

    @Override
    public void run() {
        for(int i = lo; i < hi; i ++){
            int x = (int)((censusGroups[lo].latitude-corners.south)/cellHeight)+1;
            int y = (int)((censusGroups[lo].latitude-corners.west)/ cellWidth)+1;
            if (x > numRows){
                x = populationGrid.length-1;
            }
            if(y >= numColumns){
                y = populationGrid[0].length-1;
            }
            lockGrid[x][y].lock();
            populationGrid[x][y] += censusGroups[i].population;
            lockGrid[x][y].unlock();
        }
    }
}
