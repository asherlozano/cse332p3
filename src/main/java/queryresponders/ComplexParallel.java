package queryresponders;

import cse332.exceptions.NotYetImplementedException;
import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.PopulateGridTask;

import java.util.concurrent.ForkJoinPool;

public class ComplexParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private int[][] grid;

    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        CornerFindingResult res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        this.totalPopulation = res.getTotalPopulation();
        grid = POOL.invoke(new PopulateGridTask(censusData,0,censusData.length,numRows,numColumns, res.getMapCorners()));
        for (int i = 1; i <= numColumns; i++) {
            for (int j = 1; j <= numRows; j++) {
                grid[i][j] = grid[i][j] + grid[i][j - 1] + grid[i - 1][j] - grid[i - 1][j - 1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return grid[east][north] - grid[west - 1][north] - grid[east][south - 1] + grid[west - 1][south - 1];
    }
}



