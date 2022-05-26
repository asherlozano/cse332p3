package queryresponders;

import cse332.interfaces.QueryResponder;
import cse332.types.CensusGroup;
import cse332.types.MapCorners;

public class SimpleSequential extends QueryResponder {
    MapCorners corners;
    private int numColumns;
    private int numRows;
    private int pop;
    private CensusGroup[] censusData;
    public SimpleSequential(CensusGroup[] censusData, int numColumns, int numRows) {
        this.corners = new MapCorners(censusData[0]);
        for (CensusGroup group : censusData){
            this.corners = corners.encompass(new MapCorners(group));
            this.totalPopulation += group.population;
        }
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.pop = 0;
    }

    @Override
    public int getPopulation(int west, int south, int east, int north) {
        if (west < 1 || west > this.numColumns || south < 1 || south > this.numRows || east < west ||
                east > this.numColumns
                || north < south || north > this.numRows) {
            throw new IllegalArgumentException();
        }
        double cellHeight = (this.corners.north - this.corners.south)/ numRows;
        double cellWidth = (this.corners.east - this.corners.west)/ numColumns;
        double nCorn = cellHeight * (north) + this.corners.south;
        double sCorn = cellHeight * (south - 1) + this.corners.south;
        double wCorn = cellWidth * (west-1) + this.corners.west;
        double eCorn = cellWidth * (east) + this.corners.west;
        for (CensusGroup group: censusData){
            MapCorners corners = new MapCorners(group);
            if(corners.north <= nCorn && corners.south >= sCorn && corners.east <= eCorn && corners.west >= wCorn){
                pop += group.population;
            }
        }
        return pop;
    }
}
