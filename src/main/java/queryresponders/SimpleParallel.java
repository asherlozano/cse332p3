package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;
import paralleltasks.CornerFindingTask;
import paralleltasks.GetPopulationTask;

import java.util.concurrent.ForkJoinPool;

public class SimpleParallel extends QueryResponder {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private CensusGroup[] censusData;
    private int numColumns;
    private int numRows;
    private MapCorners corners;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        numColumns = numColumns;
        numRows = numRows;
        CornerFindingResult res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        corners = res.getMapCorners();
        this.totalPopulation += res.getTotalPopulation();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return POOL.invoke(new GetPopulationTask(censusData, 0, censusData.length, west, south, east, north, corners,numRows, numColumns));
    }
}


