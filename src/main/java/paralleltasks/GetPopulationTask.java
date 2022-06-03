package paralleltasks;

import cse332.exceptions.NotYetImplementedException;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

import java.util.concurrent.RecursiveTask;

/*
   1) This class is the parallel version of the getPopulation() method from version 1 for use in version 2
   2) SEQUENTIAL_CUTOFF refers to the maximum number of census groups that should be processed by a single parallel task
   3) The double parameters(w, s, e, n) represent the bounds of the query rectangle
   4) The compute method returns an Integer representing the total population contained in the query rectangle
 */
public class GetPopulationTask extends RecursiveTask<Integer> {
    final static int SEQUENTIAL_CUTOFF = 1000;
    CensusGroup[] censusGroups;
    int lo, hi;
    int w, s, e, n;
    MapCorners grid;
    int rows, columns;

    public GetPopulationTask(CensusGroup[] censusGroups, int lo, int hi, int w, int s, int e, int n, MapCorners grid, int rows, int columns) {
        this.censusGroups = censusGroups;
        this.lo = lo;
        this.hi = hi;
        this.w = w;
        this.s = s;
        this.e = e;
        this.n = n;
        this.grid = grid;
        this.rows = rows;
        this.columns = columns;
    }

    // Returns a number for the total population
    @Override
    protected Integer compute() {
        if ( hi - lo < SEQUENTIAL_CUTOFF){
            return sequentialGetPopulation();
        }
        int mid = (hi - lo) / 2 + lo;
        GetPopulationTask left = new GetPopulationTask(censusGroups, lo, mid, w,s, e, n,grid, rows, columns);
        GetPopulationTask right = new GetPopulationTask(censusGroups, mid, hi, w,s,e,n,grid, rows, columns);
        left.fork();
        return right.compute() + left.join();
    }

    private Integer sequentialGetPopulation(CensusGroup[] censusGroups, int lo, int hi, double w, double s, double e, double n) {
        int pop = 0;
        for(int i = lo; i < hi; i++){
            int row = getRow(censusGroups[i]);
            int col = getColumn(censusGroups[i]);
            if (col >= w && col <= e && row >= s && row <= n){
                pop += censusGroups[i].population;
            }
        }
        return pop;
    }

    private int getRow(CensusGroup group){
        int row = (int) (rows * ((group.latitude - grid.south)/ (grid.north - grid.south))) + 1;
        if (row > rows)
            row = rows;
        return row;
    }

    private int getColumn(CensusGroup group){
        int col = (int) (columns * ((group.longitude - grid.west)/ (grid.east - grid.west))) + 1;
        if (col > columns)
            col = columns;
        return col;
    }
}



