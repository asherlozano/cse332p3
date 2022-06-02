package paralleltasks;

import cse332.exceptions.NotYetImplementedException;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    public final static int SEQUENTIAL_CUTOFF = 10;
    private int[][] left, right;
    private int rowLo, rowHi, colLo, colHi;

    public MergeGridTask(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        this.left = left;
        this.right = right;
        this.rowHi = rowHi;
        this.rowLo = rowLo;
        this.colHi = colHi;
        this.colLo = colLo;
    }

    @Override
    protected void compute() {
        if((rowHi - rowLo) <= SEQUENTIAL_CUTOFF || (colHi - colLo) <= SEQUENTIAL_CUTOFF){
            sequentialMergeGird(left, right, rowLo, rowHi, colLo, colHi);
        }
        int mid = rowLo +(rowHi - rowLo) / 2;
        MergeGridTask leftRow = new  MergeGridTask(left, right, rowLo, mid, colLo, colHi);
        MergeGridTask rightC = new  MergeGridTask(left, right, mid, rowHi, colLo, colHi);
        leftRow.fork();
        rightC.compute();
        leftRow.join();
    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private void sequentialMergeGird(int[][] left, int[][] right, int rowLo, int rowHi, int colLo, int colHi) {
        for(int i = rowLo; i < rowHi; i++){
            for (int j = colLo; j < colHi; j++){
                left[i][j] += right[i][j];
            }
        }
    }
}


