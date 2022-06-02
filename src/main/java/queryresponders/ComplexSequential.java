package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.CornerFindingResult;
import cse332.types.MapCorners;


public class ComplexSequential extends QueryResponder {
    CensusGroup[] censusData;
    int[][] grid;
    int numColumns, numRows;
    double cellHeight, cellWidth;
    MapCorners corners;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.censusData = censusData;
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.grid = new int[numRows+1][numColumns+1];
        this.corners = new MapCorners(censusData[0]);
        for (CensusGroup group : censusData){
            this.corners = corners.encompass(new MapCorners(group));
            this.totalPopulation += group.population;
        }
        this.cellWidth = (this.corners.east - this.corners.west) / numColumns;
        this.cellHeight = (this.corners.north - this.corners.south) / numRows;
        for(CensusGroup group : censusData){
            int xR = (int)((group.latitude - corners.south) / cellHeight) + 1;
            int yC = (int)((group.longitude - corners.west) / cellWidth)+1;
            if(xR >= numRows){
                xR = grid.length - 1;
            }
            if(yC >= numColumns){
                yC = grid[0].length-1;
            }
            grid[xR][yC]+= group.population;
        }
        for(int i =1; i <= numRows; i++){
            for(int j = 1; j <= numColumns; j++){
                grid[i][j] += grid[i-1][j] + grid[i][j-1]-grid[i-1][j-1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return grid[east][north] - grid[west - 1][north] - grid[east][south - 1] + grid[west - 1][south - 1];
    }
}

