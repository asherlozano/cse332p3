package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {
    MapCorners corners;
    private int numColumns;
    private int numRows;
    private CensusGroup[] censusData;
    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.numRows = numRows;
        this.censusData = censusData;
        this.numColumns = numColumns;
        MapCorners start = new MapCorners(censusData[0]);
        for (CensusGroup group : censusData){
            totalPopulation += group.population;
            start = start.encompass(new MapCorners(group));
        }
        corners = start;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        int pop = 0;
        for (CensusGroup group : censusData){
            int r = getRow(group);
            int c = getCol(group);
            if(c>=west&&c<=east&&r>=south&&r<=north){
                pop += group.population;
            }
        }
        return pop;
    }
    private int getRow(CensusGroup group){
        int row = (int)(numRows*((group.latitude-corners.south) / (corners.north- corners.south)))+1;
        if(row > numRows){
            row = numRows;
        }
        return row;
    }
    private int getCol(CensusGroup group){
        int col = (int)(numColumns * ((group.longitude - corners.west) / (corners.east-corners.west)))+1;
        if(col > numColumns){
            col = numColumns;
        }
        return col;
    }
}
