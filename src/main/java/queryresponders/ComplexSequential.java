package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;



public class ComplexSequential extends QueryResponder {
    int[][] grid;

    public ComplexSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        grid = new int[numColumns+1][numRows+1];
        MapCorners corners = new MapCorners(censusData[0]);
        for (CensusGroup group : censusData){
            totalPopulation += group.population;
            corners = corners.encompass(new MapCorners(group));
        }
        for(CensusGroup group : censusData){
            int xR = getRow(group, corners, numRows);
            int yC = getCol(group, corners, numColumns);
            grid[yC][xR] += group.population;
        }
        for(int i =1; i <= numColumns; i++){
            for(int j = 1; j <= numRows; j++){
                grid[i][j] = grid[i][j] + grid[i-1][j] + grid[i][j-1] - grid[i-1][j-1];
            }
        }
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        return grid[east][north]-grid[west-1][north]-grid[east][south-1]+grid[west-1][south-1];
    }
    private int getRow(CensusGroup group, MapCorners corners, int rows){
        int row = (int)(rows*((group.latitude-corners.south) / (corners.north- corners.south)))+1;
        if(row > rows){
            row = rows;
        }
        return row;
    }
    private int getCol(CensusGroup group, MapCorners corners, int cols){
        int col = (int)(cols * ((group.longitude - corners.west) / (corners.east-corners.west)))+1;
        if(col > cols){
            col = cols;
        }
        return col;
    }
}



