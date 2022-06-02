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
    CornerFindingResult res;

    public SimpleParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        CornerFindingTask corner = new CornerFindingTask(censusData, 0, censusData.length);
        this.res = POOL.invoke(corner);
        this.totalPopulation += res.getTotalPopulation();
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        assert west >= 1 && west <= this.numColumns && south <= this.numRows && east <= this.numColumns
                && north >= south && north <= this.numRows;
        MapCorners corners = res.getMapCorners();
        double cellWidth = (corners.east - corners.west) / numColumns;
        double cellHeight = (corners.north - corners.south) / numRows;
        double westCorner = cellWidth *(west-1) + corners.west;
        double eastCorner = cellWidth * (east) + corners.west;
        double northCorner = cellHeight*(north)+corners.south;
        double southCorner = cellHeight*(south-1)+corners.south;
        return POOL.invoke(new GetPopulationTask(censusData, 0, censusData.length, westCorner, southCorner, eastCorner, northCorner, corners));
    }
}



