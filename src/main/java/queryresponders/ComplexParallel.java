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
    private CensusGroup[] censusData;
    private int numColumns, numRows;
    CornerFindingResult res;
    MapCorners corners;
    private int[][] grid;
    private double cellH, cellW;
    public ComplexParallel(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        res = POOL.invoke(new CornerFindingTask(censusData, 0, censusData.length));
        this.totalPopulation = res.getTotalPopulation();
        this.corners = res.getMapCorners();
        this.cellH = (this.corners.north - this.corners.south)/numRows;
        this.cellW = (this.corners.east - this.corners.west)/numColumns;
        this.grid = POOL.invoke(new PopulateGridTask(censusData,0,censusData.length,numRows,numColumns,corners,cellW,cellH ));
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
