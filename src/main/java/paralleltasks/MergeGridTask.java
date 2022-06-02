package paralleltasks;

import cse332.exceptions.NotYetImplementedException;

import java.util.concurrent.RecursiveAction;

/*
   1) This class is used by PopulateGridTask to merge two grids in parallel
   2) SEQUENTIAL_CUTOFF refers to the maximum number of grid cells that should be processed by a single parallel task
 */

public class MergeGridTask extends RecursiveAction {
    final static int SEQUENTIAL_CUTOFF = 10;
    int[][] left, right;
    int rowLo, rowHi, colLo, colHi;

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
        if((rowHi - rowLo) * (colHi - colLo) <= SEQUENTIAL_CUTOFF){
            this.sequentialMergeGird();
            return;
        }
        int rowMid = (rowHi - rowLo) / 2 + rowLo;
        int colMid = (colHi - rowLo) / 2 + rowLo;
        MergeGridTask leftRow = new MergeGridTask(left, right, rowLo, rowMid, colLo, colHi);
        MergeGridTask rightRow = new MergeGridTask(left, right, rowLo, rowMid, colMid, colHi);
        MergeGridTask leftCol = new MergeGridTask(left, right, rowMid, rowHi, colLo, colMid);
        MergeGridTask rightCol = new MergeGridTask(left, right, rowMid, rowHi, colMid, colHi);
        leftRow.fork();
        rightRow.fork();
        leftCol.fork();
        rightCol.compute();
        leftRow.join();
        rightRow.join();
        leftCol.join();
    }

    // according to google gird means "prepare oneself for something difficult or challenging" so this typo is intentional :)
    private void sequentialMergeGird() {
        for(int i = colLo; i < colHi; i++){
            for (int j = rowLo; j < rowHi; j++){
                left[i][j] += right[i][j];
            }
        }
    }
}


